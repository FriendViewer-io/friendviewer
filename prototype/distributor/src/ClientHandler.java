import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

import java.nio.charset.Charset;
import java.util.Date;
import java.util.Scanner;

public class ClientHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        Scanner scanner = new Scanner(System.in);
        String reply = scanner.nextLine();

        byte[] bytes = reply.getBytes();
        ByteBuf text = ctx.alloc().buffer(bytes.length);
        text.writeBytes(bytes);
        ctx.writeAndFlush(text);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}