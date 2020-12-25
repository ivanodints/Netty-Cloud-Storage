package Client;


import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.CountDownLatch;

public class NettyClientNetwork {

    private  final String clientStorage_Path = "StorageClient";
    private final String host = "localhost";
    private final int port = 9003;

    private static NettyClientNetwork ourInstance = new NettyClientNetwork();

    public static NettyClientNetwork getInstance() {
        return ourInstance;
    }

    private NettyClientNetwork () {

    }

    private Channel currentChannel;

    public  Channel getCurrentChannel() {
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

                protected void initChannel(SocketChannel socketChannel) throws Exception {

                    socketChannel.pipeline();
                           // здесь будет хэндлер на обработку файлов полученных с сервера
                    currentChannel = socketChannel;
                }
            });
            ChannelFuture channelFuture = clientBootstrap.connect().sync();
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