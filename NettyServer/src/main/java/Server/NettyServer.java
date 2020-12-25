package Server;


import Server.Handlers.ServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;


public class NettyServer {

    private static final int port = 9003;
    public static final String serverStorage_Path = "StorageServer";

    public static void main(String[] args) throws Exception {

        new NettyServer().startServer();

    }


    public void startServer() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();  // поток на чтение
        EventLoopGroup workerGroup = new NioEventLoopGroup();  // поток на запись
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap(); // настройка сервера
            serverBootstrap.group(bossGroup, workerGroup) // сервак используй два пула потоков
                    .channel(NioServerSocketChannel.class)  // канал для подключения клиентов
                    .childHandler(new ChannelInitializer<SocketChannel>() { // настройка общения с клиентом
                        @Override
                        public void initChannel(SocketChannel sChannel) throws Exception{
                            sChannel.pipeline()                                                   // список хэндлеров (обработчики событий)
                                    .addLast(new ServerHandler(serverStorage_Path))

                        ;}
                    })
                            .childOption(ChannelOption.SO_KEEPALIVE, true)
            ;
            ChannelFuture future = serverBootstrap.bind(port).sync(); // Запуск сервера
                System.out.println("---------------------------");
                System.out.println("-= SERVER ONLINE =-");
                System.out.println("---------------------------\n");

            future.channel().closeFuture().sync();
                System.out.println("---------------------------");
                System.out.println("-= SERVER OFF =-");
                System.out.println("---------------------------");
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}