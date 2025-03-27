package br.com.dio.service;

import java.sql.Connection;
import java.sql.SQLException;

import br.com.dio.persistence.dao.BoardColumnDAO;
import br.com.dio.persistence.dao.BoardDAO;
import br.com.dio.persistence.entity.BoardEntity;
import lombok.AllArgsConstructor;

/**
 * Serviço responsável pelas operações de CRUD (Create, Read, Update, Delete) de Boards (Quadros).
 * Gerencia a persistência de boards e suas colunas associadas no banco de dados.
 */
@AllArgsConstructor
public class BoardService {

    // Conexão com o banco de dados
    private final Connection connection;

    /**
     * Insere um novo board no banco de dados junto com suas colunas.
     * 
     * @param entity Entidade BoardEntity contendo os dados do board e suas colunas
     * @return BoardEntity inserida com os IDs gerados
     * @throws SQLException Em caso de erro no banco de dados
     */
    public BoardEntity insert(final BoardEntity entity) throws SQLException {
        var dao = new BoardDAO(connection);
        var boardColumnDAO = new BoardColumnDAO(connection);
        
        try {
            // Insere o board principal
            dao.insert(entity);
            
            // Associa as colunas ao board e as insere no banco
            var columns = entity.getBoardColumns().stream().map(c -> {
                c.setBoard(entity);  // Estabelece a relação com o board
                return c;
            }).toList();
            
            for (var column : columns) {
                boardColumnDAO.insert(column);
            }
            
            connection.commit();
        } catch (SQLException e) {
            // Em caso de erro, faz rollback da transação
            connection.rollback();
            throw e;
        }
        return entity;
    }

    /**
     * Remove um board do banco de dados pelo seu ID.
     * 
     * @param id ID do board a ser removido
     * @return true se o board foi encontrado e removido, false caso não exista
     * @throws SQLException Em caso de erro no banco de dados
     */
    public boolean delete(final Long id) throws SQLException {
        var dao = new BoardDAO(connection);
        
        try {
            // Verifica se o board existe antes de tentar deletar
            if (!dao.exists(id)) {
                return false;
            }
            
            // Remove o board
            dao.delete(id);
            connection.commit();
            return true;
            
        } catch (SQLException e) {
            // Em caso de erro, faz rollback da transação
            connection.rollback();
            throw e;
        }
    }
}