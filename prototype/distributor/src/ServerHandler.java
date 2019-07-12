package prototype.distributor;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.sun.org.apache.xpath.internal.SourceTree;
import io.netty.buffer.ByteBuf;
// import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import prototype.networkProtocol.MessageManager;
import prototype.protobuf.Control;
import prototype.protobuf.General;

public class ServerHandler extends ChannelInboundHandlerAdapter {
    private ByteBuf buf;
    private HashMap<ChannelHandlerContext, MessageManager> managers = new HashMap<ChannelHandlerContext, MessageManager>();
    // private Control.UserList userList = Control.UserList.newBuilder().build();

    private Control.UserList.Builder userListBuilder = Control.UserList.newBuilder();
    private HashMap<String, ChannelHandlerContext> userChannels = new HashMap<String, ChannelHandlerContext>();
    // channel only was messed up for me

    public void handlerAdded(ChannelHandlerContext ctx) {
        buf = ctx.alloc().buffer();
        MessageManager manager = new MessageManager();
        managers.put(ctx, manager);
    }

    public void handlerRemoved(ChannelHandlerContext ctx) {
        buf.release();
        buf = null;
        managers.remove(ctx);
    }

    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        System.out.println("Message Recieved");

        ByteBuf m = (ByteBuf) msg;
        byte[] x = new byte[m.readableBytes()];
        m.readBytes(x);
        managers.get(ctx).parseData(x);
        m.release();
        System.out.println("Packet of len " + x.length + " received");

        if (managers.get(ctx).hasPacket()) {
            System.out.println("Packet received");
            byte[] data = managers.get(ctx).nextPacket();

            try {
                General.FvPacket outer_packet = General.FvPacket.parseFrom(data);
                General.FvPacketType type = outer_packet.getType();

                System.out.println(type);
                switch (type) {
                case UNSPECIFIED:
                    System.out.println("Unspecified packet");
                    break;
                case HANDSHAKE:
                    Control.Handshake hs = Control.Handshake.parseFrom(outer_packet.getInnerPacket());
                    System.out.println("Handshake value: " + hs.getMagicNumber());
                    break;
                case NEW_USER:
                    Control.NewUser user_packet = Control.NewUser.parseFrom(outer_packet.getInnerPacket());

                    System.out.println("New User requested name: " + user_packet.getUsername());
                    if (!userListBuilder.getUsersList().contains(user_packet.getUsername())) {
                        userListBuilder.addUsers(user_packet.getUsername());

                        // add channel to list

                        userChannels.put(user_packet.getUsername(), ctx);

                        // send user success

                        // set server message
                        Control.ServerMessage reply = Control.ServerMessage.newBuilder()
                                .setType(Control.ServerMessageType.SUCCESS).setSuccess(true).build();

                        // set outer packet
                        General.FvPacket replyOuter = General.FvPacket.newBuilder()
                                .setType(General.FvPacketType.SERVER_MESSAGE)
                                .setInnerPacket(ByteString.copyFrom(reply.toByteArray())).build();

                        // send outer packet
                        byte[] bytes = replyOuter.toByteArray();
                        ByteBuf buf = ctx.alloc().buffer(bytes.length);
                        buf.writeBytes(bytes);
                        ctx.writeAndFlush(buf);

                        // send user list to all users
                        General.FvPacket listOuter = General.FvPacket.newBuilder()
                                .setType(General.FvPacketType.USER_LIST)
                                .setInnerPacket(ByteString.copyFrom(userListBuilder.build().toByteArray())).build();

                        bytes = listOuter.toByteArray();
                        buf = ctx.alloc().buffer(bytes.length); // hoping this works for all channels
                        buf.writeBytes(bytes);
                        ctx.writeAndFlush(buf);

                        for (Map.Entry<String, ChannelHandlerContext> entry : userChannels.entrySet()) {
                            entry.getValue().writeAndFlush(buf);
                        }
                    } else {
                        // send new user failure

                        // set server message
                        Control.ServerMessage reply = Control.ServerMessage.newBuilder()
                                .setType(Control.ServerMessageType.NAME_TAKEN).setSuccess(false).build();

                        // set outer packet
                        General.FvPacket replyOuter = General.FvPacket.newBuilder()
                                .setType(General.FvPacketType.SERVER_MESSAGE)
                                .setInnerPacket(ByteString.copyFrom(reply.toByteArray())).build();

                        // send outer packet
                        byte[] bytes = replyOuter.toByteArray();
                        ByteBuf buf = ctx.alloc().buffer(bytes.length);
                        buf.writeBytes(bytes);
                        ctx.writeAndFlush(buf);
                    }

                    break;
                case SESSION_REQUEST:
                    System.out.println("Session Request packet");

                    break;
                case SESSION_CLOSE:
                    System.out.println("Session Close packet");

                    // System.out.println("Reason: " + );

                    break;
                case HEARTBEAT:
                    System.out.println("Heartbeat received" + type.getNumber());

                    Control.Heartbeat hb = Control.Heartbeat.parseFrom(outer_packet.getInnerPacket());

                    System.out.println("Timestamp: " + hb.getUtcTime());

                    // should mark heartbeats elsewhere

                    // reply
                    Control.Heartbeat heartbeat = Control.Heartbeat.newBuilder().setUtcTime(new Date().getTime())
                            .build();

                    General.FvPacket replyOuter = General.FvPacket.newBuilder().setType(General.FvPacketType.HEARTBEAT)
                            .setInnerPacket(ByteString.copyFrom(heartbeat.toByteArray())).build();

                    byte[] bytes = replyOuter.toByteArray();
                    ByteBuf buf = ctx.alloc().buffer(bytes.length);
                    buf.writeBytes(bytes);
                    ctx.writeAndFlush(buf);

                    break;
                default:
                    System.out.println("Protocol not defined");
                    break;
                }

            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        }
    }

    public void channelActive(final ChannelHandlerContext ctx) {
    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
