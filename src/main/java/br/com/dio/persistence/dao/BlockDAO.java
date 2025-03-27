package br.com.dio.persistence.dao;

import lombok.AllArgsConstructor;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.OffsetDateTime;

import static br.com.dio.persistence.converter.OffsetDateTimeConverter.toTimestamp;

/**
 * Data Access Object (DAO) para operações de bloqueio/desbloqueio de Cards.
 * Gerencia o registro histórico de estados de bloqueio no banco de dados.
 */
@AllArgsConstructor
public class BlockDAO {

    // Conexão JDBC com o banco de dados
    private final Connection connection;

    /**
     * Registra um novo bloqueio para um Card.
     * @param reason Motivo do bloqueio
     * @param cardId ID do Card a ser bloqueado
     * @throws SQLException Em caso de erro no banco de dados
     */
    public void block(final String reason, final Long cardId) throws SQLException {
        var sql = "INSERT INTO BLOCKS (blocked_at, block_reason, card_id) VALUES (?, ?, ?);";
        try(var statement = connection.prepareStatement(sql)){
            var i = 1;
            // Registra o momento atual do bloqueio
            statement.setTimestamp(i++, toTimestamp(OffsetDateTime.now()));
            statement.setString(i++, reason);
            statement.setLong(i, cardId);
            statement.executeUpdate();
        }
    }

    /**
     * Registra o desbloqueio de um Card previamente bloqueado.
     * Atualiza apenas registros que ainda não possuem data de desbloqueio.
     * @param reason Motivo do desbloqueio
     * @param cardId ID do Card a ser desbloqueado
     * @throws SQLException Em caso de erro no banco de dados
     */
    public void unblock(final String reason, final Long cardId) throws SQLException {
        var sql = "UPDATE BLOCKS SET unblocked_at = ?, unblock_reason = ? WHERE card_id = ? AND unblock_reason IS NULL;";
        try(var statement = connection.prepareStatement(sql)){
            var i = 1;
            // Registra o momento atual do desbloqueio
            statement.setTimestamp(i++, toTimestamp(OffsetDateTime.now()));
            statement.setString(i++, reason);
            statement.setLong(i, cardId);
            statement.executeUpdate();
        }
    }
}