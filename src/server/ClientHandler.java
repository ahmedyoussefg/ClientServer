package server;

import utils.TimeoutHandler;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

public class ClientHandler implements Runnable {
    private Socket socket;
    private InputStreamReader in;
    private BufferedReader bf;
    private PrintWriter pr;
    private static final String SERVER_DATA_ABSOLUTE_PATH = "src/server/serverdata/";
    private TimeoutHandler timeoutHandler;

    public ClientHandler(Socket socket, TimeoutHandler timeoutHandler) throws IOException {
        this.socket = socket;
        this.timeoutHandler = timeoutHandler;
        in = new InputStreamReader(socket.getInputStream());
        bf = new BufferedReader(in);
        pr = new PrintWriter(socket.getOutputStream(), true);
    }

    @Override
    public void run() {
        try {
            while (true) {
                // set the socket timeout based on dynamic timeout handler
                this.socket.setSoTimeout(timeoutHandler.calculateTimeout());
                String request = bf.readLine();
                String requestLine = request;

                if (requestLine == null) {
                    // disconnected, if no request is received
                    break;
                }

                // display request message from the client
                System.out.println("[REQUEST] " + requestLine);

                // read optional headers from the client message
                while ((request = bf.readLine()) != null && !request.isEmpty()) {
                    System.out.println("[OPTIONAL] " + request);
                }

                // parse request line to extract method and file path
                String[] requestTokens = requestLine.split(" ");
                String method = requestTokens[0];
                String filePath = requestTokens[1];

                // handle get/post requests
                if ("GET".equals(method)) {
                    this.handleGetRequest(filePath);
                } else if ("POST".equals(method)) {
                    this.handlePostRequest(filePath);
                } else {
                    pr.println("HTTP/1.1 400 Bad Request\r");
                }
            }
            // close resources when done
            bf.close();
            in.close();
            this.socket.close();
        } catch (IOException e) {
            try {
                socket.close();
                bf.close();
                in.close();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            System.out.println("[INFO] " + e.getMessage());
        }

    }

    // handle get for retrieving a file
    void handleGetRequest(String filePath) {
        try {
            Path path = Paths.get(SERVER_DATA_ABSOLUTE_PATH + filePath);
            if (!Files.exists(path)) {
                // case not found
                handleFileNotFound();
            } else {
                // case found
                getExistingFile(filePath, path);
            }
        } catch (Exception e) {
            System.out.println("[ERROR] " + e.getMessage());
        }
    }

    private void getExistingFile(String filePath, Path path) throws IOException {
        // send 200 OK status and content details
        pr.println("HTTP/1.1 200 OK");
        String contentType = this.getContentType(filePath);
        pr.println("Content-Type: " + contentType);
        pr.println("Content-Length: " + Files.size(path));
        pr.println("");
        pr.flush();

        // read and send file content as bytes
        byte[] fileContent = Files.readAllBytes(path);
        OutputStream stream = socket.getOutputStream();
        stream.write(fileContent);
        stream.write("\0".getBytes()); // null byte to indicate end of file
        stream.flush();
    }

    // handle a 404 error by sending "Not Found" response
    private void handleFileNotFound() {
        pr.println("HTTP/1.1 404 Not Found");
        pr.println("Content-Type: text/html");
        pr.println("Content-Length: " + 0);
        pr.println("");
        pr.println("404 Not Found");
        pr.flush();
    }

    // handle POST request for saving a file to the server
    void handlePostRequest(String filePath) throws IOException {
        // receive the file content as a Base64 encoded string
        String msg = bf.readLine();
        byte[] fileContent = Base64.getDecoder().decode(msg);
        try (FileOutputStream fos = new FileOutputStream(SERVER_DATA_ABSOLUTE_PATH + filePath)) {
            // write the decoded content to the specified file path
            fos.write(fileContent);
            System.out.println("[INFO] File written successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Send 200 OK response
        pr.println("HTTP/1.1 200 OK");
    }

    // determine the content type of a file based on its extension
    String getContentType(String filePath) {
        int lastDotIndex = filePath.lastIndexOf('.');
        return switch (filePath.substring(lastDotIndex)) {
            case ".html", ".htm" -> "text/html";
            case ".txt" -> "text/plain";
            case ".jpg", ".jpeg" -> "image/jpeg";
            case ".png" -> "image/png";
            default -> "application/octet-stream";
        };
    }
}
