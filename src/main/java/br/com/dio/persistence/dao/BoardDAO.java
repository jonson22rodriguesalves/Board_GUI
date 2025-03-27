package br.com.dio.persistence.dao;

import br.com.dio.persistence.entity.BoardEntity;
import com.mysql.cj.jdbc.StatementImpl;
import lombok.AllArgsConstructor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

/**
 * Data Access Object (DAO) para operações com Boards no banco de dados.
 * Responsável pelas operações básicas de CRUD para a entidade Board.
 */
@AllArgsConstructor
public class BoardDAO {

    // Conexão JDBC com o banco de dados
    private Connection connection;

    /**
     * Insere um novo Board no banco de dados.
     * @param entity Entidade Board a ser persistida
     * @return A mesma entidade com o ID gerado
     * @throws SQLException Em caso de erro no banco de dados
     */
    public BoardEntity insert(final BoardEntity entity) throws SQLException {
        var sql = "INSERT INTO BOARDS (name) values (?);";
        try(var statement = connection.prepareStatement(sql)){
            statement.setString(1, entity.getName());
            statement.executeUpdate();
            
            // Recupera o ID gerado (implementação específica para MySQL)
            if (statement instanceof StatementImpl impl){
                entity.setId(impl.getLastInsertID());
            }
        }
        return entity;
    }

    /**
     * Remove um Board do banco de dados pelo seu ID.
     * @param id ID do Board a ser removido
     * @throws SQLException Em caso de erro no banco de dados
     */
    public void delete(final Long id) throws SQLException {
        var sql = "DELETE FROM BOARDS WHERE id = ?;";
        try(var statement = connection.prepareStatement(sql)){
            statement.setLong(1, id);
            statement.executeUpdate();
        }
    }

    /**
     * Busca um Board pelo seu ID.
     * @param id ID do Board a ser recuperado
     * @return Optional contendo o Board se encontrado, ou vazio caso contrário
     * @throws SQLException Em caso de erro no banco de dados
     */
    public Optional<BoardEntity> findById(final Long id) throws SQLException {
        var sql = "SELECT id, name FROM BOARDS WHERE id = ?;";
        try(var statement = connection.prepareStatement(sql)){
            statement.setLong(1, id);
            statement.executeQuery();
            var resultSet = statement.getResultSet();
            
            if (resultSet.next()){
                var entity = new BoardEntity();
                entity.setId(resultSet.getLong("id"));
                entity.setName(resultSet.getString("name"));
                return Optional.of(entity);
            }
            return Optional.empty();
        }
    }

    /**
     * Verifica se um Board existe no banco de dados.
     * @param id ID do Board a ser verificado
     * @return true se o Board existe, false caso contrário
     * @throws SQLException Em caso de erro no banco de dados
     */
    public boolean exists(final Long id) throws SQLException {
        var sql = "SELECT 1 FROM BOARDS WHERE id = ?;";
        try(var statement = connection.prepareStatement(sql)){
            statement.setLong(1, id);
            statement.executeQuery();
            return statement.getResultSet().next();
        }
    }
}