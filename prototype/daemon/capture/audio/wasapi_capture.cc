#include "prototype/daemon/capture/audio/wasapi_capture.hh"

#include <Audioclient.h>
#include <mmdeviceapi.h>

#include <cstdio>
#pragma warning(disable : 4996)

namespace prototype {
namespace daemon {
namespace capture {

WasapiCapture::WasapiCapture() {}

bool WasapiCaputre::init() {
    receive_signal_ = CreateEvent(nullptr, false, false, nullptr);
    stop_signal_ = CreateEvent(nullptr, false, false, nullptr);

    CoInitialize(nullptr);
    IMMDeviceEnumerator *enumerator = nullptr;
    CoCreateInstance(__uuidof(MMDeviceEnumerator), nullptr, CLSCTX_ALL,
                     __uuidof(IMMDeviceEnumerator), (void **)&enumerator);

    enumerator->GetDefaultAudioEndpoint(eRender, eConsole, &device);

    WAVEFORMATEX *wfex;
    device->Activate(__uuidof(IAudioClient), CLSCTX_ALL, nullptr, (void **)&client);

    client->GetMixFormat(&wfex);
    sps = wfex->nSamplesPerSec;
    n_ch = wfex->nChannels;
    block_align = wfex->nBlockAlign;
    WAVEFORMATEXTENSIBLE *wfext = (WAVEFORMATEXTENSIBLE *)wfex;
    s_flags = wfext->dwChannelMask;

    uint32_t flags = AUDCLNT_STREAMFLAGS_EVENTCALLBACK | AUDCLNT_STREAMFLAGS_LOOPBACK;

    client->Initialize(AUDCLNT_SHAREMODE_SHARED, flags, 83333, 0, wfex, nullptr);

    uint32_t frames;

    client->GetBufferSize(&frames);

    init_render();

    client->GetService(__uuidof(IAudioCaptureClient), (void **)&capture);

    client->SetEventHandle(receive_signal);

    CreateThread(nullptr, 0, WASAPICapture::capture_thread, this, 0, nullptr);

    client->Start();
}

void WasapiCapture::init_render() {
    LPBYTE buffer;
    uint32_t frames;
    IAudioClient *client_tmp;
    WAVEFORMATEX *wfex;
    device->Activate(__uuidof(IAudioClient), CLSCTX_ALL, nullptr, (void **)&client_tmp);

    client_tmp->GetMixFormat(&wfex);

    client_tmp->Initialize(AUDCLNT_SHAREMODE_SHARED, 0, 83333, 0, wfex, nullptr);

    client_tmp->GetBufferSize(&frames);

    client_tmp->GetService(__uuidof(IAudioRenderClient), (void **)&render);

    render->GetBuffer(frames, &buffer);

    memset(buffer, 0, frames * wfex->nBlockAlign);
    render->ReleaseBuffer(frames, 0);

    client_tmp->Start();
}

uint32_t WasapiCapture::sample_rate() const { return sps; }

uint32_t WasapiCapture::num_channels() const { return n_ch; }

void WasapiCapture::stop() { SetEvent(stop_signal); }

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
        auto ret = capture->GetNextPacketSize(&capture_size);

        if (!capture_size) {
            break;
        }
        // printf("capture size: %d\n", capture_size);

        capture->GetBuffer(&buffer, &frames, &flags, &pos, &ts);

        if (copying) {
            std::lock_guard<std::mutex> lock(buf_mutex);
            sound_buffer.enqueue(buffer, frames * block_align);
        }

        capture->ReleaseBuffer(frames);
    }
}

DWORD WasapiCapture::capture_thread(LPVOID param) {
    WasapiCapture *inst = (WasapiCapture *)param;
    HANDLE signals[] = {inst->receive_signal, inst->stop_signal};

    while (wait_for_capture(signals)) {
        inst->process_data();
    }

    return S_OK;
}

uint32_t WasapiCapture::copy_buffers(uint8_t *data_out, uint32_t desired) {
    if (data_out == nullptr) {
        sound_buffer.flush();
    }

    {
        std::lock_guard<std::mutex> lock(buf_mutex);
        sound_buffer.dequeue(data_out, desired);
    }

    return desired;
}

uint32_t WasapiCapture::stereo_flags() const { return s_flags; }

}  // namespace capture
}  // namespace daemon
}  // namespace prototype
