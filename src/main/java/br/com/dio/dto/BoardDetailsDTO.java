package br.com.dio.dto;

import java.util.List;

/**
 * DTO (Data Transfer Object) que representa os detalhes completos de um Board (Quadro).
 * Contém informações consolidadas do Board e suas colunas associadas.
 * 
 * <p>Usado principalmente para operações de consulta que necessitam de uma visão
 * completa da estrutura do Board e suas colunas.</p>
 *
 * @param id Identificador único do Board
 * @param name Nome do Board (identificação visual)
 * @param columns Lista de DTOs das colunas que compõem este Board,
 *                ordenadas conforme definido no Board
 */
public record BoardDetailsDTO(Long id,
                             String name,
                             List<BoardColumnDTO> columns) {
}