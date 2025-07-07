import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class FileScannerLogger {
    private final PrintWriter writer;

    public FileScannerLogger(String logFilePath) throws IOException {
        this.writer = new PrintWriter(new FileWriter(logFilePath, true), true);
    }

    public void log(String message) {
        String log = "[" + java.time.LocalDateTime.now() + "] " + message;
        System.out.println(log);
        writer.println(log);
    }

    public void close() {
        writer.close();
    }
}
