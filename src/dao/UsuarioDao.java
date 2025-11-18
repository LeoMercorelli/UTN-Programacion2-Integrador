package dao;

import entities.Usuario;
import entities.CredencialAcceso;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDao implements GenericDao<Usuario> {
    private final Connection conexion;

    public UsuarioDao(Connection conexion) {
        this.conexion = conexion;
    }

    
    //️DECISIÓN DE DISEÑO: En todas las query se decidio ignorar/omitir a todos los usuarios que aparecen eliminados=true
    
    @Override
    public void crear(Usuario usuario) throws SQLException {
        String sql = "INSERT INTO usuarios (username, email, activo, fechaRegistro, eliminado, id_CredencialAcceso) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, usuario.getUsername());
            ps.setString(2, usuario.getEmail());
            ps.setBoolean(3, usuario.isActivo());
            ps.setTimestamp(4, Timestamp.valueOf(usuario.getFechaRegistro()));
            ps.setBoolean(5, usuario.isEliminado());
            ps.setLong(6, usuario.getCredencial().getId());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) usuario.setId(rs.getLong(1));
            }
        }
    }

    @Override
    public Usuario leer(long id) throws SQLException {
        String sql = "SELECT * FROM usuarios WHERE id = ? AND eliminado = false";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Usuario u = new Usuario();
                u.setId(rs.getLong("id"));
                u.setUsername(rs.getString("username"));
                u.setEmail(rs.getString("email"));
                u.setActivo(rs.getBoolean("activo"));
                u.setFechaRegistro(rs.getTimestamp("fechaRegistro").toLocalDateTime());
                u.setEliminado(rs.getBoolean("eliminado"));

                CredencialAcceso c = new CredencialAcceso();
                c.setId(rs.getLong("id_CredencialAcceso"));
                u.setCredencial(c);

                return u;
            }
        }
        return null;
    }

    @Override
    public List<Usuario> leerTodos() throws SQLException {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT * FROM usuarios WHERE eliminado = false";
        try (PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Usuario u = new Usuario();
                u.setId(rs.getLong("id"));
                u.setUsername(rs.getString("username"));
                u.setEmail(rs.getString("email"));
                u.setActivo(rs.getBoolean("activo"));
                u.setFechaRegistro(rs.getTimestamp("fechaRegistro").toLocalDateTime());
                u.setEliminado(rs.getBoolean("eliminado"));

                CredencialAcceso c = new CredencialAcceso();
                c.setId(rs.getLong("id_CredencialAcceso"));
                u.setCredencial(c);

                lista.add(u);
            }
        }
        return lista;
    }

    @Override
    public void actualizar(Usuario u) throws SQLException {
        String sql = "UPDATE usuarios SET username=?, email=?, activo=?, fechaRegistro=?, eliminado=?, id_CredencialAcceso=? WHERE id=?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, u.getUsername());
            ps.setString(2, u.getEmail());
            ps.setBoolean(3, u.isActivo());
            ps.setTimestamp(4, Timestamp.valueOf(u.getFechaRegistro()));
            ps.setBoolean(5, u.isEliminado());
            ps.setLong(6, u.getCredencial().getId());
            ps.setLong(7, u.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public void eliminar(long id) throws SQLException {
        String sql = "UPDATE usuarios SET eliminado = true WHERE id = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }
}
