import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable{
    private Socket socket;
    private InputStreamReader in;
    private BufferedReader bf;
    private PrintWriter pr;

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
                int contentLength = 2048;
                // request message from the client
                System.out.println("[REQUEST] " + msg);
                String requestLine = msg;
                while ((msg = bf.readLine()) != null && !msg.isEmpty()) {
                    System.out.println("[OPTIONAL] " + msg);
                    if (msg.contains("Content-Length")) {
                        // Extract the value after "Content-Length: "
                        String[] parts = msg.trim().split(":");
                        if (parts.length == 2) {
                            try {
                                contentLength = Integer.parseInt(parts[1]);
                            } catch (NumberFormatException e) {
                                System.err.println("[ERROR] Invalid Content-Length format.");
                            }
                        }
                    }
                }
                String[] requestTokens = requestLine.split(" ");
                if ("GET".equals(requestTokens[0])) {
                    this.handleGetRequest(requestTokens);
                } else if ("POST".equals(requestTokens[0])) {
                    this.handlePostRequest(requestTokens, contentLength);
                } else {
                    pr.println("HTTP/1.1 400 Bad Request\r");
                }
            }
            bf.close();
            in.close();
            this.socket.close();
        } catch(IOException e) {
            System.out.println(e.getMessage());
        }

    }

    void handleGetRequest(String[] requestTokens) {
    }

    void handlePostRequest(String[] requestTokens, int contentLength) throws IOException {
        DataInputStream dis = new DataInputStream(socket.getInputStream());
        byte[] fileBytes = new byte[contentLength];
        dis.readFully(fileBytes);
        try (FileOutputStream fos = new FileOutputStream(requestTokens[1])) {
            fos.write(fileBytes);
            pr.print("HTTP/1.1 200 OK\\r\\n");
        }
    }
}
