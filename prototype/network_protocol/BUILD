package(default_visibility = ["//visibility:public"])

cc_library(
    name = "message_manager_cc",
    hdrs = ["message_manager.hh"],
    srcs = ["message_manager.cc"],
)

cc_test(
    name = "message_manager_cc_test",
    srcs = ["message_manager_test.cc"],
    deps = [
        ":message_manager_cc",
        "@gtest//:gtest",
        "@gtest//:gtest_main",
    ],
)

java_library(
    name = "message_manager_java",
    srcs = ["MessageManager.java"],
)