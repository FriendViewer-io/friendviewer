#include "prototype/daemon/decoder/h264_decoder.hh"

extern "C" {
#include "third_party/libav/libavcodec/avcodec.h"
#include "third_party/libav/libavutil/avutil.h"
#include "third_party/libav/libavutil/opt.h"
#include "third_party/libav/libswscale/swscale.h"
}

namespace prototype {
namespace daemon {
namespace decoder {

namespace {
void delete_decoder_ctx(void *ctx) {
    if (ctx) {
        avcodec_free_context(&reinterpret_cast<AVCodecContext *>(ctx));
    }
}
void delete_sws(void *sws) {
    if (sws) {
        sws_freeContext(reinterpret_cast<SwsContext *>(sws));
    }
}
void delete_frame(void *frame) {
    if (frame) {
        av_frame_free(&reinterpret_cast<AVFrame *>(frame));
    }
}
void delete_packet(void *pkt) {
    if (pkt) {
        av_packet_free(&reinterpret_cast<AVPacket *>(pkt));
    }
}
}  // namespace

H264Decoder::H264Decoder()
    : decoder_context_(nullptr, delete_decoder_ctx),
      sws_context_(nullptr, delete_sws),
      frame_buffer_(nullptr, delete_frame),
      packet_buffer_(nullptr, delete_packet) {}

bool H264Decoder::init(const H264Params &params) {
    width_ = params.width;
    height_ = params.height;

    const AVCodec *codec = avcodec_find_decoder(AV_CODEC_ID_H264);
    decoder_context_ = std::unique_ptr<AVCodecContext, libav_deleter>(avcodec_alloc_context3(codec),
                                                                      delete_decoder_ctx);
    if (!decoder_context_) {
        return false;
    }

    decoder_context_->codec_id = AV_CODEC_ID_H264;
    decoder_context_->width = params.width;
    decoder_context_->height = params.height;
    decoder_context_->bit_rate = params.bit_rate;
    decoder_context_->codec_type = AVMEDIA_TYPE_VIDEO;
    decoder_context_->time_base = {params.framerate_den, params.framerate_num};
    decoder_context_->framerate = {params.framerate_num, params.framerate_den};

    frame_buffer_ = std::unique_ptr<AVFrame, libav_deleter>(av_frame_alloc(), delete_frame);
    if (!frame_buffer_) {
        return false;
    }

    packet_buffer_ = std::unique_ptr<AVPacket, libav_deleter>(av_packet_alloc(), delete_packet);
    if (!packet_buffer_) {
        return false;
    }

    sws_context_ = std::unique_ptr<SwsContext, libav_deleter>(
        sws_getContext(params.width, params.height, AVPixelFormat::AV_PIX_FMT_YUV420P, params.width,
                       params.height, AVPixelFormat::AV_PIX_FMT_RGB24, 0, nullptr, nullptr,
                       nullptr),
        delete_sws);
    if (!sws_context_) {
        return false;
    }

    if (avcodec_open2(decoder_context_.get(), codec, NULL) < 0) {
        return false;
    }
    return true;
}

H264Decoder::DecodeStatus H264Decoder::decode_packet_internal(uint8_t *data, int len,
                                                              std::vector<uint8_t> &frame_out) {
    packet_buffer_->data = data;
    packet_buffer_->size = len;

    int ret = avcodec_send_packet(decoder_context_.get(), packet_buffer_.get());
    if (ret < 0) {
        return DecodeStatus::kSendFail;
    }

    ret = avcodec_receive_frame(decoder_context_.get(), frame_buffer_.get());
    if (ret == AVERROR(EAGAIN)) {
        return DecodeStatus::kAgain;
    } else if (ret == AVERROR_EOF) {
        return DecodeStatus::kEof;
    } else if (ret < 0) {
        return DecodeStatus::kOther;
    }
    if (frame_out.size() != width_ * height_ * 3) {
        frame_out.resize(width_ * height_ * 3);
    }

    uint8_t *lines_out[1];
    lines_out[0] = (&frame_out[0]);
    const int stride[1] = {width_ * 3};

    sws_scale(sws_context_.get(), frame_buffer_->data, frame_buffer_->linesize, 0, height_,
              lines_out, stride);

    return DecodeStatus::kSuccess;
}

void H264Decoder::shutdown() {
    decoder_context_ = std::unique_ptr<AVCodecContext, libav_deleter>(nullptr, delete_decoder_ctx);
    sws_context_ = std::unique_ptr<SwsContext, libav_deleter>(nullptr, delete_sws);
    frame_buffer_ = std::unique_ptr<AVFrame, libav_deleter>(nullptr, delete_frame);
    packet_buffer_ = std::unique_ptr<AVPacket, libav_deleter>(nullptr, delete_packet);
    pts_ = 0;
    width_ = height_ = 0;
}

H264Decoder::DecodeStatus H264Decoder::decode_packet(const std::vector<uint8_t> &packet_in,
                                                     std::vector<uint8_t> &frame_out) {
    return decode_packet_internal(const_cast<uint8_t *>(packet_in.data()), packet_in.size(),
                                  frame_out);
}

H264Decoder::DecodeStatus H264Decoder::decode_packet(const std::string &packet_in,
                                                     std::vector<uint8_t> &frame_out) {
    return decode_packet_internal(reinterpret_cast<uint8_t *>(const_cast<char *>(packet_in.data())),
                                  packet_in.size(), frame_out);
}

}  // namespace decoder
}  // namespace daemon
}  // namespace prototype
