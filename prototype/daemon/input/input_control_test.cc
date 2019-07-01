#include "prototype/daemon/input/input_control.hh"

#include <windows.h>

#include <iostream>

namespace in = prototype::daemon::input;

int main() {
    std::cout << "Moving to the center of the screen" << std::endl;
    in::set_cursor_location(0.5f, 0.5f);
    Sleep(500);
    std::cout << "Left-clicking" << std::endl;
    in::send_virtual_input(VK_LBUTTON);
    Sleep(500);
    std::cout << "Typing a word" << std::endl;
    in::send_virtual_input(0x48);
    Sleep(100);
    in::send_virtual_input(0x45);
    Sleep(100);
    in::send_virtual_input(0x4C);
    Sleep(100);
    in::send_virtual_input(0x4C);
    Sleep(100);
    in::send_virtual_input(0x4F);
}
