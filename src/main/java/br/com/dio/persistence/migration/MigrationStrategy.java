package br.com.dio.persistence.migration;

import liquibase.Liquibase;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import lombok.AllArgsConstructor;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.SQLException;

import static br.com.dio.persistence.config.ConnectionConfig.getConnection;

/**
 * Classe responsável pela execução de migrações de banco de dados utilizando Liquibase.
 * Gerencia a aplicação de scripts de mudança (changelogs) para atualizar a estrutura do banco.
 */
@AllArgsConstructor
public class MigrationStrategy {

    // Conexão com o banco de dados onde as migrações serão aplicadas
    public final Connection connection;

    /**
     * Executa as migrações do banco de dados configuradas no changelog.
     * Redireciona os logs do Liquibase para um arquivo específico.
     */
    public void executeMigration() {
        // Armazena as referências originais de System.out e System.err
        var originalOut = System.out;
        var originalErr = System.err;
        
        try (var fos = new FileOutputStream("liquibase.log")) {
            // Redireciona a saída padrão e de erro para o arquivo de log
            System.setOut(new PrintStream(fos));
            System.setErr(new PrintStream(fos));
            
            try (
                // Obtém uma nova conexão com o banco de dados
                var connection = getConnection();
                // Cria uma conexão JDBC wrapper para o Liquibase
                var jdbcConnection = new JdbcConnection(connection);
            ) {
                // Configura o Liquibase com o arquivo changelog principal
                var liquibase = new Liquibase(
                    "/db/changelog/db.changelog-master.yml",
                    new ClassLoaderResourceAccessor(),
                    jdbcConnection);
                
                // Executa a atualização do banco de dados
                liquibase.update();
                
            } catch (SQLException | LiquibaseException e) {
                // Em caso de erro, restaura os streams originais e imprime o stacktrace
                e.printStackTrace();
                System.setErr(originalErr);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            // Restaura os streams originais independentemente do resultado
            System.setOut(originalOut);
            System.setErr(originalErr);
        }
    }
}