
package main.java.com.smartroom.actuators;

public class SimpleActuator implements Actuator {
    private final String id;
    private final String name;
    private boolean on;

    public SimpleActuator(String id, String name) {
        this.id = id;
        this.name = name;
        this.on = false;
    }

    @Override public String getId() { return id; }
    @Override public String getName() { return name; }

    @Override
    public void turnOn() { on = true; }

    @Override
    public void turnOff() { on = false; }

    @Override
    public boolean isOn() { return on; }
}
