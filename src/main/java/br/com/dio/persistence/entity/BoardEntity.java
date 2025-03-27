package br.com.dio.persistence.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import static br.com.dio.persistence.entity.BoardColumnKindEnum.CANCEL;
import static br.com.dio.persistence.entity.BoardColumnKindEnum.INITIAL;

/**
 * Entidade que representa um Board (Quadro) no sistema.
 * Um Board contém colunas organizadas em fluxos de trabalho
 * e serve como contêiner para Cards (tarefas).
 */
@Data
public class BoardEntity {

    /**
     * Identificador único do Board no banco de dados
     */
    private Long id;

    /**
     * Nome do Board (identificação visual)
     */
    private String name;

    /**
     * Lista de colunas que compõem o Board.
     * Anotações excluem esta propriedade dos métodos:
     * - toString() para evitar recursão infinita
     * - equals() e hashCode() para comparar apenas pelo ID
     */
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<BoardColumnEntity> boardColumns = new ArrayList<>();

    /**
     * Retorna a coluna inicial do Board.
     * @return BoardColumnEntity do tipo INITIAL
     * @throws java.util.NoSuchElementException se não encontrar a coluna
     */
    public BoardColumnEntity getInitialColumn() {
        return getFilteredColumn(bc -> bc.getKind().equals(INITIAL));
    }

    /**
     * Retorna a coluna de cancelamento do Board.
     * @return BoardColumnEntity do tipo CANCEL
     * @throws java.util.NoSuchElementException se não encontrar a coluna
     */
    public BoardColumnEntity getCancelColumn() {
        return getFilteredColumn(bc -> bc.getKind().equals(CANCEL));
    }

    /**
     * Método auxiliar para filtrar colunas por critério.
     * @param filter Predicado com a condição de filtro
     * @return Primeira coluna que atende ao filtro
     * @throws java.util.NoSuchElementException se nenhuma coluna atender
     */
    private BoardColumnEntity getFilteredColumn(Predicate<BoardColumnEntity> filter) {
        return boardColumns.stream()
                .filter(filter)
                .findFirst().orElseThrow();
    }
}