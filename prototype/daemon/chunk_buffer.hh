#pragma once

#include <mutex>
#include <queue>
#include <vector>

namespace prototype {
namespace daemon {

// Threadsafe buffer for chunks of data
template <typename T>
class ChunkBuffer {
 public:
    // Pop a single chunk of data out into chunk_out
    bool dequeue_chunk(T &chunk_out);
    // Relase a chunk of data to the buffer
    void enqueue_chunk(T &&chunk_in);
    void clear();

 private:
    std::mutex queue_mutex_;
    std::queue<T> queue_;
};

template <typename T>
bool ChunkBuffer<T>::dequeue_chunk(T &chunk_out) {
    std::lock_guard<std::mutex> lock(queue_mutex_);
    if (queue_.size() == 0) {
        return false;
    }

    chunk_out = std::move(queue_.front());
    queue_.pop();
    return true;
}

template <typename T>
void ChunkBuffer<T>::enqueue_chunk(T &&chunk_in) {
    std::lock_guard<std::mutex> lock(queue_mutex_);
    queue_.emplace(std::move(chunk_in));
}

template <typename T>
void ChunkBuffer<T>::clear() {
    std::lock_guard<std::mutex> lock(queue_mutex_);
    queue_ = std::queue<T>();
}

}  // namespace daemon
}  // namespace prototype
