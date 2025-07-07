import java.io.File;
import java.security.MessageDigest;
import java.nio.file.Files;

public class FileAnalysis {
    public static boolean isSuspicious(File file) {
        String name = file.getName().toLowerCase();
        return name.endsWith(".exe") || name.endsWith(".bat") || name.contains("crack") || name.contains("hack");
    }

    public static String calculateFileHash(File file) {
        try {
            byte[] content = Files.readAllBytes(file.toPath());
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(content);

            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes)
                sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            return null;
        }
    }
}
