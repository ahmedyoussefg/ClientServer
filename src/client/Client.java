package client;

import utils.RequestsParsingHandler;

import java.io.*;
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

            if (requestLine.contains("POST")) {
                sendFileContent(filePath, pr);
                String msg = bf.readLine();
                if (msg != null) {
                    System.out.println("Recieved from the server: " + msg);
                }
            } else if (requestLine.contains("GET")) {
                String msg;
                while ((msg = bf.readLine()) != null && !msg.isEmpty()) {
                    System.out.println("[OPTIONAL] " + msg);
                }
                byte[] fileContent = Base64.getDecoder().decode(msg);
                String fileName = requestLine.split(" ")[1].split("/")[1];
                try (FileOutputStream fos = new FileOutputStream(CLIENT_ABSOLUTE_PATH + "/clientdata/" + fileName)) {
                    fos.write(fileContent);
                    System.out.println("File written successfully.");
                } catch (IOException e) {
                    e.printStackTrace();
                }
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