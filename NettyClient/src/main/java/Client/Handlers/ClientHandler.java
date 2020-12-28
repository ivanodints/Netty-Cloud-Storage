package Client.Handlers;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

public class ClientHandler extends ChannelInboundHandlerAdapter {


    public static final char signalToFiles = '!'; // сигнальный чар

    public byte checkingByte; // байт по которому будет идти проверка, работать серверу с коммандами или с файлами

    private enum Status {  // статусы
        AWAIT, Work_With_Files, Work_With_Commands
    }

    private Status currentStatus;
//    private WorkWithServerFiles workWithServerFiles; //работа с файлами
//    private WorkWithMsg workWithMsg; // работа с коммандами (в разработке)

    public ClientHandler(String path) {  // конструктор нашего хэндлера с указанием рабочей папки

        this.currentStatus = Status.AWAIT;  // по умолчанию статус Ожидание
//        this.workWithClientFiles = new WorkWithClientFiles(path);
//        this.workWithCommands = new WorkWithCommands(path);
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        System.out.println("проверочное сообщение на попадание в хэндлер ченнел рид");

        ByteBuf buf = ((ByteBuf) msg);
        while (buf.readableBytes() > 0) {

                checkingByte = buf.readByte();

                if (checkingByte == (byte) signalToFiles) { // если контрольный байт равен ! то работаем с файлами и меняем статус

//                    currentStatus = Status.Work_With_Files;   // в разработке
//                    workWithClientFiles.prepare();            // в разработке

                } else {

                    buf.resetReaderIndex(); // cдвигаем каретку на начало буфера и  начинаем считывать полученное сообщение
                    currentStatus = Status.Work_With_Commands;
                    System.out.println("Files on server: ");

                    try {
                        while (buf.isReadable()) {
                            System.out.print((char) buf.readableBytes());
                            System.out.flush();
                        }
                    } finally {
                        ReferenceCountUtil.release(msg);
                    }
                    if (buf.readableBytes() == 0) {
                        buf.release();
                        break;
                    }
                }

//            if (currentStatus == Status.Work_With_Files) { // если статус  Work_With_Files начинаем работу с файлами
//                workWithClientFiles.receivingFileFromClient(ctx,buf);   // в разработке
//            }

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
