
java_test(
    name = "message_manager_java_test",
    srcs = [
        "MessageManagerTest.java",
        "TestNormalRetrieve.java",
        "TestPartialRetrieve.java",
        "TestNagleRetrieve.java",
        "Util.java",
    ],
    deps = [
        "//prototype/network_protocol:message_manager_java",
        "@maven//:junit_junit",
    ],
    test_class = "prototype.network_protocol.java_test.MessageManagerTest",
)
