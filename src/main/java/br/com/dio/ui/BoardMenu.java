package br.com.dio.ui;

import br.com.dio.dto.BoardColumnInfoDTO;
import br.com.dio.persistence.entity.BoardColumnEntity;
import br.com.dio.persistence.entity.BoardEntity;
import br.com.dio.persistence.entity.CardEntity;
import br.com.dio.service.BoardColumnQueryService;
import br.com.dio.service.BoardQueryService;
import br.com.dio.service.CardQueryService;
import br.com.dio.service.CardService;
import lombok.AllArgsConstructor;

import java.sql.SQLException;
import java.util.Scanner;

import static br.com.dio.persistence.config.ConnectionConfig.getConnection;

/**
 * Classe que representa o menu de operações para um board específico no modo console.
 * Permite gerenciar cards (criar, mover, bloquear, visualizar) dentro de um board.
 */
@AllArgsConstructor
public class BoardMenu {

    // Scanner para entrada de dados do usuário
    private final Scanner scanner = new Scanner(System.in);

    // Entidade do board sendo gerenciado
    private final BoardEntity entity;

    /**
     * Método principal que exibe e gerencia o menu de operações do board.
     * Controla o fluxo das operações até que o usuário escolha sair.
     */
    public void execute() {
        try {
            System.out.printf("Bem vindo ao board %s, selecione a operação desejada\n", entity.getId());
            var option = -1;
            while (option != 9) {
                exibirMenuOpcoes();
                option = scanner.nextInt();
                processarOpcao(option);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            System.exit(0);
        }
    }

    /**
     * Exibe as opções disponíveis no menu
     */
    private void exibirMenuOpcoes() {
        System.out.println("1 - Criar um card");
        System.out.println("2 - Mover um card");
        System.out.println("3 - Bloquear um card");
        System.out.println("4 - Desbloquear um card");
        System.out.println("5 - Cancelar um card");
        System.out.println("6 - Ver board");
        System.out.println("7 - Ver coluna com cards");
        System.out.println("8 - Ver card");
        System.out.println("9 - Voltar para o menu anterior um card");
        System.out.println("10 - Sair");
    }

    /**
     * Processa a opção selecionada pelo usuário
     * @param option Opção digitada pelo usuário
     * @throws SQLException Em caso de erro de acesso ao banco de dados
     */
    private void processarOpcao(int option) throws SQLException {
        switch (option) {
            case 1 -> createCard();
            case 2 -> moveCardToNextColumn();
            case 3 -> blockCard();
            case 4 -> unblockCard();
            case 5 -> cancelCard();
            case 6 -> showBoard();
            case 7 -> showColumn();
            case 8 -> showCard();
            case 9 -> System.out.println("Voltando para o menu anterior");
            case 10 -> System.exit(0);
            default -> System.out.println("Opção inválida, informe uma opção do menu");
        }
    }

    /**
     * Cria um novo card no board atual
     * @throws SQLException Em caso de erro de acesso ao banco de dados
     */
    private void createCard() throws SQLException {
        var card = new CardEntity();
        System.out.println("Informe o título do card");
        card.setTitle(scanner.next());
        System.out.println("Informe a descrição do card");
        card.setDescription(scanner.next());
        card.setBoardColumn(entity.getInitialColumn());
        
        try(var connection = getConnection()){
            new CardService(connection).create(card);
        }
    }

    /**
     * Move um card para a próxima coluna no fluxo do board
     * @throws SQLException Em caso de erro de acesso ao banco de dados
     */
    private void moveCardToNextColumn() throws SQLException {
        System.out.println("Informe o id do card que deseja mover para a próxima coluna");
        var cardId = scanner.nextLong();
        
        // Obtém informações das colunas para validação do fluxo
        var boardColumnsInfo = entity.getBoardColumns().stream()
                .map(bc -> new BoardColumnInfoDTO(bc.getId(), bc.getOrder(), bc.getKind()))
                .toList();
                
        try(var connection = getConnection()){
            new CardService(connection).moveToNextColumn(cardId, boardColumnsInfo);
        } catch (RuntimeException ex){
            System.out.println(ex.getMessage());
        }
    }

    /**
     * Bloqueia um card, impedindo sua movimentação
     * @throws SQLException Em caso de erro de acesso ao banco de dados
     */
    private void blockCard() throws SQLException {
        System.out.println("Informe o id do card que será bloqueado");
        var cardId = scanner.nextLong();
        System.out.println("Informe o motivo do bloqueio do card");
        var reason = scanner.next();
        
        // Obtém informações das colunas para validação
        var boardColumnsInfo = entity.getBoardColumns().stream()
                .map(bc -> new BoardColumnInfoDTO(bc.getId(), bc.getOrder(), bc.getKind()))
                .toList();
                
        try(var connection = getConnection()){
            new CardService(connection).block(cardId, reason, boardColumnsInfo);
        } catch (RuntimeException ex){
            System.out.println(ex.getMessage());
        }
    }

    /**
     * Desbloqueia um card previamente bloqueado
     * @throws SQLException Em caso de erro de acesso ao banco de dados
     */
    private void unblockCard() throws SQLException {
        System.out.println("Informe o id do card que será desbloqueado");
        var cardId = scanner.nextLong();
        System.out.println("Informe o motivo do desbloqueio do card");
        var reason = scanner.next();
        
        try(var connection = getConnection()){
            new CardService(connection).unblock(cardId, reason);
        } catch (RuntimeException ex){
            System.out.println(ex.getMessage());
        }
    }

    /**
     * Cancela um card, movendo-o para a coluna de cancelados
     * @throws SQLException Em caso de erro de acesso ao banco de dados
     */
    private void cancelCard() throws SQLException {
        System.out.println("Informe o id do card que deseja mover para a coluna de cancelamento");
        var cardId = scanner.nextLong();
        var cancelColumn = entity.getCancelColumn();
        
        // Obtém informações das colunas para validação
        var boardColumnsInfo = entity.getBoardColumns().stream()
                .map(bc -> new BoardColumnInfoDTO(bc.getId(), bc.getOrder(), bc.getKind()))
                .toList();
                
        try(var connection = getConnection()){
            new CardService(connection).cancel(cardId, cancelColumn.getId(), boardColumnsInfo);
        } catch (RuntimeException ex){
            System.out.println(ex.getMessage());
        }
    }

    /**
     * Exibe os detalhes do board atual
     * @throws SQLException Em caso de erro de acesso ao banco de dados
     */
    private void showBoard() throws SQLException {
        try(var connection = getConnection()){
            var optional = new BoardQueryService(connection).showBoardDetails(entity.getId());
            optional.ifPresent(b -> {
                System.out.printf("Board [%s,%s]\n", b.id(), b.name());
                b.columns().forEach(c ->
                        System.out.printf("Coluna [%s] tipo: [%s] tem %s cards\n", c.name(), c.kind(), c.cardsAmount())
                );
            });
        }
    }

    /**
     * Exibe os cards de uma coluna específica
     * @throws SQLException Em caso de erro de acesso ao banco de dados
     */
    private void showColumn() throws SQLException {
        var columnsIds = entity.getBoardColumns().stream().map(BoardColumnEntity::getId).toList();
        var selectedColumnId = -1L;
        
        // Loop até selecionar um ID de coluna válido
        while (!columnsIds.contains(selectedColumnId)){
            System.out.printf("Escolha uma coluna do board %s pelo id\n", entity.getName());
            entity.getBoardColumns().forEach(c -> System.out.printf("%s - %s [%s]\n", c.getId(), c.getName(), c.getKind()));
            selectedColumnId = scanner.nextLong();
        }
        
        try(var connection = getConnection()){
            var column = new BoardColumnQueryService(connection).findById(selectedColumnId);
            column.ifPresent(co -> {
                System.out.printf("Coluna %s tipo %s\n", co.getName(), co.getKind());
                co.getCards().forEach(ca -> System.out.printf("Card %s - %s\nDescrição: %s",
                        ca.getId(), ca.getTitle(), ca.getDescription()));
            });
        }
    }

    /**
     * Exibe os detalhes de um card específico
     * @throws SQLException Em caso de erro de acesso ao banco de dados
     */
    private void showCard() throws SQLException {
        System.out.println("Informe o id do card que deseja visualizar");
        var selectedCardId = scanner.nextLong();
        
        try(var connection = getConnection()){
            new CardQueryService(connection).findById(selectedCardId)
                    .ifPresentOrElse(
                            c -> {
                                System.out.printf("Card %s - %s.\n", c.id(), c.title());
                                System.out.printf("Descrição: %s\n", c.description());
                                System.out.println(c.blocked() ?
                                        "Está bloqueado. Motivo: " + c.blockReason() :
                                        "Não está bloqueado");
                                System.out.printf("Já foi bloqueado %s vezes\n", c.blocksAmount());
                                System.out.printf("Está no momento na coluna %s - %s\n", c.columnId(), c.columnName());
                            },
                            () -> System.out.printf("Não existe um card com o id %s\n", selectedCardId));
        }
    }
}