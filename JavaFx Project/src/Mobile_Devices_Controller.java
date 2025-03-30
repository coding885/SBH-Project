import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.*;
import java.util.Base64;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.util.Base64;

public class Mobile_Devices_Controller
{
    @FXML
    private Label mobiledevicescount;

    @FXML
    private ImageView cameraFeed;

    private static PipedInputStream pipedInputStream = new PipedInputStream();

    @FXML
    public void initialize() {
        listenForYOLOFeed();
    }

    private void listenForYOLOFeed()
    {
        new Thread(() ->
        {
            try
            {
                BufferedReader reader = new BufferedReader(new InputStreamReader(pipedInputStream));
                String line;
                while ((line = reader.readLine()) != null)
                {
                    if (line.startsWith("COUNT:"))
                    {
                        String count = line.substring(6).trim();
                        Platform.runLater(() -> mobiledevicescount.setText(count));
                    }
                    else if (line.startsWith("IMAGE:"))
                    {
                        try
                        {
                            byte[] imageData = Base64.getDecoder().decode(line.substring(6).trim());
                            Image image = new Image(new ByteArrayInputStream(imageData));
                            Platform.runLater(() -> cameraFeed.setImage(image));
                        }
                        catch (IllegalArgumentException e)
                        {
                            System.err.println("Error decoding image data: " + e.getMessage());
                        }
                    }
                    else
                    {
                        System.out.println("Unrecognized data format: " + line);
                    }
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }).start();
    }

    public static OutputStream getOutputStream() throws IOException
    {

        return new PipedOutputStream(pipedInputStream);
    }
}
