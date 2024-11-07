package client;

import utils.RequestsParsingHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;

public class Client {
    private static final String CLIENT_ABSOLUTE_PATH = "src/client/";

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


        List<String> requests = RequestsParsingHandler.readRequests("requests.txt");

        for (String request : requests) {
            System.out.println("Sending to Server..");

            pr.println(request);
            String requestLine = request.split("\r\n")[0];
            String filePath = CLIENT_ABSOLUTE_PATH + requestLine.split(" ")[1];
            sendFileContent(filePath, pr);

            String msg = bf.readLine();
            if (msg != null) {
                System.out.println("Recieved from the server: " + msg);
            }
        }

        bf.close();
        in.close();
        s.close();
    }

    private static void sendFileContent(String filePath, PrintWriter pr) {
        try {
            byte[] fileContent = Files.readAllBytes(Paths.get(filePath));
            String encodedFileContent = Base64.getEncoder().encodeToString(fileContent);
            pr.println(encodedFileContent);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}