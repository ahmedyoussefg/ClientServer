package server;

import utils.TimeoutHandler;

import java.net.*;
import java.io.*;

public class Server {
    public static void main(String[] args) throws IOException {
        // args will include portNumber
        int portNumber = 8080;
        ServerSocket ss = new ServerSocket(portNumber);
        System.out.println("[INFO] Server is listening on port " + portNumber + "...");
        ThreadGroup clientsGroup = new ThreadGroup("ClientsGroup");
        TimeoutHandler timeoutHandler = new TimeoutHandler(clientsGroup);
        while (!ss.isClosed()) {
            Socket socket = ss.accept();
            new Thread(clientsGroup, new ClientHandler(socket, timeoutHandler)).start();
            System.out.println("[INFO] Active connections: " + clientsGroup.activeCount());
        }
        ss.close();
    }
}