// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: Authentication Response Server Protocol.proto
package Nick_Stuff.Protocols;

public final class AuthnRsSP {
  private AuthnRsSP() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  public interface responseOrBuilder extends
      // @@protoc_insertion_point(interface_extends:response)
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
     * <code>required bool response = 4;</code>
     */
    boolean hasResponse();
    /**
     * <code>required bool response = 4;</code>
     */
    boolean getResponse();

    /**
     * <code>optional bool guests = 5 [default = false];</code>
     */
    boolean hasGuests();
    /**
     * <code>optional bool guests = 5 [default = false];</code>
     */
    boolean getGuests();

    /**
     * <code>optional int32 wait_time = 6 [default = 30];</code>
     */
    boolean hasWaitTime();
    /**
     * <code>optional int32 wait_time = 6 [default = 30];</code>
     */
    int getWaitTime();
  }
  /**
   * Protobuf type {@code response}
   */
  public  static final class response extends
      com.google.protobuf.GeneratedMessageV3 implements
      // @@protoc_insertion_point(message_implements:response)
      responseOrBuilder {
  private static final long serialVersionUID = 0L;
    // Use response.newBuilder() to construct.
    private response(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
      super(builder);
    }
    private response() {
      protoType_ = 0;
      protoId_ = 0L;
      utcTime_ = 0L;
      response_ = false;
      guests_ = false;
      waitTime_ = 30;
    }

    @java.lang.Override
    public final com.google.protobuf.UnknownFieldSet
    getUnknownFields() {
      return this.unknownFields;
    }
    private response(
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
              response_ = input.readBool();
              break;
            }
            case 40: {
              bitField0_ |= 0x00000010;
              guests_ = input.readBool();
              break;
            }
            case 48: {
              bitField0_ |= 0x00000020;
              waitTime_ = input.readInt32();
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
        this.unknownFields = unknownFields.build();
        makeExtensionsImmutable();
      }
    }
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return AuthnRsSP.internal_static_response_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return AuthnRsSP.internal_static_response_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              AuthnRsSP.response.class, AuthnRsSP.response.Builder.class);
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

    public static final int RESPONSE_FIELD_NUMBER = 4;
    private boolean response_;
    /**
     * <code>required bool response = 4;</code>
     */
    public boolean hasResponse() {
      return ((bitField0_ & 0x00000008) == 0x00000008);
    }
    /**
     * <code>required bool response = 4;</code>
     */
    public boolean getResponse() {
      return response_;
    }

    public static final int GUESTS_FIELD_NUMBER = 5;
    private boolean guests_;
    /**
     * <code>optional bool guests = 5 [default = false];</code>
     */
    public boolean hasGuests() {
      return ((bitField0_ & 0x00000010) == 0x00000010);
    }
    /**
     * <code>optional bool guests = 5 [default = false];</code>
     */
    public boolean getGuests() {
      return guests_;
    }

    public static final int WAIT_TIME_FIELD_NUMBER = 6;
    private int waitTime_;
    /**
     * <code>optional int32 wait_time = 6 [default = 30];</code>
     */
    public boolean hasWaitTime() {
      return ((bitField0_ & 0x00000020) == 0x00000020);
    }
    /**
     * <code>optional int32 wait_time = 6 [default = 30];</code>
     */
    public int getWaitTime() {
      return waitTime_;
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
      if (!hasResponse()) {
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
        output.writeBool(4, response_);
      }
      if (((bitField0_ & 0x00000010) == 0x00000010)) {
        output.writeBool(5, guests_);
      }
      if (((bitField0_ & 0x00000020) == 0x00000020)) {
        output.writeInt32(6, waitTime_);
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
          .computeBoolSize(4, response_);
      }
      if (((bitField0_ & 0x00000010) == 0x00000010)) {
        size += com.google.protobuf.CodedOutputStream
          .computeBoolSize(5, guests_);
      }
      if (((bitField0_ & 0x00000020) == 0x00000020)) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt32Size(6, waitTime_);
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
      if (!(obj instanceof AuthnRsSP.response)) {
        return super.equals(obj);
      }
      AuthnRsSP.response other = (AuthnRsSP.response) obj;

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
      result = result && (hasResponse() == other.hasResponse());
      if (hasResponse()) {
        result = result && (getResponse()
            == other.getResponse());
      }
      result = result && (hasGuests() == other.hasGuests());
      if (hasGuests()) {
        result = result && (getGuests()
            == other.getGuests());
      }
      result = result && (hasWaitTime() == other.hasWaitTime());
      if (hasWaitTime()) {
        result = result && (getWaitTime()
            == other.getWaitTime());
      }
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
      if (hasResponse()) {
        hash = (37 * hash) + RESPONSE_FIELD_NUMBER;
        hash = (53 * hash) + com.google.protobuf.Internal.hashBoolean(
            getResponse());
      }
      if (hasGuests()) {
        hash = (37 * hash) + GUESTS_FIELD_NUMBER;
        hash = (53 * hash) + com.google.protobuf.Internal.hashBoolean(
            getGuests());
      }
      if (hasWaitTime()) {
        hash = (37 * hash) + WAIT_TIME_FIELD_NUMBER;
        hash = (53 * hash) + getWaitTime();
      }
      hash = (29 * hash) + unknownFields.hashCode();
      memoizedHashCode = hash;
      return hash;
    }

    public static AuthnRsSP.response parseFrom(
        java.nio.ByteBuffer data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static AuthnRsSP.response parseFrom(
        java.nio.ByteBuffer data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static AuthnRsSP.response parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static AuthnRsSP.response parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static AuthnRsSP.response parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static AuthnRsSP.response parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static AuthnRsSP.response parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static AuthnRsSP.response parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }
    public static AuthnRsSP.response parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input);
    }
    public static AuthnRsSP.response parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    public static AuthnRsSP.response parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static AuthnRsSP.response parseFrom(
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
    public static Builder newBuilder(AuthnRsSP.response prototype) {
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
     * Protobuf type {@code response}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
        // @@protoc_insertion_point(builder_implements:response)
        AuthnRsSP.responseOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return AuthnRsSP.internal_static_response_descriptor;
      }

      @java.lang.Override
      protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
          internalGetFieldAccessorTable() {
        return AuthnRsSP.internal_static_response_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                AuthnRsSP.response.class, AuthnRsSP.response.Builder.class);
      }

      // Construct using AuthnRsSP.response.newBuilder()
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
        response_ = false;
        bitField0_ = (bitField0_ & ~0x00000008);
        guests_ = false;
        bitField0_ = (bitField0_ & ~0x00000010);
        waitTime_ = 30;
        bitField0_ = (bitField0_ & ~0x00000020);
        return this;
      }

      @java.lang.Override
      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return AuthnRsSP.internal_static_response_descriptor;
      }

      @java.lang.Override
      public AuthnRsSP.response getDefaultInstanceForType() {
        return AuthnRsSP.response.getDefaultInstance();
      }

      @java.lang.Override
      public AuthnRsSP.response build() {
        AuthnRsSP.response result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      @java.lang.Override
      public AuthnRsSP.response buildPartial() {
        AuthnRsSP.response result = new AuthnRsSP.response(this);
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
        result.response_ = response_;
        if (((from_bitField0_ & 0x00000010) == 0x00000010)) {
          to_bitField0_ |= 0x00000010;
        }
        result.guests_ = guests_;
        if (((from_bitField0_ & 0x00000020) == 0x00000020)) {
          to_bitField0_ |= 0x00000020;
        }
        result.waitTime_ = waitTime_;
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
        if (other instanceof AuthnRsSP.response) {
          return mergeFrom((AuthnRsSP.response)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(AuthnRsSP.response other) {
        if (other == AuthnRsSP.response.getDefaultInstance()) return this;
        if (other.hasProtoType()) {
          setProtoType(other.getProtoType());
        }
        if (other.hasProtoId()) {
          setProtoId(other.getProtoId());
        }
        if (other.hasUtcTime()) {
          setUtcTime(other.getUtcTime());
        }
        if (other.hasResponse()) {
          setResponse(other.getResponse());
        }
        if (other.hasGuests()) {
          setGuests(other.getGuests());
        }
        if (other.hasWaitTime()) {
          setWaitTime(other.getWaitTime());
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
        if (!hasResponse()) {
          return false;
        }
        return true;
      }

      @java.lang.Override
      public Builder mergeFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        AuthnRsSP.response parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (AuthnRsSP.response) e.getUnfinishedMessage();
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

      private boolean response_ ;
      /**
       * <code>required bool response = 4;</code>
       */
      public boolean hasResponse() {
        return ((bitField0_ & 0x00000008) == 0x00000008);
      }
      /**
       * <code>required bool response = 4;</code>
       */
      public boolean getResponse() {
        return response_;
      }
      /**
       * <code>required bool response = 4;</code>
       */
      public Builder setResponse(boolean value) {
        bitField0_ |= 0x00000008;
        response_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>required bool response = 4;</code>
       */
      public Builder clearResponse() {
        bitField0_ = (bitField0_ & ~0x00000008);
        response_ = false;
        onChanged();
        return this;
      }

      private boolean guests_ ;
      /**
       * <code>optional bool guests = 5 [default = false];</code>
       */
      public boolean hasGuests() {
        return ((bitField0_ & 0x00000010) == 0x00000010);
      }
      /**
       * <code>optional bool guests = 5 [default = false];</code>
       */
      public boolean getGuests() {
        return guests_;
      }
      /**
       * <code>optional bool guests = 5 [default = false];</code>
       */
      public Builder setGuests(boolean value) {
        bitField0_ |= 0x00000010;
        guests_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>optional bool guests = 5 [default = false];</code>
       */
      public Builder clearGuests() {
        bitField0_ = (bitField0_ & ~0x00000010);
        guests_ = false;
        onChanged();
        return this;
      }

      private int waitTime_ = 30;
      /**
       * <code>optional int32 wait_time = 6 [default = 30];</code>
       */
      public boolean hasWaitTime() {
        return ((bitField0_ & 0x00000020) == 0x00000020);
      }
      /**
       * <code>optional int32 wait_time = 6 [default = 30];</code>
       */
      public int getWaitTime() {
        return waitTime_;
      }
      /**
       * <code>optional int32 wait_time = 6 [default = 30];</code>
       */
      public Builder setWaitTime(int value) {
        bitField0_ |= 0x00000020;
        waitTime_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>optional int32 wait_time = 6 [default = 30];</code>
       */
      public Builder clearWaitTime() {
        bitField0_ = (bitField0_ & ~0x00000020);
        waitTime_ = 30;
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


      // @@protoc_insertion_point(builder_scope:response)
    }

    // @@protoc_insertion_point(class_scope:response)
    private static final AuthnRsSP.response DEFAULT_INSTANCE;
    static {
      DEFAULT_INSTANCE = new AuthnRsSP.response();
    }

    public static AuthnRsSP.response getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }

    @java.lang.Deprecated public static final com.google.protobuf.Parser<response>
        PARSER = new com.google.protobuf.AbstractParser<response>() {
      @java.lang.Override
      public response parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return new response(input, extensionRegistry);
      }
    };

    public static com.google.protobuf.Parser<response> parser() {
      return PARSER;
    }

    @java.lang.Override
    public com.google.protobuf.Parser<response> getParserForType() {
      return PARSER;
    }

    @java.lang.Override
    public AuthnRsSP.response getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }

  }

  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_response_descriptor;
  private static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_response_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n-Authentication Response Server Protoco" +
      "l.proto\"\202\001\n\010response\022\022\n\nproto_type\030\001 \002(\005" +
      "\022\020\n\010proto_id\030\002 \002(\003\022\020\n\010utc_time\030\003 \002(\003\022\020\n\010" +
      "response\030\004 \002(\010\022\025\n\006guests\030\005 \001(\010:\005false\022\025\n" +
      "\twait_time\030\006 \001(\005:\00230B\013B\tAuthnRsSP"
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
    internal_static_response_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_response_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_response_descriptor,
        new java.lang.String[] { "ProtoType", "ProtoId", "UtcTime", "Response", "Guests", "WaitTime", });
  }

  // @@protoc_insertion_point(outer_class_scope)
}
