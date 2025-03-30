import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.File;
import java.net.URL;

public class Main extends Application
{

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        // Load the FXML file from the current directory
        URL url = new File("scene.fxml").toURI().toURL();
        Parent root = FXMLLoader.load(getClass().getResource("scene.fxml"));
        Scene scene = new Scene(root, 1000, 600);
        // Load CSS from the current directory
        scene.getStylesheets().add("style.css");

        primaryStage.setTitle("StreamFlow Dashboard");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args)
    {
        launch(args);
    }
}