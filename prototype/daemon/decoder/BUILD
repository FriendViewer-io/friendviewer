package(default_visibility = ["//visibility:public"])

cc_library(
    name = "h264_decoder",
    srcs = ["h264_decoder.cc"],
    hdrs = ["h264_decoder.hh"],
    deps = [
        "@ffmpeg_win64//:ffmpeg_headers",
    ],
)

cc_library(
    name = "h264_encoder",
    srcs = ["h264_encoder.cc"],
    hdrs = ["h264_encoder.hh"],
    deps = [
        "@ffmpeg_win64//:ffmpeg_headers",
    ],
)

cc_binary(
    name = "h264_test",
    srcs = ["h264_test.cc"],
    deps = [
        ":h264_encoder",
        ":h264_decoder",
        "@ffmpeg_win64//:ffmpeg_full",
        "@codecs_win64//:codecs_win64",
        "//prototype/network_protocol:message_manager_cc",
        "//prototype/daemon/capture:dxgi_capture",
    ],
)
