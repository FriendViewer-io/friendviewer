cc_library(
    name = "h264_encoder",
    srcs = ["h264_encoder.cc"],
    hdrs = ["h264_encoder.hh"],
    deps = [
        "@ffmpeg_win64//:ffmpeg_headers",
    ],
)

cc_binary(
    name = "h264_encoder_test",
    srcs = ["h264_encoder_test.cc"],
    deps = [
        ":h264_encoder",
        "@ffmpeg_win64//:ffmpeg_full",
        "@codecs_win64//:codecs_win64"
    ],
)
