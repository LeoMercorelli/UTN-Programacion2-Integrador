-- ##############################################################
-- ETAPA 1 - Modelado y definición de Constraints
-- ##############################################################

USE `tpi-bd-i`;

/* ================================================================ */
/*============== CREACION TABLA "CredencialesAcceso" ============== */
/* ================================================================ */
CREATE TABLE CredencialesAcceso (
    id_CredencialAcceso MEDIUMINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    eliminado BOOLEAN NOT NULL,
    hashPassword VARCHAR(255) NOT NULL,
    salt VARCHAR(64) NOT NULL,
    ultimoCambio DATETIME,
    requiereReset BOOLEAN NOT NULL
);

/* ================================================================ */
/* ================== CREACION TABLA "usuarios" =================== */
/* ================================================================ */
CREATE TABLE usuarios (
    id MEDIUMINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    eliminado BOOLEAN NOT NULL,
    username VARCHAR(30) NOT NULL UNIQUE,
    email VARCHAR(120) NOT NULL UNIQUE, 
    activo BOOLEAN NOT NULL,
    fechaRegistro DATETIME NOT NULL,
    id_CredencialAcceso MEDIUMINT UNSIGNED UNIQUE,
    CONSTRAINT fk_id_credAcceso FOREIGN KEY (id_CredencialAcceso)
        REFERENCES credencialesAcceso (id_CredencialAcceso)
);

/* ================== BORRAR TABLAS COMPLETAS ===================== */
/* =============== **SOLO EN CASO DE NECESITARSE** ================ */
DROP TABLE usuarios;
DROP TABLE credencialesAcceso;

/* ================== VACIAR TABLAS COMPLETAS ===================== */
/* =============== **SOLO EN CASO DE NECESITARSE** ================ */
-- Desactivar las verificaciones de claves foraneas
SET FOREIGN_KEY_CHECKS = 0;

-- Vaciar primero la tabla dependiente
TRUNCATE TABLE usuarios;

-- Luego vaciar la tabla referenciada
TRUNCATE TABLE credencialesAcceso;

-- Reactivar las verificaciones de claves foraneas
SET FOREIGN_KEY_CHECKS = 1;







/* ==================== TRIGGER DE VERIFICACION =================== */
/* ===================== EMAIL DEBE CONTENER @ ==================== */
/* =================== PARA LA CONSULTA "INSERT" ================== */
DELIMITER $$
CREATE DEFINER=`root`@`localhost` TRIGGER `tpi-bd-i`.`usuarios_BEFORE_INSERT` 
BEFORE INSERT ON `usuarios` 
FOR EACH ROW
BEGIN
  IF INSTR(NEW.email, '@') = 0 THEN
    SIGNAL SQLSTATE '45000'
      SET MESSAGE_TEXT = 'El email debe contener @';
  END IF;
END$$
DELIMITER ;

/* ==================== TRIGGER DE VERIFICACION =================== */
/* ===================== EMAIL DEBE CONTENER @ ==================== */
/* =================== PARA LA CONSULTA "UPDATE" ================== */
DELIMITER $$
CREATE TRIGGER usuarios_BEFORE_UPDATE
BEFORE UPDATE ON usuarios
FOR EACH ROW
BEGIN
  IF NEW.email <> OLD.email AND INSTR(NEW.email, '@') = 0 THEN
    SIGNAL SQLSTATE '45000'
      SET MESSAGE_TEXT = 'El email debe contener @';
  END IF;
END$$
DELIMITER ;

/* ======================= ELIMINAR TRIGGERS ====================== */
/* =============== **SOLO EN CASO DE NECESITARSE** ================ */
DROP TRIGGER `usuarios_BEFORE_INSERT`;
DROP TRIGGER `usuarios_BEFORE_UPDATE`;





-- ##############################################################
-- ######### VALIDACION DE CONSTRINTS MEDIANTE PRUEBAS ##########
-- ##############################################################

-- 1) Violación de integridad referencial (Foreign Key)
INSERT INTO usuarios
VALUES (200, false, 'usuario1', 'usuario1@gmail.com', true, '2025-10-14 15:43:00', 4562);

-- 2)	Violación de restricción UNIQUE en clave foránea
INSERT INTO usuarios
VALUES 
(null, false, 'usuario1', 'usuario1@gmail.com', true, '2025-10-14 15:43:00', 4562), 
(null, false, 'usuario2', 'usuario2@gmail.com', true, '2025-10-14 20:43:00', 4562);

-- 3)	Violación de restricción UNIQUE en username
INSERT INTO usuarios 
VALUES 
(null,false, 'usuario1', 'usuario1@gmail.com', true, '2025-10-14 15:43:00', 4562), 
(null,false, 'usuario1', 'usuario2@gmail.com', true, '2025-10-14 20:43:00', 4561);