#pragma once

#include <mutex>
#include <queue>
#include <vector>

namespace prototype {
namespace daemon {

// Threadsafe buffer for chunks of data
class ChunkBuffer {
 public:
    using chunk_type = std::vector<uint8_t>;

    // Pop a single chunk of data out into chunk_out
    bool dequeue_chunk(chunk_type &chunk_out);
    // Relase a chunk of data to the buffer
    void enqueue_chunk(chunk_type &&chunk_in);

 private:
    std::mutex queue_mutex_;
    std::queue<chunk_type> queue_;
};

}  // namespace daemon
}  // namespace prototype
