package Server.Handlers.Processes;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;

import java.io.File;
import java.nio.charset.StandardCharsets;

public class WorkWithCommands {

    // Список состояний.
    public enum State {
        AWAIT, WORK
    }

    private String path;

    public WorkWithCommands(String path) {
        this.path = path;
    }


    private State currentState = State.AWAIT;   // по умолчанию клиент ожидает события

    public void prepare() {

        currentState = State.WORK;  // меняем статус

    }

    public void sendMsgToClient(ChannelHandlerContext ctx) {  //запускаем работу с коммандами передавая сетевые параметры

        final char signalChar = '*';

        if (currentState == State.WORK) { //проверка статуса

            File dir = new File(path);
            File [] file  = dir.listFiles();
            String files = "" + signalChar;

            for (int i = 0; i<file.length; i++) {
                String fileName = file[i].getName();
                files = files + fileName + '\n';
            }
            System.out.println(files);
            byte[] bytes = files.getBytes(StandardCharsets.UTF_8);
            ByteBuf byteBuf = ByteBufAllocator.DEFAULT.directBuffer(bytes.length);
            byteBuf.writeBytes(bytes);
            ctx.writeAndFlush(byteBuf);
            byteBuf.release();
        }
        currentState = State.AWAIT;
    }

}
