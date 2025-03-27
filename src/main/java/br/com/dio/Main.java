package br.com.dio;

import java.sql.SQLException;
import java.util.Scanner;

import javax.swing.SwingUtilities;

import br.com.dio.persistence.config.ConnectionConfig;
import br.com.dio.persistence.migration.MigrationStrategy;
import br.com.dio.ui.BoardWindows;
import br.com.dio.ui.InitialModeSelectionWindow;
import br.com.dio.ui.MainMenu;

public class Main {
    /**
     * Método principal que inicia a aplicação.
     * 
     * @param args Argumentos da linha de comando (não utilizados neste caso).
     * @throws SQLException Se ocorrer um erro durante a conexão com o banco de dados.
     */
    public static void main(String[] args) throws SQLException {
        // Estabelece uma conexão com o banco de dados e executa as migrações necessárias
        try (var connection = ConnectionConfig.getConnection()) {
            new MigrationStrategy(connection).executeMigration();
        }
       
        // Exibe a janela de seleção de modo inicial usando a thread de interface gráfica (Swing)
        javax.swing.SwingUtilities.invokeLater(() -> {
            new InitialModeSelectionWindow().setVisible(true);
        });
        
        // Bloco try-with-resources para garantir o fechamento automático do Scanner
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("Escolha a forma de interação:");
            System.out.println("1 - Console");
            System.out.println("2 - Interface Gráfica");

            int escolha = scanner.nextInt();

            if (escolha == 1) {
                // Inicia o menu principal no modo console
                MainMenu mainMenu = new MainMenu();
                mainMenu.execute();
            } else if (escolha == 2) {
                // Inicia a interface gráfica (Swing) em sua própria thread
                SwingUtilities.invokeLater(() -> {
                    new BoardWindows().setVisible(true);
                });
            } else {
                System.out.println("Opção inválida.");
            }
        } // O Scanner é fechado automaticamente aqui
    }
}