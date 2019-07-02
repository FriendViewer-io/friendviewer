import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

import java.nio.charset.Charset;
import java.util.Date;
import java.util.Scanner;

public class ClientHandler extends ChannelInboundHandlerAdapter {

    /*public int counter = 0;
    public ChannelHandlerContext context = null;

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        context = ctx;
    }
    
    public void initiateConvo(int x){
        final ByteBuf time = context.alloc().buffer(4); // (2)
        time.writeInt(x);

        final ChannelFuture f = context.writeAndFlush(time); // (3)
    }*/

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("client recieved");//debug code

        String reply = scanner.nextLine();

        byte[] bytes = reply.getBytes();
        ByteBuf text = ctx.alloc().buffer(bytes.length);
        text.writeBytes(bytes);
        ctx.writeAndFlush(text);

        //text.release();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}