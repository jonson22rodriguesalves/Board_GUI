package br.com.dio.dto;

import br.com.dio.persistence.entity.BoardColumnKindEnum;

/**
 * DTO (Data Transfer Object) com informações essenciais sobre uma coluna de Board.
 * Utilizado principalmente para validações de fluxo e operações de movimentação de Cards.
 *
 * @param id Identificador único da coluna
 * @param order Posição/ordem da coluna no fluxo do Board (0-based)
 * @param kind Tipo da coluna (INITIAL, PENDING, FINAL, CANCEL) que define seu papel no fluxo
 */
public record BoardColumnInfoDTO(Long id, int order, BoardColumnKindEnum kind) {
}