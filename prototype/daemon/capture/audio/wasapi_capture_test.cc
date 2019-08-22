#include "prototype/daemon/capture/audio/wasapi_capture.hh"

#include <iostream>

int main() {
    prototype::daemon::capture::WasapiCapture capture;
    capture.init();
    capture.toggle_copy();

    while (true) {
        std::vector<uint8_t> buffer;
        if (capture.copy_buffers(buffer)) {
            std::cout << "Captured audio size: " << buffer.size() << std::endl;
        } else {
            std::cout << "no audio\n";
        }
    }
}