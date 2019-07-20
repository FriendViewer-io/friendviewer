#pragma once

#include <memory>
#include <string>
#include <vector>

extern "C" {
#include "third_party/libav/libavcodec/avcodec.h"
#include "third_party/libav/libswscale/swscale.h"
}

namespace prototype {
namespace daemon {
namespace decoder {

class H264Decoder {
 public:
    enum DecodeStatus {
        kSuccess = 1,
        kSendFail = 2,
        kAgain = 3,
        kEof = 4,
        kOther = 5,
    };

    // DUPLICATE STRUCT
    struct H264Params {
        int width;
        int height;
        int64_t bit_rate;
        int framerate_num;
        int framerate_den;
    };

    H264Decoder();

    bool init(const H264Params &params);

    DecodeStatus decode_packet(const std::vector<uint8_t> &packet_in,
                               std::vector<uint8_t> &frame_out);
    DecodeStatus decode_packet(const std::string &packet_in, std::vector<uint8_t> &frame_out);
    // Gets the apparent PTS of the last received frame
    int32_t get_pts() const;
    void set_pts(int32_t pts) { pts_ = pts; }

    void shutdown();

 private:
    DecodeStatus decode_packet_internal(uint8_t *data, int len, std::vector<uint8_t> &frame_out);
    using libav_deleter = void (*)(void *);

    std::unique_ptr<AVCodecContext, libav_deleter> decoder_context_;
    std::unique_ptr<SwsContext, libav_deleter> sws_context_;
    std::unique_ptr<AVFrame, libav_deleter> frame_buffer_;
    std::unique_ptr<AVPacket, libav_deleter> packet_buffer_;

    int32_t pts_ = 0;
    int32_t width_, height_;
};

}  // namespace decoder
}  // namespace daemon
}  // namespace prototype
