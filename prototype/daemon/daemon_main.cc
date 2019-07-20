#include <chrono>

#include "prototype/daemon/daemon_backend_controller.hh"

using namespace std::chrono_literals;

int main() {
    prototype::daemon::DaemonBackendController dbc;
    dbc.init(1920, 1080);
    while (dbc.is_running()) {
        dbc.run_frame();
        std::this_thread::sleep_for(1ms);
    }
}
