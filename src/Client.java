import java.io.*;
import java.net.*;

public class Client {
    public static void main(String[] args) throws IOException {
        // args will include hostName, portNumber
        String hostName = InetAddress.getLocalHost().getHostName();
        InetAddress hostAddr = InetAddress.getByName(hostName);
        System.out.println("Client on " + hostAddr.getHostName());
        // portNumber should be same as the server
        int portNumber = 8080;
        Socket s = new Socket(hostName, portNumber);
        // send to server
        PrintWriter pr = new PrintWriter(s.getOutputStream(), true);
        InputStreamReader in = new InputStreamReader(s.getInputStream());
        BufferedReader bf = new BufferedReader(in);
        while (true) {
            System.out.println("Sending to Server..");
            pr.println("Hello from client\r");
            pr.println("\r");
            // message from server
            String msg = bf.readLine();
            if (msg != null) {
                System.out.println("Recieved from the server: "+ msg);
            }
            else {
                break;
            }
        }
        bf.close();
        in.close();
        s.close();
    }
}