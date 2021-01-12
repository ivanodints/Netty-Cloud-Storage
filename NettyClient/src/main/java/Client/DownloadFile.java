package Client;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.Channel;
import java.nio.charset.StandardCharsets;


public class DownloadFile {

    private static final char signalChar = '?'; // сигнальный чар

    public static void download (String filename, Channel outChannel) {

        ByteBuf buf = null; // создаём байт буффер (размером 1 байт)
        buf = ByteBufAllocator.DEFAULT.directBuffer(1); // записываем сигнальный файл
        buf.writeByte((byte) signalChar);


        byte[] fileTitleInBytes = (signalChar + filename).getBytes(StandardCharsets.UTF_8); // преобразуем имя файла к String, String преобразуем в байты. Получили байты имени файла
        buf = ByteBufAllocator.DEFAULT.directBuffer(4); // создаём буфер на 4 байта
        buf.writeInt(fileTitleInBytes.length); //кладём в буфер число INT равное длине имени файла


        buf = ByteBufAllocator.DEFAULT.directBuffer(fileTitleInBytes.length); //по длине имени файла выделяем место в буфере соответственного размера
        buf.writeBytes(fileTitleInBytes); // в буфер кладем все символы (массив байтов) имени файла

        outChannel.writeAndFlush(buf);

    }
}
