package service;

import dao.CredencialAccesoDao;
import entities.CredencialAcceso;
import config.DatabaseConnection;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import service.exceptions.RegistroNoEncontradoException;

public class CredencialAccesoService implements GenericService<CredencialAcceso> {

    public CredencialAccesoService() {
    }
    
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
    public void eliminar(long id) throws SQLException, RegistroNoEncontradoException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            try {
                CredencialAccesoDao dao = new CredencialAccesoDao(conn);
                if (dao.leer(id) != null) {
                    dao.eliminar(id);
                    conn.commit();
                } else {
                    conn.rollback();
                    throw new RegistroNoEncontradoException("La credencial con id " + id + " no existe.");
                }
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }finally{
                conn.setAutoCommit(true);
            }
        }
    }
}
