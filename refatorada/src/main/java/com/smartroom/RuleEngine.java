
package main.java.com.smartroom;

import java.util.Map;

public interface RuleEngine {
    /**
     * Apply automation rules based on sensor readings.
     * Returns a map of actuatorName -> on/off state after rules.
     */
    Map<String, Boolean> apply(Map<String, Double> sensorValues, Map<String, Boolean> currentActuators);
}
