package prototype.distributor;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.Scanner;

public class ClientHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
    }

    @Override
    public void channelActive(final ChannelHandlerContext ctx) {
        System.out.println("input text: ");
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
