package server;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

public class ClientHandler implements Runnable {
    private Socket socket;
    private InputStreamReader in;
    private BufferedReader bf;
    private PrintWriter pr;
    private static final String SERVER_DATA_ABSOLUTE_PATH = "src/server/serverdata/";

    public ClientHandler(Socket socket) throws IOException {
        this.socket = socket;
        in = new InputStreamReader(socket.getInputStream());
        bf = new BufferedReader(in);
        pr = new PrintWriter(socket.getOutputStream(), true);
    }

    @Override
    public void run() {
        try {
            while (true) {
                String request = bf.readLine();
                String requestLine = request;

                if (requestLine == null) {
                    // disconnected
                    break;
                }

                // request message from the client
                System.out.println(requestLine);

                while ((request = bf.readLine()) != null && !request.isEmpty()) {
                    System.out.println(request);
                }

                String[] requestTokens = requestLine.split(" ");
                String method = requestTokens[0];
                String filePath = requestTokens[1];

                if ("GET".equals(method)) {
                    this.handleGetRequest(filePath);
                } else if ("POST".equals(method)) {
                    this.handlePostRequest(filePath);
                } else {
                    pr.println("HTTP/1.1 400 Bad Request\r");
                }
            }
            bf.close();
            in.close();
            this.socket.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

    }

    void handleGetRequest(String filePath) {
        pr.println("HTTP/1.1 200 OK");
        pr.println("Content-Type: text/html");
        pr.println("");

        try {
            byte[] fileContent = Files.readAllBytes(Paths.get(SERVER_DATA_ABSOLUTE_PATH + filePath));
            String encodedFileContent = Base64.getEncoder().encodeToString(fileContent);
            pr.println(encodedFileContent);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    void handlePostRequest(String filePath) throws IOException {
        String msg = bf.readLine();
        byte[] fileContent = Base64.getDecoder().decode(msg);
        try (FileOutputStream fos = new FileOutputStream(SERVER_DATA_ABSOLUTE_PATH + filePath)) {
            fos.write(fileContent);
            System.out.println("File written successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
        pr.println("HTTP/1.1 200 OK");
    }
}
