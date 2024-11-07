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
                // request message from the client
                System.out.println("[REQUEST] " + msg);
                String requestLine = msg;
                if (msg == null) {
                    // disconnected
                    break;
                }
                msg = bf.readLine();
                while (msg != null && !msg.isEmpty()) {
                    System.out.println("[OPTIONAL] " + msg);
                    msg = bf.readLine();
                }
                if (msg == null) {
                    // disconnected
                    break;
                }
                String[] requestTokens = requestLine.split(" ");
                if ("GET".equals(requestTokens[0])) {
                    this.handleGetRequest(requestTokens);
                } else if ("POST".equals(requestTokens[0])) {
                    this.handlePostRequest();
                } else {
                    pr.println("HTTP/1.1 400 Bad Request");
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

    void handlePostRequest() throws IOException {
        String msg = bf.readLine();
        StringBuilder requestBody = new StringBuilder();
        while (msg != null) {
            requestBody.append(msg);
            msg = bf.readLine();
        }
        // to avoid overwriting duplicates
        String filename = System.currentTimeMillis() + "-" +
                Thread.currentThread().threadId() + "_FILE";
        File newFile = new File(filename);
        if (newFile.createNewFile()) {
            BufferedWriter bw = new BufferedWriter(new FileWriter(newFile));
            bw.write(requestBody.toString());
            bw.close();
        }
        else {
            System.out.println("[ERROR] Server couldn't save file after POST request.");
        }
    }
}
