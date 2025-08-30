
package main.java.com.smartroom;

public interface Actuator {
    String getId();
    String getName();
    void turnOn();
    void turnOff();
    boolean isOn();
}
