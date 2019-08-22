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