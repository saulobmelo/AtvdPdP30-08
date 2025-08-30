
package main.java.com.smartroom;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.stream.Collectors;

public class FileRepository implements Repository {

    private final Path baseDir;
    private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public FileRepository(Path baseDir) {
        this.baseDir = baseDir;
        try {
            if (!Files.exists(baseDir)) Files.createDirectories(baseDir);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void appendSensorSnapshot(LocalDateTime ts, Map<String, Double> values, Map<String, Boolean> actuators) {
        StringBuilder sb = new StringBuilder();
        sb.append("=== DADOS DOS SENSORES ===\n");
        sb.append("Timestamp: ").append(ts.format(fmt)).append("\n");
        sb.append("Sensores: ").append(values.size()).append("\n\n");
        sb.append("=== VALORES ATUAIS ===\n");
        values.forEach((k,v) -> {
            String label = k;
            if (label.toLowerCase().contains("temp")) {
                sb.append("Temperatura: ").append(String.format("%.1f", v)).append("°C\n");
            } else if (label.toLowerCase().contains("pres")) {
                sb.append("Presença: ").append(v >= 0.5 ? "true" : "false").append("\n");
            } else if (label.toLowerCase().contains("luz") || label.toLowerCase().contains("lux")) {
                sb.append("Luminosidade: ").append((int)Math.round(v)).append(" lux\n");
            } else {
                sb.append(label).append(": ").append(v).append("\n");
            }
        });
        sb.append("Dispositivos:\n");
        for (var e : actuators.entrySet()) {
            sb.append("- ").append(e.getKey()).append(": ").append(e.getValue() ? "LIGADO" : "DESLIGADO").append("\n");
        }
        sb.append(" \n"); // newline at end
        try {
            Files.writeString(baseDir.resolve("sensors.txt"), sb.toString(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void writeReport(String report) {
        try {
            Files.writeString(baseDir.resolve("reports.txt"), report, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
