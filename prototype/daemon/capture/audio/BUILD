
cc_library(
    name = "wasapi_capture",
    srcs = ["wasapi_capture.cc"],
    hdrs = ["wasapi_capture.hh"],
    deps = [
        "@wasapi//:all",
        "@user32//:all",
        "//prototype/daemon:chunk_buffer",
    ]
)

cc_binary(
    name = "wasapi_capture_test",
    srcs = ["wasapi_capture_test.cc"],
    deps = [
        ":wasapi_capture",
    ]
)