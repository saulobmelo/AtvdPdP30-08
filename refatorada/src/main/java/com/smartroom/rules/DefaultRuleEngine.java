
package main.java.com.smartroom.rules;

import java.util.HashMap;
import java.util.Map;

/**
 * Encapsulates the automation rules.
 * - Turn LIGHT on if presence == true AND lux < 300
 * - Turn FAN on if temperature > 28°C
 */
public class DefaultRuleEngine implements RuleEngine {

    @Override
    public Map<String, Boolean> apply(Map<String, Double> sensorValues, Map<String, Boolean> currentActuators) {
        Map<String, Boolean> out = new HashMap<>(currentActuators);

        double temp = sensorValues.getOrDefault("temp", 25.0);
        boolean presence = sensorValues.getOrDefault("pres", 0.0) >= 0.5;
        double lux = sensorValues.getOrDefault("lux", 600.0);

        // Light rule
        boolean lightOn = presence && lux < 300.0;
        out.put("Luz", lightOn);

        // Fan rule
        boolean fanOn = temp > 28.0;
        out.put("Ventilador", fanOn);

        return out;
    }
}
