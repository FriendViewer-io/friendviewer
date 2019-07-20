#include "prototype/daemon/daemon_backend_controller.hh"

#include <chrono>
#include <iostream>
#include <thread>

#include "prototype/protobuf/control.pb.h"
#include "prototype/protobuf/session.pb.h"

namespace prototype {
namespace daemon {

namespace util {

void split(const std::string &str, const std::string &tok, std::vector<std::string> &out) {
    size_t start = 0;
    size_t end;
    while ((end = str.find(tok, start)) != std::string::npos) {
        out.emplace_back(str.substr(start, end));
        start = end + 1;
    }
    if (start < str.length()) {
        out.emplace_back(str.substr(start));
    }
}

}  // namespace util

DaemonBackendController::DaemonBackendController()
    : running_(true),
      cli_thread_(&DaemonBackendController::handle_cli, this),
      connect_signal_(false) {}

bool DaemonBackendController::init(uint32_t scr_w, uint32_t scr_h) {
    screen_cap_.init(scr_w, scr_h, 30);
    net_mgr_.subscribe_to_packet(prototype::protobuf::FvPacketType::HANDSHAKE,
                                 [this](auto net_mgr, auto pkt, auto len) {
                                     protobuf::Handshake hs;
                                     hs.ParseFromArray(pkt, len);
                                     this->handle_handshake(hs);
                                 });
    net_mgr_.subscribe_to_packet(prototype::protobuf::FvPacketType::HEARTBEAT,
                                 [this](auto net_mgr, auto pkt, auto len) {
                                     protobuf::Heartbeat hb;
                                     hb.ParseFromArray(pkt, len);
                                     this->handle_heartbeat(hb);
                                 });
    net_mgr_.subscribe_to_packet(prototype::protobuf::FvPacketType::SERVER_MESSAGE,
                                 [this](auto net_mgr, auto pkt, auto len) {
                                     protobuf::ServerMessage sm;
                                     sm.ParseFromArray(pkt, len);
                                     this->handle_servmsg(sm);
                                 });
    net_mgr_.subscribe_to_packet(prototype::protobuf::FvPacketType::USER_LIST,
                                 [this](auto net_mgr, auto pkt, auto len) {
                                     protobuf::UserList ul;
                                     ul.ParseFromArray(pkt, len);
                                     this->handle_user_list(ul);
                                 });
    net_mgr_.subscribe_to_packet(prototype::protobuf::FvPacketType::SESSION_CLOSE,
                                 [this](auto net_mgr, auto pkt, auto len) {
                                     protobuf::SessionClose sc;
                                     sc.ParseFromArray(pkt, len);
                                     this->handle_session_close(sc);
                                 });
    net_mgr_.subscribe_to_packet(prototype::protobuf::FvPacketType::SESSION_REQUEST,
                                 [this](auto net_mgr, auto pkt, auto len) {
                                     protobuf::SessionRequest sr;
                                     sr.ParseFromArray(pkt, len);
                                     this->handle_session_request(sr);
                                 });
    net_mgr_.subscribe_to_packet(prototype::protobuf::FvPacketType::VIDEO_PARAMS,
                                 [this](auto net_mgr, auto pkt, auto len) {
                                     protobuf::VideoParams param;
                                     param.ParseFromArray(pkt, len);
                                     this->handle_video_params(param);
                                 });
    net_mgr_.subscribe_to_packet(prototype::protobuf::FvPacketType::DATA,
                                 [this](auto net_mgr, auto pkt, auto len) {
                                     protobuf::Data data;
                                     data.ParseFromArray(pkt, len);
                                     this->handle_data(data);
                                 });
    net_mgr_.subscribe_to_packet(prototype::protobuf::FvPacketType::CONTROL_INPUT,
                                 [this](auto net_mgr, auto pkt, auto len) {
                                     protobuf::ControlInput input;
                                     input.ParseFromArray(pkt, len);
                                     this->handle_input(input);
                                 });
    return true;
}

void DaemonBackendController::stop() { running_.exchange(false); }

// Will be killed by program termination or std::terminate
void DaemonBackendController::handle_cli() {
    while (true) {
        std::string command;
        std::vector<std::string> tokens;
        std::getline(std::cin, command);
        util::split(command, " ", tokens);

        if (tokens[0] == "connect") {
            if (tokens.size() < 2) {
                std::cout << "No address specified" << std::endl;
            } else {
                connect_address_ = tokens[1];
                std::cout << "Awaiting connection... ";
                connect_signal_.exchange(true);
                while (connect_signal_.load()) {
                    std::this_thread::sleep_for(std::chrono::milliseconds(1));
                }
                if (net_mgr_.connected()) {
                    std::cout << "Successfully connected to remote host " << tokens[1] << std::endl;
                } else {
                    std::cout << "Failed to connect to remote host" << std::endl;
                }
            }
        } else if (tokens[0] == "name") {
            if (tokens.size() < 2) {
                std::cout << "No name specified";
            } else {
                username_ = tokens[1];
            }
        } else if (tokens[0] == "session") {
            if (tokens.size() < 2) {
                std::cout << "No user specified";
            } else {
                prototype::protobuf::SessionRequest sr;
                sr.set_name(tokens[1]);
                enqueue_msg(protobuf::FvPacketType::SESSION_REQUEST, sr.SerializeAsString());
            }
        } else if (tokens[0] == "endsession") {
            prototype::protobuf::SessionClose sc;
            enqueue_msg(protobuf::FvPacketType::SESSION_CLOSE, sc.SerializeAsString());
        } else if (tokens[0] == "exit") {
            stop();
        } else {
            std::cout << "Invalid command" << std::endl;
        }
    }
}
void DaemonBackendController::run_frame() {
    if (net_mgr_.connected()) {
        auto dur_seconds = std::chrono::duration_cast<std::chrono::seconds>(
            std::chrono::system_clock::now() - last_heartbeat_time_.load());
        if (dur_seconds.count() > 6) {
            std::cout << "No connection to remote host, shutting down";
            stop();
        }
        net_mgr_.poll_network_async(false);
        std::string send_chunk;
        while (send_buffer_.dequeue_chunk(send_chunk)) {
            net_mgr_.send_message(send_chunk);
        }
    } else if (connect_signal_.load()) {
        net_mgr_.connect(connect_address_.c_str(), FV_DIST_PORT);
        connect_signal_.exchange(false);
        send_initial_hs();
        last_heartbeat_time_.exchange(std::chrono::system_clock::now());
    }
}

void DaemonBackendController::send_initial_hs() {
    protobuf::Handshake hs;
    hs.set_magic_number(0x12341234ABCDABCDl);
    enqueue_msg(protobuf::FvPacketType::HANDSHAKE, hs.SerializeAsString());
    protobuf::Heartbeat hb;
    hb.set_utc_time(std::chrono::system_clock::now().time_since_epoch().count());
    enqueue_msg(protobuf::FvPacketType::HEARTBEAT, hb.SerializeAsString());
}

void DaemonBackendController::handle_handshake(const protobuf::Handshake &hs) {
    if (static_cast<uint64_t>(hs.magic_number()) != 0xABCDABCABA123456l) {
        stop();
    } else {
        protobuf::NewUser nu;
        nu.set_username(username_);
        enqueue_msg(protobuf::FvPacketType::NEW_USER, nu.SerializeAsString());
    }
}

void DaemonBackendController::handle_heartbeat(const protobuf::Heartbeat &hb) {
    last_heartbeat_time_.exchange(std::chrono::system_clock::now());
    std::this_thread::sleep_for(std::chrono::seconds(2));
    protobuf::Heartbeat new_hb;
    new_hb.set_utc_time(std::chrono::system_clock::now().time_since_epoch().count());
    enqueue_msg(protobuf::FvPacketType::HEARTBEAT, new_hb.SerializeAsString());
}

void DaemonBackendController::handle_servmsg(const protobuf::ServerMessage &sm) {
    switch (sm.type()) {
        case protobuf::ServerMessageType::SUCCESS:
            break;
        case protobuf::ServerMessageType::NAME_TAKEN: {
            std::cout << "Name already taken, retrying with longer name\n";
            username_ += "1";
            protobuf::NewUser nu;
            nu.set_username(username_);
            enqueue_msg(protobuf::FvPacketType::NEW_USER, nu.SerializeAsString());
        } break;
        case protobuf::ServerMessageType::USER_NOT_FOUND: {
            std::cout << "Requested user not found\n";
        } break;
        case protobuf::ServerMessageType::USER_ALREADY_IN_SESSION: {
            std::cout << "Requested user already in a session\n";
        } break;
        case protobuf::ServerMessageType::SESSION_STARTING: {
            state_ = CLIENT;
        } break;
    }
}

void DaemonBackendController::handle_user_list(const protobuf::UserList &ul) {
    std::lock_guard<std::mutex> lock(user_list_mutex_);
    remote_users_.clear();
    remote_users_.insert(remote_users_.end(), ul.users().begin(), ul.users().end());
}

void DaemonBackendController::handle_session_close(const protobuf::SessionClose &sc) {
    if (state_ == LOBBY) {
        return;
    }
    if (state_ == CLIENT) {
        decoder_.shutdown();
        state_ = LOBBY;
        decode_buffer_.clear();
        // CLOSE WINDOW!!
    } else if (state_ == HOST) {
        encoder_running_.exchange(false);
        encoding_thread_->join();
        encoder_.shutdown();
        state_ = LOBBY;
        delete encoding_thread_;
    }
}

void DaemonBackendController::handle_encode() {
    bool sent_param = false;
    std::vector<uint8_t> raw_frame;
    std::string packet_out;
    int pts = 0;
    while (encoder_running_.load()) {
        screen_cap_.get_frame(raw_frame);
        encoder_.encode_frame(raw_frame, packet_out);
        if (!sent_param) {
            protobuf::VideoParams param;
            param.set_width(width_);
            param.set_height(height_);
            std::vector<uint8_t> pps_sps;
            encoder_.get_pps_sps(pps_sps);
            param.set_pps_sps(std::string(pps_sps.begin(), pps_sps.end()));
            param.set_framerate_num(30);
            param.set_framerate_denom(1);

            enqueue_msg(protobuf::FvPacketType::VIDEO_PARAMS, param.SerializeAsString());
        }
        protobuf::Data data;
        data.set_pts(pts++);
        data.set_dts(0);
        data.set_encoded_frame(std::move(packet_out));
        enqueue_msg(protobuf::FvPacketType::DATA, data.SerializeAsString());
    }
}

void DaemonBackendController::handle_session_request(const protobuf::SessionRequest &sr) {
    decoder::H264Encoder::H264Params param;
    param.width = width_;
    param.height = height_;
    param.bit_rate = 4000000;
    param.framerate_num = 30;
    param.framerate_den = 1;
    encoder_.init(param);
    encoding_thread_ = new std::thread(&DaemonBackendController::handle_encode, this);
}

void DaemonBackendController::handle_video_params(const protobuf::VideoParams &param) {
    decoder::H264Decoder::H264Params p;
    p.framerate_num = param.framerate_num();
    p.framerate_den = param.framerate_denom();
    p.width = param.width();
    p.height = param.height();
    p.bit_rate = 0;
    if (!decoder_.init(p)) {
        stop();
    }
    std::vector<uint8_t> junk;
    decoder_.decode_packet(param.pps_sps(), junk);
}

void DaemonBackendController::handle_data(const protobuf::Data &data) {
    std::vector<uint8_t> decoded_frame;
    if (decoder_.decode_packet(data.encoded_frame(), decoded_frame) ==
        decoder::H264Decoder::DecodeStatus::kSuccess) {
        decoder_.set_pts(data.pts());
        decode_buffer_.enqueue_chunk(std::move(decoded_frame));
    }
}

void DaemonBackendController::handle_input(const protobuf::ControlInput &input) {
    input::set_cursor_location(input.abs_x(), input.abs_y());
    if (input.mouse_bitfield() > 0) {
        input::send_virtual_input(input.mouse_bitfield());
    }
    if (input.virtual_key() > 0) {
        input::send_virtual_input(input.virtual_key());
    }
}

void DaemonBackendController::enqueue_msg(protobuf::FvPacketType type, std::string &&inner_msg) {
    protobuf::FvPacket pkt;
    pkt.set_type(type);
    pkt.set_inner_packet(std::move(inner_msg));
    send_buffer_.enqueue_chunk(pkt.SerializeAsString());
}

}  // namespace daemon
}  // namespace prototype
