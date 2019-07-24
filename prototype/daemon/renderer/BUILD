cc_library(
    name = "gl_window",
    srcs = ["gl_window.cc"],
    hdrs = ["gl_window.hh"],
    deps = [
        "@opengl//:all",
        "@glfw3//:all",
        "@glew//:all",
    ],
    visibility = ["//visibility:public"],
)

cc_binary(
    name = "gl_window_test",
    srcs = ["gl_window_test.cc"],
    deps = [
        ":gl_window",
    ],
)
