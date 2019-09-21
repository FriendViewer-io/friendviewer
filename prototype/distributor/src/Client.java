package prototype.distributor;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.io.IOException;
import java.net.Socket;

public class Client {

    public static void main(String[] args) throws IOException, InterruptedException {
        Socket clientSocket = new Socket("127.0.0.1", 61235);

        String host = "localhost";
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            Bootstrap b = new Bootstrap();
            b.group(workerGroup);
            b.channel(NioSocketChannel.class);
            b.option(ChannelOption.SO_KEEPALIVE, true);
            b.handler(new ChannelInitializer<SocketChannel>() {
                public void initChannel(SocketChannel ch)
                        throws Exception {
                    ch.pipeline().addLast(new ClientHandler());
                }
            });

            ChannelFuture f = b.connect(host, 61235).sync();

            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
        }
    }
}