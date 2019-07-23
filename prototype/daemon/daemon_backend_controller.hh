#pragma once

#include <atomic>
#include <chrono>
#include <mutex>
#include <string>
#include <thread>
#include <vector>

#include "prototype/daemon/capture/dxgi_capture.hh"
#include "prototype/daemon/chunk_buffer.hh"
#include "prototype/daemon/decoder/h264_decoder.hh"
#include "prototype/daemon/decoder/h264_encoder.hh"
#include "prototype/daemon/input/input_control.hh"
#include "prototype/daemon/networking/network_manager.hh"
#include "prototype/daemon/renderer/gl_window.hh"
#include "prototype/protobuf/control.pb.h"
#include "prototype/protobuf/session.pb.h"

namespace prototype {
namespace daemon {

constexpr uint16_t FV_DIST_PORT = 61235;

//
class DaemonBackendController {
 public:
    DaemonBackendController();
    bool init(uint32_t scr_w, uint32_t scr_h);

    void stop();
    bool is_running() { return running_.load(); }

    void run_frame();

 private:
    // TODO: USE STATUS AS SIGNIFIER IF HOST OR CLIENT
    void start_session_mode();

    void enqueue_msg(protobuf::FvPacketType type, std::string &&inner_msg);

    void handle_encode();
    void handle_cli();
    void handle_render(uint32_t w, uint32_t h);
    void send_initial_hs();

    // control type
    void handle_handshake(const protobuf::Handshake &hs);
    void handle_heartbeat(const protobuf::Heartbeat &hb);
    void handle_servmsg(const protobuf::ServerMessage &sm);
    void handle_user_list(const protobuf::UserList &ul);
    void handle_session_close(const protobuf::SessionClose &sc);
    void handle_session_request(const protobuf::SessionRequest &sr);

    // session type
    void handle_video_params(const protobuf::VideoParams &param);
    void handle_data(const protobuf::Data &data);
    void handle_input(const protobuf::ControlInput &input);

    void close_session_inner();

    // If false, attempts a smooth shutdown
    std::atomic_bool running_;
    // Thread exclusively for processing blocking stdin reads
    std::thread cli_thread_;
    // All task-based requests will throw results into a ChunkBuffer
    ChunkBuffer<std::vector<uint8_t>> decode_buffer_;
    ChunkBuffer<std::string> send_buffer_;

    networking::NetworkManager net_mgr_;
    std::string connect_address_;
    std::atomic_bool connect_signal_;
    std::atomic<std::chrono::system_clock::time_point> last_heartbeat_time_;

    // Local state
    std::string username_;
    std::mutex user_list_mutex_;
    std::vector<std::string> remote_users_;

    enum SessionState { LOBBY = 1, CLIENT = 2, HOST = 3 };
    SessionState state_;

    uint32_t width_, height_;
    
    renderer::GlWindow window_;
    decoder::H264Decoder decoder_;
    decoder::H264Encoder encoder_;
    capture::DxgiCapture screen_cap_;
    std::atomic_bool encoder_running_;
    std::atomic_bool renderer_running_;
    std::thread *encoding_thread_;
    std::thread *rendering_thread_;
};

}  // namespace daemon
}  // namespace prototype
