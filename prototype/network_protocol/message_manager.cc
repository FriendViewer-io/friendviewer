#include "prototype/network_protocol/message_manager.hh"

#include <algorithm>
#include <iostream>

namespace prototype {
namespace network_protocol {

MessageManager::MessageManager() : reading_length_(true), next_message_(0) {}

void MessageManager::parse_blob(const uint8_t *blob, size_t len) {
    size_t remaining_blob = len;

    while (remaining_blob > 0) {
        const uint8_t *blob_start = blob + (len - remaining_blob);
        if (reading_length_) {
            const size_t remaining_len = 4 - partial_message_buffer_.size();
            partial_message_buffer_.insert(partial_message_buffer_.end(), blob_start,
                                           blob_start + std::min(remaining_len, remaining_blob));
            remaining_blob -= std::min(remaining_len, remaining_blob);

            if (partial_message_buffer_.size() == 4) {
                reading_length_ = false;
                next_message_ = *reinterpret_cast<const int32_t *>(partial_message_buffer_.data());
                // Safeguard against zero-length messages
                if (next_message_ == 0) {
                    message_buffers_.emplace();
                    reading_length_ = true;
                }

                partial_message_buffer_.clear();
            }
        } else {
            const size_t remaining_len =
                static_cast<size_t>(next_message_) - partial_message_buffer_.size();
            partial_message_buffer_.insert(partial_message_buffer_.end(), blob_start,
                                           blob_start + std::min(remaining_len, remaining_blob));
            remaining_blob -= std::min(remaining_len, remaining_blob);

            if (partial_message_buffer_.size() == static_cast<size_t>(next_message_)) {
                reading_length_ = true;
                next_message_ = -1;
                message_buffers_.emplace(std::move(partial_message_buffer_));
                partial_message_buffer_.clear();
            }
        }
    }
}

void MessageManager::parse_blob(const std::vector<uint8_t> &blob) {
    parse_blob(blob.data(), blob.size());
}

int32_t MessageManager::next_message_length() const {
    return message_buffers_.size() == 0 ? -1
                                        : static_cast<int32_t>(message_buffers_.front().size());
}

bool MessageManager::retrieve_message(std::vector<uint8_t> &message_out) {
    if (next_message_length() == -1) {
        return false;
    }
    message_out.swap(message_buffers_.front());
    message_buffers_.pop();
    return true;
}

}  // namespace network_protocol
}  // namespace prototype
