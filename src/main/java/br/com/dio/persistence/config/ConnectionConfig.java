package br.com.dio.persistence.config;

import lombok.NoArgsConstructor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static lombok.AccessLevel.PRIVATE;

/**
 * Classe de configuração para conexões com o banco de dados.
 * Centraliza a criação e configuração de conexões JDBC.
 */
@NoArgsConstructor(access = PRIVATE)
public final class ConnectionConfig {

    /**
     * Obtém uma nova conexão com o banco de dados configurado.
     * A conexão é criada com auto-commit desativado para permitir
     * controle transacional explícito.
     * 
     * @return Conexão JDBC configurada
     * @throws SQLException Em caso de falha na conexão com o banco
     */
    public static Connection getConnection() throws SQLException {
        // Configurações de conexão
        var url = "jdbc:mysql://localhost/board";
        var user = "board";
        var password = "board";
        
        // Estabelece a conexão
        var connection = DriverManager.getConnection(url, user, password);
        
        // Desativa auto-commit para gerenciamento manual de transações
        connection.setAutoCommit(false);
        
        return connection;
    }
}