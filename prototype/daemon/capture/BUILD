package(default_visibility = ["//visibility:public"])

cc_library(
    name = "dxgi_capture",
    srcs = ["dxgi_capture.cc"],
    hdrs = ["dxgi_capture.hh"],
    linkopts = [
        "ole32.lib",
    ],
    deps = [
        "@directx_11//:all",
        "@dxgi_11//:all",
    ]
)

cc_binary(
    name = "dxgi_capture_test",
    srcs = ["dxgi_capture_test.cc"],
    deps = [
        ":dxgi_capture",
    ]
)
