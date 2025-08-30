
package main.java.com.smartroom;

public interface Sensor {
    String getId();
    String getName();
    SensorType getType();
    double readValue(); // temperature in °C, lux for light; presence returns 1.0 for true, 0.0 for false
}
