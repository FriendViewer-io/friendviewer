#include "prototype/daemon/capture/audio/wasapi_capture.hh"

#include <Audioclient.h>
#include <mmdeviceapi.h>

#include <cstdio>
#pragma warning(disable : 4996)

namespace prototype {
namespace daemon {
namespace capture {

WasapiCapture::WasapiCapture()
    : device_(nullptr),
      client_(nullptr),
      capture_(nullptr),
      render_(nullptr),
      samples_per_sec_(0),
      num_channels_(0),
      stereo_flags_(0),
      receive_signal_(INVALID_HANDLE_VALUE),
      stop_signal_(INVALID_HANDLE_VALUE),
      copying_(0) {}

bool WasapiCapture::init() {
    receive_signal_ = CreateEvent(nullptr, false, false, nullptr);
    stop_signal_ = CreateEvent(nullptr, false, false, nullptr);

    if (FAILED(CoInitialize(nullptr))) {
        return false;
    }

    IMMDeviceEnumerator *enumerator = nullptr;
    if (FAILED(CoCreateInstance(__uuidof(MMDeviceEnumerator), nullptr, CLSCTX_ALL,
                                __uuidof(IMMDeviceEnumerator),
                                reinterpret_cast<void **>(&enumerator)))) {
        return false;
    }

    if (FAILED(enumerator->GetDefaultAudioEndpoint(eRender, eConsole, &device_))) {
        return false;
    }

    WAVEFORMATEX *wfex;
    if (FAILED(device_->Activate(__uuidof(IAudioClient), CLSCTX_ALL, nullptr,
                                 reinterpret_cast<void **>(&client_)))) {
        return false;
    }

    if (FAILED(client_->GetMixFormat(&wfex))) {
        return false;
    }

    samples_per_sec_ = wfex->nSamplesPerSec;
    num_channels_ = wfex->nChannels;
    sample_size_ = wfex->nBlockAlign;
    WAVEFORMATEXTENSIBLE *wfext = reinterpret_cast<WAVEFORMATEXTENSIBLE *>(wfex);
    stereo_flags_ = wfext->dwChannelMask;

    uint32_t flags = AUDCLNT_STREAMFLAGS_EVENTCALLBACK | AUDCLNT_STREAMFLAGS_LOOPBACK;

    if (FAILED(client_->Initialize(AUDCLNT_SHAREMODE_SHARED, flags, 83333, 0, wfex, nullptr))) {
        return false;
    }
    uint32_t frames;

    if (FAILED(client_->GetBufferSize(&frames))) {
        return false;
    }

    if (!init_render()) {
        return false;
    }

    if (FAILED(client_->GetService(__uuidof(IAudioCaptureClient),
                                   reinterpret_cast<void **>(&capture_)))) {
        return false;
    }
    if (FAILED(client_->SetEventHandle(receive_signal_))) {
        return false;
    }
    CreateThread(nullptr, 0, WasapiCapture::capture_thread, this, 0, nullptr);
    client_->Start();

    return true;
}

bool WasapiCapture::init_render() {
    LPBYTE buffer;
    uint32_t frames;
    IAudioClient *client_tmp;
    WAVEFORMATEX *wfex;
    if (FAILED(device_->Activate(__uuidof(IAudioClient), CLSCTX_ALL, nullptr,
                                 reinterpret_cast<void **>(&client_tmp)))) {
        return false;
    }

    if (FAILED(client_tmp->GetMixFormat(&wfex))) {
        return false;
    }

    if (FAILED(client_tmp->Initialize(AUDCLNT_SHAREMODE_SHARED, 0, 83333, 0, wfex, nullptr))) {
        return false;
    }

    if (FAILED(client_tmp->GetBufferSize(&frames))) {
        return false;
    }

    if (FAILED(client_tmp->GetService(__uuidof(IAudioRenderClient),
                                      reinterpret_cast<void **>(&render_)))) {
        return false;
    }

    if (FAILED(render_->GetBuffer(frames, &buffer))) {
        return false;
    }

    memset(buffer, 0, frames * wfex->nBlockAlign);
    if (FAILED(render_->ReleaseBuffer(frames, 0))) {
        return false;
    }

    return SUCCEEDED(client_tmp->Start());
}

void WasapiCapture::stop() { SetEvent(stop_signal_); }

bool wait_for_capture(HANDLE *signals) {
    auto ret = WaitForMultipleObjects(2, signals, false, 10);

    return ret == WAIT_OBJECT_0 || ret == WAIT_TIMEOUT;
}

void WasapiCapture::process_data() {
    uint32_t capture_size = 0;
    LPBYTE buffer;
    uint32_t frames;
    DWORD flags;
    uint64_t pos, ts;

    while (true) {
        auto ret = capture_->GetNextPacketSize(&capture_size);

        if (!capture_size) {
            break;
        }

        capture_->GetBuffer(&buffer, &frames, &flags, &pos, &ts);

        if (copying_.load()) {
            std::vector<uint8_t> data(buffer, buffer + (frames * sample_size_));
            sound_buffer_.enqueue_chunk(std::move(data));
        }

        capture_->ReleaseBuffer(frames);
    }
}

DWORD WasapiCapture::capture_thread(LPVOID param) {
    WasapiCapture *inst = reinterpret_cast<WasapiCapture *>(param);
    HANDLE signals[] = {inst->receive_signal_, inst->stop_signal_};

    while (wait_for_capture(signals)) {
        inst->process_data();
    }

    return S_OK;
}

bool WasapiCapture::copy_buffers(std::vector<uint8_t> &data_out) {
    return sound_buffer_.dequeue_chunk(data_out);
}

}  // namespace capture
}  // namespace daemon
}  // namespace prototype
