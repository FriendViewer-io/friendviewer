package(default_visibility = ["//visibility:public"])

cc_library(
    name = "client_socket",
    srcs = ["client_socket.cc"],
    hdrs = ["client_socket.hh"],
)

cc_library(
    name = "network_manager",
    srcs = ["network_manager.cc"],
    hdrs = ["network_manager.hh"],
    deps = [
        ":client_socket",
        "//prototype/network_protocol:message_manager_cc",
        "//prototype/protobuf:fv_packet_cc",
    ]
)

cc_binary(
    name = "client_socket_test",
    srcs = ["client_socket_test.cc"],
    deps = [
        ":client_socket",
        "//prototype/network_protocol:message_manager_cc",
        "@winsock2//:all",
    ],
)
