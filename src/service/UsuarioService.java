package service;

import config.DatabaseConnection;
import dao.UsuarioDao;
import dao.CredencialAccesoDao;
import entities.Usuario;
import entities.CredencialAcceso;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import service.exceptions.RegistroNoEncontradoException;

public class UsuarioService implements GenericService<Usuario> {
    //Expresiones regulares para validar username y email.
    //Contienen rangos de caracteres, de numeros, simbolos y longitud
    private static final String USERNAME_REGEX = "^[a-zA-Z0-9_.-]{4,30}$";
    private static final String EMAIL_REGEX = "^(?=.{1,120}$)[\\w\\.-]+@([\\w\\-]+\\.)+[a-zA-Z]{2,4}$";
    
    @Override
    public void insertar(Usuario usuario) throws SQLException, IllegalArgumentException {
        
            if (emailValido(usuario.getEmail()) && usernameValido(usuario.getUsername())) {
                try (Connection conn = DatabaseConnection.getConnection();) {
                    conn.setAutoCommit(false);

                    try {
                        CredencialAccesoDao credDao = new CredencialAccesoDao(conn);
                        UsuarioDao usuarioDao = new UsuarioDao(conn);

                        CredencialAcceso cred = usuario.getCredencial();
                        cred.setUltimoCambio(LocalDateTime.now());
                        credDao.crear(cred);

                        usuario.setFechaRegistro(LocalDateTime.now());
                        usuarioDao.crear(usuario);

                        conn.commit();
                    } catch (SQLException e) {
                        conn.rollback();
                        throw e;
                    } finally {
                        conn.setAutoCommit(true);
                        conn.close();
                    }
                }
            }
        }
    

    @Override
    public Usuario getById(long id) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            UsuarioDao dao = new UsuarioDao(conn);
            return dao.leer(id);
        }
    }

    @Override
    public List<Usuario> getAll() throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            UsuarioDao dao = new UsuarioDao(conn);
            return dao.leerTodos();
        }
    }

    @Override
    public void actualizar(Usuario u) throws SQLException, IllegalArgumentException {
        if(emailValido(u.getEmail())){
            try (Connection conn = DatabaseConnection.getConnection()) {
                UsuarioDao dao = new UsuarioDao(conn);
                dao.actualizar(u);
            }
        }
    }
    
    
    @Override
    public void eliminar(long id) throws SQLException, RegistroNoEncontradoException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try{
                UsuarioDao dao = new UsuarioDao(conn);
                if (dao.leer(id) != null) {
                    dao.eliminar(id);
                    conn.commit();
                } else {
                    conn.rollback();
                    throw new RegistroNoEncontradoException("El usuario con id " + id + " no existe.");
                }
            }catch(SQLException e){
                conn.rollback();
                throw e;
            }finally{
                conn.setAutoCommit(true);
            }
        }
    }
    
    public static boolean usernameValido(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de usuario es obligatorio"); // Si es nulo o está vacío retorna false (Campo obligatorio).
        }

        // Validación de formato
        if (!username.matches(USERNAME_REGEX)) {
            throw new IllegalArgumentException("""
                                               El username solo puede contener letras, números, guiones, guiones bajos, puntos, 
                                               no debe contener espacios y debe tener entre 4 y 30 caracteres.""");
        }

        return true;
    }
    
    public boolean emailValido(String email) throws IllegalArgumentException{
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("El email es obligatorio"); // Si es nulo o está vacío retorna false (Campo obligatorio).
        }

        // Validación de formato y caracteres del email
        if (!email.matches(EMAIL_REGEX)) {
            throw new IllegalArgumentException("""
                                               \nEl email solo puede contener letras, números, guiones, guiones bajos y puntos,
                                               no debe contener espacios y debe tener entre 4 y 120 caracteres.""");
        }

        return true;
    }
}
