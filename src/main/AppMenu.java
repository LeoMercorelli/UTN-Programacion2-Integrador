package main;

import entities.Usuario;
import entities.CredencialAcceso;
import service.UsuarioService;
import service.CredencialAccesoService;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;
import service.exceptions.RegistroNoEncontradoException;

public class AppMenu {

    private final Scanner scanner;
    private final UsuarioService usuarioService;
    private final CredencialAccesoService credencialService;

    public AppMenu() {
        scanner = new Scanner(System.in);
        usuarioService = new UsuarioService();
        credencialService = new CredencialAccesoService();
    }

    public void mostrarMenu() {
        int opcion;
        try {
            do {
                System.out.println("\n===== MENU PRINCIPAL =====");
                System.out.println("1. Crear nuevo usuario");
                System.out.println("2. Listar usuarios");
                System.out.println("3. Buscar usuario por ID");
                System.out.println("4. Actualizar usuario");
                System.out.println("5. Eliminar usuario (baja logica)");
                System.out.println("0. Salir");
                System.out.print("Seleccione una opcion: ");

                opcion = leerEntero();

                switch (opcion) {
                    case 1 ->
                        crearUsuario();
                    case 2 ->
                        listarUsuarios();
                    case 3 ->
                        buscarUsuario();
                    case 4 ->
                        actualizarUsuario();
                    case 5 ->
                        eliminarUsuario();
                    case 0 -> {
                        System.out.println("Saliendo...");
                        Thread.sleep(2000);
                    }
                    default ->
                        System.out.println("Opcion invalida.");
                }
            } while (opcion != 0);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();

        }
    }

    private void crearUsuario() {
        try {
            System.out.println("\n--- Crear nuevo usuario ---");
            System.out.print("Username: ");
            String username = scanner.nextLine();
            System.out.print("Email: ");
            String email = scanner.nextLine();
            boolean activo = true;                    //️DECISIÓN DE DISEÑO: El atributo 'activo' se establece en TRUE por defecto al crear un nuevo usuario.

            System.out.print("Hash Password: ");      //DECISIÓN DE DISEÑO: En este trabajo se simula el ingreso manual de los campos 'hashPassword' y 'salt'.
            String hash = scanner.nextLine();         // No se implementa el algoritmo de generación ni cifrado real, dado que el objetivo del TFI es la persistencia
            System.out.print("Salt: ");               // y manejo transaccional de datos, no la lógica criptográfica de autenticación.
            String salt = scanner.nextLine();

            CredencialAcceso cred = new CredencialAcceso();
            cred.setHashPassword(hash);
            cred.setSalt(salt);
            cred.setUltimoCambio(LocalDateTime.now()); // Lo asignamos en todas las capas para asegurar robustez
            cred.setRequiereReset(true);
            cred.setEliminado(false);

            Usuario u = new Usuario();
            u.setUsername(username);
            u.setEmail(email);
            u.setActivo(activo);
            u.setFechaRegistro(LocalDateTime.now()); // Lo asignamos en todas las capas para asegurar robustez
            u.setEliminado(false);
            u.setCredencial(cred);

            try{
            usuarioService.insertar(u);
            System.out.println("Usuario creado con exito.");
            }catch(IllegalArgumentException e){
                System.out.println("Error al crear usuario: "+e.getMessage());
            }

        } catch (SQLException e) {
            System.err.println("Error al crear usuario: " + e.getMessage());
        }
    }

    private void listarUsuarios() {
        try {
            System.out.println("\n--- Lista de usuarios ---");
            List<Usuario> usuarios = usuarioService.getAll();
            if (usuarios.isEmpty()) {
                System.out.println("No hay usuarios registrados.");
            } else {
                for (Usuario u : usuarios) {
                    System.out.println(u);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al listar usuarios: " + e.getMessage());
        }
    }

    private void buscarUsuario() {
        try {
            System.out.print("\nIngrese el ID del usuario (0 para cancelar): ");
            long id = leerLong();
            if (id==0){
                this.mostrarMenu();
            }else{
                Usuario u = usuarioService.getById(id);
                if (u != null) {
                    System.out.println(u);
                } else {
                    System.out.println("Usuario no encontrado.");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar usuario: " + e.getMessage());
        }
    }

    private void actualizarUsuario() {
        try {
            System.out.print("\nIngrese el ID del usuario a actualizar: (0 para cancelar)");
            long id = leerLong();
            if (id == 0) {
                this.mostrarMenu();
            } else {
                Usuario u = usuarioService.getById(id);
                CredencialAcceso c = credencialService.getById(id);
                if (u == null) {
                    System.out.println("Usuario no encontrado.");
                    return;
                }

                
                System.out.print("Nuevo email: ");
                u.setEmail(scanner.nextLine());
                
                System.out.print("Activo? (true/false): ");
                u.setActivo(Boolean.parseBoolean(scanner.nextLine()));
                u.setFechaRegistro(LocalDateTime.now());
                
                c.setUltimoCambio(LocalDateTime.now());

                
                usuarioService.actualizar(u);
                credencialService.actualizar(c);
                System.out.println("Usuario actualizado con exito.");
            }
        } catch (SQLException | IllegalArgumentException e) {
            System.err.println("Error al actualizar usuario: " + e.getMessage());
        }
    }

    private void eliminarUsuario(){
        try {
            System.out.print("\nIngrese el ID del usuario a eliminar (0 para volver): ");
            long id = leerLong();
            if (id==0){
                this.mostrarMenu();
            }else{
                usuarioService.eliminar(id);
                credencialService.eliminar(id);
                System.out.println("Usuario eliminado (baja logica).");
            }
        } catch (SQLException | RegistroNoEncontradoException e) {
            System.err.println("Error al eliminar usuario: " + e.getMessage());
        }
    }

    private int leerEntero() {
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.print("Ingrese un numero valido: ");
            }
        }
    }

    private long leerLong() {
        while (true) {
            try {
                Long n = Long.valueOf(scanner.nextLine());
                if (n > 0) {
                    return n;
                } else if (n == 0) {
                    System.out.println("Volviendo...");
                    Thread.sleep(2000);
                    return 0;
                } else {
                    throw new IllegalArgumentException();
                }
            } catch (NumberFormatException e) {
                System.out.print("Ingrese un numero valido (0 para cancelar): ");
            } catch (IllegalArgumentException e) {
                System.out.print("El id debe ser mayor a 0.\nIngrese el id (0 para cancelar): ");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
