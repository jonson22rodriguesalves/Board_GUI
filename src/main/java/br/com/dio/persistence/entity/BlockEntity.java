package br.com.dio.persistence.entity;

import lombok.Data;

import java.time.OffsetDateTime;

/**
 * Entidade que registra o histórico de bloqueios e desbloqueios de Cards.
 * Armazena informações sobre quando e porquê um Card foi bloqueado/desbloqueado.
 */
@Data
public class BlockEntity {

    /**
     * Identificador único do registro de bloqueio
     */
    private Long id;

    /**
     * Data e hora (com offset) em que o bloqueio foi realizado
     */
    private OffsetDateTime blockedAt;

    /**
     * Motivo/justificativa para o bloqueio do Card
     */
    private String blockReason;

    /**
     * Data e hora (com offset) em que o desbloqueio foi realizado
     * (null se o Card ainda estiver bloqueado)
     */
    private OffsetDateTime unblockedAt;

    /**
     * Motivo/justificativa para o desbloqueio do Card
     */
    private String unblockReason;
}