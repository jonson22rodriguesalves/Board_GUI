package br.com.dio.dto;

import br.com.dio.persistence.entity.BoardColumnKindEnum;

/**
 * DTO (Data Transfer Object) que representa uma coluna de Board com informações resumidas.
 * Contém dados básicos da coluna e quantidade de cards para visualização otimizada.
 * 
 * @param id Identificador único da coluna
 * @param name Nome da coluna (para exibição)
 * @param kind Tipo da coluna que define seu papel no fluxo (INITIAL, PENDING, FINAL, CANCEL)
 * @param cardsAmount Quantidade de cards presentes nesta coluna (para estatísticas/exibição)
 */
public record BoardColumnDTO(Long id,
                            String name, 
                            BoardColumnKindEnum kind,
                            int cardsAmount) {
}