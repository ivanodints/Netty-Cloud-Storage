package Server;

import java.io.DataOutputStream;
import java.io.File;
import java.net.Socket;

public class ServerSendMsg {

    public static void send (String host, int port, String path){

        try {
            Socket socket = new Socket (host, port); //подключаемся к сокету и начинаем передавать в него список файлов в дерректории
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());

            // проверка на работу хэндлера у клиента на приём отправленный сообщений с сервера
            String symbolToCommand = "!asd";
            out.write(symbolToCommand.getBytes());
            out.close();
            socket.close();
            System.out.println("text sending");

            // отправка списка файлов находящихся в хранилище сервера

//            File dir = new File(path);
//            if (dir.isDirectory()) {
//                for (File item : dir.listFiles()) {
//                    System.out.println(item.getName());
//                    out.write(item.getName().getBytes());
//                }
//                out.close();
//                socket.close();
//            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}



