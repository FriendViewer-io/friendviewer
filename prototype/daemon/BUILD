package(default_visibility = ["//visibility:public"])

# ChunkBuffer is a wrapper around a queue, no need to test
cc_library(
    name = "chunk_buffer",
    hdrs = ["chunk_buffer.hh"],
)

cc_library(
    name = "daemon_backend_controller",
    hdrs = ["daemon_backend_controller.hh"],
    srcs = ["daemon_backend_controller.cc"],
    deps = [
        ":chunk_buffer",
        "//prototype/daemon/capture:dxgi_capture",
        "//prototype/daemon/decoder:h264_decoder",
        "//prototype/daemon/decoder:h264_encoder",
        "//prototype/daemon/input:input_control",
        "//prototype/daemon/networking:network_manager",
        "//prototype/protobuf:control_cc",
        "//prototype/protobuf:session_cc",
    ],
)

cc_binary(
    name = "daemon_main",
    srcs = ["daemon_main.cc"],
    deps = [
        ":daemon_backend_controller",
        "@directx_11//:all",
        "@dxgi_11//:all",
        "@user32//:all",
        "@winsock2//:all",
        "@ffmpeg_win64//:ffmpeg_full",
    ],
)
