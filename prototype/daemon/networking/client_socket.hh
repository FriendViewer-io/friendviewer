#pragma once

#include <string>
#include <vector>

namespace prototype {
namespace daemon {
namespace networking {

// Sends data under the message manager protocol
// Receives raw data
// send and receive calls are non-blocking, connect is blocking
class ClientSocket {
 public:
    enum ReceiveStatus {
        kSuccess = 1,
        kFailure = 2,
        // nonblocking mode no data on receive
        kContinue = 3,
    };

    ClientSocket();
    bool connect(const char *address, uint16_t port);
    bool send_data(const std::vector<uint8_t> &data);
    bool send_data(const std::string &data);
    ReceiveStatus receive_data(std::vector<uint8_t> &data_out);
    void close();

    const std::string &get_remote_address() const { return remote_addr_; }
    uint16_t get_port() const { return port_; }
    ~ClientSocket();

 private:
    static bool winsock_init_;

    uint64_t socket_;
    std::string remote_addr_;
    uint16_t port_;
};

}  // namespace networking
}  // namespace daemon
}  // namespace prototype
