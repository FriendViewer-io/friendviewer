java_library(
    name = "DatabaseManager",
    srcs = ["DatabaseManager.java"],
    deps = ["@maven//:org_xerial_sqlite_jdbc"],
)

java_binary(
    name = "DBTester",
    srcs = ["DBTester.java"],
    deps = [
    	"@maven//:org_xerial_sqlite_jdbc",
		":DatabaseManager",
	],
	main_class = "prototype.database.DBTester"
)