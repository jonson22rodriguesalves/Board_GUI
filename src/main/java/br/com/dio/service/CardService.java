package br.com.dio.service;

import static br.com.dio.persistence.entity.BoardColumnKindEnum.CANCEL;
import static br.com.dio.persistence.entity.BoardColumnKindEnum.FINAL;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import br.com.dio.dto.BoardColumnInfoDTO;
import br.com.dio.exception.CardBlockedException;
import br.com.dio.exception.CardFinishedException;
import br.com.dio.exception.EntityNotFoundException;
import br.com.dio.persistence.dao.BlockDAO;
import br.com.dio.persistence.dao.CardDAO;
import br.com.dio.persistence.entity.CardEntity;
import lombok.AllArgsConstructor;

/**
 * Serviço responsável pelas operações de CRUD e gestão de estados dos cards.
 * Gerencia criação, movimentação entre colunas, bloqueio e desbloqueio de cards.
 */
@AllArgsConstructor
public class CardService {

    // Conexão com o banco de dados
    private final Connection connection;

    /**
     * Cria um novo card no sistema.
     * @param entity Entidade do card a ser criada
     * @return CardEntity criada
     * @throws SQLException Em caso de erro no banco de dados
     */
    public CardEntity create(final CardEntity entity) throws SQLException {
        try {
            var dao = new CardDAO(connection);
            dao.insert(entity);
            connection.commit();
            return entity;
        } catch (SQLException ex) {
            connection.rollback();
            throw ex;
        }
    }

    /**
     * Retorna a conexão com o banco de dados.
     * @return Connection ativa
     */
    public Connection getConnection() {
        return connection;
    }

    /**
     * Move um card para a próxima coluna no fluxo do board.
     * @param cardId ID do card a ser movido
     * @param boardColumnsInfo Lista com informações das colunas do board
     * @throws SQLException Em caso de erro no banco de dados
     * @throws EntityNotFoundException Se o card não for encontrado
     * @throws CardBlockedException Se o card estiver bloqueado
     * @throws CardFinishedException Se o card já estiver na coluna final
     */
    public void moveToNextColumn(final Long cardId, final List<BoardColumnInfoDTO> boardColumnsInfo) throws SQLException {
        try {
            var dao = new CardDAO(connection);
            var optional = dao.findById(cardId);
            var dto = optional.orElseThrow(
                () -> new EntityNotFoundException("O card de id %s não foi encontrado".formatted(cardId))
            );
            
            // Verifica se o card está bloqueado
            if (dto.blocked()) {
                var message = "O card %s está bloqueado, é necessário desbloquea-lo para mover".formatted(cardId);
                throw new CardBlockedException(message);
            }
            
            // Obtém a coluna atual do card
            var currentColumn = boardColumnsInfo.stream()
                .filter(bc -> bc.id().equals(dto.columnId()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("O card informado pertence a outro board"));
            
            // Verifica se o card já está finalizado
            if (currentColumn.kind().equals(FINAL)) {
                throw new CardFinishedException("O card já foi finalizado");
            }
            
            // Encontra a próxima coluna no fluxo
            var nextColumn = boardColumnsInfo.stream()
                .filter(bc -> bc.order() == currentColumn.order() + 1)
                .findFirst().orElseThrow(() -> new IllegalStateException("O card está cancelado"));
            
            // Executa a movimentação
            dao.moveToColumn(nextColumn.id(), cardId);
            connection.commit();
        } catch (SQLException ex) {
            connection.rollback();
            throw ex;
        }
    }

    /**
     * Cancela um card, movendo-o para a coluna de cancelados.
     * @param cardId ID do card a ser cancelado
     * @param cancelColumnId ID da coluna de cancelados
     * @param boardColumnsInfo Lista com informações das colunas do board
     * @throws SQLException Em caso de erro no banco de dados
     * @throws EntityNotFoundException Se o card não for encontrado
     * @throws CardBlockedException Se o card estiver bloqueado
     * @throws CardFinishedException Se o card já estiver finalizado
     */
    public void cancel(final Long cardId, final Long cancelColumnId,
                     final List<BoardColumnInfoDTO> boardColumnsInfo) throws SQLException {
        try {
            var dao = new CardDAO(connection);
            var optional = dao.findById(cardId);
            var dto = optional.orElseThrow(
                () -> new EntityNotFoundException("O card de id %s não foi encontrado".formatted(cardId))
            );
            
            if (dto.blocked()) {
                var message = "O card %s está bloqueado, é necessário desbloquea-lo para mover".formatted(cardId);
                throw new CardBlockedException(message);
            }
            
            var currentColumn = boardColumnsInfo.stream()
                .filter(bc -> bc.id().equals(dto.columnId()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("O card informado pertence a outro board"));
            
            if (currentColumn.kind().equals(FINAL)) {
                throw new CardFinishedException("O card já foi finalizado");
            }
            
            // Valida se existe próxima coluna (verificação de fluxo)
            boardColumnsInfo.stream()
                .filter(bc -> bc.order() == currentColumn.order() + 1)
                .findFirst().orElseThrow(() -> new IllegalStateException("O card está cancelado"));
            
            // Move para coluna de cancelados
            dao.moveToColumn(cancelColumnId, cardId);
            connection.commit();
        } catch (SQLException ex) {
            connection.rollback();
            throw ex;
        }
    }

    /**
     * Bloqueia um card, impedindo sua movimentação.
     * @param id ID do card a ser bloqueado
     * @param reason Motivo do bloqueio
     * @param boardColumnsInfo Lista com informações das colunas do board
     * @throws SQLException Em caso de erro no banco de dados
     * @throws EntityNotFoundException Se o card não for encontrado
     * @throws CardBlockedException Se o card já estiver bloqueado
     * @throws IllegalStateException Se o card estiver em coluna final ou cancelada
     */
    public void block(final Long id, final String reason, final List<BoardColumnInfoDTO> boardColumnsInfo) throws SQLException {
        try {
            var dao = new CardDAO(connection);
            var optional = dao.findById(id);
            var dto = optional.orElseThrow(
                () -> new EntityNotFoundException("O card de id %s não foi encontrado".formatted(id))
            );
            
            if (dto.blocked()) {
                var message = "O card %s já está bloqueado".formatted(id);
                throw new CardBlockedException(message);
            }
            
            var currentColumn = boardColumnsInfo.stream()
                .filter(bc -> bc.id().equals(dto.columnId()))
                .findFirst()
                .orElseThrow();
            
            // Verifica se o card está em coluna final ou cancelada
            if (currentColumn.kind().equals(FINAL) || currentColumn.kind().equals(CANCEL)) {
                var message = "O card está em uma coluna do tipo %s e não pode ser bloqueado"
                    .formatted(currentColumn.kind());
                throw new IllegalStateException(message);
            }
            
            // Registra o bloqueio
            var blockDAO = new BlockDAO(connection);
            blockDAO.block(reason, id);
            connection.commit();
        } catch (SQLException ex) {
            connection.rollback();
            throw ex;
        }
    }

    /**
     * Desbloqueia um card, permitindo sua movimentação novamente.
     * @param id ID do card a ser desbloqueado
     * @param reason Motivo do desbloqueio
     * @throws SQLException Em caso de erro no banco de dados
     * @throws EntityNotFoundException Se o card não for encontrado
     * @throws CardBlockedException Se o card não estiver bloqueado
     */
    public void unblock(final Long id, final String reason) throws SQLException {
        try {
            var dao = new CardDAO(connection);
            var optional = dao.findById(id);
            var dto = optional.orElseThrow(
                () -> new EntityNotFoundException("O card de id %s não foi encontrado".formatted(id))
            );
            
            if (!dto.blocked()) {
                var message = "O card %s não está bloqueado".formatted(id);
                throw new CardBlockedException(message);
            }
            
            // Registra o desbloqueio
            var blockDAO = new BlockDAO(connection);
            blockDAO.unblock(reason, id);
            connection.commit();
        } catch (SQLException ex) {
            connection.rollback();
            throw ex;
        }
    }
}