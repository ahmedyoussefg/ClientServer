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
        if (args.length != 2) {
            System.out.println("Usage: java Client <server_ip> <port_number>");
            return;
        }
        String serverIp = args[0];
        // portNumber should be same as the server
        int portNumber = Integer.parseInt(args[1]);

        // establish a socket connection to the server
        Socket s = new Socket(serverIp, portNumber);
        System.out.println("[INFO] Connected to server at " + serverIp + ":" + portNumber);

        // setup streams for communication with the server
        PrintWriter pr = new PrintWriter(s.getOutputStream(), true);
        InputStreamReader in = new InputStreamReader(s.getInputStream());
        BufferedReader bf = new BufferedReader(in);

        // read requests from the file "requests.txt"
        List<String> requests = RequestsParsingHandler.readRequests("requests.txt");

        // process each request from the list
        for (String request : requests) {
            System.out.println("[INFO] Sending to Server..");
            processRequest(request, pr, bf, s);
        }

        // close the streams and the socket connections after the requests are sent
        bf.close();
        in.close();
        s.close();
    }

    // Process each request based on the request type (GET or POST)
    private static void processRequest(String request, PrintWriter pr,
                                       BufferedReader bf, Socket socket) throws IOException {
        // Parse the request line for HTTP method and file path
        String requestLine = request.split("\r\n")[0];
        String method = requestLine.split(" ")[0];
        String filePath = requestLine.split(" ")[1];

        // send the request to the server
        pr.println(request);

        if ("POST".equals(method)) {
            // handle post by sending file content
            sendFileContent(CLIENT_ABSOLUTE_DATA_PATH + filePath, pr);
            // read and display the response from the server
            String response = bf.readLine();
            if (response != null) {
                System.out.println("[RESPONSE] " + response);
            }
        } else if ("GET".equals(method)) {
            // handle get by receiving file content from server
            handleGetResponse(bf, filePath, socket);
        }
    }

    private static void handleGetResponse(BufferedReader bf,
                                          String filePath, Socket socket) throws IOException {
        // read the server's response header
        String response;
        while ((response = bf.readLine()) != null && !response.isEmpty()) {
            System.out.println("[RESPONSE] " + response);
        }

        // read file content
        byte[] fileContent = readSocketOutputStream(socket);

        // extract filename from the filepath
        String[] pathSplit = filePath.split("/");
        String fileName = pathSplit[pathSplit.length - 1];

        // save the recieved file content 
        try (FileOutputStream fos = new FileOutputStream(CLIENT_ABSOLUTE_DATA_PATH + fileName)) {
            fos.write(fileContent);
            System.out.println("[INFO] File written successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    // Read byte stream from the socket until a null byte is encountered
    private static byte[] readSocketOutputStream(Socket socket) throws IOException {    
        InputStream stream = socket.getInputStream();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096]; // buffer to read chunks of data
        int bytesRead;
        // read data in chunks until the end or a null byte is reached
        while ((bytesRead = stream.read(buffer)) != -1) {
            byteArrayOutputStream.write(buffer, 0, bytesRead);

            // Check if the last byte received is a null byte (\0)
            if (bytesRead > 0 && buffer[bytesRead - 1] == 0) {
                byte[] receivedBytes = byteArrayOutputStream.toByteArray();
                // Exclude the null byte before stopping
                byteArrayOutputStream.reset();
                byteArrayOutputStream.write(receivedBytes, 0, receivedBytes.length - 1);
                break; // Stop reading
            }
        }
        
        // convert to byte array
        return byteArrayOutputStream.toByteArray();
    }

    // Send file content encoded in Base64 for POST requests
    private static void sendFileContent(String filePath, PrintWriter pr) {
        try {
            byte[] fileContent = Files.readAllBytes(Paths.get(filePath));
            String encodedFileContent = Base64.getEncoder().encodeToString(fileContent);
            pr.println(encodedFileContent);
        } catch (Exception e) {
            System.out.println("[ERROR] " + e.getMessage());
        }
    }
}