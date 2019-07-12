package prototype.distributor;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.ByteString;
import com.google.protobuf.Message;
import com.sun.org.apache.xpath.internal.SourceTree;

import io.netty.buffer.ByteBuf;
//import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import prototype.protobuf.Control;
import prototype.protobuf.General;
import prototype.networkProtocol.MessageManager;

import java.util.Map;
import java.util.HashMap;
import java.util.Date;

public class ServerHandler extends ChannelInboundHandlerAdapter {

    private ByteBuf buf;
    private HashMap<ChannelHandlerContext, MessageManager> managers = new HashMap<ChannelHandlerContext, MessageManager>();
    //private Control.UserList userList = Control.UserList.newBuilder().build();

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

        if (managers.get(ctx).hasPacket()) {
            byte[] data = managers.get(ctx).nextPacket();

            try {
                General.FvPacket outer_packet = General.FvPacket.parseFrom(data);
                General.FvPacketType type = outer_packet.getType();

                Control.ServerMessage innerPacket = Control.ServerMessage.parseFrom(outer_packet.getInnerPacket());

                System.out.println(type);
                switch (type) {
                case UNSPECIFIED:
                    System.out.println("Unspecified: " + type.getNumber());


                    System.out.println(innerPacket.getMessage());
                    break;
                case HANDSHAKE:
                    System.out.println("Handshake: " + type.getNumber());
                    break;
                case NEW_USER:
                    System.out.println("New User: " + type.getNumber());

                    if(!userListBuilder.getUsersList().contains(innerPacket.getMessage())){
                        userListBuilder.addUsers(innerPacket.getMessage());

                        //add channel to list

                        userChannels.put(innerPacket.getMessage(), ctx);

                        // send user success

                        // set server message
                        Control.ServerMessage reply = Control.ServerMessage.newBuilder()
                        .setType(Control.ServerMessageType.SUCCESS)
                        .setSuccess(true)
                        .build();

                        // set outer packet
                        General.FvPacket replyOuter =  General.FvPacket.newBuilder()
                        .setType(General.FvPacketType.SERVER_MESSAGE)
                        .setInnerPacket(ByteString.copyFrom(reply.toByteArray()))
                        .build();
                        
                        // send outer packet
                        byte[] bytes = replyOuter.toByteArray();
                        ByteBuf buf = ctx.alloc().buffer(bytes.length);
                        buf.writeBytes(bytes);
                        ctx.writeAndFlush(buf);

                        // send user list to all users
                        General.FvPacket listOuter = General.FvPacket.newBuilder()
                        .setType(General.FvPacketType.USER_LIST)
                        .setInnerPacket(ByteString.copyFrom(userListBuilder.build().toByteArray()))
                        .build();
                        
                        bytes = listOuter.toByteArray();
                        buf = ctx.alloc().buffer(bytes.length); // hoping this works for all channels
                        buf.writeBytes(bytes);
                        ctx.writeAndFlush(buf);

                        for (Map.Entry<String, ChannelHandlerContext> entry : userChannels.entrySet()) {
                            entry.getValue().writeAndFlush(buf);
                        }
                    }else{
                        // send new user failure
                        
                        // set server message
                        Control.ServerMessage reply = Control.ServerMessage.newBuilder()
                        .setType(Control.ServerMessageType.NAME_TAKEN)
                        .setSuccess(false)
                        .build();

                        // set outer packet
                        General.FvPacket replyOuter = General.FvPacket.newBuilder()
                        .setType(General.FvPacketType.SERVER_MESSAGE)
                        .setInnerPacket(ByteString.copyFrom(reply.toByteArray()))
                        .build();

                        // send outer packet
                        byte[] bytes = replyOuter.toByteArray();
                        ByteBuf buf = ctx.alloc().buffer(bytes.length);
                        buf.writeBytes(bytes);
                        ctx.writeAndFlush(buf);
                    }

                    break;
                case SESSION_REQUEST:
                    System.out.println("Session Request: " + type.getNumber());

                    break;
                case SESSION_CLOSE:
                    System.out.println("Session Close: " + type.getNumber());

                    System.out.println("Reason: " + innerPacket.getMessage());

                    break;
                case HEARTBEAT:
                    System.out.println("Heartbeat: " + type.getNumber());

                    System.out.println("Time: " + innerPacket.getMessage());

                    //should mark heartbeats elsewhere

                    //reply
                    Control.Heartbeat heartbeat = Control.Heartbeat.newBuilder()
                    .setUtcTime(new Date().getTime())
                    .build();

                    General.FvPacket replyOuter = General.FvPacket.newBuilder()
                    .setType(General.FvPacketType.HEARTBEAT)
                    .setInnerPacket(ByteString.copyFrom(heartbeat.toByteArray()))
                    .build();

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
                System.out.println("Invalid Protocol");
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
