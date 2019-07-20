#include "prototype/daemon/networking/network_manager.hh"

#include <functional>
#include <future>
#include <iostream>
#include <mutex>
#include <thread>
#include <vector>

#include "prototype/protobuf/fv_packet.pb.h"

namespace prototype {
namespace daemon {
namespace networking {

namespace {
using namespace std::chrono_literals;
static constexpr auto process_wait_time = 500ms;
}  // namespace

NetworkManager::NetworkManager()
    : connected_(false),
      task_processor_active_(true),
      task_processor_(&NetworkManager::async_task_processor, this) {}

void NetworkManager::subscribe_to_packet(prototype::protobuf::FvPacketType type, SubCb &&handler) {
    handlers_[type].emplace_front(handler);
}

void NetworkManager::async_task_processor() {
    while (task_processor_active_.load()) {
        {
            std::lock_guard<std::mutex> lock(task_mutex_);
            for (int i = 0; i < nonblocking_tasks_.size(); i++) {
                if (nonblocking_tasks_[i].wait_for(std::chrono::seconds(0)) ==
                    std::future_status::ready) {
                    nonblocking_tasks_.erase(nonblocking_tasks_.begin() + i);
                    i--;
                }
            }
        }
        std::this_thread::sleep_for(process_wait_time);
    }
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
            auto future = std::async(std::launch::async, [cb, cb_pkt = std::move(packet), this] {
                cb(this, cb_pkt.inner_packet().data(), cb_pkt.inner_packet().length());
            });
            if (block_completion) {
                tasks.emplace_front(std::move(future));
            } else {
                std::lock_guard<std::mutex> lock(task_mutex_);
                nonblocking_tasks_.emplace_back(std::move(future));
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
