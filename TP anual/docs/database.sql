-- =====================================================================
-- Copa Internacional de Clubes - Programacion B - UFASTA 2026
-- Script de base de datos: crea las tablas que usa la aplicacion y
-- carga datos de ejemplo (16 ciudades, 16 estadios).
--
-- Como ejecutarlo:
--   1. En pgAdmin, crear una base vacia llamada "tpprogra".
--   2. Abrir el Query Tool sobre esa base, cargar este archivo y ejecutarlo (F5).
--   o desde una terminal:
--      psql -U postgres -d tpprogra -f docs/database.sql
--
-- ATENCION: el script BORRA primero las tablas, asi que los datos que haya en
-- "ciudades" y "estadios" se reemplazan por los datos de ejemplo.
-- =====================================================================

DROP TABLE IF EXISTS estadios;
DROP TABLE IF EXISTS ciudades;

-- ---------------------------------------------------------------------
-- Ciudades
-- ---------------------------------------------------------------------
CREATE TABLE ciudades (
    idciudad  BIGINT       PRIMARY KEY,
    nombre    VARCHAR(100) NOT NULL,
    pais      VARCHAR(100) NOT NULL,
    CONSTRAINT ck_ciudades_id     CHECK (idciudad > 0),
    CONSTRAINT ck_ciudades_nombre CHECK (length(trim(nombre)) > 0),
    CONSTRAINT ck_ciudades_pais   CHECK (length(trim(pais)) > 0)
);

-- ---------------------------------------------------------------------
-- Estadios (cada estadio pertenece a una sola ciudad)
-- ON DELETE RESTRICT: la base no permite borrar una ciudad que todavia
-- tiene estadios. La aplicacion tambien valida esta regla antes de borrar
-- (excepcion de dominio); esta restriccion es el segundo control.
-- ---------------------------------------------------------------------
CREATE TABLE estadios (
    idestadio  BIGINT       PRIMARY KEY,
    idciudad   BIGINT       NOT NULL,
    nombre     VARCHAR(150) NOT NULL,
    capacidad  INTEGER      NOT NULL,
    CONSTRAINT fk_estadios_ciudad FOREIGN KEY (idciudad)
        REFERENCES ciudades (idciudad)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,
    CONSTRAINT ck_estadios_id        CHECK (idestadio > 0),
    CONSTRAINT ck_estadios_nombre    CHECK (length(trim(nombre)) > 0),
    CONSTRAINT ck_estadios_capacidad CHECK (capacidad > 0)
);

CREATE INDEX idx_estadios_idciudad ON estadios (idciudad);

-- ---------------------------------------------------------------------
-- Datos de ejemplo
-- La eliminacion directa necesita 13 estadios distintos
-- (4 cuartos x 2 + 2 semifinales x 2 + 1 final).
-- Se cargan 16 para tener margen al probar bajas.
-- ---------------------------------------------------------------------
INSERT INTO ciudades (idciudad, nombre, pais) VALUES
    (1,  'Buenos Aires',   'Argentina'),
    (2,  'Rosario',        'Argentina'),
    (3,  'Mar del Plata',  'Argentina'),
    (4,  'Rio de Janeiro', 'Brasil'),
    (5,  'Sao Paulo',      'Brasil'),
    (6,  'Madrid',         'España'),
    (7,  'Barcelona',      'España'),
    (8,  'Milan',          'Italia'),
    (9,  'Roma',           'Italia'),
    (10, 'Munich',         'Alemania'),
    (11, 'Londres',        'Reino Unido'),
    (12, 'Manchester',     'Reino Unido'),
    (13, 'Paris',          'Francia'),
    (14, 'Estambul',       'Turquía'),
    (15, 'Tokio',          'Japón'),
    (16, 'Ciudad de Mexico', 'México');

INSERT INTO estadios (idestadio, idciudad, nombre, capacidad) VALUES
    (1,  1,  'Estadio Monumental',          84567),
    (2,  1,  'Estadio Alberto J. Armando',  54000),
    (3,  2,  'Estadio Gigante de Arroyito', 41654),
    (4,  3,  'Estadio José María Minella',  35354),
    (5,  4,  'Estadio do Maracana',         78838),
    (6,  5,  'Estadio do Morumbi',          66795),
    (7,  6,  'Estadio Santiago Bernabeu',   83186),
    (8,  7,  'Spotify Camp Nou',            99354),
    (9,  8,  'Stadio Giuseppe Meazza',      75817),
    (10, 9,  'Stadio Olimpico',             70634),
    (11, 10, 'Allianz Arena',               75024),
    (12, 11, 'Wembley Stadium',             90000),
    (13, 12, 'Old Trafford',                74310),
    (14, 13, 'Parc des Princes',            47929),
    (15, 14, 'Ataturk Olympic Stadium',     74753),
    (16, 16, 'Estadio Azteca',              83264);
-- Tokio (15) se carga sin estadios a proposito: es la ciudad que se puede
-- borrar para probar el ABM de ciudades. Intentar borrar cualquier otra
-- debe mostrar el error de "la ciudad tiene estadios".

-- Control rapido
SELECT c.nombre AS ciudad, c.pais, COUNT(e.idestadio) AS estadios
FROM ciudades c
LEFT JOIN estadios e ON e.idciudad = c.idciudad
GROUP BY c.idciudad, c.nombre, c.pais
ORDER BY c.idciudad;
