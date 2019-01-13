package Nick_Stuff;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

import java.nio.charset.Charset;
import java.util.Date;

public class Client_Handler extends ChannelInboundHandlerAdapter {

    public int counter = 0;
    public ChannelHandlerContext context = null;

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        context = ctx;
    }

    public void initiateConvo(int x){
        final ByteBuf time = context.alloc().buffer(4); // (2)
        time.writeInt(x);

        final ChannelFuture f = context.writeAndFlush(time); // (3)
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf m = (ByteBuf) msg; // (1)

        String received = "";
        received = m.toString(Charset.forName("utf-8"));

        System.out.println(received);
        m.release();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

}
