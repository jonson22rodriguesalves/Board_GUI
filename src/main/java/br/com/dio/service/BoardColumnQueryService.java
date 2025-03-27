package br.com.dio.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

import br.com.dio.persistence.dao.BoardColumnDAO;
import br.com.dio.persistence.entity.BoardColumnEntity;
import lombok.AllArgsConstructor;

/**
 * Serviço de consulta de colunas de boards (quadros).
 * Responsável por recuperar informações específicas sobre colunas
 * associadas aos boards do sistema.
 */
@AllArgsConstructor
public class BoardColumnQueryService {

    // Conexão com o banco de dados para execução das queries
    private final Connection connection;

    /**
     * Busca uma coluna pelo seu ID.
     * 
     * @param id Identificador único da coluna a ser recuperada
     * @return Um Optional contendo a BoardColumnEntity se encontrada,
     *         ou Optional.empty() caso não exista
     * @throws SQLException Em caso de erros de acesso ao banco de dados
     */
    public Optional<BoardColumnEntity> findById(final Long id) throws SQLException {
        // Cria o DAO (Data Access Object) para operações com colunas
        var dao = new BoardColumnDAO(connection);
        
        // Delega a busca ao DAO e retorna o resultado
        return dao.findById(id);
    }
}