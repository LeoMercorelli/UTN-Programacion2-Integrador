-- ##############################################################
-- ETAPA 2 - Generación y carga de datos masivos con SQL puro
-- ##############################################################
USE `tpi-bd-i`;

/* ================================================================ */
/* ======================= LIMPIEZA PREVIA ======================== */
/* ================================================================ */
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE credencialesacceso;
TRUNCATE TABLE usuarios;
SET FOREIGN_KEY_CHECKS = 1;


/* ===========================  (1)  ============================== */
/* ==================== VARIABLES Y PARAMETROS ==================== */
/* ================================================================ */
SET @N := 400000;                                                          -- cantidad de usuarios/credenciales a crear 
SET SESSION cte_max_recursion_depth = 400000;                              -- aumentar el límite de recursiones del motor para poder crear los 400000 registros
show variables like 'cte_max_recursion_depth';



/* ===========================  (2)  ============================== */
/* ================ GENERACION DE SERIE DE NUMEROS ================ */
/* ================================================================ */
CREATE TEMPORARY TABLE serie (n INT UNSIGNED PRIMARY KEY) ENGINE=MEMORY;

INSERT INTO serie
WITH RECURSIVE seq(n) AS (
  SELECT 1                                                                -- Valor inicial de la secuencia (Uno)
  UNION ALL
  SELECT n+1 FROM seq WHERE n < @N                                        -- Se va sumando 1 hasta llegar al valor @N
)
SELECT n FROM seq;                                                        -- Inserta todos los valores generados en la tabla 'serie'

SELECT MAX(n) FROM serie;                                                 -- Muestra el número más alto insertado (el máximo)



/* ===========================  (3)  ============================== */
/* ==================== GENERACION DE USUARIOS ==================== */
/* ================================================================ */
INSERT INTO usuarios (eliminado, username, email, activo, fechaRegistro, id_CredencialAcceso)
SELECT
  CASE WHEN (n % 10) < 8 THEN 0 ELSE 1 END      AS eliminado,              -- 80% no eliminados
  CONCAT('user', LPAD(n, 6, '0'))               AS username,               -- único
  CONCAT('user', LPAD(n, 6, '0'), '@mail.com')  AS email,                  -- único
  CASE WHEN (n % 10) < 8 THEN 1 ELSE 0 END      AS activo,                 -- 80% activos
  DATE_SUB(NOW(), INTERVAL (n % 365) DAY)       AS fechaRegistro,
  NULL                                          AS id_CredencialAcceso     -- se asigna en el PUNTO (5)
FROM serie;



/* ==============================  (4)  ================================= */
/* ===================== GENERACION DE CREDENCIALES ===================== */
/* ===== UNA POR CADA USUARIO /// id(usuario) = id_credencialAcceso ===== */
INSERT INTO credencialesacceso 
  (id_CredencialAcceso, eliminado, hashPassword, salt, ultimoCambio, requiereReset)
SELECT
  u.id                                          AS id_CredencialAcceso,     -- mismo id ⇒ 1↔1 directo
  CASE WHEN (u.id % 10) < 8 THEN 0 ELSE 1 END   AS eliminado,               -- 80% no eliminadas
  SHA2(CONCAT(u.id, REPLACE(UUID(), '-', '')), 256) AS hashPassword,        -- UUID() genera valores únicos con guiones
  REPLACE(UUID(), '-', '')					    AS salt,                    -- REPLACE quita los guiones al valor generado por UUID()
  u.fechaRegistro                               AS ultimoCambio,            -- igual al registro
  CASE WHEN (u.id % 10) = 0 THEN 1 ELSE 0 END   AS requiereReset            -- ~10%
FROM usuarios u;

/* ======= VERIFICAMOS QUE NO HAYA SALT's O HASH's DUPLICADOS ========== */
SELECT salt, COUNT(*) FROM credencialesacceso GROUP BY salt HAVING COUNT(*) > 1;
SELECT hashPassword, COUNT(*) FROM credencialesacceso GROUP BY hashPassword HAVING COUNT(*) > 1;




/* ==============================  (5)  ================================ */
/* ===================== ASIGNAR FK A CADA USUARIO ===================== */
/* ===================================================================== */
UPDATE usuarios
SET id_CredencialAcceso = id;




/* ==============================  (6)  ================================ */
/* ====================== VERIFICACIONES / CHECKS ====================== */
/* ===================================================================== */
SELECT COUNT(*) AS total_usuarios FROM usuarios;                                -- Cuenta cuantos registros hay en la tabla Usuario
SELECT COUNT(*) AS total_credenciales FROM credencialesacceso;                  -- Cuenta cuantos registros hay en la tabla CredencialAcceso

SELECT COUNT(*) AS usuarios_con_fk_invalida                                     -- Verifica si hay usuarios que apuntan a una credencial inexistente (FK invalida)
FROM usuarios u
LEFT JOIN credencialesacceso c ON c.id_CredencialAcceso = u.id_CredencialAcceso
WHERE u.id_CredencialAcceso IS NOT NULL AND c.id_CredencialAcceso IS NULL;      -- Si el resultado es 0, esta todo correcto

SELECT                                                                          -- Controla la relacion 1 a 1 entre usuario y credencial
  SUM(id_CredencialAcceso IS NOT NULL) AS usuarios_con_credencial,
  SUM(id_CredencialAcceso IS NULL)     AS usuarios_sin_credencial
FROM usuarios;                                                                  -- Muestra cuantos usuarios tienen credencial y cuántos no

SELECT                                                                          -- Muestra todos los datos combinados de Usuario y su Credencial asociada
  u.id,                                                                         -- Permite revisar visualmente que los registros estén correctamente vinculados
  u.eliminado,
  u.username,
  u.email,
  u.activo,
  u.fechaRegistro,
  u.id_CredencialAcceso,
  c.hashPassword,
  c.salt,
  c.ultimoCambio,
  c.requiereReset 
FROM usuarios u
LEFT JOIN credencialesacceso c 
  ON u.id_CredencialAcceso = c.id_CredencialAcceso;
