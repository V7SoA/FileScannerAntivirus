import java.io.*;
import java.nio.file.*;         //utilitie for file and directory opn

public class FileScanner {         //class declaration
    private FileScannerLogger logger;       //private field declare, for fcl to scan related msg
   
    public FileScanner(FileScannerLogger logger) {    //fcl obj as para
                this.logger = logger;    //this log to class's log; canner to log msgs during its opn
    }
            //scans file directory; folder dir where sus file will be moved
    public void scan(File directory, File quarantineDir) {
        File[] files = directory.listFiles();  //list files in dir
        if (files == null || files.length == 0) {  //no such directory or empty
            logger.log("No files found in directory: " + directory.getAbsolutePath());
            return;
        }

        for (File file : files) {     //iterates
            if (file.isFile() && isSuspicious(file)) {    //checks
                logger.log("Suspicious file detected: " + file.getName());   
                quarantineFile(file, quarantineDir);  //move the file to qur
            }
        }
    }
    // 🔧 Add to FileScanner.java
public void scanFile(File file, File quarantineDir) {
    if (file.isFile()) {
        // Replace this with your actual malware detection logic
        if (file.getName().toLowerCase().contains("virus")) {
            File quarantineFile = new File(quarantineDir, file.getName());
            boolean success = file.renameTo(quarantineFile);
            if (success) {
                logger.log("[RealTime] Quarantined file: " + file.getAbsolutePath());
            } else {
                logger.log("[RealTime] Failed to quarantine: " + file.getAbsolutePath());
            }
        } else {
            logger.log("[RealTime] Clean file: " + file.getAbsolutePath());
        }
    }
}

                     //based on ae if its sus
    private boolean isSuspicious(File file) {
        String fileName = file.getName().toLowerCase();
        return fileName.contains("virus") || fileName.contains("malware");//if file name sus keywor
    }
                 
    private void quarantineFile(File file, File quarantineDir) {
        try {
            Path sourcePath = file.toPath();  
            Path destinationPath = Paths.get(quarantineDir.getAbsolutePath(), file.getName());
            Files.move(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
            logger.log("File " + file.getName() + " moved to quarantine.");
        } catch (IOException e) {
            logger.log("Failed to quarantine file: " + file.getName() + " due to: " + e.getMessage());
        }
    }
}
