package Nick_Stuff;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.Scanner;

public class Host_Client {

    public static void main(String[] args) throws Exception {
        //String host = args[0];
        //int port = Integer.parseInt(args[1]);
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            Host_Client_Handler test = new Host_Client_Handler();

            Bootstrap b = new Bootstrap(); // (1)
            b.group(workerGroup); // (2)
            b.channel(NioSocketChannel.class); // (3)
            b.option(ChannelOption.SO_KEEPALIVE, true); // (4)
            b.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(test);
                }
            });

            // Start the client.
            //String serverIp = "198.199.81.165";
            String serverIp = "127.0.0.1";
            ChannelFuture f = b.connect(serverIp, 8080).sync(); // (5)

            //MY CODE
            test.initiateConvo(0);
            test.sendFile("C:\\Users\\nickz\\Desktop\\Ichigo.png");
            Scanner sc = new Scanner(System.in);
            boolean stop = false;
            while (!stop){
                //int next = sc.nextInt();
                //next += 1000;
                //test.initiateConvo(next);
                String text =  sc.nextLine();
                test.sendString(text);
            }

            // Wait until the connection is closed.
            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
        }
    }
}