load("@bazel_tools//tools/build_defs/repo:http.bzl", "http_archive")
load("@bazel_tools//tools/build_defs/repo:git.bzl", "git_repository")
load("//:custom-bazel/grab_winsdk.bzl", "winsdk_library")
load("//:custom-bazel/grab_ffmpeg.bzl", "ffmpeg_windows_import")

skylib_version = "0.8.0"
http_archive(
    name = "bazel_skylib",
    type = "tar.gz",
    url = "https://github.com/bazelbuild/bazel-skylib/releases/download/{}/bazel-skylib.{}.tar.gz".format (skylib_version, skylib_version),
    sha256 = "2ef429f5d7ce7111263289644d233707dba35e39696377ebab8b0bc701f7818e",
)

# Grab protobuf to allow native proto_library and cc_proto_library to function
http_archive(
    name = "com_google_protobuf",
    sha256 = "378dc2bf2ed61169521b74950180e94ddf99bbef8e98b6b05f35f7b394595a0c",
    strip_prefix = "protobuf-3.8.0",
    urls = ["https://github.com/protocolbuffers/protobuf/releases/download/v3.8.0/protobuf-all-3.8.0.zip"],
)

# GoogleTest for C++ unit testing
# Version 1.8.1 from August 31, 2018
http_archive(
    name = "gtest",
    url = "https://github.com/google/googletest/archive/release-1.8.1.zip",
    sha256 = "927827c183d01734cc5cfef85e0ff3f5a92ffe6188e0d18e909c5efebf28a0c7",
    strip_prefix = "googletest-release-1.8.1",
    build_file = "@//:custom-bazel/gtest.BUILD",
)

# TODO: Move this to a more permanent location
http_archive(
    name = "codecs_win64",
    urls = [
        "http://shiiion.me/res/codecs.zip",
    ],
    sha256 = "ebcfa57d720ca90c31444c39924fd93ba205ded39cf5300cea38455032e02acd",
    build_file_content = 
    """
cc_library(
    name = "codecs_win64",
    srcs = [
        "libx264-148.dll",
        "libopus-0.dll",
    ],
    visibility = ["//visibility:public"],
)
    """,
)

http_archive(
    name = "glfw3",
    urls = ["https://github.com/glfw/glfw/releases/download/3.3/glfw-3.3.bin.WIN64.zip"],
    sha256 = "b7ceae85dce2f3e2552f74564cb05a7500e3f1caa5d68ddbcf2ecf18f4036774",
    strip_prefix = "glfw-3.3.bin.WIN64",
    build_file_content = 
    """
cc_library(
    name = "all",
    srcs = [
        "lib-vc2017/glfw3.dll",
        "lib-vc2017/glfw3dll.lib",
    ],
    hdrs = glob(["include/**"]),
    includes = ["include/."],
    visibility = ["//visibility:public"],
)
"""
)

http_archive(
    name = "glew",
    urls = ["http://pilotfiber.dl.sourceforge.net/project/glew/glew/2.1.0/glew-2.1.0-win32.zip"],
    sha256 = "80cfc88fd295426b49001a9dc521da793f8547ac10aebfc8bdc91ddc06c5566c",
    strip_prefix = "glew-2.1.0",
    build_file_content =
    """
cc_library(
    name = "all",
    srcs = [
        "lib/release/x64/glew32.lib",
        "bin/release/x64/glew32.dll",
    ],
    hdrs = glob(["include/**"]),
    includes = ["include/."],
    visibility = ["//visibility:public"],
)
"""
)

# Search DX11 in windows sdk (version 10.0.17134.0 only)
winsdk_library(
    name = "directx_11",
    headers = ["d3d11.h"],
    static_libraries = ["d3d11.lib"],
)

# Search DXGI in windows sdk (version 10.0.17134.0 only)
winsdk_library(
    name = "dxgi_11",
    headers = ["dxgi.h", "dxgi1_2.h"],
    static_libraries = ["dxgi.lib"],
)

winsdk_library(
    name = "user32",
    headers = ["Windows.h"],
    static_libraries = ["User32.lib"],
)

winsdk_library(
    name = "winsock2",
    headers = ["WinSock2.h"],
    static_libraries = ["ws2_32.lib"],
)

winsdk_library(
    name = "opengl",
    headers = [
        "gl/GL.h",
        "gl/GLU.h",
    ],
    static_libraries = ["OpenGL32.lib"],
)

winsdk_library(
    name = "user32",
    headers = ["Windows.h"],
    static_libraries = ["User32.lib"],
)

# Using Zeranthoe win64 prebuilt for ffmpeg
ffmpeg_windows_import(
    name = "ffmpeg_win64",
)

RULES_JVM_EXTERNAL_TAG = "2.2"
RULES_JVM_EXTERNAL_SHA = "f1203ce04e232ab6fdd81897cf0ff76f2c04c0741424d192f28e65ae752ce2d6"

http_archive(
    name = "rules_jvm_external",
    strip_prefix = "rules_jvm_external-%s" % RULES_JVM_EXTERNAL_TAG,
    sha256 = RULES_JVM_EXTERNAL_SHA,
    url = "https://github.com/bazelbuild/rules_jvm_external/archive/%s.zip" % RULES_JVM_EXTERNAL_TAG,
)

load("@rules_jvm_external//:defs.bzl", "maven_install")

maven_install(
    artifacts = [
        "junit:junit:4.12",
        "io.netty:netty-all:4.1.1.Final",
        "org.xerial:sqlite-jdbc:3.27.2.1",
    ],
    repositories = [
        "https://repo1.maven.org/maven2",
        "https://mvnrepository.com/artifact",
    ],
)
