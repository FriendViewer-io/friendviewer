cc_library(
    name = "input_control",
    srcs = ["input_control.cc"],
    hdrs = ["input_control.hh"],
)

cc_binary(
    name = "input_control_test",
    srcs = ["input_control_test.cc"],
    deps = [
        ":input_control",
        "@user32//:all",
    ],
)
