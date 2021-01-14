package Client.Handlers;

import Client.Handlers.Processes.WorkWithServerFiles;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

import java.nio.charset.StandardCharsets;

public class ClientHandler extends ChannelInboundHandlerAdapter {


    public static final char signalToFiles = '!'; // сигнальный чар
    public static final char signalToMsg = '*'; // сигнальный чар
    public byte checkingByte; // байт по которому будет идти проверка, работать серверу с коммандами или с файлами


    private enum Status {  // статусы
        AWAIT, Work_With_Server_Files, Work_With_Commands
    }

    private Status currentStatus;
    private WorkWithServerFiles workWithServerFiles; //работа с файлами
//    private WorkWithMsg workWithMsg; // работа с коммандами

    public ClientHandler(String path) {  // конструктор нашего хэндлера с указанием рабочей папки

        this.currentStatus = Status.AWAIT;  // по умолчанию статус Ожидание
        this.workWithServerFiles = new WorkWithServerFiles(path);
//        this.workWithMsg = new WorkWithMsg(path);
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {


        ByteBuf buf = ((ByteBuf) msg);

        while (buf.readableBytes() > 0) {

            if (currentStatus == Status.AWAIT) {

            checkingByte = buf.readByte();

            if (checkingByte == (byte) signalToFiles) { // если контрольный байт равен ! то работаем с файлами и меняем статус

                currentStatus = Status.Work_With_Server_Files;
                workWithServerFiles.prepare();

            } else if (checkingByte == (byte) signalToMsg) {

                    currentStatus = Status.Work_With_Commands;

            }
        }

            if (currentStatus == Status.Work_With_Server_Files) {   // если статус  Work_With_Files начинаем работу с файлами
                workWithServerFiles.receivingFileFromServer(ctx, buf);

            } else if (currentStatus == Status.Work_With_Commands) {

                System.out.println("-= FILES ON SERVER =- ");
                String fileName = buf.toString(StandardCharsets.UTF_8);
                System.out.println("\n"+fileName);
                break;

            }

        }
    }
}













//    @Override
//    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//
//        ByteBuf in = (ByteBuf) msg;
//
//        try {
//            while (in.isReadable()) {
//                System.out.print((char)in.readableBytes());
//                System.out.flush();
//            }
//        } finally {
//            ReferenceCountUtil.release(msg);
//        }
//    }
