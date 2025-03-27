package br.com.dio.persistence.dao;

import br.com.dio.dto.BoardColumnDTO;
import br.com.dio.persistence.entity.BoardColumnEntity;
import br.com.dio.persistence.entity.CardEntity;
import com.mysql.cj.jdbc.StatementImpl;
import lombok.RequiredArgsConstructor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static br.com.dio.persistence.entity.BoardColumnKindEnum.findByName;
import static java.util.Objects.isNull;

/**
 * Data Access Object (DAO) para operações com colunas de boards no banco de dados.
 * Responsável pela persistência de BoardColumnEntity e suas relações.
 */
@RequiredArgsConstructor
public class BoardColumnDAO {

    // Conexão JDBC com o banco de dados
    private final Connection connection;

    /**
     * Insere uma nova coluna no banco de dados.
     * @param entity Entidade BoardColumnEntity a ser persistida
     * @return A entidade inserida com o ID gerado
     * @throws SQLException Em caso de erro no banco de dados
     */
    public BoardColumnEntity insert(final BoardColumnEntity entity) throws SQLException {
        var sql = "INSERT INTO BOARDS_COLUMNS (name, `order`, kind, board_id) VALUES (?, ?, ?, ?);";
        try(var statement = connection.prepareStatement(sql)){
            var i = 1;
            statement.setString(i++, entity.getName());
            statement.setInt(i++, entity.getOrder());
            statement.setString(i++, entity.getKind().name());
            statement.setLong(i, entity.getBoard().getId());
            statement.executeUpdate();
            
            // Recupera o ID gerado (implementação específica para MySQL)
            if (statement instanceof StatementImpl impl){
                entity.setId(impl.getLastInsertID());
            }
            return entity;
        }
    }

    /**
     * Busca todas as colunas de um board pelo ID do board.
     * @param boardId ID do board pai
     * @return Lista de BoardColumnEntity ordenadas pela propriedade 'order'
     * @throws SQLException Em caso de erro no banco de dados
     */
    public List<BoardColumnEntity> findByBoardId(final Long boardId) throws SQLException {
        List<BoardColumnEntity> entities = new ArrayList<>();
        var sql = "SELECT id, name, `order`, kind FROM BOARDS_COLUMNS WHERE board_id = ? ORDER BY `order`";
        try(var statement = connection.prepareStatement(sql)){
            statement.setLong(1, boardId);
            statement.executeQuery();
            var resultSet = statement.getResultSet();
            while (resultSet.next()){
                var entity = new BoardColumnEntity();
                entity.setId(resultSet.getLong("id"));
                entity.setName(resultSet.getString("name"));
                entity.setOrder(resultSet.getInt("order"));
                entity.setKind(findByName(resultSet.getString("kind")));
                entities.add(entity);
            }
            return entities;
        }
    }

    /**
     * Busca colunas de um board com informações resumidas de cards.
     * @param boardId ID do board pai
     * @return Lista de BoardColumnDTO contendo informações consolidadas
     * @throws SQLException Em caso de erro no banco de dados
     */
    public List<BoardColumnDTO> findByBoardIdWithDetails(final Long boardId) throws SQLException {
        List<BoardColumnDTO> dtos = new ArrayList<>();
        var sql =
                """
                SELECT bc.id,
                       bc.name,
                       bc.kind,
                       (SELECT COUNT(c.id)
                               FROM CARDS c
                              WHERE c.board_column_id = bc.id) cards_amount
                  FROM BOARDS_COLUMNS bc
                 WHERE board_id = ?
                 ORDER BY `order`;
                """;
        try(var statement = connection.prepareStatement(sql)){
            statement.setLong(1, boardId);
            statement.executeQuery();
            var resultSet = statement.getResultSet();
            while (resultSet.next()){
                var dto = new BoardColumnDTO(
                        resultSet.getLong("bc.id"),
                        resultSet.getString("bc.name"),
                        findByName(resultSet.getString("bc.kind")),
                        resultSet.getInt("cards_amount")
                );
                dtos.add(dto);
            }
            return dtos;
        }
    }

    /**
     * Busca uma coluna pelo ID incluindo seus cards associados.
     * @param boardId ID da coluna a ser buscada
     * @return Optional contendo a BoardColumnEntity com cards se encontrada
     * @throws SQLException Em caso de erro no banco de dados
     */
    public Optional<BoardColumnEntity> findById(final Long boardId) throws SQLException {
        var sql =
        """
        SELECT bc.name,
               bc.kind,
               c.id,
               c.title,
               c.description
          FROM BOARDS_COLUMNS bc
          LEFT JOIN CARDS c
            ON c.board_column_id = bc.id
         WHERE bc.id = ?;
        """;
        try(var statement = connection.prepareStatement(sql)){
            statement.setLong(1, boardId);
            statement.executeQuery();
            var resultSet = statement.getResultSet();
            if (resultSet.next()){
                var entity = new BoardColumnEntity();
                entity.setName(resultSet.getString("bc.name"));
                entity.setKind(findByName(resultSet.getString("bc.kind")));
                do {
                    var card = new CardEntity();
                    if (isNull(resultSet.getString("c.title"))){
                        break;
                    }
                    card.setId(resultSet.getLong("c.id"));
                    card.setTitle(resultSet.getString("c.title"));
                    card.setDescription(resultSet.getString("c.description"));
                    entity.getCards().add(card);
                } while (resultSet.next());
                return Optional.of(entity);
            }
            return Optional.empty();
        }
    }
}