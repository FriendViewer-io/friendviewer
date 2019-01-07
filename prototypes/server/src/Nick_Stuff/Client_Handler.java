package Nick_Stuff;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

import java.util.Date;

public class Client_Handler extends ChannelInboundHandlerAdapter {

    public int counter = 0;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf m = (ByteBuf) msg; // (1)
        try {
            //long currentTimeMillis = (m.readUnsignedInt() - 2208988800L) * 1000L;
            //System.out.println(new Date(currentTimeMillis));
            System.out.println(m.readInt());
        } finally {
            m.release();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

}
