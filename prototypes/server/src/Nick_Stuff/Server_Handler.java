package Nick_Stuff;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedList;

public class Server_Handler extends ChannelInboundHandlerAdapter{

    int counter = 0;
    static HashMap<ChannelHandlerContext, Integer> contexts = new HashMap<>();

    public void handleInt(ChannelHandlerContext ctx, Object msg) {
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

    public void handleString(ChannelHandlerContext ctx, Object msg){
        ByteBuf m = (ByteBuf) msg; // (1)
        String received = "";
        received = m.toString(Charset.forName("utf-8"));
        for (ChannelHandlerContext context: contexts.keySet()){
            if (contexts.get(context) != 0){
                byte[] bytes = received.getBytes();
                final ByteBuf text = context.alloc().buffer(bytes.length); // (2)
                text.writeBytes(bytes);
                context.writeAndFlush(text); // (3)
            }
        }
        m.release();
    }

    public void handleFile(ChannelHandlerContext ctx, Object msg){
        System.out.println("Start");
        ByteBuf m = (ByteBuf) msg; // (1)
        File path = new File("C:\\Users\\nickz\\Desktop\\myfile.txt");
        try (FileOutputStream stream = new FileOutputStream(path)) {
            System.out.println("Write");
            System.out.println(m.array().length);
            System.out.println("Length Above");
            stream.write(m.array());
        } catch (Exception e){
            System.out.println("Error?");
        }

        m.release();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (contexts.containsKey(ctx)){
            if (contexts.get(ctx) == 0){
                //handleInt(ctx, msg);
                //handleString(ctx, msg);
                handleFile(ctx, msg);
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


