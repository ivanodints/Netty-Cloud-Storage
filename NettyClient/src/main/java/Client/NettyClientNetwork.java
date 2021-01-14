package Client;


import Client.Handlers.ClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.CountDownLatch;

public class NettyClientNetwork {

    private final String clientStorage_Path = "StorageClient";
    private static  String host = "localhost";
    private static int port = 9003;

    private static NettyClientNetwork ourInstance = new NettyClientNetwork();

    public static NettyClientNetwork getInstance() {
        return ourInstance;
    }

    private NettyClientNetwork () {
    }

    private static Channel currentChannel;

    public static int getPort() {
        return port;
    }

    public static String getHost() {
        return host;
    }

    public static Channel getCurrentChannel() {
        return currentChannel;
    }

    public void run(CountDownLatch countDownLatch) {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap clientBootstrap = new Bootstrap();
            clientBootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .remoteAddress(new InetSocketAddress(host, port))
                    .handler(new ChannelInitializer<SocketChannel>() {

                        @Override
                        public void initChannel(SocketChannel socketChannel) throws Exception {
                            currentChannel = socketChannel;
                            socketChannel.pipeline()
                                    .addLast(new ClientHandler(clientStorage_Path)); // хэндлер на обработку сообещений от сервера
                        }
                    })
                    .option(ChannelOption.SO_KEEPALIVE, true)
        ;
            ChannelFuture channelFuture = clientBootstrap.connect(host, port).sync();
                System.out.println("---------------------------");
                System.out.println("-= CONNECTION COMPLETED =-");
                System.out.println("---------------------------\n");
                countDownLatch.countDown();
                channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                group.shutdownGracefully().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void stopConnection() throws IOException {
        currentChannel.close();
    }
}