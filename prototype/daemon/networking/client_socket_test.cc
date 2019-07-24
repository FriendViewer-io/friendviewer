#include "prototype/daemon/networking/client_socket.hh"

#include <WinSock2.h>

#include <atomic>
#include <iostream>
#include <thread>

#include "prototype/network_protocol/message_manager.hh"

namespace {
namespace cn = prototype::daemon::networking;
std::atomic<bool> exit_signal(false);

void server_fn() {
    SOCKET s = socket(AF_INET, SOCK_STREAM, IPPROTO_TCP);
    sockaddr_in sock_addr = {};
    sock_addr.sin_port = htons(61001);
    sock_addr.sin_family = AF_INET;
    sock_addr.sin_addr.S_un.S_addr = INADDR_ANY;

    if (SOCKET_ERROR == bind(s, reinterpret_cast<sockaddr *>(&sock_addr), sizeof(sockaddr_in))) {
        std::cout << "[Server] Bind failure";
        exit(1);
    }
    if (SOCKET_ERROR == listen(s, 1)) {
        std::cout << "[Server] Listen failure" << std::endl;
        exit(2);
    }
    std::cout << "[Server] Awaiting connection..." << std::endl;
    SOCKET temp = SOCKET_ERROR;
    while (temp == SOCKET_ERROR) {
        temp = accept(s, nullptr, nullptr);
    }
    s = temp;

    std::cout << "[Server] Connection accepted" << std::endl;

    u_long nonblocking_mode = 1;
    ioctlsocket(s, FIONBIO, &nonblocking_mode);
    prototype::network_protocol::MessageManager mgr;

    uint8_t buf[1024];
    std::vector<uint8_t> message;
    while (!exit_signal.load()) {
        int num = recv(s, reinterpret_cast<char *>(buf), 1024, 0);
        if (num == SOCKET_ERROR) {
            if (WSAGetLastError() != WSAEWOULDBLOCK) {
                break;
            }
        } else {
            std::cout << "[Server] recv length: " << num << std::endl;
            mgr.parse_blob(buf, num);
            while (mgr.retrieve_message(message)) {
                std::string str_msg(message.begin(), message.end());
                std::cout << "[Server] Received message of length " << message.size() << " : "
                          << str_msg << std::endl;
            }
        }
    }
}
}  // namespace

int main() {
    cn::ClientSocket s;
    std::thread server_thread(server_fn);

    if (!s.connect("127.0.0.1", 61001)) {
        std::cout << "[Client] Failed to connect!" << std::endl;
        return 1;
    }

    for (int i = 0; i < 10; i++) {
        s.send_data("Message number " + std::to_string(i));
        if (i <= 5) {
            Sleep(100);
        }
    }

    Sleep(500);
    exit_signal = true;
}
