#pragma once

#include <array>
#include <forward_list>
#include <functional>

#include "prototype/daemon/networking/client_socket.hh"
#include "prototype/network_protocol/message_manager.hh"
#include "prototype/protobuf/fv_packet.pb.h"

namespace prototype {
namespace daemon {
namespace networking {

class NetworkManager {
 public:
    using SubCb = std::function<void(NetworkManager *, const void *)>;
    NetworkManager();
    // Forwards to ClientSocket::connect
    bool connect(const char *address, uint16_t port) {
        return connected_ = socket_.connect(address, port);
    }

    void subscribe_to_packet(prototype::protobuf::FvPacketType type, SubCb &&handler);
    // Polls socket for all available data and invokes corresponding callbacks for all
    // received packets
    void poll_network();
    // Ditto from above, but invokes callbacks asynchronously
    void poll_network_async(bool block_completion);

    void send_message(const std::string &message) { socket_.send_data(message); }

    const ClientSocket &get_socket() const { return socket_; }

 private:
    ClientSocket socket_;
    prototype::network_protocol::MessageManager decode_mgr_;
    std::array<std::forward_list<SubCb>, prototype::protobuf::FvPacketType_MAX> handlers_;
    bool connected_;
};

}  // namespace networking
}  // namespace daemon
}  // namespace prototype
