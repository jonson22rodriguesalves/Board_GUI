package br.com.dio.persistence.entity;

import lombok.Data;

/**
 * Entidade que representa um Card (Cartão) no sistema.
 * Um Card contém informações de tarefas ou itens de trabalho
 * que podem ser movidos entre colunas de um Board (Quadro).
 * 
 * <p>Utiliza a anotação @Data do Lombok para gerar automaticamente:
 * - Getters e Setters
 * - Métodos toString(), equals() e hashCode()</p>
 */
@Data
public class CardEntity {

    /**
     * Identificador único do card no banco de dados
     */
    private Long id;

    /**
     * Título do card, representando o nome/resumo da tarefa
     */
    private String title;

    /**
     * Descrição detalhada do card, com informações sobre a tarefa
     */
    private String description;

    /**
     * Coluna do board à qual este card pertence atualmente.
     * Inicializado com uma nova instância vazia por padrão.
     */
    private BoardColumnEntity boardColumn = new BoardColumnEntity();
}