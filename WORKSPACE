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

# Search DX11 in windows sdk (version 10.0.17134.0 only)
winsdk_library(
    name = "directx_11",
    sdk_lib_subfolder = "um",
    sdk_include_subfolder = "um",
    headers = ["d3d11.h"],
    static_libraries = ["d3d11.lib"],
)

# Search DXGI in windows sdk (version 10.0.17134.0 only)
winsdk_library(
    name = "dxgi_11",
    sdk_lib_subfolder = "um",
    sdk_include_subfolder = "shared",
    headers = ["dxgi.h", "dxgi1_2.h"],
    static_libraries = ["dxgi.lib"],
)

# Using Zeranthoe win64 prebuilt for ffmpeg
ffmpeg_windows_import(
    name = "ffmpeg_win64",
)
