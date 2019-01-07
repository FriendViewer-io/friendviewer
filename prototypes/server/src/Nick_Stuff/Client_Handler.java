package Nick_Stuff;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

import java.util.Date;

public class Client_Handler extends ChannelInboundHandlerAdapter {

    public int counter = 0;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf m = (ByteBuf) msg; // (1)
        int received = 0;
        try {
            //long currentTimeMillis = (m.readUnsignedInt() - 2208988800L) * 1000L;
            //System.out.println(new Date(currentTimeMillis));
            received = m.readInt();
            System.out.println(received);
        } finally {
            m.release();
        }
        final ByteBuf time = ctx.alloc().buffer(4); // (2)
        time.writeInt((int) (received + 1));

        final ChannelFuture f = ctx.writeAndFlush(time); // (3)
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

}
