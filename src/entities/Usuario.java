package entities;

import java.time.LocalDateTime;

public class Usuario{
    private Long id;
    private boolean eliminado;
    private String username;
    private String email;
    private boolean activo;
    private LocalDateTime fechaRegistro;
    private CredencialAcceso credencial;

    public Usuario() {}

    public Usuario(Long id, boolean eliminado, String username, String email, boolean activo, LocalDateTime fechaRegistro, CredencialAcceso credencial) {
        this.id = id;
        this.eliminado = eliminado;
        this.username = username;
        this.email = email;
        this.activo = activo;
        this.fechaRegistro = fechaRegistro;
        this.credencial = credencial;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public boolean isEliminado() { return eliminado; }
    public void setEliminado(boolean eliminado) { this.eliminado = eliminado; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; }

    public CredencialAcceso getCredencial() { return credencial; }
    public void setCredencial(CredencialAcceso credencial) { this.credencial = credencial; }

    @Override
    public String toString() {
        return "Usuario{" +
                "id=" + id +
                ", eliminado=" + eliminado +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", activo=" + activo +
                ", fechaRegistro=" + fechaRegistro +
                ", credencial=" + credencial +
                '}';
    }
}
