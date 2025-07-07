import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SimpleHttpServer {
    public static void main(String[] args) throws Exception {
        // Create HTTP server on port 8000
        HttpServer server = HttpServer.create(new java.net.InetSocketAddress(8000), 0);
        
        // Set the root handler to serve the index.html and other static files
        server.createContext("/", new StaticFileHandler());
        
        // Set the /runJar handler to execute the .jar file
        server.createContext("/runJar", new RunJarHandler());
        
        // Set the executor for handling requests
        ExecutorService executor = Executors.newFixedThreadPool(10);
        server.setExecutor(executor); 
        
        // Start the server
        server.start();
        System.out.println("Server started at http://localhost:8000");
    }
    
    // Handler for serving static files (HTML, CSS, JS, etc.)
    static class StaticFileHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // Get the requested file path
            String path = exchange.getRequestURI().getPath();
            if (path.equals("/")) {
                path = "/index.html"; // Default to index.html
            }

            // Define the file path relative to the project directory
            String filePath = "." + path; // This assumes the files are in the root of the project
            
            // Check if the file exists
            File file = new File(filePath);
            if (!file.exists()) {
                // Send a 404 response if the file is not found
                exchange.sendResponseHeaders(404, -1);
                return;
            }

            // Read the file content
            byte[] fileContent = Files.readAllBytes(file.toPath());

            // Set the content type based on file extension
            String contentType = "text/html";
            if (filePath.endsWith(".css")) {
                contentType = "text/css";
            } else if (filePath.endsWith(".js")) {
                contentType = "application/javascript";
            } else if (filePath.endsWith(".jpg") || filePath.endsWith(".jpeg")) {
                contentType = "image/jpeg";
            } else if (filePath.endsWith(".png")) {
                contentType = "image/png";
            }

            // Set the response headers and send the file content
            exchange.getResponseHeaders().set("Content-Type", contentType);
            exchange.sendResponseHeaders(200, fileContent.length);

            OutputStream os = exchange.getResponseBody();
            os.write(fileContent);
            os.close();
        }
    }

    // Handler for executing the .jar file and redirecting
    static class RunJarHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                // Execute the .jar file silently in the background
                ProcessBuilder builder = new ProcessBuilder("java", "-jar", "C:/Users/Dell/Desktop/java/Antivirus/antivirusapp.jar");
                builder.directory(new File("C:/Users/Dell/Desktop/java/Antivirus"));
                builder.inheritIO();  // This line can be removed if you don't want any output in the console
                Process process = builder.start();
                process.waitFor(); // Wait for the process to finish

                // After the .jar runs, redirect to the service page
                exchange.getResponseHeaders().set("Location", "service.html"); // Redirect to the services page
                exchange.sendResponseHeaders(302, -1); // 302: Found (Redirect)
            } catch (Exception e) {
                // Handle errors, send a 500 response if something goes wrong
                e.printStackTrace();
                exchange.sendResponseHeaders(500, -1); // Internal server error
            }
        }
    }
}
