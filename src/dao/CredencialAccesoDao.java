package dao;

import entities.CredencialAcceso;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CredencialAccesoDao implements GenericDao<CredencialAcceso> {
    private Connection conexion;

    public CredencialAccesoDao(Connection conexion) {
        this.conexion = conexion;
    }

    @Override
    public void crear(CredencialAcceso credencial) throws SQLException {
        String sql = "INSERT INTO credencialesAcceso (hashPassword, salt, ultimoCambio, requiereReset, eliminado) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, credencial.getHashPassword());
            ps.setString(2, credencial.getSalt());
            ps.setTimestamp(3, Timestamp.valueOf(credencial.getUltimoCambio()));
            ps.setBoolean(4, credencial.isRequiereReset());
            ps.setBoolean(5, credencial.isEliminado());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) credencial.setId(rs.getLong(1));
            }
        }
    }

    @Override
    public CredencialAcceso leer(long id) throws SQLException {
        String sql = "SELECT * FROM credencialesAcceso WHERE id = ? AND eliminado = false";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                CredencialAcceso c = new CredencialAcceso();
                c.setId(rs.getLong("id"));
                c.setHashPassword(rs.getString("hashPassword"));
                c.setSalt(rs.getString("salt"));
                c.setUltimoCambio(rs.getTimestamp("ultimoCambio").toLocalDateTime());
                c.setRequiereReset(rs.getBoolean("requiereReset"));
                c.setEliminado(rs.getBoolean("eliminado"));
                return c;
            }
        }
        return null;
    }

    @Override
    public List<CredencialAcceso> leerTodos() throws SQLException {
        List<CredencialAcceso> lista = new ArrayList<>();
        String sql = "SELECT * FROM credencialesAcceso WHERE eliminado = false";
        try (PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                CredencialAcceso c = new CredencialAcceso();
                c.setId(rs.getLong("id"));
                c.setHashPassword(rs.getString("hashPassword"));
                c.setSalt(rs.getString("salt"));
                c.setUltimoCambio(rs.getTimestamp("ultimoCambio").toLocalDateTime());
                c.setRequiereReset(rs.getBoolean("requiereReset"));
                c.setEliminado(rs.getBoolean("eliminado"));
                lista.add(c);
            }
        }
        return lista;
    }

    @Override
    public void actualizar(CredencialAcceso c) throws SQLException {
        String sql = "UPDATE credencialesAcceso SET hashPassword=?, salt=?, ultimoCambio=?, requiereReset=?, eliminado=? WHERE id=?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, c.getHashPassword());
            ps.setString(2, c.getSalt());
            ps.setTimestamp(3, Timestamp.valueOf(c.getUltimoCambio()));
            ps.setBoolean(4, c.isRequiereReset());
            ps.setBoolean(5, c.isEliminado());
            ps.setLong(6, c.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public void eliminar(long id) throws SQLException {
        String sql = "UPDATE credencialesAcceso SET eliminado = true WHERE id = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }
}
