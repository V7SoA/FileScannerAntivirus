import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import javax.swing.*;

public class AntivirusApp {
    private final FileScannerLogger logger;
    private final ThreatDatabase db;

    public AntivirusApp(FileScannerLogger logger, ThreatDatabase db) {
        this.logger = logger;
        this.db = db;
    }

    public void scanFile(File file, File quarantineDir) {
        String hash = FileAnalysis.calculateFileHash(file);

        if (hash == null) {
            logger.log("Could not read file: " + file.getName());
            return;
        }

        // If previously blocked by user, auto-block now
        if (db.isThreat(hash)) {
            logger.log("Threat detected (auto-blocked): " + file.getName());
            quarantineOrDelete(file, quarantineDir);
            return;
        }

        // Check if file is suspicious by your existing logic
        if (FileAnalysis.isSuspicious(file)) {
            int choice = JOptionPane.showConfirmDialog(null,
                "This file may be dangerous. Open it or block it?\nFile: " + file.getName(),
                "Potential Threat Detected",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

            if (choice == JOptionPane.NO_OPTION) {
                // User blocked file → delete/quarantine + log + save decision in DB
                quarantineOrDelete(file, quarantineDir);
                db.saveDecision(hash, false);
                logger.log("Threat removed by user: " + file.getName());
            } else {
                // User allowed file → open + log + save decision
                openFile(file);
                db.saveDecision(hash, true);
                logger.log("User allowed file: " + file.getName());
            }
        } else {
            // File safe, just log it
            logger.log("Safe file: " + file.getName());
        }
    }

    private void quarantineOrDelete(File file, File quarantineDir) {
        try {
            // Try to move to quarantine folder first
            if (quarantineDir.exists() && quarantineDir.isDirectory()) {
                File target = new File(quarantineDir, file.getName());
                if (file.renameTo(target)) {
                    logger.log("File moved to quarantine: " + file.getName());
                    return;
                }
            }
            // If move fails, delete file
            if (file.delete()) {
                logger.log("File deleted: " + file.getName());
            } else {
                logger.log("Failed to delete or quarantine: " + file.getName());
            }
        } catch (Exception e) {
            logger.log("Error quarantining/deleting file: " + file.getName() + " - " + e.getMessage());
        }
    }

    private void openFile(File file) {
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(file);
                logger.log("Opened file: " + file.getName());
            } else {
                logger.log("Desktop not supported, cannot open file: " + file.getName());
            }
        } catch (IOException e) {
            logger.log("Failed to open file: " + file.getName() + " - " + e.getMessage());
        }
    }
}







