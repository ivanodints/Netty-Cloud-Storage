package Client;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;

import java.io.DataOutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class SendMsg {

    static final String symbolToCommand = "*";

    public static void send (String host, int port){

        try {
            Socket socket = new Socket (host, port);
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            out.write(symbolToCommand.getBytes());
            out.close();
            socket.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void send () {

        String test = "*";
        byte[] bytes = test.getBytes(StandardCharsets.UTF_8);
        ByteBuf byteBuf = ByteBufAllocator.DEFAULT.directBuffer(bytes.length);
        byteBuf.writeBytes(bytes);
        NettyClientNetwork.getCurrentChannel().writeAndFlush(byteBuf);

    }

}
