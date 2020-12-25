package Server.Handlers;


import Server.Handlers.Processes.WorkWithClientFiles;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class ServerHandler extends ChannelInboundHandlerAdapter {

    public static final char signalToFiles = '!'; // сигнальный чар
    public static final char signalToCommands = '*'; // сигнальный чар
    public byte checkingByte; // байт по которому будет идти проверка, работать серверу с коммандами или с файлами

    private enum Status {  // статусы
        AWAIT, Work_With_Files, Work_With_Commands
    }

    private Status currentStatus;
    private WorkWithClientFiles workWithClientFiles; //работа с файлами
//    private ServerCommands serverCommands; // работа с коммандами (в разработке)



    public ServerHandler(String path) {  // конструктор нашего хэндлера с указанием рабочей папки

        this.currentStatus = Status.AWAIT;  // по умолчанию статус Ожидание
        this.workWithClientFiles = new WorkWithClientFiles(path);
//        this.serverCommands = serverCommands;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("-=New Client connection=-");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = ((ByteBuf) msg);
        while (buf.readableBytes() > 0) {
            if (currentStatus == Status.AWAIT) {
                checkingByte = buf.readByte();
                if (checkingByte == (byte) signalToFiles) { // если контрольный байт равен ! то работаем с файлами и меняем статус
                    currentStatus = Status.Work_With_Files;
                    workWithClientFiles.prepare();

                } else if (checkingByte == (byte) signalToCommands) { // если контрольный байт равен * то работаем с коммандами и меняем статус
                    currentStatus = Status.Work_With_Commands;
//                    serverCommands.prepare();   // в разработке
                }
            }
            if (currentStatus == Status.Work_With_Files) { // если статус  Work_With_Files начинаем работу с файлами
                workWithClientFiles.receivingFileFromClient(ctx,buf);
            }
            if (currentStatus == Status.Work_With_Commands) { // если статус  Work_With_Commands начинаем работу с коммандами
//                    serverCommands.run(); // в разработке

            }
        }
        if (buf.readableBytes() == 0) {
            buf.release();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}