package utils;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class RequestsReader {

    public static List<String> readRequests(String filePath) {
        List<String> requests = new ArrayList<>();
        try {
            List<String> fileContent = Files.readAllLines(Paths.get(filePath));
            requests.addAll(fileContent);
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
        }
    }
}
