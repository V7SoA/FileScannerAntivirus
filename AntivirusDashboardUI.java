import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class AntivirusDashboardUI extends Application {
    @Override
    public void start(Stage stage) {
        stage.setTitle("Antivirus Dashboard");

        TextArea logArea = new TextArea();
        logArea.setEditable(false);

        try {
            String logs = new String(Files.readAllBytes(Paths.get("scan_log.txt")));
            logArea.setText(logs);
        } catch (IOException e) {
            logArea.setText("Log file not found.");
        }

        VBox layout = new VBox(logArea);
        Scene scene = new Scene(layout, 600, 400);
        stage.setScene(scene);
        stage.show();
    }
}
