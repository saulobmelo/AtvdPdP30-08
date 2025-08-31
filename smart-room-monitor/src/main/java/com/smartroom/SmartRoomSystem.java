import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

/**
 * GOD OBJECT + SPAGHETTI CODE - Classe que faz TUDO!
 * 
 * Anti-patterns implementados:
 * - God Object: Uma classe fazendo todas as responsabilidades
 * - Spaghetti Code: Métodos interdependentes e confusos
 * - Magic Numbers: Valores hardcoded
 * - Copy-Paste Programming: Código duplicado
 * - Lava Flow: Código morto/desnecessário misturado
 * - Violation of DRY: Repetição excessiva
 * - Violation of SOLID: Todos os princípios violados
 */
public class SmartRoomSystem implements HttpHandler {
    
    // ANTI-PATTERN: Magic Numbers espalhados
    private static final int MAX_TEMP = 30;
    private static final int MIN_TEMP = 18;
    private static final String DATA_FILE = "smart-room-monitor/data/sensors.txt";
    private static final String REPORT_FILE = "smart-room-monitor/data/reports.txt";
    
    // ANTI-PATTERN: Variáveis globais públicas sem encapsulamento
    public static List<String> sensorsData = new ArrayList<>();
    public static Map<String, Object> currentValues = new HashMap<>();
    public static List<String> actionLog = new ArrayList<>();
    public static boolean lightStatus = false;
    public static boolean fanStatus = false;
    public static int sensorCount = 0;
    public static int processCount = 0;
    public static String lastError = "";
    public static Timer timer;
    
    // ANTI-PATTERN: Lava Flow - variáveis que nunca são usadas
    public static boolean oldSystemFlag = true;
    public static String deprecatedConfig = "old_config_value";
    public static int unusedCounter = 0;
    
    // ANTI-PATTERN: Construtor GOD fazendo TUDO
    public SmartRoomSystem() {
        System.out.println("=== SMART ROOM MONITOR SYSTEM ===");
        
        // Criar diretório
        new File("smart-room-monitor/data").mkdirs();
        
        // Inicializar valores
        currentValues.put("temperatura", 22.5);
        currentValues.put("presenca", false);
        currentValues.put("luminosidade", 450);
        
        // Setup sensores padrão
        setupDefaultSensors();
        
        // Carregar dados
        loadDataFromFile();
        
        // Iniciar timer
        startDataCollection();
        
        System.out.println("✅ Sistema inicializado!");
    }
    
    // ANTI-PATTERN: Método para deletar sensor com lógica ineficiente
    private String handleDeleteSensor(String sensorId) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            String timestamp = sdf.format(new Date());
            
            // Procurar sensor pelo ID (INEFICIENTE)
            String sensorToRemove = null;
            String sensorName = "";
            
            for (String sensor : sensorsData) {
                String[] parts = sensor.split("\\|");
                if (parts.length > 0 && parts[0].equals(sensorId)) {
                    sensorToRemove = sensor;
                    if (parts.length > 1) {
                        sensorName = parts[1];
                    }
                    break;
                }
            }
            
            if (sensorToRemove != null) {
                sensorsData.remove(sensorToRemove);
                sensorCount--;
                
                actionLog.add(timestamp + " - Sensor removido: " + sensorName + " (ID: " + sensorId + ")");
                System.out.println("🗑️ Sensor removido: " + sensorName);
                
                // Salvar mudanças
                saveDataToFile();
                
                return "{\"success\": true, \"message\": \"Sensor " + sensorName + " removido com sucesso\", \"sensorCount\": " + sensorCount + "}";
            } else {
                return "{\"success\": false, \"error\": \"Sensor com ID " + sensorId + " não encontrado\"}";
            }
            
        } catch (Exception e) {
            return "{\"success\": false, \"error\": \"Erro ao remover sensor: " + e.getMessage() + "\"}";
        }
    }
    
    // ANTI-PATTERN: Violação DRY - código repetitivo
    public void setupDefaultSensors() {
        // Sensor temperatura 1
        sensorsData.add("TEMP001|Sensor Temperatura 1|temperatura|22.5|true");
        sensorCount++;
        
        // Sensor temperatura 2 (DUPLICAÇÃO DESNECESSÁRIA)
        sensorsData.add("TEMP002|Sensor Temperatura 2|temperatura|22.0|true");
        sensorCount++;
        
        // Sensor presença 1
        sensorsData.add("PRES001|Sensor Presença 1|presenca|false|true");
        sensorCount++;
        
        // Sensor presença 2 (MAIS DUPLICAÇÃO)
        sensorsData.add("PRES002|Sensor Presença 2|presenca|false|true");
        sensorCount++;
        
        // Sensor luminosidade
        sensorsData.add("LUX001|Sensor Luminosidade|luminosidade|450|true");
        sensorCount++;
        
        System.out.println("📊 " + sensorCount + " sensores configurados");
    }
    
    // ANTI-PATTERN: Método fazendo múltiplas coisas
    public void startDataCollection() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                collectSensorData();
                processAutomaticActions();
                saveDataToFile();
                generateReport();
                processCount++;
            }
        }, 0, 5000); // A cada 5 segundos
    }
    
    // ANTI-PATTERN: Lógica hardcoded e spaghetti
    public void collectSensorData() {
        Random random = new Random();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String timestamp = sdf.format(new Date());
        
        // Simular temperatura
        double temp = 18 + (random.nextDouble() * 15);
        temp = Math.round(temp * 10.0) / 10.0;
        currentValues.put("temperatura", temp);
        
        // Simular presença
        boolean presenca = random.nextInt(100) < 30;
        currentValues.put("presenca", presenca);
        
        // Simular luminosidade baseada na presença (LÓGICA MISTURADA)
        int lux;
        if (presenca) {
            lux = 200 + random.nextInt(600);
        } else {
            lux = 50 + random.nextInt(200);
        }
        currentValues.put("luminosidade", lux);
        
        System.out.println(timestamp + " - Temp: " + temp + "°C, Presença: " + presenca + ", Lux: " + lux);
    }
    
    // ANTI-PATTERN: Condicionais aninhadas profundamente
    public void processAutomaticActions() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String timestamp = sdf.format(new Date());
        
        // Lógica da luz (SPAGHETTI CODE)
        boolean presenca = (Boolean) currentValues.get("presenca");
        int luminosidade = (Integer) currentValues.get("luminosidade");
        
        if (presenca == true) {
            if (luminosidade < 300) {
                if (lightStatus == false) {
                    lightStatus = true;
                    actionLog.add(timestamp + " - Luz LIGADA");
                    System.out.println("💡 LUZ LIGADA!");
                }
            } else {
                if (lightStatus == true) {
                    lightStatus = false;
                    actionLog.add(timestamp + " - Luz DESLIGADA");
                    System.out.println("💡 LUZ DESLIGADA!");
                }
            }
        } else {
            if (lightStatus == true) {
                lightStatus = false;
                actionLog.add(timestamp + " - Luz DESLIGADA (sem presença)");
                System.out.println("💡 LUZ DESLIGADA!");
            }
        }
        
        // Lógica do ventilador (CÓDIGO DUPLICADO)
        double temperatura = (Double) currentValues.get("temperatura");
        if (temperatura > MAX_TEMP) {
            if (fanStatus == false) {
                fanStatus = true;
                actionLog.add(timestamp + " - Ventilador LIGADO");
                System.out.println("🌀 VENTILADOR LIGADO!");
            }
        } else if (temperatura < MIN_TEMP + 2) {
            if (fanStatus == true) {
                fanStatus = false;
                actionLog.add(timestamp + " - Ventilador DESLIGADO");
                System.out.println("🌀 VENTILADOR DESLIGADO!");
            }
        }
    }
    
    // ANTI-PATTERN: Método gigante para salvar dados
    public void saveDataToFile() {
        try {
            FileWriter writer = new FileWriter(DATA_FILE, false);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            String timestamp = sdf.format(new Date());
            
            writer.write("=== DADOS DOS SENSORES ===\n");
            writer.write("Timestamp: " + timestamp + "\n");
            writer.write("Sensores: " + sensorCount + "\n\n");
            
            // ANTI-PATTERN: Loop com lógica complexa desnecessária
            for (int i = 0; i < sensorsData.size(); i++) {
                String sensor = sensorsData.get(i);
                if (sensor != null) {
                    if (!sensor.isEmpty()) {
                        writer.write(sensor + "\n");
                    }
                }
            }
            
            writer.write("\n=== VALORES ATUAIS ===\n");
            writer.write("Temperatura: " + currentValues.get("temperatura") + "°C\n");
            writer.write("Presença: " + currentValues.get("presenca") + "\n");
            writer.write("Luminosidade: " + currentValues.get("luminosidade") + " lux\n");
            writer.write("Luz: " + (lightStatus ? "LIGADA" : "DESLIGADA") + "\n");
            writer.write("Ventilador: " + (fanStatus ? "LIGADO" : "DESLIGADO") + "\n");
            
            writer.close();
        } catch (IOException e) {
            lastError = "Erro ao salvar: " + e.getMessage();
            System.err.println(lastError);
        }
    }
    
    // ANTI-PATTERN: Geração de relatório hardcoded
    public void generateReport() {
        try {
            FileWriter writer = new FileWriter(REPORT_FILE, false);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            String timestamp = sdf.format(new Date());
            
            writer.write("=== RELATÓRIO DO SISTEMA ===\n");
            writer.write("Gerado em: " + timestamp + "\n\n");
            
            writer.write("SENSORES:\n");
            writer.write("- Total: " + sensorCount + "\n");
            writer.write("- Temperatura: " + currentValues.get("temperatura") + "°C\n");
            writer.write("- Presença: " + (((Boolean)currentValues.get("presenca")) ? "SIM" : "NÃO") + "\n");
            writer.write("- Luminosidade: " + currentValues.get("luminosidade") + " lux\n\n");
            
            writer.write("DISPOSITIVOS:\n");
            writer.write("- Luz: " + (lightStatus ? "LIGADA" : "DESLIGADA") + "\n");
            writer.write("- Ventilador: " + (fanStatus ? "LIGADO" : "DESLIGADO") + "\n\n");
            
            writer.write("ESTATÍSTICAS:\n");
            writer.write("- Ciclos processados: " + processCount + "\n");
            writer.write("- Ações no log: " + actionLog.size() + "\n\n");
            
            writer.write("ÚLTIMAS AÇÕES:\n");
            int start = Math.max(0, actionLog.size() - 5);
            for (int i = start; i < actionLog.size(); i++) {
                writer.write("- " + actionLog.get(i) + "\n");
            }
            
            writer.close();
        } catch (IOException e) {
            lastError = "Erro ao gerar relatório: " + e.getMessage();
            System.err.println(lastError);
        }
    }
    
    // ANTI-PATTERN: Carregamento com lógica misturada
    public void loadDataFromFile() {
        File file = new File(DATA_FILE);
        if (file.exists()) {
            try {
                Scanner scanner = new Scanner(file);
                sensorsData.clear();
                sensorCount = 0;
                
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    if (line.contains("|") && !line.startsWith("===")) {
                        sensorsData.add(line);
                        sensorCount++;
                    }
                }
                scanner.close();
                System.out.println("📥 " + sensorCount + " sensores carregados");
            } catch (FileNotFoundException e) {
                lastError = "Arquivo não encontrado";
                System.err.println(lastError);
            }
        }
    }
    
    // ANTI-PATTERN: Cadastro com validação hardcoded
    public String cadastrarSensor(String id, String nome, String tipo, String valorInicial) {
        // Validação hardcoded
        if (id == null || id.trim().isEmpty()) {
            return "ERRO: ID vazio";
        }
        if (nome == null || nome.trim().isEmpty()) {
            return "ERRO: Nome vazio";
        }
        
        // ANTI-PATTERN: Múltiplos if/else ao invés de estratégia
        if (!tipo.equals("temperatura") && !tipo.equals("presenca") && 
            !tipo.equals("luminosidade") && !tipo.equals("umidade")) {
            return "ERRO: Tipo inválido";
        }
        
        // Verificar duplicação (INEFICIENTE)
        for (String sensor : sensorsData) {
            String[] parts = sensor.split("\\|");
            if (parts[0].equals(id)) {
                return "ERRO: ID já existe";
            }
        }
        
        // Adicionar sensor
        String sensorData = id + "|" + nome + "|" + tipo + "|" + valorInicial + "|true";
        sensorsData.add(sensorData);
        sensorCount++;
        
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String timestamp = sdf.format(new Date());
        actionLog.add(timestamp + " - Sensor cadastrado: " + nome);
        
        return "SUCCESS: Sensor cadastrado!";
    }
    
    // ANTI-PATTERN: Handle HTTP gigante com tudo misturado
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        
        System.out.println("🌐 " + method + " " + path);
        
        String response = "";
        int statusCode = 200;
        
        // ANTI-PATTERN: Giant if-else chain
        if (path.equals("/api/sensors") && method.equals("GET")) {
            response = getSensorsJson();
        } else if (path.equals("/api/sensors") && method.equals("POST")) {
            String body = new String(exchange.getRequestBody().readAllBytes());
            response = handleAddSensor(body);
        } else if (path.startsWith("/api/sensor/") && method.equals("DELETE")) {
            String sensorId = path.substring("/api/sensor/".length());
            response = handleDeleteSensor(sensorId);
        } else if (path.equals("/api/data") && method.equals("GET")) {
            response = getCurrentDataJson();
        } else if (path.equals("/api/actions") && method.equals("GET")) {
            response = getActionsJson();
        } else if (path.equals("/api/report") && method.equals("GET")) {
            response = getReportJson();
        } else if (path.equals("/api/devices") && method.equals("POST")) {
            String body = new String(exchange.getRequestBody().readAllBytes());
            response = handleDeviceControl(body);
        } else if (path.equals("/api/devices") && method.equals("GET")) {
            response = getDevicesJson();
        } else {
            response = "{\"error\": \"Endpoint não encontrado\"}";
            statusCode = 404;
        }
        
        // Headers CORS (hardcoded)
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, DELETE, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
        
        exchange.sendResponseHeaders(statusCode, response.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
    
    // ANTI-PATTERN: JSON hardcoded sem biblioteca
    private String getSensorsJson() {
        StringBuilder json = new StringBuilder();
        json.append("{\"sensors\": [");
        
        for (int i = 0; i < sensorsData.size(); i++) {
            String[] parts = sensorsData.get(i).split("\\|");
            if (i > 0) json.append(",");
            json.append("{")
                .append("\"id\": \"").append(parts[0]).append("\",")
                .append("\"nome\": \"").append(parts[1]).append("\",")
                .append("\"tipo\": \"").append(parts[2]).append("\",")
                .append("\"valor\": \"").append(parts[3]).append("\",")
                .append("\"ativo\": ").append(parts[4])
                .append("}");
        }
        
        json.append("], \"total\": ").append(sensorCount).append("}");
        return json.toString();
    }
    
    private String getCurrentDataJson() {
        return "{" +
                "\"temperatura\": " + currentValues.get("temperatura") + "," +
                "\"presenca\": " + currentValues.get("presenca") + "," +
                "\"luminosidade\": " + currentValues.get("luminosidade") + "," +
                "\"lightStatus\": " + lightStatus + "," +
                "\"fanStatus\": " + fanStatus + "," +
                "\"timestamp\": \"" + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()) + "\"" +
                "}";
    }
    
    private String getActionsJson() {
        StringBuilder json = new StringBuilder();
        json.append("{\"actions\": [");
        
        int start = Math.max(0, actionLog.size() - 10);
        for (int i = start; i < actionLog.size(); i++) {
            if (i > start) json.append(",");
            json.append("\"").append(actionLog.get(i)).append("\"");
        }
        
        json.append("]}");
        return json.toString();
    }
    
    private String getReportJson() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String timestamp = sdf.format(new Date());
        
        return "{" +
                "\"timestamp\": \"" + timestamp + "\"," +
                "\"totalSensors\": " + sensorCount + "," +
                "\"processCount\": " + processCount + "," +
                "\"temperatura\": " + currentValues.get("temperatura") + "," +
                "\"presenca\": " + currentValues.get("presenca") + "," +
                "\"luminosidade\": " + currentValues.get("luminosidade") + "," +
                "\"lightStatus\": " + lightStatus + "," +
                "\"fanStatus\": " + fanStatus + "," +
                "\"actionsCount\": " + actionLog.size() +
                "}";
    }
    
    private String getDevicesJson() {
        return "{" +
                "\"luz\": " + lightStatus + "," +
                "\"ventilador\": " + fanStatus + "," +
                "\"timestamp\": \"" + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()) + "\"" +
                "}";
    }
    
    // ANTI-PATTERN: Método gigante para controle de dispositivos
    private String handleDeviceControl(String body) {
        try {
            String device = extractJsonValue(body, "device");
            String action = extractJsonValue(body, "action");
            
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            String timestamp = sdf.format(new Date());
            
            boolean success = false;
            String message = "";
            
            // ANTI-PATTERN: Giant if-else para controle
            if ("luz".equals(device)) {
                if ("ligar".equals(action)) {
                    if (!lightStatus) {
                        lightStatus = true;
                        actionLog.add(timestamp + " - Luz LIGADA manualmente via API");
                        message = "Luz ligada com sucesso";
                        success = true;
                        System.out.println("💡 LUZ LIGADA VIA API!");
                    } else {
                        message = "Luz já estava ligada";
                        success = true;
                    }
                } else if ("desligar".equals(action)) {
                    if (lightStatus) {
                        lightStatus = false;
                        actionLog.add(timestamp + " - Luz DESLIGADA manualmente via API");
                        message = "Luz desligada com sucesso";
                        success = true;
                        System.out.println("💡 LUZ DESLIGADA VIA API!");
                    } else {
                        message = "Luz já estava desligada";
                        success = true;
                    }
                } else {
                    message = "Ação inválida para luz. Use: ligar ou desligar";
                }
            } else if ("ventilador".equals(device)) {
                if ("ligar".equals(action)) {
                    if (!fanStatus) {
                        fanStatus = true;
                        actionLog.add(timestamp + " - Ventilador LIGADO manualmente via API");
                        message = "Ventilador ligado com sucesso";
                        success = true;
                        System.out.println("🌀 VENTILADOR LIGADO VIA API!");
                    } else {
                        message = "Ventilador já estava ligado";
                        success = true;
                    }
                } else if ("desligar".equals(action)) {
                    if (fanStatus) {
                        fanStatus = false;
                        actionLog.add(timestamp + " - Ventilador DESLIGADO manualmente via API");
                        message = "Ventilador desligado com sucesso";
                        success = true;
                        System.out.println("🌀 VENTILADOR DESLIGADO VIA API!");
                    } else {
                        message = "Ventilador já estava desligado";
                        success = true;
                    }
                } else {
                    message = "Ação inválida para ventilador. Use: ligar ou desligar";
                }
            } else {
                message = "Dispositivo inválido. Use: luz ou ventilador";
            }
            
            return "{\"success\": " + success + ", \"message\": \"" + message + "\"}";
            
        } catch (Exception e) {
            return "{\"success\": false, \"error\": \"Erro ao controlar dispositivo: " + e.getMessage() + "\"}";
        }
    }
    
    private String handleAddSensor(String body) {
        try {
            // Parse manual JSON (PRIMITIVO)
            String id = extractJsonValue(body, "id");
            String nome = extractJsonValue(body, "nome");
            String tipo = extractJsonValue(body, "tipo");
            String valor = extractJsonValue(body, "valor");
            
            String result = cadastrarSensor(id, nome, tipo, valor);
            
            if (result.startsWith("SUCCESS")) {
                // Salvar mudanças automaticamente
                saveDataToFile();
                return "{\"success\": true, \"message\": \"" + result + "\"}";
            } else {
                return "{\"success\": false, \"error\": \"" + result + "\"}";
            }
        } catch (Exception e) {
            return "{\"success\": false, \"error\": \"Erro: " + e.getMessage() + "\"}";
        }
    }
    
    // ANTI-PATTERN: Parser JSON primitivo e ineficiente
    private String extractJsonValue(String json, String key) {
        String searchKey = "\"" + key + "\"";
        int startIndex = json.indexOf(searchKey);
        if (startIndex == -1) return "";
        
        startIndex = json.indexOf(":", startIndex) + 1;
        startIndex = json.indexOf("\"", startIndex) + 1;
        int endIndex = json.indexOf("\"", startIndex);
        
        return json.substring(startIndex, endIndex);
    }
    
    // ANTI-PATTERN: Lava Flow - método que nunca é chamado
    public void obsoleteMethod() {
        System.out.println("Este método nunca é usado mas está aqui");
        unusedCounter++;
        deprecatedConfig = "still_here";
    }
    
    // ANTI-PATTERN: Método de shutdown simples
    public void shutdown() {
        System.out.println("🔴 Parando sistema...");
        if (timer != null) {
            timer.cancel();
        }
        saveDataToFile();
        generateReport();
        System.out.println("✅ Sistema parado!");
    }
}