
package main.java.com.smartroom;

import main.java.com.smartroom.repository.Repository;

import java.nio.file.Path;
import java.util.Scanner;

/**
 * Terminal entrypoint (no web/front-end).
 * Offers a simple loop: run N cycles or watch mode.
 */
public class Main {
    public static void main(String[] args) throws Exception {
        Path dataDir = Path.of("data");
        SmartRoomController controller = SmartRoomController.createDefault(dataDir);

        Scanner sc = new Scanner(System.in);
        System.out.println("=== Smart Room (versão refatorada - terminal) ===");
        System.out.println("1) Executar 1 ciclo");
        System.out.println("2) Executar 10 ciclos");
        System.out.println("3) Modo contínuo (Ctrl+C para sair)");
        System.out.println("4) Gerar relatório e sair");
        System.out.print("Escolha: ");
        String choice = sc.nextLine().trim();

        switch (choice) {            case "1" -> {
                controller.runOnce();
                System.out.println(controller.generateReport());
                ServiceLocator.resolve(Repository.class).writeReport(controller.generateReport());
            }
            case "2" -> {
                for (int i=0;i<10;i++) {
                    controller.runOnce();
                    Thread.sleep(500);
                }
                ServiceLocator.resolve(Repository.class).writeReport(controller.generateReport());
            }
            case "3" -> {
                while (true) {
                    controller.runOnce();
                    Thread.sleep(2000);
                }
            }
            case "4" -> {
                ServiceLocator.resolve(Repository.class).writeReport(controller.generateReport());
                System.out.println("Relatório salvo em data/reports.txt");
            }
            default -> System.out.println("Opção inválida.");
        }
    }
}
