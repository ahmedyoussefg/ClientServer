package server;

import java.net.*;
import java.io.*;

public class Server {
    public static void main(String[] args) throws IOException {
        // args will include portNumber
        int portNumber = 8080;
        ServerSocket ss = new ServerSocket(portNumber);
        System.out.println("[INFO] Server is listening on port " + portNumber + "...");
        while (!ss.isClosed()) {
            Socket socket = ss.accept();
            new Thread(new ClientHandler(socket)).start();
            System.out.println("[INFO] Active connections: " + (Thread.activeCount() - 1));
        }
        ss.close();
    }
}