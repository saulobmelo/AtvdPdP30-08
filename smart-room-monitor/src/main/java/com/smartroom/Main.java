import java.io.*;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

public class Main {
    
    private static final int PORT = 8080;
    private static final String WEBAPP_DIR = "smart-room-monitor/src/main/webapp";
    private static SmartRoomSystem smartRoomSystem;
    
    public static void main(String[] args) {
        try {
            // Inicializar o sistema Smart Room (GOD OBJECT)
            smartRoomSystem = new SmartRoomSystem();
            
            // Criar servidor HTTP
            HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
            
            // Configurar contextos
            server.createContext("/", new StaticFileHandler());
            server.createContext("/api/", smartRoomSystem);
            
            // Shutdown hook
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("\n🔴 Parando servidor...");
                smartRoomSystem.shutdown();
                server.stop(2);
            }));
            
            // Iniciar servidor
            server.start();
            
            System.out.println("🌐 Servidor rodando em: http://localhost:" + PORT);
            System.out.println("📁 Arquivos em: " + WEBAPP_DIR);
            System.out.println("🔧 Para parar: Ctrl+C");
            
        } catch (Exception e) {
            System.err.println("❌ Erro ao iniciar servidor: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Handler simples para arquivos estáticos
     */
    static class StaticFileHandler implements HttpHandler {
        
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String requestPath = exchange.getRequestURI().getPath();
            
            // Redirecionar raiz para index.html
            if ("/".equals(requestPath)) {
                requestPath = "/index.html";
            }
            
            // Caminho do arquivo
            String filePath = WEBAPP_DIR + requestPath;
            Path path = Paths.get(filePath);
            
            try {
                if (Files.exists(path) && !Files.isDirectory(path)) {
                    // Arquivo encontrado
                    byte[] content = Files.readAllBytes(path);
                    String contentType = getContentType(filePath);
                    
                    exchange.getResponseHeaders().set("Content-Type", contentType);
                    exchange.sendResponseHeaders(200, content.length);
                    
                    OutputStream os = exchange.getResponseBody();
                    os.write(content);
                    os.close();
                    
                } else {
                    // 404 - Não encontrado
                    String notFound = "<html><body><h1>404 - Não encontrado</h1>" +
                                    "<p>Arquivo: " + requestPath + "</p>" +
                                    "<a href='/'>Voltar</a></body></html>";
                    
                    exchange.getResponseHeaders().set("Content-Type", "text/html");
                    exchange.sendResponseHeaders(404, notFound.getBytes().length);
                    
                    OutputStream os = exchange.getResponseBody();
                    os.write(notFound.getBytes());
                    os.close();
                }
                
            } catch (Exception e) {
                // Erro interno
                String error = "<html><body><h1>500 - Erro interno</h1>" +
                             "<p>" + e.getMessage() + "</p></body></html>";
                
                exchange.getResponseHeaders().set("Content-Type", "text/html");
                exchange.sendResponseHeaders(500, error.getBytes().length);
                
                OutputStream os = exchange.getResponseBody();
                os.write(error.getBytes());
                os.close();
            }
        }
        
        private String getContentType(String filePath) {
            if (filePath.endsWith(".html")) return "text/html";
            if (filePath.endsWith(".css")) return "text/css";
            if (filePath.endsWith(".js")) return "application/javascript";
            if (filePath.endsWith(".json")) return "application/json";
            if (filePath.endsWith(".png")) return "image/png";
            if (filePath.endsWith(".jpg") || filePath.endsWith(".jpeg")) return "image/jpeg";
            if (filePath.endsWith(".ico")) return "image/x-icon";
            return "text/plain";
        }
    }
}