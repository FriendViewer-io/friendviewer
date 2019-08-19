#pragma once

#include <Audioclient.h>
#include <Windows.h>
#include <mmdeviceapi.h>
#include <stdint.h>

#include <atomic>
#include <cstdio>
#include <mutex>
#include <queue>

#include "prototype/daemon/chunk_buffer.hh"

namespace prototype {
namespace daemon {
namespace capture {

class WasapiCapture {
 public:
    WASAPICapture();
    bool init();

    uint32_t sample_rate() const { return samples_per_sec_; }
    uint32_t num_channels() const { return num_channels_; }
    uint32_t stereo_flags() const { return stereo_flags_; }
    uint32_t sample_size() const { return sample_size_; }

    void toggle_copy() { copying_ ^= 1; }

    void stop();

    uint32_t copy_buffers(uint8_t *data_out, uint32_t desired);
    uint32_t buffer_size() const { return sound_buffer.usage; }

 private:
    IMMDevice *device_;
    IAudioClient *client_;
    IAudioCaptureClient *capture_;
    // Feed in empty sound during no capture
    IAudioRenderClient *render_;

    uint32_t samples_per_sec_;
    uint32_t num_channels_;
    uint32_t sample_size_;
    uint32_t stereo_flags_;

    HANDLE receive_signal_;
    HANDLE stop_signal_;

    ChunkBuffer sound_buffer_;
    std::atomic_char copying_;
    static DWORD WINAPI capture_thread(LPVOID param);

    void process_data();

    void init_render();
};

}  // namespace capture
}  // namespace daemon
}  // namespace prototype
