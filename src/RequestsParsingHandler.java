import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class RequestsParsingHandler {

    public static List<String> readRequests(String filePath) {

        List<String> requests = new ArrayList<>();

        try {
            List<String> fileContent = Files.readAllLines(Paths.get(filePath));
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < fileContent.size(); i++) {
                String s = fileContent.get(i);
                if (s.isEmpty()) {
                    sb.append("\r\n");
                    if (isPostRequest(sb.toString())) {
                        String entityBody = getEntityBody(fileContent, i + 1);
                        sb.append(entityBody).append("\r\n");
                        i += entityBody.split("\r\n").length + 1;
                    }
                    requests.add(sb.toString());
                    sb = new StringBuilder();
                    continue;
                }
                sb.append(s).append("\r\n");
            }

            if (!sb.isEmpty()) {
                sb.append("\r\n");
                requests.add(sb.toString());
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

        return requests;
    }

    private static boolean isPostRequest(String string) {
        return string.contains("POST");
    }

    private static String getEntityBody(List<String> fileContent, int start) {
        StringBuilder sb = new StringBuilder();
        int i = start;
        while (i < fileContent.size() && !fileContent.get(i).isEmpty()) {
            sb.append(fileContent.get(i));
            sb.append("\r\n");
            i++;
        }

        return sb.toString();
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
