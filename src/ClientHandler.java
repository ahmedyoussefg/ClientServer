import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable{
    Socket socket;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }
    @Override
    public void run() {
        try {
            InputStreamReader in = new InputStreamReader(socket.getInputStream());
            BufferedReader bf = new BufferedReader(in);
            PrintWriter pr = new PrintWriter(socket.getOutputStream(), true);
            while (true) {
                String msg = bf.readLine();
                if (msg == null) {
                    break;
                }
                // message from the client
//                System.out.println("Message Received: " + msg);
                // message to the client
                pr.println("Welcome from the server!");
            }
            bf.close();
            in.close();
            this.socket.close();
        } catch(IOException e) {
            System.out.println(e.getMessage());
        }

    }
}
