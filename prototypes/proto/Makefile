CPP_OUT = --cpp_out=out/cpp
JS_OUT = --js_out=out/js
SRC_PATH = --proto_path=src
ALL_PROTO = src/demuxer.proto

all: make_proto_cpp make_proto_js

make_proto_cpp: $(ALL_PROTO)
	protoc $(SRC_PATH) $(CPP_OUT) $(ALL_PROTO)

make_proto_js: $(ALL_PROTO)
	protoc $(SRC_PATH) $(JS_OUT) $(ALL_PROTO)