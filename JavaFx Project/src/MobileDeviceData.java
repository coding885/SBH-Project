import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class MobileDeviceData
{
    private final StringProperty deviceId;
    private final StringProperty deviceType;
    private final StringProperty lastSeen;
    private final StringProperty location;

    public MobileDeviceData(String deviceId, String deviceType, String lastSeen, String location) {
        this.deviceId = new SimpleStringProperty(deviceId);
        this.deviceType = new SimpleStringProperty(deviceType);
        this.lastSeen = new SimpleStringProperty(lastSeen);
        this.location = new SimpleStringProperty(location);
    }

    public StringProperty deviceIdProperty() {
        return deviceId;
    }

    public StringProperty deviceTypeProperty() {
        return deviceType;
    }

    public StringProperty lastSeenProperty() {
        return lastSeen;
    }

    public StringProperty locationProperty() {
        return location;
    }
}