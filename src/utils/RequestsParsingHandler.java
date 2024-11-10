package utils;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class RequestsParsingHandler {
    
    /**
     * Reads HTTP requests from a specified file, with each request separated by an empty line.
     * @param filePath The path of the file containing HTTP requests.
     * @return A list of HTTP request strings.
     */
    public static List<String> readRequests(String filePath) {

        List<String> requests = new ArrayList<>();

        try {
            // Read all lines from the file
            List<String> fileContent = Files.readAllLines(Paths.get(filePath));
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < fileContent.size(); i++) {
                String s = fileContent.get(i);
                // If an empty line is encountered, the current request is complete
                if (s.isEmpty()) {
                    requests.add(sb.toString());
                    sb = new StringBuilder();
                    continue;
                }
                sb.append(s).append("\r\n");
            }

            if (!sb.isEmpty()) {
                requests.add(sb.toString());
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

        return requests;
    }

    public static void main(String[] args) {
        String filePath = "requests.txt";
        List<String> requests = readRequests(filePath);
        for (String request : requests) {
            System.out.print(request);
            System.out.println("==================================");
        }
    }
}
