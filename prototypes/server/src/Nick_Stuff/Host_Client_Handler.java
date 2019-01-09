package Nick_Stuff;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

import java.util.Date;

public class Host_Client_Handler extends ChannelInboundHandlerAdapter {

    public int counter = 0;
    public ChannelHandlerContext context = null;

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        context = ctx;
    }

    public void initiateConvo(int x){
        final ByteBuf time = context.alloc().buffer(4);
        time.writeInt(x);

        final ChannelFuture f = context.writeAndFlush(time);
    }

    public void sendString(String x){
        byte[] bytes = x.getBytes();
        final ByteBuf text = context.alloc().buffer(bytes.length);
        text.writeBytes(bytes);
        context.writeAndFlush(text);
    }

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
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

}
