#include "prototype/daemon/renderer/gl_window.hh"

#include <chrono>
#include <iostream>
#include <thread>

int main() {
    prototype::daemon::renderer::GlWindow window;
    window.create_window(1280, 720);
    std::vector<uint8_t> colors;
    colors.resize(720 * 1280 * 3);
    uint8_t val = 0;
    while (val < 255) {
        auto pre = std::chrono::system_clock::now();
        std::fill(colors.begin(), colors.end(), val++);
        window.render_frame(colors);
        auto elap = std::chrono::duration_cast<std::chrono::milliseconds>(
                        std::chrono::system_clock::now() - pre)
                        .count();
        std::cout << "Complete in " << elap << "ms" << std::endl;
    }
    window.close_window();
}
