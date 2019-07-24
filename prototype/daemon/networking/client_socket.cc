#include "prototype/daemon/networking/client_socket.hh"

#include <WinSock2.h>

#include <array>
#include <iostream>

namespace prototype {
namespace daemon {
namespace networking {

bool ClientSocket::winsock_init_ = false;

ClientSocket::ClientSocket() : socket_(INVALID_SOCKET), remote_addr_(""), port_(0) {
    if (!winsock_init_) {
        WSADATA wsa_data;
        WSAStartup(MAKEWORD(2, 2), &wsa_data);
        winsock_init_ = true;
    }
}

bool ClientSocket::connect(const char *address, uint16_t port) {
    if (socket_ != INVALID_SOCKET) {
        close();
    }

    socket_ = socket(AF_INET, SOCK_STREAM, IPPROTO_TCP);
    if (socket_ == INVALID_SOCKET) {
        return false;
    }

    port_ = port;
    remote_addr_ = address;

    sockaddr_in sock_addr = {};
    sock_addr.sin_port = htons(port);
    sock_addr.sin_family = AF_INET;
    sock_addr.sin_addr.S_un.S_addr = inet_addr(address);
    if (::connect(socket_, reinterpret_cast<sockaddr *>(&sock_addr), sizeof(sockaddr_in)) ==
        SOCKET_ERROR) {
        close();
        return false;
    }
    u_long nonblocking_mode = 1;
    ioctlsocket(socket_, FIONBIO, &nonblocking_mode);
    return true;
}

bool ClientSocket::send_data(const std::vector<uint8_t> &data) {
    uint32_t len = data.size();
    // These will be nagle'd together for sure
    int r1 = ::send(socket_, reinterpret_cast<const char *>(&len), sizeof(uint32_t), 0);
    int r2 = ::send(socket_, reinterpret_cast<const char *>(data.data()), data.size(), 0);

    return ((r1 != SOCKET_ERROR) && (r2 != SOCKET_ERROR));
}

bool send_block(SOCKET socket, const char *data, int len) {
    int amt_sent, offset = 0;
    do {
        amt_sent = ::send(socket, data + offset, len, 0);
        if (amt_sent == SOCKET_ERROR && WSAGetLastError() != 10035) {
            return false;
        } else if (amt_sent != SOCKET_ERROR) {
            offset += amt_sent;
            len -= amt_sent;
        }
    } while (len > 0);
    return true;
}

// FIXME
bool ClientSocket::send_data(const std::string &data) {
    uint32_t len = data.length();
    bool r1, r2;
    r1 = send_block(socket_, reinterpret_cast<const char *>(&len), sizeof(uint32_t));
    r2 = send_block(socket_, data.data(), data.size());

    return r1 && r2;
}

ClientSocket::ReceiveStatus ClientSocket::receive_data(std::vector<uint8_t> &data_out) {
    std::array<uint8_t, 4096> data;
    data_out.clear();
    bool received = false;
    int status;
    do {
        status = ::recv(socket_, reinterpret_cast<char *>(&data[0]), 4096, 0);
        if (status != SOCKET_ERROR) {
            data_out.insert(data_out.end(), data.begin(), data.begin() + status);
            received = true;
        }
    } while (status != SOCKET_ERROR);
    if (WSAGetLastError() != WSAEWOULDBLOCK) {
        return kFailure;
    }
    if (received) {
        return kSuccess;
    }
    return kContinue;
}

void ClientSocket::close() {
    if (socket_ != INVALID_SOCKET) {
        closesocket(socket_);
    }
    socket_ = INVALID_SOCKET;
    remote_addr_ = "";
    port_ = 0;
}

ClientSocket::~ClientSocket() { close(); }

}  // namespace networking
}  // namespace daemon
}  // namespace prototype
