CC = --cpp_out=
JS = --js_out=
SRC_PATH = --proto_path=src

all: make_out_directories demuxer_proto

make_out_directories:
	mkdir -p out/demuxer/cpp
	mkdir -p out/demuxer/js
#...

demuxer_proto: src/demuxer/demuxer.proto
	protoc $(SRC_PATH)/demuxer $(CC)out/demuxer/cpp src/demuxer/demuxer.proto
	protoc $(SRC_PATH)/demuxer $(JS)out/demuxer/js src/demuxer/demuxer.proto