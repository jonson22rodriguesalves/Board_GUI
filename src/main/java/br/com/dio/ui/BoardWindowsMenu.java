package br.com.dio.ui;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import br.com.dio.dto.BoardColumnInfoDTO;
import br.com.dio.persistence.dao.CardDAO;
import br.com.dio.persistence.entity.BoardColumnEntity;
import br.com.dio.persistence.entity.BoardEntity;
import br.com.dio.persistence.entity.CardEntity;
import br.com.dio.service.BoardColumnQueryService;
import br.com.dio.service.CardQueryService;
import br.com.dio.service.CardService;

/**
 * Painel de menu para manipulação de cards em um quadro (board) através de interface gráfica.
 * Oferece operações como criação, movimentação, bloqueio e visualização de cards.
 */
public class BoardWindowsMenu extends JPanel {

    // Entidade do board sendo gerenciado
    private final BoardEntity entity;
    
    // Serviço para operações com cards
    private final CardService cardService;
    
    // Frame pai para diálogos modais
    private final JFrame parentFrame;
    
    // Serviço para consulta de colunas do board
    private final BoardColumnQueryService boardColumnQueryService;

    /**
     * Construtor do painel de menu
     * @param entity Entidade do board a ser gerenciado
     * @param connection Conexão com o banco de dados
     * @param parentFrame Janela pai para diálogos
     * @param boardWindowsConnection Conexão específica para operações no board
     */
    public BoardWindowsMenu(BoardEntity entity, Connection connection, JFrame parentFrame, Connection boardWindowsConnection) {
        this.entity = entity;
        this.cardService = new CardService(boardWindowsConnection);
        this.boardColumnQueryService = new BoardColumnQueryService(boardWindowsConnection);
        this.parentFrame = parentFrame;
    }

    /**
     * Obtém informações das colunas do board no formato DTO
     * @return Lista de DTOs com informações das colunas
     */
    private List<BoardColumnInfoDTO> getBoardColumnInfoDTOs() {
        return entity.getBoardColumns().stream()
                .map(bc -> new BoardColumnInfoDTO(bc.getId(), bc.getOrder(), bc.getKind()))
                .collect(Collectors.toList());
    }

    /**
     * Cria um novo card no board
     * Solicita título e descrição através de diálogos e persiste o novo card
     */
    public void createCard() {
        String title = JOptionPane.showInputDialog(parentFrame,"Informe o título do card:",   
                    "Novo Card - Título",
                    JOptionPane.PLAIN_MESSAGE);

        String description = JOptionPane.showInputDialog(parentFrame,"Informe a descrição do card:",
                    "Novo Card - Descrição",
                    JOptionPane.PLAIN_MESSAGE);

        if (title != null && description != null) {
            CardEntity card = new CardEntity();
            card.setTitle(title);
            card.setDescription(description);
            card.setBoardColumn(entity.getInitialColumn());

            try {
                cardService.create(card);
                JOptionPane.showMessageDialog(parentFrame,"Card criado com sucesso.",
                    "Novo Card",
                    JOptionPane.PLAIN_MESSAGE);
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Erro ao criar card: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Move um card para a próxima coluna no fluxo do board
     * Solicita o ID do card através de diálogo
     */
    public void moveCardToNextColumn() {
        String cardIdStr = JOptionPane.showInputDialog(null, "Informe o ID do card:", "Mover Card", JOptionPane.QUESTION_MESSAGE);
        if (cardIdStr != null) {
            try {
                long cardId = Long.parseLong(cardIdStr);
                try {
                    cardService.moveToNextColumn(cardId, getBoardColumnInfoDTOs());
                    JOptionPane.showMessageDialog(null, "Card movido com sucesso.", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Erro ao mover card: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                } catch (RuntimeException ex) {
                    JOptionPane.showMessageDialog(null, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "ID inválido.", "Aviso", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    /**
     * Bloqueia um card, impedindo sua movimentação
     * Solicita ID do card e motivo do bloqueio através de diálogos
     */
    public void blockCard() {
        String cardIdStr = JOptionPane.showInputDialog(null, "Informe o ID do card:", "Bloquear Card", JOptionPane.QUESTION_MESSAGE);
        String reason = JOptionPane.showInputDialog(null, "Informe o motivo do bloqueio:", "Motivo", JOptionPane.QUESTION_MESSAGE);
    
        if (cardIdStr != null && reason != null) {
            try {
                long cardId = Long.parseLong(cardIdStr);
                try {
                    cardService.block(cardId, reason, getBoardColumnInfoDTOs());
                    JOptionPane.showMessageDialog(null, "Card bloqueado com sucesso.", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Erro ao bloquear card: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                } catch (RuntimeException ex) {
                    JOptionPane.showMessageDialog(null, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "ID inválido.", "Aviso", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    /**
     * Desbloqueia um card previamente bloqueado
     * Solicita ID do card e motivo do desbloqueio através de diálogos
     */
    public void unblockCard() {
        String cardIdStr = JOptionPane.showInputDialog(null, "Informe o ID do card:", "Desbloquear Card", JOptionPane.QUESTION_MESSAGE);
        String reason = JOptionPane.showInputDialog(null, "Informe o motivo do desbloqueio:", "Motivo", JOptionPane.QUESTION_MESSAGE);
    
        if (cardIdStr != null && reason != null) {
            try {
                long cardId = Long.parseLong(cardIdStr);
                try {
                    cardService.unblock(cardId, reason);
                    JOptionPane.showMessageDialog(null, "Card desbloqueado com sucesso.", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Erro ao desbloquear card: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                } catch (RuntimeException ex) {
                    JOptionPane.showMessageDialog(null, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "ID inválido.", "Aviso", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    /**
     * Cancela um card, movendo-o para a coluna de cancelados
     * Solicita ID do card através de diálogo
     */
    public void cancelCard() {
        String cardIdStr = JOptionPane.showInputDialog(null, "Informe o ID do card:", "Cancelar Card", JOptionPane.QUESTION_MESSAGE);
        if (cardIdStr != null) {
            try {
                long cardId = Long.parseLong(cardIdStr);
                BoardColumnEntity cancelColumn = entity.getCancelColumn();
                try {
                    cardService.cancel(cardId, cancelColumn.getId(), getBoardColumnInfoDTOs());
                    JOptionPane.showMessageDialog(null, "Card cancelado com sucesso.", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Erro ao cancelar card: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                } catch (RuntimeException ex) {
                    JOptionPane.showMessageDialog(null, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "ID inválido.", "Aviso", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    /**
     * Exibe os cards de uma coluna selecionada
     * Mostra combo box para seleção da coluna e lista seus cards
     */
    public void showColumn() {
        List<Long> columnsIds = entity.getBoardColumns().stream().map(BoardColumnEntity::getId).collect(Collectors.toList());
        JComboBox<Long> columnComboBox = new JComboBox<>(columnsIds.toArray(new Long[0]));
    
        int result = JOptionPane.showConfirmDialog(parentFrame, columnComboBox, "Escolha uma coluna", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            long selectedColumnId = (long) columnComboBox.getSelectedItem();
            try {
                boardColumnQueryService.findById(selectedColumnId).ifPresent(co -> {
                    StringBuilder message = new StringBuilder("Coluna " + co.getName() + " tipo " + co.getKind() + "\n");
                    co.getCards().forEach(ca ->
                            message.append("Card ").append(ca.getId()).append(" - ").append(ca.getTitle()).append("\nDescrição: ").append(ca.getDescription()).append("\n"));
                    JOptionPane.showMessageDialog(parentFrame, message.toString(), "Visualizar coluna", JOptionPane.INFORMATION_MESSAGE);
                });
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(parentFrame, "Erro ao exibir coluna: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Exibe detalhes de um card específico
     * Solicita ID do card e mostra suas informações completas
     */
    public void showCard() {
        String cardIdStr = JOptionPane.showInputDialog(null, "Informe o ID do card:", "Visualizar Card", JOptionPane.QUESTION_MESSAGE);
        if (cardIdStr != null && !cardIdStr.trim().isEmpty()) {
            try {
                long selectedCardId = Long.parseLong(cardIdStr);
                try {
                    var cardDAO = new CardDAO(cardService.getConnection());
                    new CardQueryService(cardDAO).findById(selectedCardId)
                        .ifPresentOrElse(
                            c -> {
                                StringBuilder message = new StringBuilder("Card " + c.id() + " - " + c.title() + ".\n");
                                message.append("Descrição: ").append(c.description()).append("\n");
                                message.append(c.blocked() ? "Está bloqueado. Motivo: " + c.blockReason() : "Não está bloqueado").append("\n");
                                message.append("Já foi bloqueado ").append(c.blocksAmount()).append(" vezes\n");
                                message.append("Está no momento na coluna ").append(c.columnId()).append(" - ").append(c.columnName()).append("\n");
                                JOptionPane.showMessageDialog(null, message.toString(), "Detalhes do Card", JOptionPane.INFORMATION_MESSAGE);
                            },
                            () -> JOptionPane.showMessageDialog(null, "Não existe um card com o id " + selectedCardId, "Aviso", JOptionPane.WARNING_MESSAGE));
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(null, "Erro ao exibir card: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "ID inválido.", "Aviso", JOptionPane.WARNING_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(null, "ID do card é obrigatório.", "Aviso", JOptionPane.WARNING_MESSAGE);
        }
    }
}