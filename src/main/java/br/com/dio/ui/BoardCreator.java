package br.com.dio.ui;

import java.util.ArrayList;
import java.util.List;

import br.com.dio.persistence.entity.BoardColumnEntity;
import br.com.dio.persistence.entity.BoardColumnKindEnum;
import br.com.dio.persistence.entity.BoardEntity;

/**
 * Classe utilitária para criação de boards (quadros) com sua estrutura padrão de colunas.
 * Responsável por construir a entidade BoardEntity com todas as colunas necessárias.
 */
public class BoardCreator {

    /**
     * Cria uma entidade BoardEntity com a estrutura padrão de colunas.
     * 
     * @param boardName Nome do board a ser criado
     * @param additionalColumns Quantidade de colunas adicionais de pendências (deve ser >= 0)
     * @return BoardEntity configurado com todas as colunas
     * @throws IllegalArgumentException Se o número de colunas adicionais for negativo
     */
    public static BoardEntity createBoardEntity(String boardName, int additionalColumns) {
        // Validação do parâmetro
        if (additionalColumns < 0) {
            throw new IllegalArgumentException("O número de colunas adicionais deve ser maior ou igual a 0.");
        }

        // Cria a entidade principal do board
        BoardEntity entity = new BoardEntity();
        entity.setName(boardName);

        // Lista para armazenar as colunas do board
        List<BoardColumnEntity> columns = new ArrayList<>();

        // Adiciona a coluna inicial (obrigatória)
        columns.add(createColumn("Inicial", BoardColumnKindEnum.INITIAL, 0));

        // Adiciona colunas de pendência conforme solicitado
        for (int i = 0; i < additionalColumns; i++) {
            columns.add(createColumn("Pendente " + (i + 1), BoardColumnKindEnum.PENDING, i + 1));
        }

        // Adiciona colunas finais obrigatórias
        columns.add(createColumn("Final", BoardColumnKindEnum.FINAL, additionalColumns + 1));
        columns.add(createColumn("Cancelado", BoardColumnKindEnum.CANCEL, additionalColumns + 2));

        // Associa as colunas ao board
        entity.setBoardColumns(columns);
        return entity;
    }

    /**
     * Método auxiliar para criação de colunas do board.
     * 
     * @param name Nome da coluna
     * @param kind Tipo da coluna (enum BoardColumnKindEnum)
     * @param order Ordem de exibição da coluna no board
     * @return BoardColumnEntity configurada
     */
    private static BoardColumnEntity createColumn(String name, BoardColumnKindEnum kind, int order) {
        BoardColumnEntity boardColumn = new BoardColumnEntity();
        boardColumn.setName(name);
        boardColumn.setKind(kind);
        boardColumn.setOrder(order);
        return boardColumn;
    }
}