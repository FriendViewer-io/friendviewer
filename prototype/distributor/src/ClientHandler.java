package prototype.distributor;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import network_protocol.MessageManager;
import prototype.protobuf.Control;
import prototype.protobuf.General;

import java.util.Date;

public class ClientHandler extends ChannelInboundHandlerAdapter {

    private Channel channel;
    private String selfName = "";
    private MessageManager mgr = new MessageManager();
    private long lastHeartbeat;
    // TODO: THIS
    private Thread heartbeatThread;
    private boolean running = true;

    public void sendProto(ByteString inner, General.FvPacketType type) {
        //Create outer packet
        General.FvPacket outerPacket = General.FvPacket.newBuilder()
                .setType(type)
                .setInnerPacket(inner)
                .build();

        byte[] bytes = outerPacket.toByteArray();
        ByteBuf buf = channel.alloc().buffer(bytes.length + 4);
        buf.writeIntLE(bytes.length);
        buf.writeBytes(bytes);
        channel.writeAndFlush(buf);

    }

    public void sendHeartbeat() {
        lastHeartbeat = new Date().getTime();
        Control.Heartbeat heartbeat =
                Control.Heartbeat.newBuilder().setUtcTime(lastHeartbeat).build();

        sendProto(ByteString.copyFrom(heartbeat.toByteArray()), General.FvPacketType.HEARTBEAT);
    }

    public void handlePacket(byte[] data) throws InvalidProtocolBufferException {
        General.FvPacket outer_packet = General.FvPacket.parseFrom(data);
        General.FvPacketType type = outer_packet.getType();

        switch (type) {
            case UNSPECIFIED:
                System.out.println("Unspecified packet");
                break;
            case SERVER_MESSAGE:
                Control.ServerMessage inner = Control.ServerMessage.parseFrom(outer_packet.getInnerPacket());
                System.out.println(inner.getSuccess());
                System.out.println(inner.getType());
            default:
                break;
        }
    }

    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf buffer = (ByteBuf) msg;
        byte[] raw_packet = new byte[buffer.readableBytes()];
        //System.out.println("Received raw packet size " + raw_packet.length);
        buffer.readBytes(raw_packet);
        mgr.parseData(raw_packet);
        buffer.release();

        while (mgr.hasPacket()) {
            try {
                handlePacket(mgr.nextPacket());
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        }
    }

    //Sends a Heartbeat packet
    public void channelActive(final ChannelHandlerContext ctx) {
        channel = ctx.channel();

        lastHeartbeat = (new Date()).getTime();
        heartbeatThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (running) {
                    try {
                        Thread.sleep(3000);
                    } catch (Exception e) { }
                    sendHeartbeat();
                }
            }
        });
        heartbeatThread.start();

        Control.Login.Builder login = Control.Login.newBuilder();


        //Login packet 1            (Success)
        login.setNewUser(true);
        login.setUsername("User1");
        login.setPassword("password");
        sendProto(login.build().toByteString(), General.FvPacketType.LOGIN);
        //Resend the same packet    (Failure)
        sendProto(login.build().toByteString(), General.FvPacketType.LOGIN);
        //Resend as a genuine login request (Should still fail, but not bc of new code)
        login.setNewUser(false);
        sendProto(login.build().toByteString(), General.FvPacketType.LOGIN);

        //Login packet 2 (Success)
        login.setNewUser(true);
        login.setUsername("User2");
        login.setPassword("ALLAHU AKBAR");
        sendProto(login.build().toByteString(), General.FvPacketType.LOGIN);

        //Login packet 3 (Failure)
        login.setNewUser(false);
        login.setUsername("Saaapling");
        login.setPassword("summer");
        sendProto(login.build().toByteString(), General.FvPacketType.LOGIN);

        //Login packet 4 (Success)
        login.setNewUser(true);
        login.setUsername("Saaapling");
        login.setPassword("summer");
        sendProto(login.build().toByteString(), General.FvPacketType.LOGIN);

        //Login packet 5 (Failure)
        login.setNewUser(true);
        login.setUsername("Saaapling");
        login.setPassword("AHHHaaaAAAHHHhhhAAA");
        sendProto(login.build().toByteString(), General.FvPacketType.LOGIN);

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}