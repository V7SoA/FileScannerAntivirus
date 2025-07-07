import java.sql.*;
import java.time.LocalDateTime;

public class DatabaseLogger {
    private Connection connection;

    public DatabaseLogger() throws SQLException {
        String url = "jdbc:postgresql://localhost:5432/your_database"; // Replace your DB name
        String user = "postgres"; // Replace with your DB user
        String password = "root"; // Replace with your DB password
        connection = DriverManager.getConnection(url, user, password);
    }

    public boolean hasPreviousDecision(String fileHash) throws SQLException {
        String query = "SELECT user_response FROM file_events WHERE file_hash = ? ORDER BY event_time DESC LIMIT 1";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, fileHash);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        }
    }

    public String getPreviousResponse(String fileHash) throws SQLException {
        String query = "SELECT user_response FROM file_events WHERE file_hash = ? ORDER BY event_time DESC LIMIT 1";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, fileHash);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("user_response");
            }
        }
        return null;
    }

    public void logEvent(String fileName, String filePath, long fileSize, String fileHash, boolean isThreat, String userResponse) throws SQLException {
        String insert = "INSERT INTO file_events (file_name, file_path, file_size, file_hash, is_threat, user_response, event_time) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(insert)) {
            stmt.setString(1, fileName);
            stmt.setString(2, filePath);
            stmt.setLong(3, fileSize);
            stmt.setString(4, fileHash);
            stmt.setBoolean(5, isThreat);
            stmt.setString(6, userResponse);
            stmt.setTimestamp(7, Timestamp.valueOf(LocalDateTime.now()));
            stmt.executeUpdate();
        }
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed())
                connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
