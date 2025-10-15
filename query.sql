--BalanzApp query

CREATE TABLE tbl_roles (
    id_rol SERIAL PRIMARY KEY,
    nombre_rol VARCHAR(50) NOT NULL,
    descripcion TEXT,
    nivel_acceso INT DEFAULT 3 -- 1=Admin, 2=Contador, 3=Auditor
);

CREATE TABLE tbl_usuarios (
    id_usuario SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    genero VARCHAR(20),
    fecha_nacimiento DATE,
    dui VARCHAR(15) UNIQUE,
    telefono VARCHAR(15),
    direccion TEXT,
    correo VARCHAR(100) UNIQUE NOT NULL,
    usuario VARCHAR(50) UNIQUE NOT NULL,
    contraseña VARCHAR(255) NOT NULL,
    id_rol INT NOT NULL,
    FOREIGN KEY (id_rol) REFERENCES tbl_roles(id_rol)
);

CREATE TABLE tbl_cntaContables (
    id_cuenta SERIAL PRIMARY KEY,
    codigo VARCHAR(20) UNIQUE NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    tipo VARCHAR(50) NOT NULL, -- Ej: Activo, Pasivo, Capital, Ingreso, Gasto
    naturaleza VARCHAR(20) NOT NULL,
    grupo VARCHAR(50), -- Ej: Corriente, No Corriente, etc.
    id_cuenta_padre INT REFERENCES tbl_cntaContables(id_cuenta) -- jerarquía
);

CREATE TABLE tbl_clasDocumento (
    id_clasificacion SERIAL PRIMARY KEY,
    nombre_clasificacion VARCHAR(100) NOT NULL
);

CREATE TABLE tbl_partidas (
    id_partida SERIAL PRIMARY KEY,
    numero_partida SERIAL UNIQUE,
    fecha DATE NOT NULL,
    concepto TEXT NOT NULL,
    tipo_partida VARCHAR(50), -- Ej: Ajuste, Apertura, Cierre, Regular
    estado VARCHAR(20) DEFAULT 'Registrada', -- o Cerrada, Anulada
    id_usuario INT NOT NULL,
    FOREIGN KEY (id_usuario) REFERENCES tbl_usuarios(id_usuario)
);

CREATE TABLE tbl_docFuente (
    id_documento SERIAL PRIMARY KEY,
    archivo_pdf VARCHAR(255) NOT NULL,
    fecha_subida TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    id_partida INT NOT NULL,
    id_clasificacion INT,
    FOREIGN KEY (id_partida) REFERENCES tbl_partidas(id_partida) ON DELETE CASCADE,
    FOREIGN KEY (id_clasificacion) REFERENCES tbl_clasDocumento(id_clasificacion)
);

CREATE TABLE tbl_detallePartida (
    id_detalle SERIAL PRIMARY KEY,
    id_partida INT NOT NULL,
    id_cuenta INT NOT NULL,
    descripcion TEXT,
    debe NUMERIC(12,2) DEFAULT 0 CHECK (debe >= 0),
    haber NUMERIC(12,2) DEFAULT 0 CHECK (haber >= 0),
    FOREIGN KEY (id_partida) REFERENCES tbl_partidas(id_partida) ON DELETE CASCADE,
    FOREIGN KEY (id_cuenta) REFERENCES tbl_cntaContables(id_cuenta)
);

CREATE TABLE tbl_bitacAud (
    id_auditoria SERIAL PRIMARY KEY,
    id_usuario INT NOT NULL,
    accion TEXT NOT NULL,
    modulo VARCHAR(100) NOT NULL,
    detalles TEXT,
    fecha DATE NOT NULL DEFAULT CURRENT_DATE,
    hora TIME NOT NULL DEFAULT CURRENT_TIME,
    FOREIGN KEY (id_usuario) REFERENCES tbl_usuarios(id_usuario)
);

INSERT INTO tbl_roles (nombre_rol, descripcion, nivel_acceso) VALUES
('Administrador', 'Acceso total al sistema y gestión de usuarios.', 1),
('Contador', 'Registro de partidas contables y gestión diaria.', 2),
('Auditor', 'Visualización de reportes y partidas; solo lectura.', 3);

INSERT INTO tbl_usuarios (nombre, genero, fecha_nacimiento, dui, telefono, direccion, correo, usuario, contraseña, id_rol) VALUES
('Ana García', 'Femenino', '1990-05-15', '01234567-8', '7890-1234', 'Calle Principal #123, Ciudad A', 'ana.garcia@empresa.com', 'agarcia', 'clave123', 1),
('Juan Pérez', 'Masculino', '1985-11-20', '02345678-9', '6543-2109', 'Avenida Central #456, Ciudad B', 'juan.perez@empresa.com', 'jperez', 'clave123', 2),
('Sofía López', 'Femenino', '1998-08-01', '03456789-0', '7777-8888', 'Residencial El Sol, Casa #10', 'sofia.lopez@empresa.com', 'slopez', 'clave123', 3);