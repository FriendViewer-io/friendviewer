#pragma once

#include <memory>
#include <vector>

extern "C" {
#include "third_party/libav/libavcodec/avcodec.h"
#include "third_party/libav/libswscale/swscale.h"
}

namespace prototype {
namespace daemon {
namespace encoder {

// Takes in frames of video, transforms into YUV420P and spits back a packet
// Encoder should be configured for zerolatency, so if sending a frame in doesn't
// immediately give a packet back, there's a problem
class H264Encoder {
 public:
    struct H264Params {
        int width;
        int height;
        int64_t bit_rate;
    };

    H264Encoder();

    bool init(const H264Params &params);

    // NOTE: first frame will exclude the pps & sps, retrieve it from get_pps_sps
    int encode_frame(const std::vector<uint8_t> &frame_in, std::vector<uint8_t> &packet_out);
    int32_t get_pts() const;
    bool get_pps_sps(std::vector<uint8_t> &packet_out) const;

 private:
    using libav_deleter = void (*)(void *);

    std::unique_ptr<AVCodecContext, libav_deleter> encoder_context_;
    std::unique_ptr<SwsContext, libav_deleter> sws_context_;
    std::unique_ptr<AVFrame, libav_deleter> frame_buffer_;
    std::unique_ptr<AVPacket, libav_deleter> packet_buffer_;
    H264Params params_;

    int32_t pts_ = 0;
    std::vector<uint8_t> pps_sps_;
};

}  // namespace encoder
}  // namespace daemon
}  // namespace prototype
