package dao;

import java.sql.SQLException;
import java.util.List;

public interface GenericDao<T> {
    void crear(T entidad) throws SQLException;
    T leer(long id) throws SQLException;
    List<T> leerTodos() throws SQLException;
    void actualizar(T entidad) throws SQLException;
    void eliminar(long id) throws SQLException;
}
