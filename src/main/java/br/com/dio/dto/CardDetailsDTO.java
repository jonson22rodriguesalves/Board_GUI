package br.com.dio.dto;

import java.time.OffsetDateTime;

/**
 * DTO (Data Transfer Object) que representa os detalhes completos de um Card.
 * Contém todas as informações necessárias para exibição detalhada de um Card,
 * incluindo seu estado atual e relacionamentos.
 *
 * @param id Identificador único do Card
 * @param title Título/resumo do Card
 * @param description Descrição detalhada do Card
 * @param blocked Indica se o Card está atualmente bloqueado
 * @param blockedAt Data/hora do último bloqueio (null se nunca foi bloqueado)
 * @param blockReason Motivo do último bloqueio
 * @param blocksAmount Quantidade total de vezes que o Card foi bloqueado
 * @param columnId ID da coluna onde o Card está localizado
 * @param columnName Nome da coluna onde o Card está localizado
 */
public record CardDetailsDTO(Long id,
                            String title,
                            String description,
                            boolean blocked,
                            OffsetDateTime blockedAt,
                            String blockReason,
                            int blocksAmount,
                            Long columnId,
                            String columnName) {
}