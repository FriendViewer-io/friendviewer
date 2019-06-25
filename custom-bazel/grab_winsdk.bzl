load("@bazel_tools//tools/cpp:windows_cc_configure.bzl", "find_vc_path")
load("@bazel_tools//tools/cpp:windows_cc_configure.bzl", "setup_vc_env_vars")

def _impl(ctx):
    vc_path = find_vc_path(ctx)
    env_map = setup_vc_env_vars(ctx, vc_path)
    lib_paths = env_map["LIB"].split(";")
    include_paths = env_map["INCLUDE"].split(";")
    all_libs = "["
    all_includes = "["

    winsdk_lib_symlink = "winsdk_lib_" + ctx.name
    for path in lib_paths:
        if path.find("lib\\\\10.0.17134.0\\\\" + ctx.attr.sdk_lib_subfolder + "\\\\x64") != -1:
            ctx.symlink(path, winsdk_lib_symlink)

    winsdk_include_symlink = "winsdk_include_" + ctx.name
    for path in include_paths:
        if path.find("include\\\\10.0.17134.0\\\\" + ctx.attr.sdk_include_subfolder) != -1:
            ctx.symlink(path, winsdk_include_symlink)

    for lib in ctx.attr.static_libraries:
        all_libs = all_libs + "\"" + winsdk_lib_symlink + "/" + lib + "\", "
    all_libs = all_libs + "]"
        
    for include in ctx.attr.headers:
        all_includes = all_includes + "\"" + winsdk_include_symlink + "/" + include + "\", "
    all_includes = all_includes + "]"

    ctx.file("BUILD",
        """cc_library(
    name = "all",
    srcs = """ + all_libs + """,
    hdrs = """ + all_includes + """,
    visibility = ["//visibility:public"],
)
        """
    )

winsdk_library = repository_rule(
    implementation = _impl,
    attrs = {
        "headers": attr.string_list(mandatory=True),
        "static_libraries": attr.string_list(mandatory=True),
        "sdk_lib_subfolder": attr.string(mandatory=True),
        "sdk_include_subfolder": attr.string(mandatory=True),
    },
)
