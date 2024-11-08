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

    private static final String CLIENT_ABSOLUTE_PATH = "src/client";
    private static final String CLIENT_ABSOLUTE_DATA_PATH = CLIENT_ABSOLUTE_PATH + "/clientdata/";

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
            processRequest(request, pr, bf);
        }

        bf.close();
        in.close();
        s.close();
    }

    private static void processRequest(String request, PrintWriter pr, BufferedReader bf) throws IOException {
        String requestLine = request.split("\r\n")[0];
        String method = requestLine.split(" ")[0];
        String filePath = requestLine.split(" ")[1];

        pr.println(request);

        if ("POST".equals(method)) {
            sendFileContent(CLIENT_ABSOLUTE_DATA_PATH + filePath, pr);
            String response = bf.readLine();
            if (response != null) {
                System.out.println(response);
            }
        } else if ("GET".equals(method)) {
            handleGetResponse(bf, filePath);
        }
    }

    private static void handleGetResponse(BufferedReader bf, String filePath) throws IOException {
        String response;
        while ((response = bf.readLine()) != null && !response.isEmpty()) {
             System.out.println(response);
        }

        byte[] fileContent = Base64.getDecoder().decode(bf.readLine());
        String[] pathSplit = filePath.split("/");
        String fileName = pathSplit[pathSplit.length - 1];

        try (FileOutputStream fos = new FileOutputStream(CLIENT_ABSOLUTE_DATA_PATH + fileName)) {
            fos.write(fileContent);
            System.out.println("File written successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
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