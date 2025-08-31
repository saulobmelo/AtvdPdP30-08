
package main.java.com.smartroom.sensors;

import java.util.Random;

public class FakeTemperatureSensor implements Sensor {
    private final String id;
    private final String name;
    private final Random rnd = new Random();

    public FakeTemperatureSensor(String id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override public String getId() { return id; }
    @Override public String getName() { return name; }
    @Override public SensorType getType() { return SensorType.TEMPERATURE; }

    @Override
    public double readValue() {
        // 22.0 to 31.0 oscillation
        return 22.0 + rnd.nextDouble() * 9.0;
    }
}
