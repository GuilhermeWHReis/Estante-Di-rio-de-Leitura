package view;

import dao.LivroDAO;
import model.Livro;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.sql.SQLException;

public class LivroFormDialog extends JDialog {
    private final LivroDAO livroDAO = new LivroDAO();
    private final Livro livroExistente;

    private final JTextField campoTitulo;
    private final JTextField campoAutor;
    private final JTextField campoAno;
    private final JComboBox<String> comboStatus;

    private boolean salvo = false;

    public LivroFormDialog(JFrame parent, Livro livroExistente) {
        super(parent, livroExistente == null ? "Novo Livro" : "Editar Livro", true);
        this.livroExistente = livroExistente;

        setSize(500, 380);
        setMinimumSize(new Dimension(380, 260));
        setResizable(true);
        setLocationRelativeTo(parent);

        JPanel painelRaiz = new JPanel(new BorderLayout(12, 12));
        painelRaiz.setBackground(TemaEscuro.FUNDO_JANELA);
        painelRaiz.setBorder(new EmptyBorder(16, 16, 16, 16));
        setContentPane(painelRaiz);

        JLabel cabecalho = new JLabel(
                livroExistente == null ? "Cadastrar Novo Livro" : "Editar Informações do Livro"
        );
        TemaEscuro.aplicarRotulo(cabecalho, true);
        painelRaiz.add(cabecalho, BorderLayout.NORTH);

        // --- Formulário central em coluna com rolagem ---
        JPanel painelForm = new JPanel();
        painelForm.setLayout(new BoxLayout(painelForm, BoxLayout.Y_AXIS));
        TemaEscuro.aplicarPainel(painelForm);
        painelForm.setBorder(new EmptyBorder(14, 16, 14, 16));

        campoTitulo = new JTextField();
        TemaEscuro.aplicarCampo(campoTitulo);
        configurarCampo(campoTitulo);

        campoAutor = new JTextField();
        TemaEscuro.aplicarCampo(campoAutor);
        configurarCampo(campoAutor);

        campoAno = new JTextField();
        TemaEscuro.aplicarCampo(campoAno);
        campoAno.setToolTipText("Opcional (ex.: 2024)");
        configurarCampo(campoAno);

        comboStatus = new JComboBox<>(new String[]{"Quero ler", "Lendo", "Concluído"});
        TemaEscuro.aplicarCombo(comboStatus);
        configurarCampo(comboStatus);

        painelForm.add(criarBlocoCampo("Título *", campoTitulo));
        painelForm.add(Box.createVerticalStrut(12));
        painelForm.add(criarBlocoCampo("Autor *", campoAutor));
        painelForm.add(Box.createVerticalStrut(12));
        painelForm.add(criarBlocoCampo("Ano", campoAno));
        painelForm.add(Box.createVerticalStrut(12));
        painelForm.add(criarBlocoCampo("Status", comboStatus));

        JScrollPane scrollForm = new JScrollPane(
                painelForm,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );
        scrollForm.setBorder(BorderFactory.createLineBorder(TemaEscuro.BORDA, 1));
        scrollForm.getVerticalScrollBar().setUnitIncrement(16);
        scrollForm.getViewport().setBackground(TemaEscuro.FUNDO_PAINEL);

        painelRaiz.add(scrollForm, BorderLayout.CENTER);

        // --- Botões inferiores ---
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 4));
        TemaEscuro.aplicarPainel(painelBotoes);

        JButton botaoCancelar = TemaEscuro.criarBotao(
                "Cancelar", TemaEscuro.FUNDO_CAMPO, TemaEscuro.BORDA
        );
        JButton botaoSalvar = TemaEscuro.criarBotao(
                "Salvar", TemaEscuro.DESTAQUE, TemaEscuro.DESTAQUE_HOVER
        );

        painelBotoes.add(botaoCancelar);
        painelBotoes.add(botaoSalvar);
        painelRaiz.add(painelBotoes, BorderLayout.SOUTH);

        if (livroExistente != null) {
            preencherCampos(livroExistente);
        }

        botaoCancelar.addActionListener(e -> dispose());
        botaoSalvar.addActionListener(e -> salvarLivro());
        getRootPane().setDefaultButton(botaoSalvar);
    }

    private JPanel criarBlocoCampo(String textoRotulo, JComponent campo) {
        JPanel bloco = new JPanel(new BorderLayout(0, 4));
        bloco.setOpaque(false);
        bloco.setAlignmentX(LEFT_ALIGNMENT);

        JLabel rotulo = new JLabel(textoRotulo);
        TemaEscuro.aplicarRotulo(rotulo, false);

        bloco.add(rotulo, BorderLayout.NORTH);
        bloco.add(campo, BorderLayout.CENTER);

        int alturaTotal = campo.getPreferredSize().height + 24;
        bloco.setPreferredSize(new Dimension(200, alturaTotal));
        bloco.setMinimumSize(new Dimension(120, alturaTotal));
        bloco.setMaximumSize(new Dimension(Integer.MAX_VALUE, alturaTotal));
        return bloco;
    }

    private void configurarCampo(JComponent campo) {
        campo.setPreferredSize(new Dimension(220, 34));
        campo.setMinimumSize(new Dimension(120, 34));
        campo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        campo.setFocusable(true);

        // Quando ganhar foco (ex.: apertando Tab), rola o JScrollPane até mostrar o campo inteiro
        campo.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                JComponent alvo = (JComponent) e.getComponent();
                Rectangle areaVisivel = new Rectangle(
                        0,
                        -12,
                        alvo.getWidth(),
                        alvo.getHeight() + 24
                );
                alvo.scrollRectToVisible(areaVisivel);
            }
        });
    }

    private void preencherCampos(Livro livro) {
        campoTitulo.setText(livro.getTitulo());
        campoAutor.setText(livro.getAutor());
        if (livro.getAno() != null) {
            campoAno.setText(String.valueOf(livro.getAno()));
        }
        if (livro.getStatus() != null) {
            comboStatus.setSelectedItem(livro.getStatus());
        }
    }

    private void salvarLivro() {
        String titulo = campoTitulo.getText().trim();
        String autor = campoAutor.getText().trim();
        String anoTexto = campoAno.getText().trim();
        String status = (String) comboStatus.getSelectedItem();

        if (titulo.isEmpty() || autor.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Preencha pelo menos o Título e o Autor.",
                    "Campos obrigatórios",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        Integer ano = null;
        if (!anoTexto.isEmpty()) {
            try {
                ano = Integer.parseInt(anoTexto);
                if (ano < 0 || ano > 2100) {
                    JOptionPane.showMessageDialog(
                            this,
                            "Informe um ano válido.",
                            "Ano inválido",
                            JOptionPane.WARNING_MESSAGE
                    );
                    return;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(
                        this,
                        "O campo Ano deve conter apenas números.",
                        "Formato inválido",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }
        }

        try {
            if (livroExistente == null) {
                Livro novo = new Livro(titulo, autor, ano, status);
                livroDAO.inserir(novo);
            } else {
                livroExistente.setTitulo(titulo);
                livroExistente.setAutor(autor);
                livroExistente.setAno(ano);
                livroExistente.setStatus(status);
                livroDAO.atualizar(livroExistente);
            }

            salvo = true;
            dispose();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Erro ao salvar no banco de dados:\n" + e.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    public boolean isSalvo() {
        return salvo;
    }
}