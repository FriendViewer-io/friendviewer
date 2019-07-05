#include "prototype/daemon/encoder/h264_encoder.hh"

extern "C" {
#include "third_party/libav/libavcodec/avcodec.h"
#include "third_party/libav/libavutil/avutil.h"
#include "third_party/libav/libavutil/opt.h"
#include "third_party/libav/libswscale/swscale.h"
}

namespace prototype {
namespace daemon {
namespace encoder {

namespace {
void delete_encoder_ctx(void *ctx) {}
void delete_sws(void *sws) {}
void delete_frame(void *frame) {}
void delete_packet(void *ctx) {}

// Assume data starts at a nalu
const uint8_t *seek_nalu(const uint8_t *data, int num_nalu) {
    std::ptrdiff_t off = 0;
    for (int i = 0; i < num_nalu; i++) {
        auto tmp = off + 2;
        for (;; tmp++) {
            if (*(data + off) == 0 && *(data + off + 1) == 0 && *(data + off + 2) == 0 &&
                *(data + off + 3) == 1) {
                break;
            } else if (*(data + off) == 0 && *(data + off + 1) == 0 && *(data + off + 2) == 1) {
                break;
            }
        }
        off = tmp;
    }
    return off + data;
}
}  // namespace

H264Encoder::H264Encoder()
    : encoder_context_(nullptr, delete_encoder_ctx),
      sws_context_(nullptr, delete_sws),
      frame_buffer_(nullptr, delete_frame),
      packet_buffer_(nullptr, delete_packet) {}

bool H264Encoder::init(const H264Params &params) {
    params_ = params;

    const AVCodec *codec = avcodec_find_encoder(AV_CODEC_ID_H264);
    encoder_context_ = std::unique_ptr<AVCodecContext, libav_deleter>(avcodec_alloc_context3(codec),
                                                                      delete_encoder_ctx);
    if (!encoder_context_) {
        return false;
    }

    encoder_context_->codec_id = AV_CODEC_ID_H264;
    encoder_context_->width = params.width;
    encoder_context_->height = params.height;
    encoder_context_->bit_rate = params.bit_rate;
    encoder_context_->codec_type = AVMEDIA_TYPE_VIDEO;
    // 1 I-frame per 10 P-frames, should be tuned
    encoder_context_->gop_size = 10;
    // Standard h264 format
    encoder_context_->pix_fmt = AV_PIX_FMT_YUV420P;
    // Disable B-frames to get zerolatency
    encoder_context_->max_b_frames = 0;
    av_opt_set(encoder_context_->priv_data, "tune", "zerolatency", 0);
    if (avcodec_open2(encoder_context_.get(), codec, nullptr)) {
        return false;
    }

    sws_context_ = std::unique_ptr<SwsContext, libav_deleter>(
        sws_getContext(params.width, params.height, AVPixelFormat::AV_PIX_FMT_BGRA, params.width,
                       params.height, AVPixelFormat::AV_PIX_FMT_YUV420P, 0, nullptr, nullptr,
                       nullptr),
        delete_sws);

    frame_buffer_ = std::unique_ptr<AVFrame, libav_deleter>(av_frame_alloc(), delete_frame);

    frame_buffer_->format = encoder_context_->pix_fmt;
    frame_buffer_->width = encoder_context_->width;
    frame_buffer_->height = encoder_context_->height;

    // Pre-allocate frame buffer
    av_frame_get_buffer(frame_buffer_.get(), 32);
    av_frame_make_writable(frame_buffer_.get());

    packet_buffer_ = std::unique_ptr<AVPacket, libav_deleter>(av_packet_alloc(), delete_packet);

    return true;
}

int H264Encoder::encode_frame(const std::vector<uint8_t> &frame_in,
                              std::vector<uint8_t> &packet_out) {
    int stride[1] = {-4 * params_.width};
    auto image_end = frame_in.data() + ((params_.height - 1) * 4 * params_.width);
    auto status = sws_scale(sws_context_.get(), &image_end, stride, 0, params_.height,
                            frame_buffer_->data, frame_buffer_->linesize);
    if (status) {
        return status;
    }

    frame_buffer_->pts = pts_;
    status = avcodec_send_frame(encoder_context_.get(), frame_buffer_.get());
    if (status) {
        return status;
    }

    // No matter what, a full frame of video should immediately encode
    status = avcodec_receive_packet(encoder_context_.get(), packet_buffer_.get());
    if (status) {
        return status;
    }

    const uint8_t *packet_data = packet_buffer_->data;
    const uint8_t *packet_data_end = packet_buffer_->data + packet_buffer_->size;
    // First received packet contains pps and sps
    if (pts_ == 0) {
        const uint8_t *pps_sps_end = seek_nalu(packet_buffer_->data, 2);
        pps_sps_.insert(pps_sps_.end(), packet_data, pps_sps_end);
        packet_data = pps_sps_end;
    }

    packet_out.insert(packet_out.end(), packet_data, packet_data_end);
    return 0;
}

int32_t H264Encoder::get_pts() const { return pts_; }

bool H264Encoder::get_pps_sps(std::vector<uint8_t> &packet_out) const {
    if (pps_sps_.empty()) {
        return false;
    }
    packet_out = pps_sps_;
    return true;
}

}  // namespace encoder
}  // namespace daemon
}  // namespace prototype
