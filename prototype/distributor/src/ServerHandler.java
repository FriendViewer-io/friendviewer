package prototype.distributor;
import com.google.protobuf.InvalidProtocolBufferException;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import prototype.protobuf.Control;
import prototype.protobuf.General;
import prototype.network_protocol.MessageManager;

import java.util.HashMap;

public class ServerHandler extends ChannelInboundHandlerAdapter {

    private ByteBuf buf;
    private HashMap<ChannelHandlerContext, MessageManager> managers = new HashMap<ChannelHandlerContext, MessageManager>();

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

        if (managers.get(ctx).hasPacket()){
            byte[] data = managers.get(ctx).nextPacket();

            try {
                General.FvPacket outer_packet = General.FvPacket.parseFrom(data);
                General.FvPacketType type = outer_packet.getType();

                System.out.println(type);
                switch (type){
                    case UNSPECIFIED:
                        System.out.println("Unspecified: " + type.getNumber());
                        Control.ServerMessage inner_packer = Control.ServerMessage.parseFrom(outer_packet.getInnerPacket());
                        System.out.println(inner_packer.getMessage());
                        break;
                    case HANDSHAKE:
                        System.out.println("Handshake: " + type.getNumber());
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
