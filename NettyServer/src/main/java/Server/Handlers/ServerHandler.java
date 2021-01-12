package Server.Handlers;


import Server.Handlers.Processes.WorkWithClientFiles;
import Server.Handlers.Processes.WorkWithCommands;
import Server.Handlers.Processes.WorkWithServerFiles;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.nio.charset.StandardCharsets;

public class ServerHandler extends ChannelInboundHandlerAdapter {

    public static final char signalToFiles = '!'; // сигнальный чар
    public static final char signalToServerFiles = '?'; // сигнальный чар
    public static final char signalToCommands = '*'; // сигнальный чар
    public byte checkingByte; // байт по которому будет идти проверка, работать серверу с коммандами или с файлами

    private enum Status {  // статусы
        AWAIT, Work_With_Files, Work_With_Commands, Work_With_Server_Files;
    }

    private Status currentStatus;
    private WorkWithClientFiles workWithClientFiles; //работа с файлами
    private WorkWithCommands workWithCommands; // работа с коммандами
    private WorkWithServerFiles workWithServerFiles; //работа с файлами которые лежат в хранилище на сервере


    public ServerHandler(String path) {  // конструктор нашего хэндлера с указанием рабочей папки

        this.currentStatus = Status.AWAIT;  // по умолчанию статус Ожидание
        this.workWithClientFiles = new WorkWithClientFiles(path);
        this.workWithCommands = new WorkWithCommands(path);
        this.workWithServerFiles = new WorkWithServerFiles(path);
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
                    workWithCommands.prepare();

                } else if (checkingByte == (byte) signalToServerFiles) { // если контрольный байт равен ? то работаем с серверными файлами и меняем статус
                    currentStatus = Status.Work_With_Server_Files;


                }
            }
            if (currentStatus == Status.Work_With_Files) { // если статус  Work_With_Files начинаем работу с файлами
                workWithClientFiles.receivingFileFromClient(buf);

            }
            if (currentStatus == Status.Work_With_Commands) { // если статус  Work_With_Commands начинаем работу с коммандами

                workWithCommands.sendMsgToClient(ctx);

                buf.release();
                break;

            }
            if (currentStatus == Status.Work_With_Server_Files) { // если статус  Work_With_Server_Files начинаем работу с файлами лежащими на сервере
                workWithServerFiles.sendFileToClient(ctx, buf);

                buf.release();
                break;

            }
        }
    }

    @Override
    public void exceptionCaught (ChannelHandlerContext ctx, Throwable cause) throws Exception {
       cause.printStackTrace();
       ctx.close();
        }
    }