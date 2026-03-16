-- Creación de BD (Comentado igual que el original)
-- CREATE DATABASE tiendaq;

-- En Postgres no se usa USE, se conecta a la base de datos directamente.
-- \c tiendaq;

-- Creación de Tipos ENUM
-- Nota: Si los tipos ya existen, esto generará error. Se puede envolver en un bloque DO o simplemente borrar y crear si es un script de inicialización.
-- Para simplificar, asumimos que es una creación desde cero.

DO $$ BEGIN
    CREATE TYPE tipo_documento_enum AS ENUM ('CC', 'TI', 'CE', 'PASAPORTE');
EXCEPTION
    WHEN duplicate_object THEN null;
END $$;

DO $$ BEGIN
    CREATE TYPE tipo_usuario_enum AS ENUM ('REGISTRADO', 'SIN_REGISTRAR');
EXCEPTION
    WHEN duplicate_object THEN null;
END $$;

DO $$ BEGIN
    CREATE TYPE tipo_empleado_enum AS ENUM ('ADMINISTRADOR', 'VENDEDOR');
EXCEPTION
    WHEN duplicate_object THEN null;
END $$;

DO $$ BEGIN
    CREATE TYPE categoria_producto_enum AS ENUM ('ROPA', 'ACCESORIOS', 'LIBRERIA', 'PAPELERIA');
EXCEPTION
    WHEN duplicate_object THEN null;
END $$;

DO $$ BEGIN
    CREATE TYPE estado_carrito_enum AS ENUM ('VACIO', 'CON_PRODUCTOS', 'EN_PROCESO_DE_PAGO', 'PAGO_PENDIENTE', 'PAGO_EXITOSO');
EXCEPTION
    WHEN duplicate_object THEN null;
END $$;

DO $$ BEGIN
    CREATE TYPE metodo_pago_enum AS ENUM ('PSE', 'TARJETA_CREDITO', 'TARJETA_DEBITO', 'EFECTIVO', 'TRANSFERENCIA');
EXCEPTION
    WHEN duplicate_object THEN null;
END $$;


-- Tabla Usuario
CREATE TABLE IF NOT EXISTS Usuario (
    idUsuario SERIAL PRIMARY KEY,
    nombre VARCHAR(20) NOT NULL,
    apellido VARCHAR(20) NOT NULL,
    tipoDocumento tipo_documento_enum NOT NULL,
    documento VARCHAR(20) NOT NULL UNIQUE,
    telefono VARCHAR(20) NOT NULL UNIQUE,
    correo VARCHAR(70) NOT NULL UNIQUE,
    direccion VARCHAR(100) NOT NULL,
    contrasena VARCHAR(250) NOT NULL,
    tipoUsuario tipo_usuario_enum NOT NULL
);

-- Tabla Empleado
CREATE TABLE IF NOT EXISTS Empleado (
    idEmpleado SERIAL PRIMARY KEY,
    tipoEmpleado tipo_empleado_enum NOT NULL,
    idUsuario INT NOT NULL,
    FOREIGN KEY (idUsuario) REFERENCES Usuario(idUsuario)
);

-- Tabla Cliente
CREATE TABLE IF NOT EXISTS Cliente (
    idCliente SERIAL PRIMARY KEY,
    idUsuario INT NOT NULL,
    FOREIGN KEY (idUsuario) REFERENCES Usuario(idUsuario)
);

-- Tabla Producto
CREATE TABLE IF NOT EXISTS Producto (
    idProducto SERIAL PRIMARY KEY,
    categoria categoria_producto_enum NOT NULL,
    nombreProducto VARCHAR(50) NOT NULL,
    precioUnitario NUMERIC(10,2) NOT NULL
);

-- Tabla Stock
CREATE TABLE IF NOT EXISTS Stock (
    idStock SERIAL PRIMARY KEY,
    idProducto INT NOT NULL,
    fechaIngreso TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    stock INT NOT NULL,
    FOREIGN KEY (idProducto) REFERENCES Producto(idProducto)
);

-- Tabla CarritoCompra
CREATE TABLE IF NOT EXISTS CarritoCompra (
    idCarritoCompra SERIAL PRIMARY KEY,
    fechaCreacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    estado estado_carrito_enum NOT NULL,
    idUsuario INT NOT NULL,
    FOREIGN KEY (idUsuario) REFERENCES Usuario(idUsuario)
);

-- Tabla ItemCarrito
CREATE TABLE IF NOT EXISTS ItemCarrito (
    cantidad INT NOT NULL,
    precioUnitario NUMERIC(10,2) NOT NULL,
    idCarritoCompra INT NOT NULL,
    idProducto INT NOT NULL,
    PRIMARY KEY (idCarritoCompra, idProducto),
    FOREIGN KEY (idCarritoCompra) REFERENCES CarritoCompra(idCarritoCompra),
    FOREIGN KEY (idProducto) REFERENCES Producto(idProducto)
);

-- Tabla Factura
CREATE TABLE IF NOT EXISTS Factura (
    idFactura SERIAL PRIMARY KEY,
    fechaCompra TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    totalCompra NUMERIC(10,2) NOT NULL,
    metodoPago metodo_pago_enum NOT NULL,
    iva NUMERIC(10,2) NOT NULL,
    idEmpleado INT NOT NULL,
    idCliente INT NOT NULL,
    idCarritoCompra INT NOT NULL,
    FOREIGN KEY (idEmpleado) REFERENCES Empleado(idEmpleado),
    FOREIGN KEY (idCliente) REFERENCES Cliente(idCliente),
    FOREIGN KEY (idCarritoCompra) REFERENCES CarritoCompra(idCarritoCompra)
);

CREATE TABLE DetalleFactura (
    idFactura INT NOT NULL,
    idProducto INT NOT NULL,
    cantidad INT NOT NULL CHECK (cantidad > 0),
    precioUnitario NUMERIC(10,2) NOT NULL,
    PRIMARY KEY (idFactura, idProducto),
    FOREIGN KEY (idFactura) REFERENCES Factura(idFactura),
    FOREIGN KEY (idProducto) REFERENCES Producto(idProducto)
);

-- TODO: Revisar cardinalidad de roles 