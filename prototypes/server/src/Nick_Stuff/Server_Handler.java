package Nick_Stuff;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;

import java.util.HashMap;
import java.util.LinkedList;

public class Server_Handler extends ChannelInboundHandlerAdapter{

    int counter = 0;
    static HashMap<ChannelHandlerContext, Integer> contexts = new HashMap<>();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (contexts.containsKey(ctx)){
            if (contexts.get(ctx) == 0){
                ByteBuf m = (ByteBuf) msg; // (1)
                int received = 0;
                received = m.readInt();
                for (ChannelHandlerContext context: contexts.keySet()){
                    if (contexts.get(context) != 0){
                        final ByteBuf time = ctx.alloc().buffer(4);
                        time.writeInt((int) (received));
                        context.writeAndFlush(time);
                    }
                }
                m.release();
            }
        }else{
            System.out.println("Client Connected");
            ByteBuf m = (ByteBuf) msg;
            int received = 0;
            received = m.readInt();
            if (received == 0){
                contexts.put(ctx, 0);
            }else{
                contexts.put(ctx, 1);
            }
            m.release();
        }
    }

    // Here is how we send out heart beat for idle to long
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {

    }

    @Override
    public void channelActive(final ChannelHandlerContext ctx) { // (1)
//        final ByteBuf time = ctx.alloc().buffer(4); // (2)
//        time.writeInt((int) 0);
//
//        final ChannelFuture f = ctx.writeAndFlush(time); // (3)
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) { // (4)
        // Close the connection when an exception is raised.
        cause.printStackTrace();
        ctx.close();
    }
}


