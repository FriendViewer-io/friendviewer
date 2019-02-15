package Nick_Stuff;

import Nick_Stuff.Protocols.AuthnRqCP;
import Nick_Stuff.Protocols.AuthnRqSP;
import Nick_Stuff.Protocols.AuthnRsSP;
import Nick_Stuff.Protocols.Test_Protocol;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import sun.misc.IOUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

public class Host_Client_Handler extends ChannelInboundHandlerAdapter {

    public ChannelHandlerContext context = null;
    static File path = new File("C:\\Users\\nickz\\Desktop\\myimage.jpg");

    private ByteBuf buf;
    static int size = 0;

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        context = ctx;
        buf = ctx.alloc().buffer(4); // (1)
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) {
        buf.release(); // (1)
        buf = null;
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

    public void sendFile(String x){
        File file = new File(x);
        byte[] fileContents = new byte[10];
        try {
            fileContents = Files.readAllBytes(file.toPath());
        } catch(Exception e){

        }
        final ByteBuf bytebuf = context.alloc().buffer(fileContents.length + 4);
        System.out.println(fileContents.length);
        bytebuf.writeInt(fileContents.length);
        bytebuf.writeBytes(fileContents);



//        OutputStream out = null;
//
//        byte[] data = new byte[bytebuf.readableBytes() - 4];
//        System.out.println(bytebuf.readableBytes());
//        int size = bytebuf.readInt();
//        System.out.println(size);
//        bytebuf.readBytes(data);
//
//        try{
//            out = new BufferedOutputStream(new FileOutputStream(path));
//            out.write(data);
//            if (out != null) out.close();
//        }catch (Exception e){
//
//        }
        context.writeAndFlush(bytebuf);
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

            if (type == 1){ //Authentication Request Server Protocol
                AuthnRqCP(data, ctx);
            } else if (type == 3){ //Authentication Response Server Protocol
                System.out.println("Response Recieved");
                //Read the message
                AuthnRsSP.response message = null;
                try {
                    message = AuthnRsSP.response.parseFrom(data);
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }
                System.out.println(new Date(message.getUtcTime()).toString());
                System.out.println(message.getResponse());
                System.out.println(message.getGuests());
                System.out.println(message.getWaitTime());


                //Sending a large protobuf message
                Test_Protocol.dataChunk.Builder dataBuilder = Test_Protocol.dataChunk.newBuilder();
                dataBuilder.setName("Example 1");
                dataBuilder.setId(001);
                dataBuilder.setDescription("The very first protobuf sent. How very exciting!");

                File file = new File("C:\\Users\\nickz\\Desktop\\Cake.png");
                //File file = new File("~/Received.jpg");
                byte[] fileContents = new byte[10];
                try {
                    fileContents = Files.readAllBytes(file.toPath());
                } catch(Exception e){

                }
                ByteString s = ByteString.copyFrom(fileContents);
                dataBuilder.setImage(s);

                ArrayList<String> text = new ArrayList<>();
                text.add("Paragraph 1");
                text.add("Textfield 2");
                text.add("Salaam, ALLAHU AKBAR!");
                text.add("Glory to the faithful! Death to the infidels! ALLAHU AKBAR!!!");
                text.add("pew pew pew");
                dataBuilder.addAllText(text);

                Test_Protocol.dataChunk big_file = dataBuilder.build();

                byte[] bytes = big_file.toByteArray();

                //Send the size of the protocol before sending the protocol itself
                int size = bytes.length;
                ByteBuf bytebuf = ctx.alloc().buffer(size + 8);
                bytebuf.writeInt(size);
                bytebuf.writeInt(0);

                //Send the protocol
                bytebuf.writeBytes(bytes);
                ctx.writeAndFlush(bytebuf);
            }
        }
    }

    public void AuthnRqCP(byte[] data, ChannelHandlerContext ctx){
        //Reset the size
        size = 0;

        //Read the message
        AuthnRqSP.response message = null;
        try {
            message = AuthnRqSP.response.parseFrom(data);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        System.out.println(new Date(message.getUtcTime()).toString());

        //Formulate a response
        Scanner sc = new Scanner(System.in);

        AuthnRqCP.request.Builder dataBuilder = AuthnRqCP.request.newBuilder();
        dataBuilder.setProtoType(2);
        dataBuilder.setProtoId(0);
        dataBuilder.setUtcTime(new Date().getTime());
        dataBuilder.setPassword(sc.nextLine());

        AuthnRqCP.request request = dataBuilder.build();
        byte[] bytes = request.toByteArray();

        //Send the size of the protocol before sending the protocol itself
        int size = bytes.length;
        ByteBuf bytebuf = ctx.alloc().buffer(size + 8);
        bytebuf.writeInt(size);
        bytebuf.writeInt(2);

        //Send the protocol
        bytebuf.writeBytes(bytes);
        ctx.writeAndFlush(bytebuf);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

}
