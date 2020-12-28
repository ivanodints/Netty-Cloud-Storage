package Server.Handlers.Processes;

import Server.ServerSendMsg;
import io.netty.channel.ChannelHandlerContext;

import java.io.DataOutputStream;
import java.io.File;
import java.net.Socket;

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

    public void run(String host, int port) {  //запускаем работу с коммандами передавая сетевые параметры

        if (currentState == State.WORK) { //проверка статуса
            ServerSendMsg.send(host,port,path);
        }
        currentState = State.AWAIT;
    }
}
