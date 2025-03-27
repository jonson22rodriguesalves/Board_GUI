package br.com.dio.persistence.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * Entidade que representa uma coluna dentro de um Board (Quadro).
 * Define um estágio específico no fluxo de trabalho onde Cards (tarefas) podem estar.
 */
@Data
public class BoardColumnEntity {

    /**
     * Identificador único da coluna no banco de dados
     */
    private Long id;

    /**
     * Nome da coluna (para identificação visual)
     */
    private String name;

    /**
     * Ordem de exibição da coluna dentro do Board
     */
    private int order;

    /**
     * Tipo da coluna, que define seu papel no fluxo (INITIAL, PENDING, FINAL, CANCEL)
     */
    private BoardColumnKindEnum kind;

    /**
     * Board ao qual esta coluna pertence.
     * Inicializado com uma nova instância vazia por padrão.
     */
    private BoardEntity board = new BoardEntity();

    /**
     * Lista de Cards contidos nesta coluna.
     * Anotações excluem esta propriedade dos métodos:
     * - toString() para evitar recursão infinita
     * - equals() e hashCode() para comparar apenas pelos campos principais
     */
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<CardEntity> cards = new ArrayList<>();
}