package br.com.dio.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

/**
 * Janela de seleção inicial que permite ao usuário escolher entre
 * o modo console ou interface gráfica para interagir com o Board Manager.
 */
public class InitialModeSelectionWindow extends JFrame {

    /**
     * Constrói a janela de seleção de modo e inicializa seus componentes.
     */
    public InitialModeSelectionWindow() {
        configurarJanela();
        inicializarInterface();
    }

    /**
     * Configura as propriedades básicas da janela (título, tamanho e comportamento).
     */
    private void configurarJanela() {
        setTitle("Bem-vindo ao Board Manager");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  // Fecha a aplicação quando a janela é fechada
        setLocationRelativeTo(null);  // Centraliza a janela na tela
        setResizable(false);  // Impede redimensionamento da janela
    }

    /**
     * Inicializa e organiza todos os componentes da interface gráfica.
     */
    private void inicializarInterface() {
        // Painel principal com layout e margens
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Configuração do título
        JLabel titleLabel = new JLabel("Selecione o modo de interação:", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(titleLabel, BorderLayout.NORTH);

        // Painel de botões com dois botões de tamanho igual
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        
        // Botão para modo console
        JButton consoleButton = criarBotaoModo("Console", 
            e -> iniciarModoConsole());
        
        // Botão para interface gráfica
        JButton guiButton = criarBotaoModo("Interface Gráfica", 
            e -> iniciarModoGrafico());

        buttonPanel.add(consoleButton);
        buttonPanel.add(guiButton);
        panel.add(buttonPanel, BorderLayout.CENTER);

        add(panel);
    }

    /**
     * Cria um botão padronizado para seleção de modo.
     * @param texto O texto exibido no botão
     * @param acao A ação executada quando o botão é clicado
     * @return Uma instância de JButton configurada
     */
    private JButton criarBotaoModo(String texto, ActionListener acao) {
        JButton button = new JButton(texto);
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        button.setPreferredSize(new Dimension(150, 50));
        button.addActionListener(acao);
        return button;
    }

    /**
     * Trata a seleção do modo console, fechando esta janela
     * e iniciando a interface baseada em console.
     */
    private void iniciarModoConsole() {
        dispose(); // Fecha a janela de seleção
        SwingUtilities.invokeLater(() -> {
            try {
                new br.com.dio.ui.MainMenu().execute(); // Inicia o menu console
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null,
                    "Erro ao iniciar modo console: " + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    /**
     * Trata a seleção do modo gráfico, fechando esta janela
     * e iniciando a interface gráfica.
     */
    private void iniciarModoGrafico() {
        dispose(); // Fecha a janela de seleção
        SwingUtilities.invokeLater(() -> {
            try {
                new BoardWindows().setVisible(true); // Inicia a interface gráfica
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null,
                    "Erro ao iniciar interface gráfica: " + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}