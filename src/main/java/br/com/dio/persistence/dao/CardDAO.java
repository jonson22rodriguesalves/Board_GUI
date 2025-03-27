package br.com.dio.persistence.dao;

import br.com.dio.dto.CardDetailsDTO;
import br.com.dio.persistence.entity.CardEntity;
import com.mysql.cj.jdbc.StatementImpl;
import lombok.AllArgsConstructor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

import static br.com.dio.persistence.converter.OffsetDateTimeConverter.toOffsetDateTime;
import static java.util.Objects.nonNull;

/**
 * Data Access Object (DAO) para operações com Cards no banco de dados.
 * Responsável por todas as operações de persistência relacionadas a Cards.
 */
@AllArgsConstructor
public class CardDAO {

    // Conexão com o banco de dados
    private Connection connection;

    /**
     * Insere um novo Card no banco de dados.
     * @param entity Entidade Card a ser persistida
     * @return A mesma entidade com o ID gerado
     * @throws SQLException Em caso de erro no banco de dados
     */
    public CardEntity insert(final CardEntity entity) throws SQLException {
        var sql = "INSERT INTO CARDS (title, description, board_column_id) values (?, ?, ?);";
        try(var statement = connection.prepareStatement(sql)){
            var i = 1;
            statement.setString(i++, entity.getTitle());
            statement.setString(i++, entity.getDescription());
            statement.setLong(i, entity.getBoardColumn().getId());
            statement.executeUpdate();
            
            // Recupera o ID gerado (específico para MySQL)
            if (statement instanceof StatementImpl impl){
                entity.setId(impl.getLastInsertID());
            }
        }
        return entity;
    }

    /**
     * Move um Card para uma nova coluna.
     * @param columnId ID da coluna de destino
     * @param cardId ID do Card a ser movido
     * @throws SQLException Em caso de erro no banco de dados
     */
    public void moveToColumn(final Long columnId, final Long cardId) throws SQLException {
        var sql = "UPDATE CARDS SET board_column_id = ? WHERE id = ?;";
        try(var statement = connection.prepareStatement(sql)){
            var i = 1;
            statement.setLong(i++, columnId);
            statement.setLong(i, cardId);
            statement.executeUpdate();
        }
    }

    /**
     * Busca um Card pelo ID com todos os seus detalhes.
     * @param id ID do Card a ser buscado
     * @return Optional contendo CardDetailsDTO se encontrado, vazio caso contrário
     * @throws SQLException Em caso de erro no banco de dados
     */
    public Optional<CardDetailsDTO> findById(final Long id) throws SQLException {
        var sql =
                """
                SELECT c.id,
                       c.title,
                       c.description,
                       b.blocked_at,
                       b.block_reason,
                       c.board_column_id,
                       bc.name,
                       (SELECT COUNT(sub_b.id)
                               FROM BLOCKS sub_b
                              WHERE sub_b.card_id = c.id) blocks_amount
                  FROM CARDS c
                  LEFT JOIN BLOCKS b
                    ON c.id = b.card_id
                   AND b.unblocked_at IS NULL
                 INNER JOIN BOARDS_COLUMNS bc
                    ON bc.id = c.board_column_id
                  WHERE c.id = ?;
                """;
        try(var statement = connection.prepareStatement(sql)){
            statement.setLong(1, id);
            statement.executeQuery();
            var resultSet = statement.getResultSet();
            
            if (resultSet.next()){
                var dto = new CardDetailsDTO(
                        resultSet.getLong("c.id"),
                        resultSet.getString("c.title"),
                        resultSet.getString("c.description"),
                        nonNull(resultSet.getString("b.block_reason")),
                        toOffsetDateTime(resultSet.getTimestamp("b.blocked_at")),
                        resultSet.getString("b.block_reason"),
                        resultSet.getInt("blocks_amount"),
                        resultSet.getLong("c.board_column_id"),
                        resultSet.getString("bc.name")
                );
                return Optional.of(dto);
            }
        }
        return Optional.empty();
    }
}