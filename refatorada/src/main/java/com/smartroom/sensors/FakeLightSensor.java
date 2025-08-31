
package main.java.com.smartroom.sensors;

import java.util.Random;

public class FakeLightSensor implements Sensor {
    private final String id;
    private final String name;
    private final Random rnd = new Random();

    public FakeLightSensor(String id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override public String getId() { return id; }
    @Override public String getName() { return name; }
    @Override public SensorType getType() { return SensorType.LIGHT; }

    @Override
    public double readValue() {
        // lux between 100 and 900
        return 100 + rnd.nextInt(801);
    }
}
