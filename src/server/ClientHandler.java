package server;

import java.io.*;
import java.net.Socket;
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
                String msg = bf.readLine();
                if (msg == null) {
                    // disconnected
                    break;
                }
                // request message from the client
                System.out.println("[REQUEST] " + msg);
                String requestLine = msg;
                while ((msg = bf.readLine()) != null && !msg.isEmpty()) {
                    System.out.println("[OPTIONAL] " + msg);
                }
                String[] requestTokens = requestLine.split(" ");
                if ("GET".equals(requestTokens[0])) {
                    this.handleGetRequest(requestTokens);
                } else if ("POST".equals(requestTokens[0])) {
                    this.handlePostRequest(requestTokens[1].split("/")[1]);
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

    void handleGetRequest(String[] requestTokens) {
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
