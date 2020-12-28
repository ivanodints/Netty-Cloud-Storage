package Client;

import java.io.DataOutputStream;
import java.net.Socket;

public class SendMsg {

    static final String symbolToCommand = "*";

    public static void send (String host, int port){

        try {
            Socket socket = new Socket (host, port);
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            out.write(symbolToCommand.getBytes());
            out.close();
            socket.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
