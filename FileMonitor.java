import java.io.File;
import java.io.IOException;
import java.nio.file.*;

public class FileMonitor extends Thread {
    private Path watchPath;

    public FileMonitor() {
        this.watchPath = Paths.get("C:\\Users\\Dell\\Downloads"); // Set your desired watch path
    }

    @Override
    public void run() {
        try {
            WatchService watchService = FileSystems.getDefault().newWatchService();

            // Register for both CREATE and MODIFY events
            watchPath.register(watchService, 
                StandardWatchEventKinds.ENTRY_CREATE,
                StandardWatchEventKinds.ENTRY_MODIFY);

            FileScannerLogger logger = new FileScannerLogger("scan_log.txt");
            ThreatDatabase db = new ThreatDatabase();
            AntivirusApp app = new AntivirusApp(logger, db);

            File quarantineDir = new File("quarantine");
            if (!quarantineDir.exists()) quarantineDir.mkdir();

            System.out.println("[✓] Real-time monitoring started at: " + watchPath);

            while (true) {
                WatchKey key = watchService.take();
                for (WatchEvent<?> event : key.pollEvents()) {
                    WatchEvent.Kind<?> kind = event.kind();

                    Path affectedPath = watchPath.resolve((Path) event.context());
                    File file = affectedPath.toFile();

                    if (file.exists() && file.isFile()) {
                        if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
                            System.out.println("[+] New file detected: " + file.getName());
                            app.scanFile(file, quarantineDir);
                        } else if (kind == StandardWatchEventKinds.ENTRY_MODIFY) {
                            System.out.println("[*] File modified/accessed: " + file.getName());
                            app.scanFile(file, quarantineDir);
                        }
                    }
                }
                key.reset();
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
