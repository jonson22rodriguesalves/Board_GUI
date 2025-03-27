package br.com.dio;

import java.sql.SQLException;
import javax.swing.SwingUtilities;
import br.com.dio.persistence.config.ConnectionConfig;
import br.com.dio.persistence.migration.MigrationStrategy;
import br.com.dio.ui.InitialModeSelectionWindow;
import br.com.dio.ui.MainMenu;

/**
 * Classe principal que inicia a aplicação de gerenciamento de boards.
 * Oferece dois modos de interação: console ou interface gráfica.
 */
public class Main {

    /**
     * Ponto de entrada principal da aplicação.
     *
     * @param args Argumentos da linha de comando. Aceita "--console" para iniciar
     *             diretamente no modo texto. Se nenhum argumento for fornecido,
     *             exibe a interface gráfica de seleção de modo.
     * @throws SQLException Se ocorrer um erro durante a conexão com o banco de dados
     *                     ou execução das migrações.
     */
    public static void main(String[] args) throws SQLException {
        // Estabelece conexão com o banco de dados usando try-with-resources
        // para garantir o fechamento automático da conexão
        try (var connection = ConnectionConfig.getConnection()) {
            // Executa as migrações do banco de dados necessárias
            new MigrationStrategy(connection).executeMigration();
        }

        // Verifica se foi solicitado o modo console via argumento
        if (args.length > 0 && args[0].equals("--console")) {
            // Inicia diretamente o menu de console
            new MainMenu().execute();
        } else {
            // Caso contrário, inicia a interface gráfica de seleção de modo
            // usando a thread de despacho de eventos do Swing
            SwingUtilities.invokeLater(() -> {
                new InitialModeSelectionWindow().setVisible(true);
            });
        }
    }
}