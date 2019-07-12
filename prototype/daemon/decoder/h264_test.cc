#define DEBUG
#include <chrono>
#include <fstream>
#include <iostream>
#include <vector>

#include "prototype/daemon/capture/dxgi_capture.hh"
#include "prototype/daemon/decoder/h264_decoder.hh"
#include "prototype/daemon/decoder/h264_encoder.hh"
#include "prototype/network_protocol/message_manager.hh"

namespace enc = prototype::daemon::decoder;
namespace cap = prototype::daemon::capture;
namespace net = prototype::network_protocol;

int capture_and_encode() {
    cap::DxgiCapture screen_cap;
    screen_cap.init(1920, 1080, 14);
    enc::H264Encoder encoder;
    if (!encoder.init(enc::H264Encoder::H264Params{1920, 1080, 1000000, 30, 1})) {
        return 1;
    }

    std::vector<uint8_t> sample_data;
    sample_data.resize(1920 * 1080 * 4);
    std::vector<uint8_t> packet, pps_sps;
    std::ofstream video_file("video.h264", std::ios::binary);
    int len;
    for (int i = 0; i < 30; i++) {
        screen_cap.get_frame(sample_data);
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
            len = pps_sps.size();
            video_file.write((char *)&len, 4);
            video_file.write((char *)pps_sps.data(), pps_sps.size());
        }
        std::cout << "Packet size - " << packet.size() << std::endl;
        std::cout << "Encode time (approx.) - " << time_ms.count() << "μs" << std::endl;
        std::cout << "------------------------------------" << std::endl;
        len = packet.size();
        video_file.write((char *)&len, 4);
        video_file.write((char *)packet.data(), packet.size());
        packet.clear();
        std::fill(sample_data.begin(), sample_data.end(), i);
    }
    video_file.flush();
    return 0;
}

bool attempt_decode() {
    enc::H264Decoder decoder;
    if (!decoder.init(enc::H264Decoder::H264Params{1920, 1080, 100000, 30, 1})) {
        return false;
    }
    net::MessageManager mgr;

    std::basic_ifstream<uint8_t> video_file("video.h264", std::ios::binary);
    std::vector<uint8_t> video_data = std::vector<uint8_t>(
        std::istreambuf_iterator<uint8_t>(video_file), std::istreambuf_iterator<uint8_t>());

    mgr.parse_blob(video_data);
    std::vector<uint8_t> frame;
    int packet_idx = 0;
    bool decoded_succesfully = false;
    while (mgr.retrieve_message(video_data)) {
        // PPS/SPS first
        if (packet_idx == 0) {
            decoder.decode_packet(video_data, frame);
        } else if (packet_idx > 9) {
            decoded_succesfully = (decoder.decode_packet(video_data, frame) ==
                                   enc::H264Decoder::DecodeStatus::kSuccess);
        }
        packet_idx++;
    }
    return decoded_succesfully;
}

int main() {
    int ret_code;
    std::cout << "Encode attempt return code: " << (ret_code = capture_and_encode()) << std::endl;
    if (ret_code) {
        return ret_code;
    }
    std::cout << "Decode attempt result: " << (attempt_decode() ? "Success" : "Failure")
              << std::endl;
    return 0;
}
