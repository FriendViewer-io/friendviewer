
def _impl(ctx):
    ctx.download_and_extract(
        url = "https://ffmpeg.zeranoe.com/builds/win64/shared/ffmpeg-4.1.3-win64-shared.zip",
        stripPrefix = "ffmpeg-4.1.3-win64-shared",
        sha256 = "0b974578e07d974c4bafb36c7ed0b46e46b001d38b149455089c13b57ddefe5d",
    )
    ctx.download_and_extract(
        url = "https://ffmpeg.zeranoe.com/builds/win64/dev/ffmpeg-4.1.3-win64-dev.zip",
        stripPrefix = "ffmpeg-4.1.3-win64-dev",
        sha256 = "334b473467db096a5b74242743592a73e120a137232794508e4fc55593696a5b",
    )
    ctx.file("BUILD",
"""
cc_library(
    name = "ffmpeg_headers",
    hdrs = glob(["include/**"]),
    includes = ["include/."],
    visibility = ["//visibility:public"],
    include_prefix = "third_party/libav",
    strip_include_prefix = "include",
)

cc_library(
    name = "ffmpeg_full",
    srcs = [
        "bin/avcodec-58.dll",
        "bin/avformat-58.dll",
        "bin/avutil-56.dll",
        "bin/swresample-3.dll",
        "bin/swscale-5.dll",
        "lib/avcodec.lib",
        "lib/avfilter.lib",
        "lib/avutil.lib",
        "lib/swscale.lib",
    ],
    deps = [
        ":ffmpeg_headers",
    ],
    linkstatic = True,
    visibility = ["//visibility:public"],
)
"""
    )

ffmpeg_windows_import = repository_rule(
    implementation = _impl
)
