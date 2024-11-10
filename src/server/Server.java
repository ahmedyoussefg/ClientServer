package server;

import utils.TimeoutHandler;

import java.net.*;
import java.io.*;

public class Server {
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.out.println("Usage: java Server <port_number>");
            return;
        }
        int portNumber = Integer.parseInt(args[0]);

        // create a ServerSocket to listen for incoming connections on the specified port
        ServerSocket ss = new ServerSocket(portNumber);
        System.out.println("[INFO] Server is listening on port " + portNumber + "...");

        // initialize a thread group to manage client connections
        ThreadGroup clientsGroup = new ThreadGroup("ClientsGroup");
        
        // create a timeout handler to monitor and manage client connections
        TimeoutHandler timeoutHandler = new TimeoutHandler(clientsGroup);

        while (!ss.isClosed()) {
            // accept a new client connection
            Socket socket = ss.accept();

            // start a new thread for each client
            new Thread(clientsGroup, new ClientHandler(socket, timeoutHandler)).start();

            System.out.println("[INFO] Active connections: " + clientsGroup.activeCount());
        }
        // Close the server socket after exiting the loop
        ss.close();
    }
}