package br.com.dio.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;

import br.com.dio.persistence.config.ConnectionConfig;
import br.com.dio.persistence.entity.BoardEntity;
import br.com.dio.service.BoardQueryService;
import br.com.dio.service.BoardService;

/**
 * Janela principal do sistema de gerenciamento de boards (quadros).
 * Permite criar, selecionar, visualizar e deletar boards através de interface gráfica.
 */
public class BoardWindows extends JFrame {

    // Serviço para consulta de boards
    private final BoardQueryService boardQueryService;
    
    // Serviço para operações com boards
    private final BoardService boardService;
    
    // Conexão com o banco de dados
    private final Connection connection;

    /**
     * Construtor principal que inicializa a janela e os serviços.
     * @throws RuntimeException se falhar na conexão com o banco
     */
    public BoardWindows() {
        try {
            this.connection = ConnectionConfig.getConnection();
            this.boardQueryService = new BoardQueryService(connection);
            this.boardService = new BoardService(connection);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, 
                "Erro ao conectar ao banco de dados: " + e.getMessage(),
                "Erro", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
            throw new RuntimeException("Falha na inicialização", e);
        }

        configurarJanelaPrincipal();
        inicializarInterface();
    }

    /**
     * Configura as propriedades básicas da janela principal.
     */
    private void configurarJanelaPrincipal() {
        setTitle("Gerenciador de Boards");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centraliza na tela
    }

    /**
     * Inicializa a interface gráfica com os botões principais.
     */
    private void inicializarInterface() {
        // Cria os botões principais
        JButton criarBoardBtn = criarBotao("Criar Board", this::mostrarJanelaCriacao);
        JButton selecionarBoardBtn = criarBotao("Selecionar Board", this::mostrarJanelaSelecao);
        JButton deletarBoardBtn = criarBotao("Deletar Board", this::mostrarJanelaExclusao);
        JButton visualizarBoardsBtn = criarBotao("Ver Boards", this::mostrarTodosBoards);

        // Configura o painel principal
        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Adiciona os botões
        panel.add(criarBoardBtn);
        panel.add(selecionarBoardBtn);
        panel.add(deletarBoardBtn);
        panel.add(visualizarBoardsBtn);

        add(panel);
    }

    /**
     * Cria um botão padronizado.
     * @param texto Texto do botão
     * @param acao Ação a ser executada quando clicado
     * @return JButton configurado
     */
    private JButton criarBotao(String texto, Runnable acao) {
        JButton button = new JButton(texto);
        button.addActionListener(e -> acao.run());
        return button;
    }

    /**
     * Mostra a janela de criação de novo board.
     */
    private void mostrarJanelaCriacao() {
        JDialog dialog = new JDialog(this, "Criar Board", true);
        dialog.setSize(400, 200);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new GridBagLayout());
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Campo do nome
        gbc.gridx = 0;
        gbc.gridy = 0;
        dialog.add(new JLabel("Nome do Board*:"), gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JTextField campoNome = new JTextField(20);
        dialog.add(campoNome, gbc);
        
        // Campo de colunas adicionais
        gbc.gridx = 0;
        gbc.gridy = 1;
        dialog.add(new JLabel("Colunas Adicionais:"), gbc);
        
        gbc.gridx = 1;
        JSpinner spinnerColunas = new JSpinner(new SpinnerNumberModel(0, 0, 10, 1));
        dialog.add(spinnerColunas, gbc);
        
        // Botões de ação
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.CENTER;
        JPanel painelBotoes = new JPanel();
        
        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.addActionListener(e -> dialog.dispose());
        
        JButton btnCriar = new JButton("Criar");
        btnCriar.addActionListener(e -> {
            if (campoNome.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "O nome do board é obrigatório!", 
                    "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            try {
                BoardEntity board = BoardCreator.createBoardEntity(
                    campoNome.getText(), 
                    (int) spinnerColunas.getValue()
                );
                boardService.insert(board);
                JOptionPane.showMessageDialog(dialog, "Board criado com sucesso! ID: " + board.getId());
                dialog.dispose();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "Erro ao criar board: " + ex.getMessage(), 
                    "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        painelBotoes.add(btnCancelar);
        painelBotoes.add(btnCriar);
        dialog.add(painelBotoes, gbc);
        
        dialog.pack();
        dialog.setVisible(true);
    }

    /**
     * Mostra a janela para seleção de um board existente.
     */
    private void mostrarJanelaSelecao() {
        JDialog dialog = new JDialog(this, "Selecionar Board", true);
        dialog.setSize(350, 200);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));
        
        JPanel painelSelecao = new JPanel(new BorderLayout(5, 5));
        
        // Combo box para seleção dos boards
        JComboBox<BoardEntity> comboBoards = new JComboBox<>();
        comboBoards.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, 
                                                         boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof BoardEntity) {
                    BoardEntity b = (BoardEntity) value;
                    setText(String.format("%d - %s", b.getId(), b.getName()));
                }
                return this;
            }
        });
        
        // Botão para carregar os boards
        JButton btnCarregar = new JButton("Carregar Boards");
        btnCarregar.addActionListener(e -> {
            try {
                comboBoards.removeAllItems();
                boardQueryService.findAll().forEach(comboBoards::addItem);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "Erro ao carregar boards: " + ex.getMessage(), 
                    "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        painelSelecao.add(new JLabel("Selecione um board:"), BorderLayout.NORTH);
        painelSelecao.add(comboBoards, BorderLayout.CENTER);
        painelSelecao.add(btnCarregar, BorderLayout.SOUTH);
        
        // Painel de botões de ação
        JPanel painelBotoes = new JPanel();
        JButton btnCancelar = new JButton("Cancelar");
        JButton btnSelecionar = new JButton("Selecionar");
        
        btnCancelar.addActionListener(e -> dialog.dispose());
        btnSelecionar.addActionListener(e -> {
            BoardEntity selecionado = (BoardEntity) comboBoards.getSelectedItem();
            if (selecionado != null) {
                dialog.dispose();
                mostrarMenuBoard(selecionado);
            } else {
                JOptionPane.showMessageDialog(dialog, "Nenhum board selecionado!", 
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            }
        });
        
        painelBotoes.add(btnCancelar);
        painelBotoes.add(btnSelecionar);
        
        dialog.add(painelSelecao, BorderLayout.CENTER);
        dialog.add(painelBotoes, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    /**
     * Mostra a janela para exclusão de um board.
     */
    private void mostrarJanelaExclusao() {
        JDialog dialog = new JDialog(this, "Deletar Board", true);
        dialog.setSize(400, 200);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new GridBagLayout());
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Configuração dos componentes
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        dialog.add(new JLabel("Digite o ID do Board que deseja deletar:"), gbc);
        
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        dialog.add(new JLabel("ID do Board*:"), gbc);
        
        gbc.gridx = 1;
        JTextField campoId = new JTextField(15);
        dialog.add(campoId, gbc);
        
        // Painel de botões
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.CENTER;
        JPanel painelBotoes = new JPanel();
        painelBotoes.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setPreferredSize(new Dimension(100, 25));
        btnCancelar.addActionListener(e -> dialog.dispose());
        
        JButton btnDeletar = new JButton("Deletar");
        btnDeletar.setPreferredSize(new Dimension(100, 25));
        btnDeletar.addActionListener(e -> {
            String idTexto = campoId.getText().trim();
            
            if (idTexto.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, 
                    "Por favor, informe o ID do Board", 
                    "Campo obrigatório", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            try {
                long boardId = Long.parseLong(idTexto);
                int confirmacao = JOptionPane.showConfirmDialog(dialog,
                    "Tem certeza que deseja deletar o Board ID: " + boardId + "?\nEsta ação não pode ser desfeita.",
                    "Confirmação", JOptionPane.YES_NO_OPTION);
                
                if (confirmacao == JOptionPane.YES_OPTION) {
                    boolean deletado = boardService.delete(boardId);
                    
                    if (deletado) {
                        JOptionPane.showMessageDialog(dialog,
                            "Board deletado com sucesso!",
                            "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                        dialog.dispose();
                    } else {
                        JOptionPane.showMessageDialog(dialog,
                            "Nenhum board encontrado com ID: " + boardId,
                            "Board não encontrado", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog,
                    "ID inválido. Digite apenas números.",
                    "Erro de formato", JOptionPane.ERROR_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog,
                    "Erro ao deletar board: " + ex.getMessage(),
                    "Erro no banco de dados", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        painelBotoes.add(btnCancelar);
        painelBotoes.add(Box.createRigidArea(new Dimension(10, 0)));
        painelBotoes.add(btnDeletar);
        
        dialog.add(painelBotoes, gbc);
        dialog.pack();
        dialog.setVisible(true);
        SwingUtilities.invokeLater(() -> campoId.requestFocus());
    }

    /**
     * Mostra todos os boards existentes em uma janela de visualização.
     */
    private void mostrarTodosBoards() {
        try {
            List<BoardEntity> boards = boardQueryService.findAll();
            if (boards.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Nenhum board encontrado.", 
                    "Informação", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            JTextArea areaTexto = new JTextArea(15, 40);
            areaTexto.setEditable(false);
            StringBuilder sb = new StringBuilder();
            
            for (BoardEntity board : boards) {
                sb.append("ID: ").append(board.getId())
                  .append(" | Nome: ").append(board.getName())
                  .append(" | Colunas: ").append(board.getBoardColumns().size())
                  .append("\n");
            }
            
            areaTexto.setText(sb.toString());
            JScrollPane scrollPane = new JScrollPane(areaTexto);
            
            JDialog dialog = new JDialog(this, "Todos os Boards", true);
            dialog.add(scrollPane);
            dialog.pack();
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, 
                "Erro ao carregar boards: " + ex.getMessage(), 
                "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Mostra o menu de operações para um board específico.
     * @param board O board selecionado
     */
    private void mostrarMenuBoard(BoardEntity board) {
        // Validação do board
        if (board == null) {
            JOptionPane.showMessageDialog(this, 
                "Board inválido", 
                "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }
    
        try {
            // Atualiza o board do banco de dados
            Optional<BoardEntity> boardAtualizado = boardQueryService.findById(board.getId());
            if (!boardAtualizado.isPresent()) {
                JOptionPane.showMessageDialog(this,
                    "Board não encontrado no banco de dados",
                    "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            board = boardAtualizado.get();
            
            // Verifica se tem colunas configuradas
            if (board.getBoardColumns() == null || board.getBoardColumns().isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "O board não possui colunas configuradas",
                    "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
    
            // Cria a janela do board
            JFrame janelaBoard = new JFrame("Board: " + board.getName());
            janelaBoard.setSize(800, 600);
            janelaBoard.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            janelaBoard.setLocationRelativeTo(null);
    
            // Cria o menu de operações do board
            BoardWindowsMenu menuBoard = new BoardWindowsMenu(board, connection, janelaBoard, connection);
            
            // Painel de botões de operação
            JPanel painelBotoes = new JPanel(new GridLayout(2, 4, 5, 5));
            
            // Adiciona os botões de operação
            adicionarBotaoOperacao(painelBotoes, "Criar Card", menuBoard::createCard);
            adicionarBotaoOperacao(painelBotoes, "Mover Card", menuBoard::moveCardToNextColumn);
            adicionarBotaoOperacao(painelBotoes, "Bloquear Card", menuBoard::blockCard);
            adicionarBotaoOperacao(painelBotoes, "Desbloquear Card", menuBoard::unblockCard);
            adicionarBotaoOperacao(painelBotoes, "Cancelar Card", menuBoard::cancelCard);
            adicionarBotaoOperacao(painelBotoes, "Mostrar Coluna", menuBoard::showColumn);
            adicionarBotaoOperacao(painelBotoes, "Ver Card", menuBoard::showCard);
            adicionarBotaoOperacao(painelBotoes, "Voltar", janelaBoard::dispose);
            
            // Configura o layout da janela
            janelaBoard.setLayout(new BorderLayout());
            janelaBoard.add(painelBotoes, BorderLayout.NORTH);
            janelaBoard.add(menuBoard, BorderLayout.CENTER);
            janelaBoard.setVisible(true);
    
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                "Erro ao carregar board: " + ex.getMessage(),
                "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Adiciona um botão de operação ao painel.
     * @param painel O painel onde o botão será adicionado
     * @param texto O texto do botão
     * @param acao A ação a ser executada
     */
    private void adicionarBotaoOperacao(JPanel painel, String texto, Runnable acao) {
        JButton botao = new JButton(texto);
        botao.addActionListener(e -> acao.run());
        painel.add(botao);
    }

    /**
     * Método principal para iniciar a aplicação.
     * @param args Argumentos da linha de comando (não utilizados)
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            BoardWindows janela = new BoardWindows();
            janela.setVisible(true);
        });
    }
}