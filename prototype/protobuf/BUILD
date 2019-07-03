package(default_visibility = ["//visibility:public"])

proto_library(
    name = "fv_packet",
    srcs = ["fv_packet.proto"]
)

proto_library(
    name = "control",
    srcs = ["control.proto"],
)

proto_library(
    name = "session",
    srcs = ["session.proto"],
)

cc_proto_library(
    name = "fv_packet_cc",
    deps = [":fv_packet"],
)

cc_proto_library(
    name = "session_cc",
    deps = [":session"],
)

cc_proto_library(
    name = "control_cc",
    deps = [":control"],
)

java_proto_library(
    name = "fv_packet_java",
    deps = [":fv_packet"],
)

java_proto_library(
    name = "session_java",
    deps = [":session"],
)

java_proto_library(
    name = "control_java",
    deps = [":control"],
)
