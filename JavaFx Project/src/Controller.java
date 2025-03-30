import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.*;
import javafx.scene.Parent;
import javafx.scene.Scene;
import com.fazecast.jSerialComm.SerialPort;
import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import com.google.firebase.database.*;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

import org.opencv.core.Core;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;
import org.opencv.core.Mat;
import org.opencv.highgui.HighGui;
public class Controller implements Initializable
{
    @FXML
    private Button openCameraButton;

    private VideoCapture camera;
    private boolean isCameraOpen = false;


    @FXML
    private BorderPane mainPane;
    @FXML
    private Label mobiledevicescount;

    @FXML
    private Label wificountlabel;

    @FXML
    private Label blecountlabel;

    @FXML
    private TableView<TableData> tableView;

    @FXML
    private TableColumn<TableData, String> nameColumn;

    @FXML
    private TableColumn<TableData, String> macColumn;

    @FXML
    private TableColumn<TableData, String> signalColumn;

    @FXML
    private TableColumn<TableData, String> typeColumn;


    @FXML
    private void handleMobileDevicesClick()
    {
        try {
            // Load the mobile devices UI view
            loadView("mobile_devices.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Uncomment and use this helper method to load different views
    @FXML
    private void loadView(String fxmlFile) throws IOException
    {
        Parent view = FXMLLoader.load(getClass().getResource("mobile_devices.fxml"));
        mainPane.setCenter(view);
    }

    @FXML
    private void toggleCamera() {
        if (!isCameraOpen) {
            // Open camera
            System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
            camera = new VideoCapture();
            camera.open(0); // Use default camera (usually webcam)

            if (camera.isOpened()) {
                isCameraOpen = true;
                openCameraButton.setText("Close Camera");

                // Start camera feed in new thread
                new Thread(() -> {
                    Mat frame = new Mat();
                    while (isCameraOpen) {
                        if (camera.read(frame)) {
                            // Here you would convert the Mat to JavaFX image and show it
                            // This is just a placeholder - you'll need to implement the conversion
                            // Platform.runLater(() -> updateCameraPreview(frame));
                        }
                        try {
                            Thread.sleep(33); // ~30 fps
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        } else {
            // Close camera
            isCameraOpen = false;
            openCameraButton.setText("Open Camera");
            if (camera != null && camera.isOpened()) {
                camera.release();
            }
        }
    }


    @FXML
    private void showLiveYOLO() {
        new Thread(() -> {
            try {
                ProcessBuilder processBuilder = new ProcessBuilder("python", "C:\\Users\\aritr\\IdeaProjects\\Aritra Python Intellij\\detect.py");
                processBuilder.redirectErrorStream(true);
                Process process = processBuilder.start();
                process.waitFor();  // Wait until the live window closes
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }






    private final ObservableList<TableData> data = FXCollections.observableArrayList();
    private int wifi_device_count=0;
    private int ble_device_count=0;



    private void readFromSerial() {
        new Thread(() -> {
            try {
                SerialPort serialPort = SerialPort.getCommPort("COM3"); // Change if needed
                serialPort.setBaudRate(115200);
                serialPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 1000, 0);

                if (!serialPort.openPort()) {
                    System.out.println("Failed to open port!");
                    return;
                }

                BufferedReader reader = new BufferedReader(new InputStreamReader(serialPort.getInputStream()));
                int wifiCount = 0; // Counter for WiFi devices

                while (true) {
                    if (serialPort.bytesAvailable() > 0) {
                        String line = reader.readLine(); // Read incoming data
                        if (line == null || line.trim().isEmpty()) continue; // Ignore empty lines

                        String name = "Unknown", mac = "Unknown", signal = "Unknown", type = "Unknown";

                        if (line.startsWith("WIFI:"))
                        {
                            // Update WiFi count
                            wifi_device_count = Integer.parseInt(line.replace("WIFI:", "").trim());
                            System.out.println("Total WiFi Devices: " + wifiCount);
                        }
                        else if (line.startsWith("WIFI_SSID:")) {
                            // Process WiFi device details
                            String[] parts = line.replace("WIFI_SSID:", "").split(",RSSI:");
                            if (parts.length == 2) {
                                name = parts[0].trim();
                                signal = parts[1].trim();
                                type = "WiFi";
                            }
                        }
                        else if (line.startsWith("BLE_DEVICE:"))
                        {
                            // Process BLE device details
                            String[] parts = line.replace("BLE_DEVICE:", "").split(",RSSI:");
                            if (parts.length == 2)
                            {
                                mac = parts[0].trim();
                                signal = parts[1].trim();
                                type = "BLE";
                            }
                        }
                        else if (line.startsWith("BLE:"))
                        {
                            // Update WiFi count
                            ble_device_count = Integer.parseInt(line.replace("BLE:", "").trim());
                            System.out.println("Total BLE Devices: " + ble_device_count);
                        }

                        // Debugging: Print extracted values
                        System.out.println("Extracted -> Name: " + name + ", MAC: " + mac + ", RSSI: " + signal + ", Type: " + type);

                        // Add to TableView & Update UI
                        if (!type.equals("Unknown"))
                        {
                            String finalName = name, finalMac = mac, finalSignal = signal, finalType = type;
                            Platform.runLater(() -> {
                                data.add(new TableData(finalName, finalMac, finalSignal, finalType));
                                updateMetricCards(wifi_device_count,ble_device_count); // Update UI labels for WiFi count
                            });
                        }
                    } else {
                        Thread.sleep(500);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }


    private void startYOLO() {
        new Thread(() -> {
            try {
                OutputStream mobileDevicesOutputStream = Mobile_Devices_Controller.getOutputStream();

                ProcessBuilder processBuilder = new ProcessBuilder("python", "C:\\Users\\aritr\\IdeaProjects\\Aritra Python Intellij\\yolodetct2.py");
                processBuilder.redirectErrorStream(true);
                Process process = processBuilder.start();

                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println("YOLO Output: " + line);

                    // Forward everything to Mobile_Devices_Controller
                    mobileDevicesOutputStream.write((line + "\n").getBytes());
                    mobileDevicesOutputStream.flush();

                    // Optional: If you still want to update something in the main controller based on counts
                    if (line.startsWith("COUNT:")) {
                        int count = Integer.parseInt(line.substring(6).trim());
                        Platform.runLater(() -> updateMobileCount(count));
                    }
                }

                process.waitFor();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }


/* This works fine
    private void runYOLODetection() {
        new Thread(() -> {
            try {
                ProcessBuilder pb = new ProcessBuilder("python", "C:\\Users\\aritr\\IdeaProjects\\Aritra Python Intellij\\yolodetect.py");
                pb.redirectErrorStream(true);
                Process process = pb.start();

                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println("YOLO Output: " + line);

                    // Extract only numbers
                    if (line.matches("\\d+")) {  // Check if line is only a number
                        int count = Integer.parseInt(line.trim());
                        Platform.runLater(() -> updateMobileCount(count));
                    } else {
                        System.out.println("Invalid YOLO output: " + line);
                    }
                }

                process.waitFor();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

*/



    @Override
    public void initialize(URL location, ResourceBundle resources)
    {


        // Setting up TableView columns
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        macColumn.setCellValueFactory(cellData -> cellData.getValue().macAddressProperty());
        signalColumn.setCellValueFactory(cellData -> cellData.getValue().signalStrengthProperty());
        typeColumn.setCellValueFactory(cellData -> cellData.getValue().deviceTypeProperty());
        tableView.setItems(data); // Bind data to TableView


        readFromSerial();
        //runYOLODetection();
        //startYOLOBackground();
        startYOLO();
    }



    private void updateMetricCards(int wifi_device_count,int ble_device_count)
    {
        wificountlabel.setText(String.valueOf(wifi_device_count));
        blecountlabel.setText(String.valueOf(ble_device_count));
    }


    public void updateMobileCount(int count)
    {
        Platform.runLater(() ->
        {
            // 🔹 Update UI Label (Replace 'mobileCountLabel' with your actual UI element)
            mobiledevicescount.setText(String.valueOf(count));

            // 🔹 Update Firebase
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference ref = database.getReference("/detections/mobile_count");
            ref.setValueAsync(count);
        });
    }


}