package Nick_Stuff;

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
import java.util.Date;

public class Host_Client_Handler extends ChannelInboundHandlerAdapter {

    public int counter = 0;
    public ChannelHandlerContext context = null;

    static File path = new File("C:\\Users\\nickz\\Desktop\\myimage.jpg");

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        context = ctx;
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
        final ByteBuf bytebuf = context.alloc().buffer(fileContents.length);
        System.out.println(fileContents.length);
        bytebuf.writeBytes(fileContents);

        OutputStream out = null;

        byte[] data = new byte[bytebuf.readableBytes()];
        System.out.println(data.length);
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
        ByteBuf m = (ByteBuf) msg; // (1)
        int received = 0;
        try {
            //long currentTimeMillis = (m.readUnsignedInt() - 2208988800L) * 1000L;
            //System.out.println(new Date(currentTimeMillis));
            received = m.readInt();
            System.out.println(received);
        } finally {
            m.release();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

}
