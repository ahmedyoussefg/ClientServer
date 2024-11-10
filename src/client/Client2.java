package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client2 {
    public static void main(String[] args) throws IOException, InterruptedException {
        // args will include hostName, portNumber
        String hostName = InetAddress.getLocalHost().getHostName();
        InetAddress hostAddr = InetAddress.getByName(hostName);
        System.out.println("[INFO] Client on " + hostAddr.getHostName());

        // portNumber should be same as the server
        int portNumber = 8080;
        Socket s = new Socket(hostName, portNumber);

        // send to server
        PrintWriter pr = new PrintWriter(s.getOutputStream(), true);
        InputStreamReader in = new InputStreamReader(s.getInputStream());
        pr.println("HELLO SERVER");
        Thread.sleep(4000);
        System.out.println("HELP");
        pr.println("ARE U STILL ALIVE");


        Thread.sleep(50000);
    }
}
