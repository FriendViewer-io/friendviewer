#define DEBUG
#include "prototype/daemon/encoder/h264_encoder.hh"

#include <chrono>
#include <iostream>
#include <vector>

namespace enc = prototype::daemon::encoder;

int main() {
    // Expecting to immediately get back some packets
    enc::H264Encoder encoder;
    if (!encoder.init(enc::H264Encoder::H264Params{1920, 1080, 1000000, 30, 1})) {
        return 1;
    }

    std::vector<uint8_t> sample_data;
    sample_data.resize(1920 * 1080 * 4);
    std::fill(sample_data.begin(), sample_data.end(), 255);
    std::vector<uint8_t> packet, pps_sps;

    std::cout << "30 frame encode results:" << std::endl;
    for (int i = 0; i < 30; i++) {
        int ret = encoder.encode_frame(sample_data, packet);
        if (ret) {
            return ret;
        }

        auto start = std::chrono::system_clock::now();
        if (!encoder.get_pps_sps(pps_sps)) {
            return 2;
        }
        auto elapsed = std::chrono::system_clock::now() - start;
        auto time_ms = std::chrono::duration_cast<std::chrono::microseconds>(elapsed);
        std::cout << "Frame " << i << std::endl;
        if (i == 0) {
            std::cout << "PPS & SPS size - " << pps_sps.size() << std::endl;
        }
        std::cout << "Packet size - " << packet.size() << std::endl;
        std::cout << "Encode time (approx.) - " << time_ms.count() << "μs" << std::endl;
        std::cout << "------------------------------------" << std::endl;

        packet.clear();
        std::fill(sample_data.begin(), sample_data.end(), i);
    }
}
