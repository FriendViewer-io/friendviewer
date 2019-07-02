import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class ServerHandler extends ChannelInboundHandlerAdapter {

    // called when message recieved
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("message recieved");

        ByteBuf byteBuf = ctx.alloc().buffer(4);

        byteBuf.writeInt(0);

        ctx.writeAndFlush(byteBuf);
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        System.out.println("Handler added");
    }

    // called when connection is started
    @Override
    public void channelActive(final ChannelHandlerContext ctx) {
        System.out.println("server active");

        ByteBuf byteBuf = ctx.alloc().buffer(4);

        byteBuf.writeInt(0);

        ctx.writeAndFlush(byteBuf);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // Close the connection when an exception is raised.
        cause.printStackTrace();
        ctx.close();
    }
}