package prototype.distributor;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import prototype.distributor.UsersData;
import prototype.networkProtocol.MessageManager;
import prototype.protobuf.Control;
import prototype.protobuf.General;

public class ServerHandler extends ChannelInboundHandlerAdapter {
    private Channel channel;
    private String selfName = "";
    private MessageManager mgr = new MessageManager();
    private long lastHeartbeat;
    // TODO: THIS
    private Thread heartbeatThread;
    private boolean running = true;

    private synchronized long getLastHeartbeat() {
        return lastHeartbeat;
    }

    private synchronized void setLastHeartbeat(long hb) {
        lastHeartbeat = hb;
    }

    public void sendProto(Message proto) {
        byte[] bytes = proto.toByteArray();
        ByteBuf buf = channel.alloc().buffer(bytes.length + 4);
        buf.writeIntLE(bytes.length);
        buf.writeBytes(bytes);
        channel.writeAndFlush(buf);
    }

    public void sendProto(Message proto, Channel c) {
        byte[] bytes = proto.toByteArray();
        ByteBuf buf = channel.alloc().buffer(bytes.length + 4);
        buf.writeIntLE(bytes.length);
        buf.writeBytes(bytes);
        c.writeAndFlush(buf);
    }

    public boolean onHandshake(Control.Handshake handshake_packet) {
        if (handshake_packet.getMagicNumber() != 0x12341234ABCDABCDl) {
            return false;
        }
        Control.Handshake reply =
            Control.Handshake.newBuilder().setMagicNumber(0xABCDABCABA123456l).build();

        // set outer packet
        General.FvPacket replyOuter = General.FvPacket.newBuilder()
                                          .setType(General.FvPacketType.HANDSHAKE)
                                          .setInnerPacket(ByteString.copyFrom(reply.toByteArray()))
                                          .build();
        sendProto(replyOuter);
        return true;
    }

    public void onNewUser(Control.NewUser user_packet) {
        if (UsersData.getUser(user_packet.getUsername()) == null) {
            UsersData.addUser(user_packet.getUsername(), channel);
            Control.ServerMessage reply = Control.ServerMessage.newBuilder()
                                              .setType(Control.ServerMessageType.SUCCESS)
                                              .setSuccess(true)
                                              .build();
            General.FvPacket replyOuter =
                General.FvPacket.newBuilder()
                    .setType(General.FvPacketType.SERVER_MESSAGE)
                    .setInnerPacket(ByteString.copyFrom(reply.toByteArray()))
                    .build();
            sendProto(replyOuter);
            Control.UserList userListProto =
                Control.UserList.newBuilder().addAllUsers(UsersData.keySet()).build();
            General.FvPacket listOuter =
                General.FvPacket.newBuilder()
                    .setType(General.FvPacketType.USER_LIST)
                    .setInnerPacket(ByteString.copyFrom(userListProto.toByteArray()))
                    .build();

            for (Channel target : UsersData.values()) {
                sendProto(listOuter, target);
            }
            selfName = user_packet.getUsername();
        } else {
            Control.ServerMessage reply = Control.ServerMessage.newBuilder()
                                              .setType(Control.ServerMessageType.NAME_TAKEN)
                                              .setSuccess(false)
                                              .build();
            General.FvPacket replyOuter =
                General.FvPacket.newBuilder()
                    .setType(General.FvPacketType.SERVER_MESSAGE)
                    .setInnerPacket(ByteString.copyFrom(reply.toByteArray()))
                    .build();
            sendProto(replyOuter);
        }
    }

    public void onHeartbeat(Control.Heartbeat heartbeat_in) {
        lastHeartbeat = new Date().getTime();
        Control.Heartbeat heartbeat =
            Control.Heartbeat.newBuilder().setUtcTime(lastHeartbeat).build();

        General.FvPacket replyOuter =
            General.FvPacket.newBuilder()
                .setType(General.FvPacketType.HEARTBEAT)
                .setInnerPacket(ByteString.copyFrom(heartbeat.toByteArray()))
                .build();

        sendProto(replyOuter);
    }

    public void handlePacket(byte[] data) throws InvalidProtocolBufferException {
        General.FvPacket outer_packet = General.FvPacket.parseFrom(data);
        General.FvPacketType type = outer_packet.getType();

        switch (type) {
            case UNSPECIFIED:
                System.out.println("Unspecified packet");
                break;
            case HANDSHAKE:
                Control.Handshake hs = Control.Handshake.parseFrom(outer_packet.getInnerPacket());
                System.out.println("Handshake value: " + hs.getMagicNumber());
                if (!onHandshake(hs)) {
                    channel.close();
                }
                break;
            case NEW_USER:
                Control.NewUser user_packet =
                    Control.NewUser.parseFrom(outer_packet.getInnerPacket());
                System.out.println("New User requested name: " + user_packet.getUsername());
                onNewUser(user_packet);
                break;
            case SESSION_REQUEST:
                System.out.println("Session Request packet");
                break;
            case SESSION_CLOSE:
                System.out.println("Session Close packet");
                break;
            case HEARTBEAT:
                System.out.println("Heartbeat packet");
                Control.Heartbeat hb = Control.Heartbeat.parseFrom(outer_packet.getInnerPacket());
                System.out.println("Timestamp: " + hb.getUtcTime());
                onHeartbeat(hb);
                break;
            default:
                System.out.println("Protocol not defined");
                break;
        }
    }

    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        System.out.println("Message Recieved");
        ByteBuf buffer = (ByteBuf) msg;
        byte[] raw_packet = new byte[buffer.readableBytes()];
        buffer.readBytes(raw_packet);
        mgr.parseData(raw_packet);
        buffer.release();

        if (mgr.hasPacket()) {
            try {
                handlePacket(mgr.nextPacket());
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        }
    }

    public void channelActive(final ChannelHandlerContext ctx) {
        channel = ctx.channel();
        lastHeartbeat = (new Date()).getTime();
        heartbeatThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (running) {
                    long timeMs = (new Date()).getTime();
                    if (timeMs - getLastHeartbeat() > 7000) {
                        removeSelf();
                        channel.close();
                        System.out.println("Heartbeat OOD closing");
                        return;
                    } else {
                        System.out.println("Heartbeat ok");
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (Exception e) {
                    }
                }
            }
        });
        heartbeatThread.start();
    }

    private void removeSelf() {
        running = false;
        if (selfName.equals("")) {
            return;
        }
        UsersData.remove(selfName);
    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        removeSelf();
        cause.printStackTrace();
        ctx.close();
    }
}
