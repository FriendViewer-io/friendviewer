#pragma once

#include <cstdint>
#include <forward_list>
#include <vector>

namespace prototype {
namespace network_protocol {

// SUMMARY: Transforms incoming bytestream into well-defined chunks based on
// the "message manager" protocol. It is defined as follows:
// For any reliable, orderly connection, data will be sent in two modes. One, a
// size descriptor, will be four bytes defining the length of the next incoming
// packet. The second mode will read out K bytes (value K defined by mode 1)
// into a buffer. The data read out will be a single, well defined message.
// Initial mode is 1.
class MessageManager {
 public:
    MessageManager();
    // Input will be a stream of bytes.
    // For each call to parse_blob, the parsed messages are saved internally.
    void parse_blob(const uint8_t *blob, size_t len);
    void parse_blob(const std::vector<uint8_t> &blob);

    // Returns the length of the next immediately retrievable message, or -1
    // if there are no available messages left.
    int32_t next_message_length() const;

    // Outputs the next available message to message_out, returns true if more
    // messages are available to be retrieved immediately, otherwise returns
    // false.
    // Each successful call clears out the retrieved message from the internal buffer.
    bool retrieve_message(std::vector<uint8_t> &message_out);

 private:
    std::forward_list<std::vector<uint8_t>> message_buffers_;
    bool reading_length_;
    int32_t next_message_;
    std::vector<uint8_t> partial_message_buffer_;
};

}  // namespace network_protocol
}  // namespace prototype
