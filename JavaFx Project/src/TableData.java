
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class TableData
{
    private final StringProperty name;
    private final StringProperty macAddress;
    private final StringProperty signalStrength;
    private final StringProperty deviceType;

    public TableData(String name, String macAddress, String signalStrength, String deviceType)
    {
        this.name = new SimpleStringProperty(name);
        this.macAddress = new SimpleStringProperty(macAddress);
        this.signalStrength= new SimpleStringProperty(signalStrength);
        this.deviceType = new SimpleStringProperty(deviceType);
    }

    // Getters and setters
    public String getName()
    {
        return name.get();
    }

    public void setName(String value)
    {
        name.set(value);
    }

    public StringProperty nameProperty()
    {
        return name;
    }

    public String getmacAddress()
    {
        return macAddress.get();
    }

    public void setMacAddress(String value)
    {
        macAddress.set(value);
    }

    public StringProperty macAddressProperty()
    {
        return macAddress;
    }

    public String getsignalStrength()
    {
        return signalStrength.get();
    }

    public void setsignalStrength(String value)
    {
        signalStrength.set(value);
    }

    public StringProperty signalStrengthProperty()
    {
        return signalStrength;
    }

    public String getdeviceType()
    {
        return deviceType.get();
    }

    public void setDeviceType(String value)
    {
        deviceType.set(value);
    }

    public StringProperty deviceTypeProperty()
    {
        return deviceType;
    }
}