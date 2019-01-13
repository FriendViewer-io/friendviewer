package Nick_Stuff;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;

public class Server_Handler extends ChannelInboundHandlerAdapter{

    int counter = 0;
    static HashMap<ChannelHandlerContext, Integer> contexts = new HashMap<>();
    static File path = new File("C:\\Users\\nickz\\Desktop\\myimage.jpg");

    private ByteBuf buf;

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        buf = ctx.alloc().buffer(3016978); // (1)
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) {
        buf.release(); // (1)
        buf = null;
    }

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
        ByteBuf m = (ByteBuf) msg;
        buf.writeBytes(m);
        m.release();

        if (buf.readableBytes() >= 3016978) {
            OutputStream out = null;
            byte[] data = new byte[buf.readableBytes()];
            System.out.println(data.length);
            buf.readBytes(data);

            try {
                out = new BufferedOutputStream(new FileOutputStream(path));
                out.write(data);
                if (out != null) out.close();
            } catch (Exception e) {

            }
        }

//        Date date = new Date();
//        String strDateFormat = "hh:mm:ss a";
//        DateFormat dateFormat = new SimpleDateFormat(strDateFormat);
//        String formattedDate= dateFormat.format(date);
//        System.out.println("Current time of the day using Date - 12 hour format: " + formattedDate);

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


