#pragma once

#include <array>
#include <forward_list>
#include <functional>
#include <future>
#include <mutex>
#include <thread>
#include <utility>

#include "prototype/daemon/networking/client_socket.hh"
#include "prototype/network_protocol/message_manager.hh"
#include "prototype/protobuf/fv_packet.pb.h"

namespace prototype {
namespace daemon {
namespace networking {

class NetworkManager {
 public:
    using SubCb = std::function<void(NetworkManager *, const void *, uint32_t)>;
    NetworkManager();
    // Forwards to ClientSocket::connect
    bool connect(const char *address, uint16_t port) {
        return connected_ = socket_.connect(address, port);
    }

    void subscribe_to_packet(prototype::protobuf::FvPacketType type, SubCb &&handler, bool task);
    // Ditto from above, but invokes callbacks asynchronously
    void poll_network(bool block_completion);

    void send_message(const std::string &message) { socket_.send_data(message); }

    const ClientSocket &get_socket() const { return socket_; }

    bool connected() const { return connected_; }

 private:
    void async_task_processor();

    ClientSocket socket_;
    prototype::network_protocol::MessageManager decode_mgr_;
    std::array<std::forward_list<std::pair<bool, SubCb>>, prototype::protobuf::FvPacketType_MAX + 1>
        handlers_;
    bool connected_;

    std::mutex task_mutex_;
    // ORDERING IMPORTANT
    std::atomic_bool task_processor_active_;
    std::vector<std::future<void>> nonblocking_tasks_;
    std::thread task_processor_;
};

}  // namespace networking
}  // namespace daemon
}  // namespace prototype
