java_binary(
    name = "server",
    srcs = ["Server.java", "ServerHandler.java", "UsersData.java", "SessionData.java"],
    deps = [
    	"@maven//:io_netty_netty_all",
		"//prototype/protobuf:control_java",
		"//prototype/protobuf:session_java",
		"//prototype/protobuf:fv_packet_java",
		"//prototype/network_protocol:message_manager_java",
		"@com_google_protobuf//:protobuf_java",
	],
    main_class = "prototype.distributor.Server",
)

java_binary(
    name = "client",
    srcs = ["Client.java"],
    deps = [
    	"@maven//:io_netty_netty_all",
		"//prototype/protobuf:control_java",
		"//prototype/protobuf:session_java",
		"//prototype/protobuf:fv_packet_java",
		"@com_google_protobuf//:protobuf_java",
	],
	main_class = "prototype.distributor.Client"
)
