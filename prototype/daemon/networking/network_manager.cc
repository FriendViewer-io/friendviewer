#include "prototype/daemon/networking/network_manager.hh"

#include <functional>
#include <future>
#include <iostream>
#include <vector>

#include "prototype/protobuf/fv_packet.pb.h"

namespace prototype {
namespace daemon {
namespace networking {

NetworkManager::NetworkManager() {}

void NetworkManager::subscribe_to_packet(prototype::protobuf::FvPacketType type, SubCb &&handler) {
    handlers_[type].emplace_front(handler);
}

void NetworkManager::poll_network() {
    if (!connected_) {
        return;
    }
    std::vector<uint8_t> buffer;
    auto status = socket_.receive_data(buffer);
    if (status == ClientSocket::ReceiveStatus::kContinue) {
        return;
    }
    if (status == ClientSocket::ReceiveStatus::kFailure) {
        std::cerr << "ClientSocket failed to receive data" << std::endl;
        return;
    }
    decode_mgr_.parse_blob(buffer);

    while (decode_mgr_.retrieve_message(buffer)) {
        prototype::protobuf::FvPacket packet;
        packet.ParseFromArray(buffer.data(), buffer.size());
        for (auto &&cb : handlers_[packet.type()]) {
            cb(this, packet.inner_packet().data(), packet.inner_packet().length());
        }
    }
}

void NetworkManager::poll_network_async(bool block_completion) {
    if (!connected_) {
        return;
    }
    std::vector<uint8_t> buffer;
    auto status = socket_.receive_data(buffer);
    if (status == ClientSocket::ReceiveStatus::kContinue) {
        return;
    }
    if (status == ClientSocket::ReceiveStatus::kFailure) {
        std::cerr << "ClientSocket failed to receive data" << std::endl;
        return;
    }
    decode_mgr_.parse_blob(buffer);

    std::forward_list<std::future<void>> tasks;

    prototype::protobuf::FvPacket packet;
    while (decode_mgr_.retrieve_message(buffer)) {
        packet.ParseFromArray(buffer.data(), buffer.size());
        for (auto &&cb : handlers_[packet.type()]) {
            auto future = std::async(std::launch::async, [cb, &packet, this] {
                cb(this, packet.inner_packet().data(), packet.inner_packet().length());
            });
            if (block_completion) {
                tasks.emplace_front(std::move(future));
            }
        }
    }
    for (auto &&future : tasks) {
        future.wait();
    }
}

}  // namespace networking
}  // namespace daemon
}  // namespace prototype
