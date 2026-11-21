package view;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.JTableHeader;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;

public class TemaEscuro {
    public static final Color FUNDO_JANELA = new Color(24, 26, 32);
    public static final Color FUNDO_PAINEL = new Color(34, 37, 46);
    public static final Color FUNDO_CAMPO = new Color(45, 49, 61);
    public static final Color BORDA = new Color(68, 74, 92);
    public static final Color TEXTO_PRINCIPAL = new Color(235, 238, 245);
    public static final Color TEXTO_SECUNDARIO = new Color(165, 173, 190);
    public static final Color DESTAQUE = new Color(99, 102, 241);
    public static final Color DESTAQUE_HOVER = new Color(79, 82, 214);
    public static final Color PERIGO = new Color(185, 60, 75);

    public static final Font FONTE_TITULO = new Font("SansSerif", Font.BOLD, 18);
    public static final Font FONTE_PADRAO = new Font("SansSerif", Font.PLAIN, 13);
    public static final Font FONTE_BOTAO = new Font("SansSerif", Font.BOLD, 13);

    public static void aplicarPainel(JPanel painel) {
        painel.setBackground(FUNDO_PAINEL);
    }

    public static void aplicarRotulo(JLabel label, boolean titulo) {
        label.setForeground(titulo ? TEXTO_PRINCIPAL : TEXTO_SECUNDARIO);
        label.setFont(titulo ? FONTE_TITULO : FONTE_PADRAO);
    }

    public static void aplicarCampo(JComponent campo) {
        campo.setBackground(FUNDO_CAMPO);
        campo.setForeground(TEXTO_PRINCIPAL);
        campo.setFont(FONTE_PADRAO);
        campo.setBorder(new CompoundBorder(
                new LineBorder(BORDA, 1, true),
                new EmptyBorder(6, 10, 6, 10)
        ));
        if (campo instanceof JTextField tf) {
            tf.setCaretColor(TEXTO_PRINCIPAL);
        } else if (campo instanceof JTextArea ta) {
            ta.setCaretColor(TEXTO_PRINCIPAL);
        }
    }

    public static void aplicarCombo(JComboBox<?> combo) {
        combo.setBackground(FUNDO_CAMPO);
        combo.setForeground(TEXTO_PRINCIPAL);
        combo.setFont(FONTE_PADRAO);
        combo.setBorder(new LineBorder(BORDA, 1, true));
    }

    public static JButton criarBotao(String texto, Color corFundo, Color corHover) {
        JButton botao = new JButton(texto);
        botao.setFont(FONTE_BOTAO);
        botao.setForeground(Color.WHITE);
        botao.setBackground(corFundo);
        botao.setFocusPainted(false);
        botao.setCursor(new Cursor(Cursor.HAND_CURSOR));
        botao.setBorder(new CompoundBorder(
                new LineBorder(corFundo.darker(), 1, true),
                new EmptyBorder(8, 14, 8, 14)
        ));

        botao.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                botao.setBackground(corHover);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                botao.setBackground(corFundo);
            }
        });

        return botao;
    }

    public static void aplicarTabela(JTable tabela) {
        tabela.setBackground(FUNDO_CAMPO);
        tabela.setForeground(TEXTO_PRINCIPAL);
        tabela.setFont(FONTE_PADRAO);
        tabela.setRowHeight(28);
        tabela.setGridColor(BORDA);
        tabela.setSelectionBackground(DESTAQUE);
        tabela.setSelectionForeground(Color.WHITE);
        tabela.setBorder(BorderFactory.createEmptyBorder());

        JTableHeader cabecalho = tabela.getTableHeader();
        cabecalho.setBackground(FUNDO_PAINEL);
        cabecalho.setForeground(TEXTO_PRINCIPAL);
        cabecalho.setFont(FONTE_BOTAO);
        cabecalho.setBorder(new LineBorder(BORDA, 1));
    }
}