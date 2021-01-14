package Server.Handlers.Processes;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

public class WorkWithClientFiles {

    // Список состояний.
    public enum State {
        AWAIT, TITLE_Length, TITLE, FILE_Length, DOWNLOAD_FILE
    }

    private String path;

    public WorkWithClientFiles(String path) {
        this.path = path;
    }

    private State currentState = State.AWAIT;   // по умолчанию клиент ожидает события
    private int titleLength;  // длина имени файла
    private long fileLength;  // сколько мы должны дождатся байтов (длина входящего файла)
    private long receivedFileLength; // сколько мы уже получили байтов при скачивании
    public BufferedOutputStream outputStream; // буфер нужный для записи байтов в файл


    public void prepare() {
        currentState = State.TITLE_Length;  // меняем статус
        receivedFileLength = 0L; // счётчик размера файла выставляем равный
    }

    public void receivingFileFromClient(ByteBuf buf) throws Exception {

        while (true) {
            if (currentState == State.TITLE_Length) {
                if (buf.readableBytes() > 3) { // Размер имени файла зашит в int. Если имя больше 3 (а int (в чём измеряется длина файла) > 3 байт) значит файл не пустой
                    System.out.println("Get fileName length"); // для проверки выведена печать
                    titleLength = buf.readInt(); // прочитываем из входящего буфера один int (то есть узнаём длину имени файла (к примеру 10 символов))
                    currentState = State.TITLE; // меняем статус
                }
            }

            if (currentState == State.TITLE) {
                if (buf.readableBytes() >= titleLength) {  // проверяем наличие места в буфере байтов столько сколько в длине имени. Страхуемся от частичной считки имени файла
                    byte[] fileTitle = new byte[titleLength]; // если пришло нужное количество байтов то мы формируем массив байтов с длиной массива равной длине имени файла
                    buf.readBytes(fileTitle); // делаем запись байтов из буффера в байтовый массив
                    String fileName = new String(fileTitle, "UTF-8"); //собираем строчку с именем файла
                    System.out.println("Downloading file: " + fileName);  // выводим имя файла // для проверки выведена печать
                    fileName = "Copy_" + fileName; // добавляем к файлу приписку что это копия оригинального файла который лежит у клиента (чтобы проверять было проще и не запутатся в файлах)
                    System.out.println("File name after downloading: " + fileName);
                    Path filePath = Paths.get(path, fileName);
                    System.out.println(filePath);
                    outputStream = new BufferedOutputStream(new FileOutputStream(String.valueOf(filePath))); // открываем стрим буфер на запись входящего стрима байтов  для записи байтов в файл
                    currentState = State.FILE_Length; // меняем статус
                }
            }
            if (currentState == State.FILE_Length) {
                if (buf.readableBytes() > 7) { // Размер файла зашит в лонг. Если размер файла больше 7 (а long > 7 байт) значит файл не пустой
                    fileLength = buf.readLong(); // вычитываем лонг из входящего. Теперь мы знаем размер входящего файла
                    System.out.println("Downloading file length: " + fileLength); // для проверки выведена печать
                    currentState = State.DOWNLOAD_FILE;
                }
            }

            if (currentState == State.DOWNLOAD_FILE) {
                while (buf.readableBytes() > 0) { // пока есть в буфере файлы которые мы ещё не прочитали
                    outputStream.write(buf.readByte()); // по одному байту из буфера достаём и записываем в outputStream
                    receivedFileLength++; // после каждого переданного байта счётчик полученных байтов увелич на 1
                    if (fileLength == receivedFileLength) { // Если счётчик стал равен размеры файла то успешно файл скачан переходим в режим ожидания

                        // Если в буфере остаются ещё байты (могла быть выбрана скачка 2-х, 3-х файлов и тд) То цикл запускается занового и дообрабатываем буфер
                        System.out.println("File downloaded"); // для проверки выведена печать
                        currentState = State.AWAIT;
                        outputStream.close(); // закрываем файл в который записывали входящий поток байтов
                        return;
                    }
                }
            }
            if (buf.readableBytes() == 0) {
                buf.release();
                break;
            }
        }
    }

}
