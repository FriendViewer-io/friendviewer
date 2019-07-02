java_binary(
    name = "server",
    srcs = ["Server.java", "ServerHandler.java"],
    deps = ["@maven//:io_netty_netty_all"],
    main_class = "prototype.distributor.Server"
)

java_binary(
    name = "client",
    srcs = ["Client.java", "ClientHandler.java"],
    deps = ["@maven//:io_netty_netty_all"],
    main_class = "prototype.distributor.Client"
)
