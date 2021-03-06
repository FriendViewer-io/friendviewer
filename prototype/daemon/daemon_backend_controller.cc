#include "prototype/daemon/daemon_backend_controller.hh"

#include <chrono>
#include <fstream>
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
    width_ = scr_w;
    height_ = scr_h;
    screen_cap_.init(scr_w, scr_h, 30);
    window_.register_callbacks(
        [this](int key) {
            prototype::protobuf::ControlInput input;
            input.set_abs_x(-1);
            input.set_abs_y(-1);
            input.set_mouse_bitfield(0);
            input.set_virtual_key(key);
            enqueue_msg(prototype::protobuf::FvPacketType::CONTROL_INPUT,
                        input.SerializeAsString());
        },
        [this](int button) {
            prototype::protobuf::ControlInput input;
            input.set_abs_x(-1);
            input.set_abs_y(-1);
            input.set_mouse_bitfield(button);
            input.set_virtual_key(0);
            enqueue_msg(prototype::protobuf::FvPacketType::CONTROL_INPUT,
                        input.SerializeAsString());
        },
        [this](double x, double y) {
            prototype::protobuf::ControlInput input;
            input.set_abs_x(static_cast<float>(x));
            input.set_abs_y(static_cast<float>(y));
            input.set_mouse_bitfield(0);
            input.set_virtual_key(0);
            enqueue_msg(prototype::protobuf::FvPacketType::CONTROL_INPUT,
                        input.SerializeAsString());
        });
    net_mgr_.subscribe_to_packet(
        prototype::protobuf::FvPacketType::HANDSHAKE,
        [this](auto net_mgr, auto pkt, auto len) {
            protobuf::Handshake hs;
            hs.ParseFromArray(pkt, len);
            this->handle_handshake(hs);
        },
        true);
    net_mgr_.subscribe_to_packet(
        prototype::protobuf::FvPacketType::HEARTBEAT,
        [this](auto net_mgr, auto pkt, auto len) {
            protobuf::Heartbeat hb;
            hb.ParseFromArray(pkt, len);
            this->handle_heartbeat(hb);
        },
        true);
    net_mgr_.subscribe_to_packet(
        prototype::protobuf::FvPacketType::SERVER_MESSAGE,
        [this](auto net_mgr, auto pkt, auto len) {
            protobuf::ServerMessage sm;
            sm.ParseFromArray(pkt, len);
            this->handle_servmsg(sm);
        },
        true);
    net_mgr_.subscribe_to_packet(
        prototype::protobuf::FvPacketType::USER_LIST,
        [this](auto net_mgr, auto pkt, auto len) {
            protobuf::UserList ul;
            ul.ParseFromArray(pkt, len);
            this->handle_user_list(ul);
        },
        true);
    net_mgr_.subscribe_to_packet(
        prototype::protobuf::FvPacketType::SESSION_CLOSE,
        [this](auto net_mgr, auto pkt, auto len) {
            protobuf::SessionClose sc;
            sc.ParseFromArray(pkt, len);
            this->handle_session_close(sc);
        },
        true);
    net_mgr_.subscribe_to_packet(
        prototype::protobuf::FvPacketType::SESSION_REQUEST,
        [this](auto net_mgr, auto pkt, auto len) {
            protobuf::SessionRequest sr;
            sr.ParseFromArray(pkt, len);
            this->handle_session_request(sr);
        },
        true);
    net_mgr_.subscribe_to_packet(
        prototype::protobuf::FvPacketType::VIDEO_PARAMS,
        [this](auto net_mgr, auto pkt, auto len) {
            protobuf::VideoParams param;
            param.ParseFromArray(pkt, len);
            this->handle_video_params(param);
        },
        true);
    net_mgr_.subscribe_to_packet(
        prototype::protobuf::FvPacketType::DATA,
        [this](auto net_mgr, auto pkt, auto len) {
            protobuf::Data data;
            data.ParseFromArray(pkt, len);
            this->handle_data(data);
        },
        false);
    net_mgr_.subscribe_to_packet(
        prototype::protobuf::FvPacketType::CONTROL_INPUT,
        [this](auto net_mgr, auto pkt, auto len) {
            protobuf::ControlInput input;
            input.ParseFromArray(pkt, len);
            this->handle_input(input);
        },
        true);
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
        } else if (tokens[0] == "userlist") {
            std::cout << "Available users:\n";
            for (auto &&user : remote_users_) {
                std::cout << user << "\n";
            }
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
        net_mgr_.poll_network(false);
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
    close_session_inner();
}

void DaemonBackendController::close_session_inner() {
    if (state_ == LOBBY) {
        return;
    }
    if (state_ == CLIENT) {
        decoder_running_.exchange(false);
        decoding_thread_->join();
        renderer_running_.exchange(false);
        rendering_thread_->join();
        window_.close_window();
        decoder_.shutdown();
        state_ = LOBBY;
        decode_buffer_.clear();
        delete rendering_thread_;
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
            sent_param = true;
        }
        protobuf::Data data;
        data.set_pts(pts++);
        data.set_dts(0);
        data.set_encoded_frame(std::move(packet_out));
        enqueue_msg(protobuf::FvPacketType::DATA, data.SerializeAsString());
        std::this_thread::sleep_for(std::chrono::milliseconds(32));
    }
}

void DaemonBackendController::handle_render(uint32_t w, uint32_t h) {
    window_.create_window(w, h);
    std::vector<uint8_t> raw_frame;
    std::vector<uint8_t> garbage(w * h * 3);
    std::fill(garbage.begin(), garbage.end(), 128);
    while (renderer_running_.load()) {
        if (decode_buffer_.dequeue_chunk(raw_frame)) {
            window_.render_frame(raw_frame);
        }
        std::this_thread::sleep_for(std::chrono::milliseconds(2));
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
    encoder_running_ = true;
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
    renderer_running_ = true;
    rendering_thread_ =
        new std::thread(&DaemonBackendController::handle_render, this, p.width, p.height);
    decoding_thread_ = new std::thread(&DaemonBackendController::handle_decode, this);
    got_pps_sps_.exchange(true);
}

void DaemonBackendController::handle_data(const protobuf::Data &data) {
    encoded_buffer_.enqueue_chunk(std::move(const_cast<protobuf::Data &>(data)));
}

void DaemonBackendController::handle_decode() {
    while (decoder_running_.load()) {
        if (got_pps_sps_.load()) {
            protobuf::Data encoded_frame;
            std::vector<uint8_t> decoded_frame;
            if (encoded_buffer_.dequeue_chunk(encoded_frame)) {

                if (decoder_.decode_packet(encoded_frame.encoded_frame(), decoded_frame) ==
                    decoder::H264Decoder::DecodeStatus::kSuccess) {
                    decode_buffer_.enqueue_chunk(std::move(decoded_frame));
                }
            }
        }
        std::this_thread::sleep_for(std::chrono::milliseconds(1));
    }
}

void DaemonBackendController::handle_input(const protobuf::ControlInput &input) {
    if (input.abs_x() > 0 && input.abs_y() > 0) {
        input::set_cursor_location(input.abs_x(), input.abs_y());
    }
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
