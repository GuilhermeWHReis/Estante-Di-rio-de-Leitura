package dao;

import model.Livro;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class LivroDAO {

    public void inserir(Livro livro) throws SQLException {
        String sql = """
            INSERT INTO livros (titulo, autor, ano, status, avaliacao, resenha)
            VALUES (?, ?, ?, ?, ?, ?);
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, livro.getTitulo());
            pstmt.setString(2, livro.getAutor());

            if (livro.getAno() != null) {
                pstmt.setInt(3, livro.getAno());
            } else {
                pstmt.setNull(3, Types.INTEGER);
            }

            pstmt.setString(4, livro.getStatus());

            if (livro.getAvaliacao() != null) {
                pstmt.setInt(5, livro.getAvaliacao());
            } else {
                pstmt.setNull(5, Types.INTEGER);
            }

            pstmt.setString(6, livro.getResenha());
            pstmt.executeUpdate();
        }
    }

    public List<Livro> listar(String termoBusca, String filtroStatus) throws SQLException {
        List<Livro> livros = new ArrayList<>();

        StringBuilder sql = new StringBuilder("""
            SELECT id, titulo, autor, ano, status, avaliacao, resenha
            FROM livros
            WHERE 1=1
            """);

        List<Object> params = new ArrayList<>();

        if (termoBusca != null && !termoBusca.isBlank()) {
            sql.append(" AND (LOWER(titulo) LIKE ? OR LOWER(autor) LIKE ?)");
            String like = "%" + termoBusca.trim().toLowerCase() + "%";
            params.add(like);
            params.add(like);
        }

        if (filtroStatus != null && !filtroStatus.equals("Todos")) {
            sql.append(" AND status = ?");
            params.add(filtroStatus);
        }

        sql.append(" ORDER BY id DESC;");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Livro livro = new Livro();
                    livro.setId(rs.getInt("id"));
                    livro.setTitulo(rs.getString("titulo"));
                    livro.setAutor(rs.getString("autor"));

                    int ano = rs.getInt("ano");
                    livro.setAno(rs.wasNull() ? null : ano);

                    livro.setStatus(rs.getString("status"));

                    int avaliacao = rs.getInt("avaliacao");
                    livro.setAvaliacao(rs.wasNull() ? null : avaliacao);

                    livro.setResenha(rs.getString("resenha"));
                    livros.add(livro);
                }
            }
        }

        return livros;
    }

    public void atualizar(Livro livro) throws SQLException {
        String sql = """
            UPDATE livros
            SET titulo = ?, autor = ?, ano = ?, status = ?, avaliacao = ?, resenha = ?
            WHERE id = ?;
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, livro.getTitulo());
            pstmt.setString(2, livro.getAutor());

            if (livro.getAno() != null) {
                pstmt.setInt(3, livro.getAno());
            } else {
                pstmt.setNull(3, Types.INTEGER);
            }

            pstmt.setString(4, livro.getStatus());

            if (livro.getAvaliacao() != null) {
                pstmt.setInt(5, livro.getAvaliacao());
            } else {
                pstmt.setNull(5, Types.INTEGER);
            }

            pstmt.setString(6, livro.getResenha());
            pstmt.setInt(7, livro.getId());
            pstmt.executeUpdate();
        }
    }

    public void excluir(int id) throws SQLException {
        String sql = "DELETE FROM livros WHERE id = ?;";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }
}