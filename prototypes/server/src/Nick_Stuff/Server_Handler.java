package Nick_Stuff;
import Nick_Stuff.Protocols.AuthnRqCP;
import Nick_Stuff.Protocols.AuthnRqSP;
import Nick_Stuff.Protocols.AuthnRsSP;
import Nick_Stuff.Protocols.Test_Protocol;
import com.google.protobuf.InvalidProtocolBufferException;
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
import java.util.*;

public class Server_Handler extends ChannelInboundHandlerAdapter{

    int counter = 0;
    static HashMap<ChannelHandlerContext, Integer> contexts = new HashMap<>();
    static HashMap<ChannelHandlerContext, Integer> loginAttempts = new HashMap<>();

    static File path = new File("C:\\Users\\nickz\\Desktop\\myimage.jpg");

    private ByteBuf buf;
    static int size = 0;

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        buf = ctx.alloc().buffer(4); // (1)
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

        if (size == 0){
            size = buf.readInt();
            System.out.println(size);
        }

        if (buf.readableBytes() >= size && size != 0) {
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
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf m = (ByteBuf) msg;
        buf.writeBytes(m);
        m.release();

        if (size == 0){
            size = buf.readInt();
        }

        if (buf.readableBytes() >= size && size != 0) {
            int type = buf.readInt();

            byte[] data = new byte[buf.readableBytes()];
            buf.readBytes(data);

            if (type == 2){ //Authentication Request Client Protocol
                AuthnRqCP.request message = null;
                try {
                    message = AuthnRqCP.request.parseFrom(data);
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }

                System.out.println(new Date(message.getUtcTime()).toString());
                System.out.println(message.getPassword());
                if (message.getPassword().equals("password")){
                    System.out.println("correct");
                    AuthnRsSP(ctx, true);
                }else{
                    System.out.println(loginAttempts.get(ctx));
                    if (loginAttempts.get(ctx) == 0){
                        AuthnRsSP(ctx, false);
                    }else{
                        AuthnRqSP(ctx);
                    }
                }

            }else if (type == 0){
                size = 0;

                Test_Protocol.dataChunk message = null;
                try {
                    message = Test_Protocol.dataChunk.parseFrom(data);
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }

                System.out.println("Name: " + message.getName());
                System.out.println("ID: " + message.getId());
                System.out.println("Description: " + message.getDescription());

                for (String text : message.getTextList()) {
                    System.out.println(text);
                }

                byte[] image = message.getImage().toByteArray();

                try {
                    //File path = new File("root/friendviewer/Received.jpg");
                    File path = new File("C:\\Users\\nickz\\Desktop\\Received.jpg");
                    OutputStream  out = new BufferedOutputStream(new FileOutputStream(path));
                    out.write(image);
                    if (out != null) out.close();
                } catch (Exception e) {

                }
            }
        }

//        if (contexts.containsKey(ctx)){
//            if (contexts.get(ctx) == 0){
//                //handleInt(ctx, msg);
//                //handleString(ctx, msg);
//                //handleFile(ctx, msg);
//            }
//        }else{
//            System.out.println("Client Connected");
//            ByteBuf m = (ByteBuf) msg;
//            int received = 0;
//            received = m.readInt();
//            if (received == 0){
//                contexts.put(ctx, 0);
//            }else{
//                contexts.put(ctx, 1);
//            }
//            m.release();
//        }
    }

    public void AuthnRqSP(ChannelHandlerContext ctx){
        //Reset the size
        size = 0;

        AuthnRqSP.response.Builder dataBuilder = AuthnRqSP.response.newBuilder();
        dataBuilder.setProtoType(1);
        dataBuilder.setProtoId(0); //8 byte magic number
        dataBuilder.setUtcTime(new Date().getTime());

        if (loginAttempts.containsKey(ctx)){
            int remaining = loginAttempts.get(ctx) - 1;
            dataBuilder.setAttemptsRemaining(remaining);
            loginAttempts.put(ctx, remaining);
        }else{
            dataBuilder.setAttemptsRemaining(5);
            loginAttempts.put(ctx, 2);
        }

        AuthnRqSP.response data = dataBuilder.build();
        byte[] bytes = data.toByteArray();

        //Send the size of the protocol before sending the protocol itself
        int size = bytes.length;
        ByteBuf bytebuf = ctx.alloc().buffer(size + 8);
        bytebuf.writeInt(size);
        bytebuf.writeInt(1);

        //Send the protocol
        bytebuf.writeBytes(bytes);
        ctx.writeAndFlush(bytebuf);
    }

    public void AuthnRsSP(ChannelHandlerContext ctx, boolean verify){
        //Reset the size
        size = 0;

        loginAttempts.remove(ctx);

        AuthnRsSP.response.Builder dataBuilder = AuthnRsSP.response.newBuilder();
        dataBuilder.setProtoType(3);
        dataBuilder.setProtoId(0); //8 byte magic number
        dataBuilder.setUtcTime(new Date().getTime());
        dataBuilder.setGuests(true);
        dataBuilder.setResponse(verify);
        if (verify){
            contexts.put(ctx, 0);
            dataBuilder.setWaitTime(0);
        }

        AuthnRsSP.response data = dataBuilder.build();
        byte[] bytes = data.toByteArray();

        //Send the size of the protocol before sending the protocol itself
        int size = bytes.length;
        ByteBuf bytebuf = ctx.alloc().buffer(size + 8);
        bytebuf.writeInt(size);
        bytebuf.writeInt(3);

        //Send the protocol
        bytebuf.writeBytes(bytes);
        ctx.writeAndFlush(bytebuf);
    }

    @Override
    public void channelActive(final ChannelHandlerContext ctx) {
        AuthnRqSP(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // Close the connection when an exception is raised.
        cause.printStackTrace();
        ctx.close();
    }
}


