package Client;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CountDownLatch;

public class Client {

    private static final String host = NettyClientNetwork.getHost();
    private static final int port = NettyClientNetwork.getPort();
    public static final String clientStorage_Path = "StorageClient";


    public static void main(String[] args) throws Exception {

        runClient();

    }

    public static void runClient() throws InterruptedException, IOException {

        String inputCommand; //выбор команды
        final String uploadCommand = "/upload";    // команда на заливку файлов
        final String downloadCommand = "/download";  // команда на скачивание файлов
        final String serverFilesList = "/ls";  // список файлов на сервере
        final String commandsList = "/help";      // список команд
        final String disconnect1 = "/exit";   // отсоединится
        final String disconnect2 = "/quit";   // отсоединится

        boolean circle = true;


        CountDownLatch startConnection = new CountDownLatch(1);
        new Thread(() -> NettyClientNetwork.getInstance().run(startConnection)).start(); // в параллельном потоке запускаем сеть
        startConnection.await(); // добавляем await, чтобы код ниже не мог запустится раньше установившегося соединения с сервером

         BufferedReader reader = new BufferedReader(new InputStreamReader(System.in)); // добавляем буфер ридер для считывания с консоли команд

        System.out.println("Input /help for command list\n");
        while (circle) { //запускаем цикл

            inputCommand = reader.readLine(); //присваиваем переменной введёное в консоли значение


            if (inputCommand.equals(disconnect1)) {  //если введённое значение совпадает со значением переменной то прерываем цикл
                break;


            } else if (inputCommand.equals(disconnect2)) { //если введённое значение совпадает со значением переменной то прерываем цикл
                break;


            } else if (inputCommand.equals(uploadCommand)) { //если введённое значение совпадает со значением переменной то начинаем загрузку файла

                System.out.println("\nPlease write the file name\nFor example 'demo.txt'");

                String fileName = reader.readLine();  //в консоль вписываем имя файла

                Path filePath = Paths.get(clientStorage_Path, fileName); //добавляем имя файла к пути до него

                if (!Files.exists(filePath)) { //если такого файла нет в нашем хранилище то выводим сообщение
                    System.out.println("File + " + fileName + " not found");
                    continue;
                }

                SendFile.send(filePath, NettyClientNetwork.getInstance().getCurrentChannel(), channelFuture -> {  // отправляем файл


                    if (!channelFuture.isSuccess()) {           // если задача провалилась
                        System.out.println("###########################");
                        System.out.println("<Error - upload terminate>");
                        System.out.println("###########################");
                        channelFuture.cause().printStackTrace();
                    }


                    if (channelFuture.isSuccess()) {           // если задача успешно завершилась
                        System.out.println("\n< Upload file completed >");
                    }
                         });
                            continue;


                    } else if (inputCommand.equals(downloadCommand)) { // скачиваем файлы

                            System.out.println("\nPlease write the file name\nFor example 'demo.txt'");   // код в разработке

                            String fileName = reader.readLine();  //в консоль вписываем имя файла



                    } else if (inputCommand.equals(commandsList)) {  // список комманд
                        System.out.println("-----------Commands-------------");
                        System.out.println(uploadCommand + " - upload file\n" + downloadCommand + " - download file\n"
                            + serverFilesList + " - files list on server storage\n" + commandsList + " - help");



                    } else if (inputCommand.equals(serverFilesList)) {  // список файлов на сервере
                        SendMsg.send(host,port);

                    } else {
                        System.out.println("###########################");
                        System.out.println("<Error - command unknown>");
                        System.out.println("###########################");
            }
        }
    }
}
