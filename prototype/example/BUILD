# example/test bazel targets

cc_library(
    name = "example_lib",
    srcs = ["example_class.cc"],
    hdrs = ["example_class.hh"],
)

cc_binary(
    name = "example",
    srcs = ["example.cc"],
    deps = [
        ":example_lib",
    ]
)

# (not really) all dependencies
cc_library(
    name = "pull_all_dependencies",
    deps = [
        "@ffmpeg_win64//:ffmpeg_full",
        "@directx_11//:all",
        "@dxgi_11//:all",
        "@codecs_win64//:codecs_win64",
    ]
)

cc_test(
    name = "example_test",
    srcs = ["example_test.cc"],
    deps = [
        "@gtest//:gtest",
        "@gtest//:gtest_main",
    ]
)
