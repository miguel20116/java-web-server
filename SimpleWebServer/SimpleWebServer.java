import java.io.*;
import java.net.*;
import java.nio.file.Files;

public class SimpleWebServer {
    public static void main(String[] args) throws IOException {
        int port = 8080;
        File rootFolder = new File("www"); // folder where your website files are stored
        if (!rootFolder.exists()) rootFolder.mkdir();

        ServerSocket serverSocket = new ServerSocket(port, 50, InetAddress.getByName("0.0.0.0"));
        System.out.println("Server running at http://0.0.0.0:" + port);
        System.out.println("Serving files from folder: " + rootFolder.getAbsolutePath());

        while (true) {
            Socket clientSocket = serverSocket.accept();
            new Thread(new ClientHandler(clientSocket, rootFolder)).start();
        }
    }
}

class ClientHandler implements Runnable {
    private Socket client;
    private File rootFolder;

    public ClientHandler(Socket client, File rootFolder) {
        this.client = client;
        this.rootFolder = rootFolder;
    }

    @Override
    public void run() {
        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            OutputStream out = client.getOutputStream()
        ) {
            String line = in.readLine();
            if (line == null || line.length() > 2048) {
                sendBadRequest(out);
                return;
            }

            System.out.println("[" + client.getInetAddress() + "] " + line);

            if (!line.startsWith("GET")) {
                sendBadRequest(out);
                return;
            }

            // Parse requested path
            String[] parts = line.split(" ");
            String requestedPath = parts.length >= 2 ? parts[1] : "/";
            if (requestedPath.equals("/")) requestedPath = "/index.html";

            File file = new File(rootFolder, requestedPath);
            if (file.isDirectory()) {
                file = new File(file, "index.html");
            }

            if (!file.exists() || !file.getCanonicalPath().startsWith(rootFolder.getCanonicalPath())) {
                send404(out);
                return;
            }

            // Read headers but limit to 50 lines
            int headerCount = 0;
            while ((line = in.readLine()) != null && !line.isEmpty()) {
                headerCount++;
                if (headerCount > 50) break;
            }

            // Send file
            byte[] content = Files.readAllBytes(file.toPath());
            String header = "HTTP/1.1 200 OK\r\n" +
                            "Content-Type: " + guessContentType(file) + "\r\n" +
                            "Content-Length: " + content.length + "\r\n" +
                            "Connection: close\r\n\r\n";

            out.write(header.getBytes());
            out.write(content);
            out.flush();

        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        } finally {
            try { client.close(); } catch (IOException ignored) {}
        }
    }

    private void sendBadRequest(OutputStream out) throws IOException {
        String html = "<html><body><h1>400 Bad Request</h1></body></html>";
        String header = "HTTP/1.1 400 Bad Request\r\n" +
                        "Content-Type: text/html\r\n" +
                        "Content-Length: " + html.length() + "\r\n" +
                        "Connection: close\r\n\r\n";
        out.write(header.getBytes());
        out.write(html.getBytes());
    }

    private void send404(OutputStream out) throws IOException {
        String html = "<html><body><h1>404 Not Found</h1></body></html>";
        String header = "HTTP/1.1 404 Not Found\r\n" +
                        "Content-Type: text/html\r\n" +
                        "Content-Length: " + html.length() + "\r\n" +
                        "Connection: close\r\n\r\n";
        out.write(header.getBytes());
        out.write(html.getBytes());
    }

    private String guessContentType(File file) {
        try {
            String type = Files.probeContentType(file.toPath());
            return type != null ? type : "application/octet-stream";
        } catch (IOException e) {
            return "application/octet-stream";
        }
    }
}
