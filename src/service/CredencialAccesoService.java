package service;

import dao.CredencialAccesoDao;
import entities.CredencialAcceso;
import config.DatabaseConnection;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class CredencialAccesoService implements GenericService<CredencialAcceso> {

    @Override
    public void insertar(CredencialAcceso c) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            CredencialAccesoDao dao = new CredencialAccesoDao(conn);
            c.setUltimoCambio(LocalDateTime.now());
            dao.crear(c);
        }
    }

    @Override
    public CredencialAcceso getById(long id) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            CredencialAccesoDao dao = new CredencialAccesoDao(conn);
            return dao.leer(id);
        }
    }

    @Override
    public List<CredencialAcceso> getAll() throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            CredencialAccesoDao dao = new CredencialAccesoDao(conn);
            return dao.leerTodos();
        }
    }

    @Override
    public void actualizar(CredencialAcceso c) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            CredencialAccesoDao dao = new CredencialAccesoDao(conn);
            dao.actualizar(c);
        }
    }

    @Override
    public void eliminar(long id) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            CredencialAccesoDao dao = new CredencialAccesoDao(conn);
            dao.eliminar(id);
        }
    }
}
