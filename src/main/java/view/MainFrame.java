package view;

import dao.DatabaseConnection;
import dao.LivroDAO;
import model.Livro;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MainFrame extends JFrame {
    private final LivroDAO livroDAO = new LivroDAO();
    private final DefaultTableModel tableModel;
    private final JTable tabela;
    private final JTextField campoBusca;
    private final JComboBox<String> comboStatus;
    private final JLabel labelContador;

    private List<Livro> livrosAtuais = new ArrayList<>();

    public MainFrame() {
        setTitle("Estante & Diário de Leitura");
        setSize(900, 560);
        setMinimumSize(new Dimension(760, 460));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel painelRaiz = new JPanel(new BorderLayout(12, 12));
        painelRaiz.setBackground(TemaEscuro.FUNDO_JANELA);
        painelRaiz.setBorder(new EmptyBorder(16, 16, 16, 16));
        setContentPane(painelRaiz);

        // --- TOPO: Título + Busca e Filtro ---
        JPanel painelTopo = new JPanel();
        painelTopo.setLayout(new BoxLayout(painelTopo, BoxLayout.Y_AXIS));
        painelTopo.setBackground(TemaEscuro.FUNDO_JANELA);

        JPanel linhaTitulo = new JPanel(new BorderLayout());
        linhaTitulo.setBackground(TemaEscuro.FUNDO_JANELA);

        JLabel tituloApp = new JLabel("Estante & Diário de Leitura");
        TemaEscuro.aplicarRotulo(tituloApp, true);

        labelContador = new JLabel("0 livros");
        TemaEscuro.aplicarRotulo(labelContador, false);

        linhaTitulo.add(tituloApp, BorderLayout.WEST);
        linhaTitulo.add(labelContador, BorderLayout.EAST);

        JPanel painelFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 10));
        TemaEscuro.aplicarPainel(painelFiltros);
        painelFiltros.setBorder(new EmptyBorder(4, 8, 4, 8));

        JLabel rotuloBusca = new JLabel("Buscar:");
        TemaEscuro.aplicarRotulo(rotuloBusca, false);

        campoBusca = new JTextField(22);
        TemaEscuro.aplicarCampo(campoBusca);
        campoBusca.setToolTipText("Digite título ou autor e pressione Enter ou clique em Filtrar");

        JLabel rotuloStatus = new JLabel("Status:");
        TemaEscuro.aplicarRotulo(rotuloStatus, false);

        comboStatus = new JComboBox<>(new String[]{"Todos", "Quero ler", "Lendo", "Concluído"});
        TemaEscuro.aplicarCombo(comboStatus);

        JButton botaoFiltrar = TemaEscuro.criarBotao(
                "Filtrar", TemaEscuro.FUNDO_CAMPO, TemaEscuro.BORDA
        );
        JButton botaoLimpar = TemaEscuro.criarBotao(
                "Limpar", TemaEscuro.FUNDO_CAMPO, TemaEscuro.BORDA
        );

        painelFiltros.add(rotuloBusca);
        painelFiltros.add(campoBusca);
        painelFiltros.add(rotuloStatus);
        painelFiltros.add(comboStatus);
        painelFiltros.add(botaoFiltrar);
        painelFiltros.add(botaoLimpar);

        painelTopo.add(linhaTitulo);
        painelTopo.add(Box.createVerticalStrut(8));
        painelTopo.add(painelFiltros);

        painelRaiz.add(painelTopo, BorderLayout.NORTH);

        // --- CENTRO: Tabela de Livros ---
        String[] colunas = {"ID", "Título", "Autor", "Ano", "Status", "Nota", "Resenha"};
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabela = new JTable(tableModel);
        TemaEscuro.aplicarTabela(tabela);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Esconde a coluna ID visualmente (continuamos usando o objeto Livro para pegar o ID)
        tabela.getColumnModel().getColumn(0).setMinWidth(0);
        tabela.getColumnModel().getColumn(0).setMaxWidth(0);
        tabela.getColumnModel().getColumn(0).setPreferredWidth(0);

        JScrollPane scrollTabela = new JScrollPane(tabela);
        scrollTabela.getViewport().setBackground(TemaEscuro.FUNDO_CAMPO);
        scrollTabela.setBorder(BorderFactory.createLineBorder(TemaEscuro.BORDA, 1));

        painelRaiz.add(scrollTabela, BorderLayout.CENTER);

        // --- RODAPÉ: Botões de Ação ---
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        TemaEscuro.aplicarPainel(painelBotoes);

        JButton botaoNovo = TemaEscuro.criarBotao(
                "+ Novo Livro", TemaEscuro.DESTAQUE, TemaEscuro.DESTAQUE_HOVER
        );
        JButton botaoEditar = TemaEscuro.criarBotao(
                "Editar", TemaEscuro.FUNDO_CAMPO, TemaEscuro.BORDA
        );
        JButton botaoResenha = TemaEscuro.criarBotao(
                "Resenha & Nota", TemaEscuro.FUNDO_CAMPO, TemaEscuro.BORDA
        );
        JButton botaoExcluir = TemaEscuro.criarBotao(
                "Excluir", TemaEscuro.PERIGO, new Color(155, 45, 60)
        );

        painelBotoes.add(botaoNovo);
        painelBotoes.add(botaoEditar);
        painelBotoes.add(botaoResenha);
        painelBotoes.add(botaoExcluir);

        painelRaiz.add(painelBotoes, BorderLayout.SOUTH);

        // --- EVENTOS ---
        botaoFiltrar.addActionListener(e -> carregarLivros());
        campoBusca.addActionListener(e -> carregarLivros());
        comboStatus.addActionListener(e -> carregarLivros());

        botaoLimpar.addActionListener(e -> {
            campoBusca.setText("");
            comboStatus.setSelectedIndex(0);
            carregarLivros();
        });

        botaoNovo.addActionListener(e -> abrirFormulario(null));
        botaoEditar.addActionListener(e -> {
            Livro selecionado = getLivroSelecionado();
            if (selecionado != null) {
                abrirFormulario(selecionado);
            }
        });
        botaoResenha.addActionListener(e -> {
            Livro selecionado = getLivroSelecionado();
            if (selecionado != null) {
                abrirResenha(selecionado);
            }
        });
        botaoExcluir.addActionListener(e -> excluirSelecionado());

        carregarLivros();
    }

    private void carregarLivros() {
        try {
            livrosAtuais = livroDAO.listar(
                    campoBusca.getText(),
                    (String) comboStatus.getSelectedItem()
            );
            tableModel.setRowCount(0);

            for (Livro livro : livrosAtuais) {
                String anoTexto = livro.getAno() != null ? String.valueOf(livro.getAno()) : "—";
                String notaTexto = livro.getAvaliacao() != null ? livro.getAvaliacao() + "/5" : "—";
                String temResenha = (livro.getResenha() != null && !livro.getResenha().isBlank())
                        ? "Sim"
                        : "Não";

                tableModel.addRow(new Object[]{
                        livro.getId(),
                        livro.getTitulo(),
                        livro.getAutor(),
                        anoTexto,
                        livro.getStatus(),
                        notaTexto,
                        temResenha
                });
            }

            int total = livrosAtuais.size();
            labelContador.setText(total == 1 ? "1 livro" : total + " livros");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Erro ao carregar livros do banco:\n" + e.getMessage(),
                    "Erro de Banco de Dados",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private Livro getLivroSelecionado() {
        int linha = tabela.getSelectedRow();
        if (linha < 0 || linha >= livrosAtuais.size()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Selecione um livro na tabela primeiro.",
                    "Nenhum livro selecionado",
                    JOptionPane.INFORMATION_MESSAGE
            );
            return null;
        }
        return livrosAtuais.get(linha);
    }

    private void abrirFormulario(Livro livroExistente) {
        LivroFormDialog dialog = new LivroFormDialog(this, livroExistente);
        dialog.setVisible(true);
        if (dialog.isSalvo()) {
            carregarLivros();
        }
    }

    private void abrirResenha(Livro livro) {
        ResenhaDialog dialog = new ResenhaDialog(this, livro);
        dialog.setVisible(true);
        if (dialog.isSalvo()) {
            carregarLivros();
        }
    }

    private void excluirSelecionado() {
        Livro selecionado = getLivroSelecionado();
        if (selecionado == null) {
            return;
        }

        int confirmacao = JOptionPane.showConfirmDialog(
                this,
                "Tem certeza de que deseja excluir \"" + selecionado.getTitulo() + "\"?",
                "Confirmar exclusão",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirmacao == JOptionPane.YES_OPTION) {
            try {
                livroDAO.excluir(selecionado.getId());
                carregarLivros();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(
                        this,
                        "Erro ao excluir livro:\n" + e.getMessage(),
                        "Erro",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            DatabaseConnection.initDatabase();
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}