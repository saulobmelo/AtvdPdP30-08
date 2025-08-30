
package main.java.com.smartroom;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.nio.file.Path;

/**
 * SmartRoomController coordinates sensors, rules and actuators.
 * Reads sensors, applies rules, persists snapshot and prints to terminal.
 * Dependencies are provided via ServiceLocator (IoC/Service Locator).
 */
public class SmartRoomController {

    private final List<Sensor> sensors = new ArrayList<>();
    private final Map<String, Actuator> actuators = new HashMap<>();
    private final RuleEngine rules;
    private final Repository repo;
    private final Clock clock;
    private final AtomicLong cycles = new AtomicLong(0);

    public SmartRoomController(RuleEngine rules, Repository repo, Clock clock) {
        this.rules = rules;
        this.repo = repo;
        this.clock = clock;
    }

    public void addSensor(Sensor s) {
        sensors.add(s);
    }

    public void addActuator(String name, Actuator a) {
        actuators.put(name, a);
    }

    public void runOnce() {
        LocalDateTime ts = clock.now();
        Map<String, Double> values = new HashMap<>();
        for (Sensor s : sensors) {
            double v = s.readValue();
            switch (s.getType()) {
                case TEMPERATURE -> values.put("temp", v);
                case PRESENCE -> values.put("pres", v);
                case LIGHT -> values.put("lux", v);
            }
        }

        Map<String, Boolean> currentStates = new HashMap<>();
        actuators.forEach((k, a) -> currentStates.put(k, a.isOn()));

        Map<String, Boolean> newStates = rules.apply(values, currentStates);
        // apply
        newStates.forEach((name, on) -> {
            Actuator a = actuators.get(name);
            if (a == null) return;
            if (on) a.turnOn(); else a.turnOff();
        });

        // Persist
        Map<String, Boolean> snapshot = new HashMap<>();
        actuators.forEach((k, a) -> snapshot.put(k, a.isOn()));
        repo.appendSensorSnapshot(ts, values, snapshot);

        // Terminal output
        long c = cycles.incrementAndGet();
        System.out.printf("Ciclo #%d @ %s%n", c, ts);
        System.out.printf("  Temperatura: %.1f°C | Presença: %s | Luminosidade: %d lux%n",
                values.getOrDefault("temp", 0.0),
                values.getOrDefault("pres", 0.0) >= 0.5 ? "SIM" : "NÃO",
                (int)Math.round(values.getOrDefault("lux", 0.0)));
        System.out.printf("  Atuadores → Luz: %s | Ventilador: %s%n",
                actuators.get("Luz").isOn() ? "LIGADA" : "DESLIGADA",
                actuators.get("Ventilador").isOn() ? "LIGADO" : "DESLIGADO");
        System.out.println();
    }

    public String generateReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== RELATÓRIO DO SISTEMA ===\n");
        sb.append("Gerado em: ").append(clock.now()).append("\n\n");
        sb.append("SENSORES:\n");
        sb.append("- Total: ").append(sensors.size()).append("\n");
        sb.append("- Tipos: ");
        sb.append(sensors.stream().map(s -> s.getType().name()).distinct().toList());
        sb.append("\n\nDISPOSITIVOS:\n");
        actuators.forEach((k, a) -> sb.append("- ").append(k).append(": ").append(a.isOn() ? "LIGADO" : "DESLIGADO").append("\n"));
        sb.append("\nESTATÍSTICAS:\n- Ciclos processados: ").append(cycles.get()).append("\n");
        return sb.toString();
    }

    // Bootstrap using ServiceLocator for dependencies & wiring
    public static SmartRoomController createDefault(Path dataDir) {
        // Register core services
        ServiceLocator.register(Clock.class, new SystemClock());
        ServiceLocator.registerFactory(Repository.class, () -> new FileRepository(dataDir));
        ServiceLocator.register(RuleEngine.class, new DefaultRuleEngine());

        Clock clock = ServiceLocator.resolve(Clock.class);
        Repository repo = ServiceLocator.resolve(Repository.class);
        RuleEngine rules = ServiceLocator.resolve(RuleEngine.class);

        SmartRoomController c = new SmartRoomController(rules, repo, clock);
        // Sensors
        c.addSensor(new FakeTemperatureSensor("TEMP001", "Sensor Temperatura"));
        c.addSensor(new FakePresenceSensor("PRES001", "Sensor Presença"));
        c.addSensor(new FakeLightSensor("LUX001", "Sensor Luminosidade"));
        // Actuators
        c.addActuator("Luz", new SimpleActuator("ACT001", "Luz"));
        c.addActuator("Ventilador", new SimpleActuator("ACT002", "Ventilador"));
        return c;
    }
}
