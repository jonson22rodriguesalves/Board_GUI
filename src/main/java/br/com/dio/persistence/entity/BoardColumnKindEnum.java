package br.com.dio.persistence.entity;

import java.util.stream.Stream;

/**
 * Enumeração que representa os tipos de colunas possíveis em um Board.
 * Define os diferentes estágios que um Card pode percorrer no fluxo do Board.
 */
public enum BoardColumnKindEnum {

    /** 
     * Coluna inicial - onde novos Cards são criados 
     */
    INITIAL, 
    
    /** 
     * Coluna final - indica que o Card foi concluído 
     */
    FINAL, 
    
    /** 
     * Coluna de cancelamento - indica que o Card foi cancelado 
     */
    CANCEL, 
    
    /** 
     * Coluna de pendência - representa estágios intermediários do Card 
     */
    PENDING;

    /**
     * Busca um valor do enum pelo seu nome (case-sensitive).
     * 
     * @param name Nome do valor do enum a ser buscado
     * @return O valor correspondente do enum
     * @throws java.util.NoSuchElementException Se nenhum valor for encontrado com o nome especificado
     */
    public static BoardColumnKindEnum findByName(final String name) {
        return Stream.of(BoardColumnKindEnum.values())
                .filter(b -> b.name().equals(name))
                .findFirst().orElseThrow();
    }
}