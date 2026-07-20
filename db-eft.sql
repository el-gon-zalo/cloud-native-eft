
-- Crear base de datos


CREATE DATABASE IF NOT EXISTS db-eft;
USE db-eft;


-- Tabla CURSOS


CREATE TABLE cursos (

    id BIGINT NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(255) NOT NULL,
    fecha_creacion DATETIME NOT NULL,
    fecha_actualizacion DATETIME,
    cupo INT NOT NULL,
    profesor_a_cargo_username VARCHAR(100) NOT NULL,
    estado VARCHAR(20) NOT NULL,
    s3_key VARCHAR(255),
    s3_url VARCHAR(500),

    PRIMARY KEY (id)

);


-- Tabla CURSO_INSCRITOS


CREATE TABLE curso_inscritos (

    curso_id BIGINT NOT NULL,
    username VARCHAR(100) NOT NULL,

    PRIMARY KEY (curso_id, username),

    CONSTRAINT fk_curso_inscritos
        FOREIGN KEY (curso_id)
        REFERENCES cursos(id)
        ON DELETE CASCADE

);

-- Datos de ejemplo CURSOS


INSERT INTO cursos
(
nombre,
fecha_creacion,
fecha_actualizacion,
cupo,
profesor_a_cargo_username,
estado,
s3_key,
s3_url
)
VALUES

(
'Programación Java',
NOW(),
NOW(),
30,
'profesor1',
'PENDIENTE',
NULL,
NULL
),

(
'Bases de Datos',
NOW(),
NOW(),
25,
'profesor1',
'ACTIVO',
NULL,
NULL
),

(
'Cloud Computing',
NOW(),
NOW(),
40,
'admin',
'FINALIZADO',
'cursos/CUR-0003.pdf',
'https://mi-bucket.s3.amazonaws.com/cursos/CUR-0003.pdf'
);


-- Inscritos de ejemplo


INSERT INTO curso_inscritos
(curso_id, username)
VALUES
(1,'alumno1'),
(1,'alumno2'),
(2,'alumno3'),
(3,'alumno4'),
(3,'alumno5');


-- Tabla USUARIO


CREATE TABLE usuario (

    usuario VARCHAR(100) PRIMARY KEY,
    password VARCHAR(255) NOT NULL,
    correo VARCHAR(255) NOT NULL UNIQUE,
    rol VARCHAR(30) NOT NULL

);


-- Usuarios de ejemplo


INSERT INTO usuario
(usuario,password,correo,rol)
VALUES

(
'admin',
'admin123',
'admin@duoc.cl',
'ADMIN'
),

(
'profesor1',
'profesor123',
'profesor1@duoc.cl',
'PROFESOR'
);
