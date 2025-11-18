# Sistema Usuario – Credencial de Acceso (TFI Programación 2)

Proyecto Integrador para la materia Programación 2 de la Tecnicatura Universitaria en Programación (UTN).

## 📝 Descripción del Dominio

Se eligió como dominio la gestión de usuarios y credenciales de acceso. Este escenario permite aplicar los conceptos clave de la materia: persistencia con JDBC, arquitectura multicapa (DAO/Service), relaciones 1-a-1 y gestión de transacciones (commit/rollback).

El sistema modela una relación 1-a-1 unidireccional `Usuario -> CredencialAcceso`.

## 💻 Requisitos Técnicos

* **Java:** JDK 21 [cite: 12]
* **IDE:** Apache NetBeans 21 [cite: 385]
* **Base de Datos:** MySQL Server 8.0 [cite: 386]
* **Driver:** MySQL Connector/J (incluido en el proyecto).

## 🗄️ Pasos para la Base de Datos

Para levantar el entorno de base de datos:

1.  Crear una nueva base de datos (schema) en MySQL con el nombre `tpi-bd-i`.
2.  Ejecutar el script `sql/etapa1.sql` para crear las tablas (`usuarios`, `credencialesacceso`) y sus relaciones.
3.  Ejecutar el script `sql/etapa2.sql` para cargar los datos de prueba iniciales.

## 🚀 Cómo compilar y ejecutar

1. Abra el proyecto en su IDE (por ejemplo, Apache NetBeans).

2. En el archivo `config/DatabaseConnection.java`, configure los datos de conexión a MySQL:
   - URL de conexión (host, puerto y nombre de la base de datos).
   - Usuario.
   - Contraseña.

3. Compile el proyecto (en NetBeans: **Run > Clean and Build Project**).

4. Ejecute la clase `main/main.java`.  
   Esto iniciará la aplicación por consola y mostrará el menú principal (`AppMenu`), desde donde se pueden realizar todas las operaciones sobre usuarios.


## 🎥 Video Demostración

Enlace al video de la demostración: https://www.youtube.com/watch?v=b4vKtbd40tc

## 👥 Integrantes

* Pablo Molinari
* Nicolás Olima
* Leonel Mercorelli
* Nicolás Pannunzio