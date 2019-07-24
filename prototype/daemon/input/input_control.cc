#include <Windows.h>

#include <algorithm>
#include <future>
#include <tuple>

namespace prototype {
namespace daemon {
namespace input {

namespace {
std::pair<int, int> get_primary_monitor_dimensions() {
    HMONITOR primary_monitor = MonitorFromWindow(GetDesktopWindow(), MONITOR_DEFAULTTOPRIMARY);
    MONITORINFO info;
    info.cbSize = sizeof(MONITORINFO);

    GetMonitorInfo(primary_monitor, &info);

    return std::make_pair(static_cast<int>(info.rcMonitor.right - info.rcMonitor.left),
                          static_cast<int>(info.rcMonitor.bottom - info.rcMonitor.top));
}
void send_mouse_input(int virtual_key, bool down) {
    INPUT input;
    input.type = INPUT_MOUSE;
    switch (virtual_key) {
        case VK_LBUTTON:
            input.mi.dwFlags |= MOUSEEVENTF_LEFTDOWN;
            break;
        case VK_RBUTTON:
            input.mi.dwFlags |= MOUSEEVENTF_RIGHTDOWN;
            break;
        case VK_MBUTTON:
            input.mi.dwFlags |= MOUSEEVENTF_MIDDLEDOWN;
            break;
    }
    SendInput(1, &input, sizeof(INPUT));
}

// Faking a mouse release will require a short wait-time between the press & release
constexpr DWORD kFakeReleaseWaitTime = 16;
}  // namespace

void set_cursor_location(float x, float y) {
    // Clamp for safety
    x = std::max(0.f, std::min(1.f, x));
    y = std::max(0.f, std::min(1.f, y));

    int width, height;
    std::tie(width, height) = get_primary_monitor_dimensions();

    SetCursorPos(static_cast<int>(x * width), static_cast<int>(y * height));
}

void send_virtual_input(int virtual_key) {
    INPUT input = {};
    if (virtual_key >= VK_LBUTTON && virtual_key <= VK_XBUTTON2) {
        // Schedule this to happen later, time accuracy really doesn't matter here
        std::async([virtual_key]() {
            // !!HACK!! No *_up inputs sent, so mimic what likely happens
            send_mouse_input(virtual_key, true);
            Sleep(kFakeReleaseWaitTime);
            send_mouse_input(virtual_key, false);
        });
    } else {
        input.type = INPUT_KEYBOARD;
        input.ki.wVk = static_cast<WORD>(virtual_key);
        SendInput(1, &input, sizeof(INPUT));
    }
}

}  // namespace input
}  // namespace daemon
}  // namespace prototype
