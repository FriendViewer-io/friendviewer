#include "prototype/daemon/chunk_buffer.hh"

#include <mutex>
#include <queue>
#include <vector>

namespace prototype {
namespace daemon {

void ChunkBuffer::dequeue_chunk(chunk_type &chunk_out) {
    std::lock_guard<std::mutex> lock(queue_mutex_);
    if (queue_.size() == 0) {
        return false;
    }

    queue_.front().swap(chunk_out);
    queue_.pop();
    return true;
}

void ChunkBuffer::enqueue_chunk(chunk_type &&chunk_in) {
    std::lock_guard<std::mutex> lock(queue_mutex_);
    queue_.emplace(chunk_in);
}

}  // namespace daemon
}  // namespace prototype
