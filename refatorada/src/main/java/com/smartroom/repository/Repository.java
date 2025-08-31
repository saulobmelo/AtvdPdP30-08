
package main.java.com.smartroom.repository;

import java.time.LocalDateTime;
import java.util.Map;

public interface Repository {
    void appendSensorSnapshot(LocalDateTime ts, Map<String, Double> values, Map<String, Boolean> actuators);
    void writeReport(String report);
}
