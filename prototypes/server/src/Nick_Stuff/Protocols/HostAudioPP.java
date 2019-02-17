// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: Audio Packet Protocol.proto

package Nick_Stuff.Protocols;

public final class HostAudioPP {
  private HostAudioPP() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  public interface dataOrBuilder extends
      // @@protoc_insertion_point(interface_extends:data)
      com.google.protobuf.MessageOrBuilder {

    /**
     * <code>required int32 proto_type = 1;</code>
     */
    boolean hasProtoType();
    /**
     * <code>required int32 proto_type = 1;</code>
     */
    int getProtoType();

    /**
     * <code>required int64 proto_id = 2;</code>
     */
    boolean hasProtoId();
    /**
     * <code>required int64 proto_id = 2;</code>
     */
    long getProtoId();

    /**
     * <code>required int64 utc_time = 3;</code>
     */
    boolean hasUtcTime();
    /**
     * <code>required int64 utc_time = 3;</code>
     */
    long getUtcTime();

    /**
     * <code>required int32 pts = 4;</code>
     */
    boolean hasPts();
    /**
     * <code>required int32 pts = 4;</code>
     */
    int getPts();

    /**
     * <code>required int32 dts = 5;</code>
     */
    boolean hasDts();
    /**
     * <code>required int32 dts = 5;</code>
     */
    int getDts();

    /**
     * <code>repeated bytes H246_audio_data = 6;</code>
     */
    java.util.List<com.google.protobuf.ByteString> getH246AudioDataList();
    /**
     * <code>repeated bytes H246_audio_data = 6;</code>
     */
    int getH246AudioDataCount();
    /**
     * <code>repeated bytes H246_audio_data = 6;</code>
     */
    com.google.protobuf.ByteString getH246AudioData(int index);
  }
  /**
   * Protobuf type {@code data}
   */
  public  static final class data extends
      com.google.protobuf.GeneratedMessageV3 implements
      // @@protoc_insertion_point(message_implements:data)
      dataOrBuilder {
  private static final long serialVersionUID = 0L;
    // Use data.newBuilder() to construct.
    private data(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
      super(builder);
    }
    private data() {
      protoType_ = 0;
      protoId_ = 0L;
      utcTime_ = 0L;
      pts_ = 0;
      dts_ = 0;
      h246AudioData_ = java.util.Collections.emptyList();
    }

    @java.lang.Override
    public final com.google.protobuf.UnknownFieldSet
    getUnknownFields() {
      return this.unknownFields;
    }
    private data(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      this();
      if (extensionRegistry == null) {
        throw new java.lang.NullPointerException();
      }
      int mutable_bitField0_ = 0;
      com.google.protobuf.UnknownFieldSet.Builder unknownFields =
          com.google.protobuf.UnknownFieldSet.newBuilder();
      try {
        boolean done = false;
        while (!done) {
          int tag = input.readTag();
          switch (tag) {
            case 0:
              done = true;
              break;
            case 8: {
              bitField0_ |= 0x00000001;
              protoType_ = input.readInt32();
              break;
            }
            case 16: {
              bitField0_ |= 0x00000002;
              protoId_ = input.readInt64();
              break;
            }
            case 24: {
              bitField0_ |= 0x00000004;
              utcTime_ = input.readInt64();
              break;
            }
            case 32: {
              bitField0_ |= 0x00000008;
              pts_ = input.readInt32();
              break;
            }
            case 40: {
              bitField0_ |= 0x00000010;
              dts_ = input.readInt32();
              break;
            }
            case 50: {
              if (!((mutable_bitField0_ & 0x00000020) == 0x00000020)) {
                h246AudioData_ = new java.util.ArrayList<com.google.protobuf.ByteString>();
                mutable_bitField0_ |= 0x00000020;
              }
              h246AudioData_.add(input.readBytes());
              break;
            }
            default: {
              if (!parseUnknownField(
                  input, unknownFields, extensionRegistry, tag)) {
                done = true;
              }
              break;
            }
          }
        }
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        throw e.setUnfinishedMessage(this);
      } catch (java.io.IOException e) {
        throw new com.google.protobuf.InvalidProtocolBufferException(
            e).setUnfinishedMessage(this);
      } finally {
        if (((mutable_bitField0_ & 0x00000020) == 0x00000020)) {
          h246AudioData_ = java.util.Collections.unmodifiableList(h246AudioData_);
        }
        this.unknownFields = unknownFields.build();
        makeExtensionsImmutable();
      }
    }
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return Nick_Stuff.Protocols.HostAudioPP.internal_static_data_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return Nick_Stuff.Protocols.HostAudioPP.internal_static_data_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              Nick_Stuff.Protocols.HostAudioPP.data.class, Nick_Stuff.Protocols.HostAudioPP.data.Builder.class);
    }

    private int bitField0_;
    public static final int PROTO_TYPE_FIELD_NUMBER = 1;
    private int protoType_;
    /**
     * <code>required int32 proto_type = 1;</code>
     */
    public boolean hasProtoType() {
      return ((bitField0_ & 0x00000001) == 0x00000001);
    }
    /**
     * <code>required int32 proto_type = 1;</code>
     */
    public int getProtoType() {
      return protoType_;
    }

    public static final int PROTO_ID_FIELD_NUMBER = 2;
    private long protoId_;
    /**
     * <code>required int64 proto_id = 2;</code>
     */
    public boolean hasProtoId() {
      return ((bitField0_ & 0x00000002) == 0x00000002);
    }
    /**
     * <code>required int64 proto_id = 2;</code>
     */
    public long getProtoId() {
      return protoId_;
    }

    public static final int UTC_TIME_FIELD_NUMBER = 3;
    private long utcTime_;
    /**
     * <code>required int64 utc_time = 3;</code>
     */
    public boolean hasUtcTime() {
      return ((bitField0_ & 0x00000004) == 0x00000004);
    }
    /**
     * <code>required int64 utc_time = 3;</code>
     */
    public long getUtcTime() {
      return utcTime_;
    }

    public static final int PTS_FIELD_NUMBER = 4;
    private int pts_;
    /**
     * <code>required int32 pts = 4;</code>
     */
    public boolean hasPts() {
      return ((bitField0_ & 0x00000008) == 0x00000008);
    }
    /**
     * <code>required int32 pts = 4;</code>
     */
    public int getPts() {
      return pts_;
    }

    public static final int DTS_FIELD_NUMBER = 5;
    private int dts_;
    /**
     * <code>required int32 dts = 5;</code>
     */
    public boolean hasDts() {
      return ((bitField0_ & 0x00000010) == 0x00000010);
    }
    /**
     * <code>required int32 dts = 5;</code>
     */
    public int getDts() {
      return dts_;
    }

    public static final int H246_AUDIO_DATA_FIELD_NUMBER = 6;
    private java.util.List<com.google.protobuf.ByteString> h246AudioData_;
    /**
     * <code>repeated bytes H246_audio_data = 6;</code>
     */
    public java.util.List<com.google.protobuf.ByteString>
        getH246AudioDataList() {
      return h246AudioData_;
    }
    /**
     * <code>repeated bytes H246_audio_data = 6;</code>
     */
    public int getH246AudioDataCount() {
      return h246AudioData_.size();
    }
    /**
     * <code>repeated bytes H246_audio_data = 6;</code>
     */
    public com.google.protobuf.ByteString getH246AudioData(int index) {
      return h246AudioData_.get(index);
    }

    private byte memoizedIsInitialized = -1;
    @java.lang.Override
    public final boolean isInitialized() {
      byte isInitialized = memoizedIsInitialized;
      if (isInitialized == 1) return true;
      if (isInitialized == 0) return false;

      if (!hasProtoType()) {
        memoizedIsInitialized = 0;
        return false;
      }
      if (!hasProtoId()) {
        memoizedIsInitialized = 0;
        return false;
      }
      if (!hasUtcTime()) {
        memoizedIsInitialized = 0;
        return false;
      }
      if (!hasPts()) {
        memoizedIsInitialized = 0;
        return false;
      }
      if (!hasDts()) {
        memoizedIsInitialized = 0;
        return false;
      }
      memoizedIsInitialized = 1;
      return true;
    }

    @java.lang.Override
    public void writeTo(com.google.protobuf.CodedOutputStream output)
                        throws java.io.IOException {
      if (((bitField0_ & 0x00000001) == 0x00000001)) {
        output.writeInt32(1, protoType_);
      }
      if (((bitField0_ & 0x00000002) == 0x00000002)) {
        output.writeInt64(2, protoId_);
      }
      if (((bitField0_ & 0x00000004) == 0x00000004)) {
        output.writeInt64(3, utcTime_);
      }
      if (((bitField0_ & 0x00000008) == 0x00000008)) {
        output.writeInt32(4, pts_);
      }
      if (((bitField0_ & 0x00000010) == 0x00000010)) {
        output.writeInt32(5, dts_);
      }
      for (int i = 0; i < h246AudioData_.size(); i++) {
        output.writeBytes(6, h246AudioData_.get(i));
      }
      unknownFields.writeTo(output);
    }

    @java.lang.Override
    public int getSerializedSize() {
      int size = memoizedSize;
      if (size != -1) return size;

      size = 0;
      if (((bitField0_ & 0x00000001) == 0x00000001)) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt32Size(1, protoType_);
      }
      if (((bitField0_ & 0x00000002) == 0x00000002)) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt64Size(2, protoId_);
      }
      if (((bitField0_ & 0x00000004) == 0x00000004)) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt64Size(3, utcTime_);
      }
      if (((bitField0_ & 0x00000008) == 0x00000008)) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt32Size(4, pts_);
      }
      if (((bitField0_ & 0x00000010) == 0x00000010)) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt32Size(5, dts_);
      }
      {
        int dataSize = 0;
        for (int i = 0; i < h246AudioData_.size(); i++) {
          dataSize += com.google.protobuf.CodedOutputStream
            .computeBytesSizeNoTag(h246AudioData_.get(i));
        }
        size += dataSize;
        size += 1 * getH246AudioDataList().size();
      }
      size += unknownFields.getSerializedSize();
      memoizedSize = size;
      return size;
    }

    @java.lang.Override
    public boolean equals(final java.lang.Object obj) {
      if (obj == this) {
       return true;
      }
      if (!(obj instanceof Nick_Stuff.Protocols.HostAudioPP.data)) {
        return super.equals(obj);
      }
      Nick_Stuff.Protocols.HostAudioPP.data other = (Nick_Stuff.Protocols.HostAudioPP.data) obj;

      boolean result = true;
      result = result && (hasProtoType() == other.hasProtoType());
      if (hasProtoType()) {
        result = result && (getProtoType()
            == other.getProtoType());
      }
      result = result && (hasProtoId() == other.hasProtoId());
      if (hasProtoId()) {
        result = result && (getProtoId()
            == other.getProtoId());
      }
      result = result && (hasUtcTime() == other.hasUtcTime());
      if (hasUtcTime()) {
        result = result && (getUtcTime()
            == other.getUtcTime());
      }
      result = result && (hasPts() == other.hasPts());
      if (hasPts()) {
        result = result && (getPts()
            == other.getPts());
      }
      result = result && (hasDts() == other.hasDts());
      if (hasDts()) {
        result = result && (getDts()
            == other.getDts());
      }
      result = result && getH246AudioDataList()
          .equals(other.getH246AudioDataList());
      result = result && unknownFields.equals(other.unknownFields);
      return result;
    }

    @java.lang.Override
    public int hashCode() {
      if (memoizedHashCode != 0) {
        return memoizedHashCode;
      }
      int hash = 41;
      hash = (19 * hash) + getDescriptor().hashCode();
      if (hasProtoType()) {
        hash = (37 * hash) + PROTO_TYPE_FIELD_NUMBER;
        hash = (53 * hash) + getProtoType();
      }
      if (hasProtoId()) {
        hash = (37 * hash) + PROTO_ID_FIELD_NUMBER;
        hash = (53 * hash) + com.google.protobuf.Internal.hashLong(
            getProtoId());
      }
      if (hasUtcTime()) {
        hash = (37 * hash) + UTC_TIME_FIELD_NUMBER;
        hash = (53 * hash) + com.google.protobuf.Internal.hashLong(
            getUtcTime());
      }
      if (hasPts()) {
        hash = (37 * hash) + PTS_FIELD_NUMBER;
        hash = (53 * hash) + getPts();
      }
      if (hasDts()) {
        hash = (37 * hash) + DTS_FIELD_NUMBER;
        hash = (53 * hash) + getDts();
      }
      if (getH246AudioDataCount() > 0) {
        hash = (37 * hash) + H246_AUDIO_DATA_FIELD_NUMBER;
        hash = (53 * hash) + getH246AudioDataList().hashCode();
      }
      hash = (29 * hash) + unknownFields.hashCode();
      memoizedHashCode = hash;
      return hash;
    }

    public static Nick_Stuff.Protocols.HostAudioPP.data parseFrom(
        java.nio.ByteBuffer data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static Nick_Stuff.Protocols.HostAudioPP.data parseFrom(
        java.nio.ByteBuffer data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static Nick_Stuff.Protocols.HostAudioPP.data parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static Nick_Stuff.Protocols.HostAudioPP.data parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static Nick_Stuff.Protocols.HostAudioPP.data parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static Nick_Stuff.Protocols.HostAudioPP.data parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static Nick_Stuff.Protocols.HostAudioPP.data parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static Nick_Stuff.Protocols.HostAudioPP.data parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }
    public static Nick_Stuff.Protocols.HostAudioPP.data parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input);
    }
    public static Nick_Stuff.Protocols.HostAudioPP.data parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    public static Nick_Stuff.Protocols.HostAudioPP.data parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static Nick_Stuff.Protocols.HostAudioPP.data parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }

    @java.lang.Override
    public Builder newBuilderForType() { return newBuilder(); }
    public static Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
    }
    public static Builder newBuilder(Nick_Stuff.Protocols.HostAudioPP.data prototype) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
    }
    @java.lang.Override
    public Builder toBuilder() {
      return this == DEFAULT_INSTANCE
          ? new Builder() : new Builder().mergeFrom(this);
    }

    @java.lang.Override
    protected Builder newBuilderForType(
        com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
      Builder builder = new Builder(parent);
      return builder;
    }
    /**
     * Protobuf type {@code data}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
        // @@protoc_insertion_point(builder_implements:data)
        Nick_Stuff.Protocols.HostAudioPP.dataOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return Nick_Stuff.Protocols.HostAudioPP.internal_static_data_descriptor;
      }

      @java.lang.Override
      protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
          internalGetFieldAccessorTable() {
        return Nick_Stuff.Protocols.HostAudioPP.internal_static_data_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                Nick_Stuff.Protocols.HostAudioPP.data.class, Nick_Stuff.Protocols.HostAudioPP.data.Builder.class);
      }

      // Construct using Nick_Stuff.Protocols.HostAudioPP.data.newBuilder()
      private Builder() {
        maybeForceBuilderInitialization();
      }

      private Builder(
          com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
        super(parent);
        maybeForceBuilderInitialization();
      }
      private void maybeForceBuilderInitialization() {
        if (com.google.protobuf.GeneratedMessageV3
                .alwaysUseFieldBuilders) {
        }
      }
      @java.lang.Override
      public Builder clear() {
        super.clear();
        protoType_ = 0;
        bitField0_ = (bitField0_ & ~0x00000001);
        protoId_ = 0L;
        bitField0_ = (bitField0_ & ~0x00000002);
        utcTime_ = 0L;
        bitField0_ = (bitField0_ & ~0x00000004);
        pts_ = 0;
        bitField0_ = (bitField0_ & ~0x00000008);
        dts_ = 0;
        bitField0_ = (bitField0_ & ~0x00000010);
        h246AudioData_ = java.util.Collections.emptyList();
        bitField0_ = (bitField0_ & ~0x00000020);
        return this;
      }

      @java.lang.Override
      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return Nick_Stuff.Protocols.HostAudioPP.internal_static_data_descriptor;
      }

      @java.lang.Override
      public Nick_Stuff.Protocols.HostAudioPP.data getDefaultInstanceForType() {
        return Nick_Stuff.Protocols.HostAudioPP.data.getDefaultInstance();
      }

      @java.lang.Override
      public Nick_Stuff.Protocols.HostAudioPP.data build() {
        Nick_Stuff.Protocols.HostAudioPP.data result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      @java.lang.Override
      public Nick_Stuff.Protocols.HostAudioPP.data buildPartial() {
        Nick_Stuff.Protocols.HostAudioPP.data result = new Nick_Stuff.Protocols.HostAudioPP.data(this);
        int from_bitField0_ = bitField0_;
        int to_bitField0_ = 0;
        if (((from_bitField0_ & 0x00000001) == 0x00000001)) {
          to_bitField0_ |= 0x00000001;
        }
        result.protoType_ = protoType_;
        if (((from_bitField0_ & 0x00000002) == 0x00000002)) {
          to_bitField0_ |= 0x00000002;
        }
        result.protoId_ = protoId_;
        if (((from_bitField0_ & 0x00000004) == 0x00000004)) {
          to_bitField0_ |= 0x00000004;
        }
        result.utcTime_ = utcTime_;
        if (((from_bitField0_ & 0x00000008) == 0x00000008)) {
          to_bitField0_ |= 0x00000008;
        }
        result.pts_ = pts_;
        if (((from_bitField0_ & 0x00000010) == 0x00000010)) {
          to_bitField0_ |= 0x00000010;
        }
        result.dts_ = dts_;
        if (((bitField0_ & 0x00000020) == 0x00000020)) {
          h246AudioData_ = java.util.Collections.unmodifiableList(h246AudioData_);
          bitField0_ = (bitField0_ & ~0x00000020);
        }
        result.h246AudioData_ = h246AudioData_;
        result.bitField0_ = to_bitField0_;
        onBuilt();
        return result;
      }

      @java.lang.Override
      public Builder clone() {
        return (Builder) super.clone();
      }
      @java.lang.Override
      public Builder setField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          java.lang.Object value) {
        return (Builder) super.setField(field, value);
      }
      @java.lang.Override
      public Builder clearField(
          com.google.protobuf.Descriptors.FieldDescriptor field) {
        return (Builder) super.clearField(field);
      }
      @java.lang.Override
      public Builder clearOneof(
          com.google.protobuf.Descriptors.OneofDescriptor oneof) {
        return (Builder) super.clearOneof(oneof);
      }
      @java.lang.Override
      public Builder setRepeatedField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          int index, java.lang.Object value) {
        return (Builder) super.setRepeatedField(field, index, value);
      }
      @java.lang.Override
      public Builder addRepeatedField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          java.lang.Object value) {
        return (Builder) super.addRepeatedField(field, value);
      }
      @java.lang.Override
      public Builder mergeFrom(com.google.protobuf.Message other) {
        if (other instanceof Nick_Stuff.Protocols.HostAudioPP.data) {
          return mergeFrom((Nick_Stuff.Protocols.HostAudioPP.data)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(Nick_Stuff.Protocols.HostAudioPP.data other) {
        if (other == Nick_Stuff.Protocols.HostAudioPP.data.getDefaultInstance()) return this;
        if (other.hasProtoType()) {
          setProtoType(other.getProtoType());
        }
        if (other.hasProtoId()) {
          setProtoId(other.getProtoId());
        }
        if (other.hasUtcTime()) {
          setUtcTime(other.getUtcTime());
        }
        if (other.hasPts()) {
          setPts(other.getPts());
        }
        if (other.hasDts()) {
          setDts(other.getDts());
        }
        if (!other.h246AudioData_.isEmpty()) {
          if (h246AudioData_.isEmpty()) {
            h246AudioData_ = other.h246AudioData_;
            bitField0_ = (bitField0_ & ~0x00000020);
          } else {
            ensureH246AudioDataIsMutable();
            h246AudioData_.addAll(other.h246AudioData_);
          }
          onChanged();
        }
        this.mergeUnknownFields(other.unknownFields);
        onChanged();
        return this;
      }

      @java.lang.Override
      public final boolean isInitialized() {
        if (!hasProtoType()) {
          return false;
        }
        if (!hasProtoId()) {
          return false;
        }
        if (!hasUtcTime()) {
          return false;
        }
        if (!hasPts()) {
          return false;
        }
        if (!hasDts()) {
          return false;
        }
        return true;
      }

      @java.lang.Override
      public Builder mergeFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        Nick_Stuff.Protocols.HostAudioPP.data parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (Nick_Stuff.Protocols.HostAudioPP.data) e.getUnfinishedMessage();
          throw e.unwrapIOException();
        } finally {
          if (parsedMessage != null) {
            mergeFrom(parsedMessage);
          }
        }
        return this;
      }
      private int bitField0_;

      private int protoType_ ;
      /**
       * <code>required int32 proto_type = 1;</code>
       */
      public boolean hasProtoType() {
        return ((bitField0_ & 0x00000001) == 0x00000001);
      }
      /**
       * <code>required int32 proto_type = 1;</code>
       */
      public int getProtoType() {
        return protoType_;
      }
      /**
       * <code>required int32 proto_type = 1;</code>
       */
      public Builder setProtoType(int value) {
        bitField0_ |= 0x00000001;
        protoType_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>required int32 proto_type = 1;</code>
       */
      public Builder clearProtoType() {
        bitField0_ = (bitField0_ & ~0x00000001);
        protoType_ = 0;
        onChanged();
        return this;
      }

      private long protoId_ ;
      /**
       * <code>required int64 proto_id = 2;</code>
       */
      public boolean hasProtoId() {
        return ((bitField0_ & 0x00000002) == 0x00000002);
      }
      /**
       * <code>required int64 proto_id = 2;</code>
       */
      public long getProtoId() {
        return protoId_;
      }
      /**
       * <code>required int64 proto_id = 2;</code>
       */
      public Builder setProtoId(long value) {
        bitField0_ |= 0x00000002;
        protoId_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>required int64 proto_id = 2;</code>
       */
      public Builder clearProtoId() {
        bitField0_ = (bitField0_ & ~0x00000002);
        protoId_ = 0L;
        onChanged();
        return this;
      }

      private long utcTime_ ;
      /**
       * <code>required int64 utc_time = 3;</code>
       */
      public boolean hasUtcTime() {
        return ((bitField0_ & 0x00000004) == 0x00000004);
      }
      /**
       * <code>required int64 utc_time = 3;</code>
       */
      public long getUtcTime() {
        return utcTime_;
      }
      /**
       * <code>required int64 utc_time = 3;</code>
       */
      public Builder setUtcTime(long value) {
        bitField0_ |= 0x00000004;
        utcTime_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>required int64 utc_time = 3;</code>
       */
      public Builder clearUtcTime() {
        bitField0_ = (bitField0_ & ~0x00000004);
        utcTime_ = 0L;
        onChanged();
        return this;
      }

      private int pts_ ;
      /**
       * <code>required int32 pts = 4;</code>
       */
      public boolean hasPts() {
        return ((bitField0_ & 0x00000008) == 0x00000008);
      }
      /**
       * <code>required int32 pts = 4;</code>
       */
      public int getPts() {
        return pts_;
      }
      /**
       * <code>required int32 pts = 4;</code>
       */
      public Builder setPts(int value) {
        bitField0_ |= 0x00000008;
        pts_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>required int32 pts = 4;</code>
       */
      public Builder clearPts() {
        bitField0_ = (bitField0_ & ~0x00000008);
        pts_ = 0;
        onChanged();
        return this;
      }

      private int dts_ ;
      /**
       * <code>required int32 dts = 5;</code>
       */
      public boolean hasDts() {
        return ((bitField0_ & 0x00000010) == 0x00000010);
      }
      /**
       * <code>required int32 dts = 5;</code>
       */
      public int getDts() {
        return dts_;
      }
      /**
       * <code>required int32 dts = 5;</code>
       */
      public Builder setDts(int value) {
        bitField0_ |= 0x00000010;
        dts_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>required int32 dts = 5;</code>
       */
      public Builder clearDts() {
        bitField0_ = (bitField0_ & ~0x00000010);
        dts_ = 0;
        onChanged();
        return this;
      }

      private java.util.List<com.google.protobuf.ByteString> h246AudioData_ = java.util.Collections.emptyList();
      private void ensureH246AudioDataIsMutable() {
        if (!((bitField0_ & 0x00000020) == 0x00000020)) {
          h246AudioData_ = new java.util.ArrayList<com.google.protobuf.ByteString>(h246AudioData_);
          bitField0_ |= 0x00000020;
         }
      }
      /**
       * <code>repeated bytes H246_audio_data = 6;</code>
       */
      public java.util.List<com.google.protobuf.ByteString>
          getH246AudioDataList() {
        return java.util.Collections.unmodifiableList(h246AudioData_);
      }
      /**
       * <code>repeated bytes H246_audio_data = 6;</code>
       */
      public int getH246AudioDataCount() {
        return h246AudioData_.size();
      }
      /**
       * <code>repeated bytes H246_audio_data = 6;</code>
       */
      public com.google.protobuf.ByteString getH246AudioData(int index) {
        return h246AudioData_.get(index);
      }
      /**
       * <code>repeated bytes H246_audio_data = 6;</code>
       */
      public Builder setH246AudioData(
          int index, com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  ensureH246AudioDataIsMutable();
        h246AudioData_.set(index, value);
        onChanged();
        return this;
      }
      /**
       * <code>repeated bytes H246_audio_data = 6;</code>
       */
      public Builder addH246AudioData(com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  ensureH246AudioDataIsMutable();
        h246AudioData_.add(value);
        onChanged();
        return this;
      }
      /**
       * <code>repeated bytes H246_audio_data = 6;</code>
       */
      public Builder addAllH246AudioData(
          java.lang.Iterable<? extends com.google.protobuf.ByteString> values) {
        ensureH246AudioDataIsMutable();
        com.google.protobuf.AbstractMessageLite.Builder.addAll(
            values, h246AudioData_);
        onChanged();
        return this;
      }
      /**
       * <code>repeated bytes H246_audio_data = 6;</code>
       */
      public Builder clearH246AudioData() {
        h246AudioData_ = java.util.Collections.emptyList();
        bitField0_ = (bitField0_ & ~0x00000020);
        onChanged();
        return this;
      }
      @java.lang.Override
      public final Builder setUnknownFields(
          final com.google.protobuf.UnknownFieldSet unknownFields) {
        return super.setUnknownFields(unknownFields);
      }

      @java.lang.Override
      public final Builder mergeUnknownFields(
          final com.google.protobuf.UnknownFieldSet unknownFields) {
        return super.mergeUnknownFields(unknownFields);
      }


      // @@protoc_insertion_point(builder_scope:data)
    }

    // @@protoc_insertion_point(class_scope:data)
    private static final Nick_Stuff.Protocols.HostAudioPP.data DEFAULT_INSTANCE;
    static {
      DEFAULT_INSTANCE = new Nick_Stuff.Protocols.HostAudioPP.data();
    }

    public static Nick_Stuff.Protocols.HostAudioPP.data getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }

    @java.lang.Deprecated public static final com.google.protobuf.Parser<data>
        PARSER = new com.google.protobuf.AbstractParser<data>() {
      @java.lang.Override
      public data parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return new data(input, extensionRegistry);
      }
    };

    public static com.google.protobuf.Parser<data> parser() {
      return PARSER;
    }

    @java.lang.Override
    public com.google.protobuf.Parser<data> getParserForType() {
      return PARSER;
    }

    @java.lang.Override
    public Nick_Stuff.Protocols.HostAudioPP.data getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }

  }

  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_data_descriptor;
  private static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_data_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\033Audio Packet Protocol.proto\"q\n\004data\022\022\n" +
      "\nproto_type\030\001 \002(\005\022\020\n\010proto_id\030\002 \002(\003\022\020\n\010u" +
      "tc_time\030\003 \002(\003\022\013\n\003pts\030\004 \002(\005\022\013\n\003dts\030\005 \002(\005\022" +
      "\027\n\017H246_audio_data\030\006 \003(\014B#\n\024Nick_Stuff.P" +
      "rotocolsB\013HostAudioPP"
    };
    com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner assigner =
        new com.google.protobuf.Descriptors.FileDescriptor.    InternalDescriptorAssigner() {
          public com.google.protobuf.ExtensionRegistry assignDescriptors(
              com.google.protobuf.Descriptors.FileDescriptor root) {
            descriptor = root;
            return null;
          }
        };
    com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
        }, assigner);
    internal_static_data_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_data_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_data_descriptor,
        new java.lang.String[] { "ProtoType", "ProtoId", "UtcTime", "Pts", "Dts", "H246AudioData", });
  }

  // @@protoc_insertion_point(outer_class_scope)
}
