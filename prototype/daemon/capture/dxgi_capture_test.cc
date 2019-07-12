#include "dxgi_capture.hh"

#include <iostream>

namespace dc = prototype::daemon::capture;

int main() {
    dc::DxgiCapture desktop_capture;
    if (!desktop_capture.init(1920, 1080, 15)) {
        std::cout << "Failed to init dxgi capture";
        return 1;
    }

    std::vector<uint8_t> test_out;
    if (!desktop_capture.get_frame(test_out)) {
        std::cout << "Failed to get frame";
        return 2;
    }
    std::cout << "Successfully captured frame";
    return 0;
}
