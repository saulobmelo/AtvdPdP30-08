
package main.java.com.smartroom.sensors;

import java.util.Random;

public class FakePresenceSensor implements Sensor {
    private final String id;
    private final String name;
    private final Random rnd = new Random();

    public FakePresenceSensor(String id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override public String getId() { return id; }
    @Override public String getName() { return name; }
    @Override public SensorType getType() { return SensorType.PRESENCE; }

    @Override
    public double readValue() {
        // 40% chance of presence
        return rnd.nextDouble() < 0.4 ? 1.0 : 0.0;
    }
}
