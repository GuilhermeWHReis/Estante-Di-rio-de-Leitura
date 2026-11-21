package view;

import dao.LivroDAO;
import model.Livro;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.sql.SQLException;

public class ResenhaDialog extends JDialog {
    private final LivroDAO livroDAO = new LivroDAO();
    private final Livro livro;

    private final JComboBox<String> comboNota;
    private final JTextArea areaResenha;

    private boolean salvo = false;

    public ResenhaDialog(JFrame parent, Livro livro) {
        super(parent, "Resenha & Nota — " + livro.getTitulo(), true);
        this.livro = livro;

        setSize(480, 380);
        setLocationRelativeTo(parent);

        JPanel painelRaiz = new JPanel(new BorderLayout(12, 12));
        painelRaiz.setBackground(TemaEscuro.FUNDO_JANELA);
        painelRaiz.setBorder(new EmptyBorder(16, 16, 16, 16));
        setContentPane(painelRaiz);

        // --- Topo: Informações do livro + Nota ---
        JPanel painelTopo = new JPanel(new BorderLayout(8, 8));
        TemaEscuro.aplicarPainel(painelTopo);
        painelTopo.setBorder(new EmptyBorder(10, 12, 10, 12));

        JLabel tituloLivro = new JLabel(livro.getTitulo() + " (" + livro.getAutor() + ")");
        TemaEscuro.aplicarRotulo(tituloLivro, true);

        JPanel linhaNota = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        TemaEscuro.aplicarPainel(linhaNota);

        JLabel rotuloNota = new JLabel("Avaliação:");
        TemaEscuro.aplicarRotulo(rotuloNota, false);

        comboNota = new JComboBox<>(new String[]{
                "Sem nota", "1", "2", "3", "4", "5"
        });
        TemaEscuro.aplicarCombo(comboNota);

        if (livro.getAvaliacao() != null) {
            comboNota.setSelectedItem(String.valueOf(livro.getAvaliacao()));
        }

        linhaNota.add(rotuloNota);
        linhaNota.add(comboNota);

        painelTopo.add(tituloLivro, BorderLayout.NORTH);
        painelTopo.add(linhaNota, BorderLayout.SOUTH);
        painelRaiz.add(painelTopo, BorderLayout.NORTH);

        // --- Centro: Área de texto da resenha ---
        JPanel painelTexto = new JPanel(new BorderLayout(6, 6));
        TemaEscuro.aplicarPainel(painelTexto);
        painelTexto.setBorder(new EmptyBorder(10, 12, 10, 12));

        JLabel rotuloResenha = new JLabel("Sua resenha / anotações:");
        TemaEscuro.aplicarRotulo(rotuloResenha, false);

        areaResenha = new JTextArea();
        areaResenha.setLineWrap(true);
        areaResenha.setWrapStyleWord(true);
        TemaEscuro.aplicarCampo(areaResenha);
        if (livro.getResenha() != null) {
            areaResenha.setText(livro.getResenha());
        }

        JScrollPane scrollResenha = new JScrollPane(areaResenha);
        scrollResenha.setBorder(BorderFactory.createLineBorder(TemaEscuro.BORDA, 1));

        painelTexto.add(rotuloResenha, BorderLayout.NORTH);
        painelTexto.add(scrollResenha, BorderLayout.CENTER);
        painelRaiz.add(painelTexto, BorderLayout.CENTER);

        // --- Rodapé: Botões ---
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 4));
        TemaEscuro.aplicarPainel(painelBotoes);

        JButton botaoCancelar = TemaEscuro.criarBotao(
                "Cancelar", TemaEscuro.FUNDO_CAMPO, TemaEscuro.BORDA
        );
        JButton botaoSalvar = TemaEscuro.criarBotao(
                "Salvar Resenha", TemaEscuro.DESTAQUE, TemaEscuro.DESTAQUE_HOVER
        );

        painelBotoes.add(botaoCancelar);
        painelBotoes.add(botaoSalvar);
        painelRaiz.add(painelBotoes, BorderLayout.SOUTH);

        botaoCancelar.addActionListener(e -> dispose());
        botaoSalvar.addActionListener(e -> salvarResenha());
    }

    private void salvarResenha() {
        String selecionado = (String) comboNota.getSelectedItem();
        Integer nota = null;
        if (selecionado != null && !selecionado.equals("Sem nota")) {
            nota = Integer.parseInt(selecionado);
        }

        String textoResenha = areaResenha.getText().trim();

        livro.setAvaliacao(nota);
        livro.setResenha(textoResenha.isEmpty() ? null : textoResenha);

        try {
            livroDAO.atualizar(livro);
            salvo = true;
            dispose();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Erro ao salvar resenha:\n" + e.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    public boolean isSalvo() {
        return salvo;
    }
}