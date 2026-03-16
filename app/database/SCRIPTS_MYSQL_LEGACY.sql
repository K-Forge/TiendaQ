-- ============================================================
-- SCRIPT LEGACY — MySQL (solo referencia historica)
-- El proyecto actualmente usa PostgreSQL.
-- Ver SCRIPTS_POSTGRES.sql para el esquema actual.
-- ============================================================

--- Ingresar mysql
--- mysql -h localhost -u root -p

--- Creación de BD
--- CREATE DATABASE tiendaq DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

--- Usar DB
USE tiendaq;
---Show Tables;

--- Tabla Usuario
CREATE TABLE IF NOT EXISTS Usuario (
	idUsuario INT AUTO_INCREMENT PRIMARY KEY NOT NULL,
	nombre VARCHAR(20) NOT NULL,
	apellido VARCHAR(20) NOT NULL,
    tipoDocumento ENUM('CC','TI', 'CE', 'PASAPORTE') NOT NULL,
    documento INT NOT NULL UNIQUE,
	telefono VARCHAR(20) NOT NULL UNIQUE,
	correo VARCHAR(70) NOT NULL UNIQUE,
	direccion VARCHAR(20) NOT NULL,
	contrasena VARCHAR(20) NOT NULL,
	tipoUsuario ENUM('REGISTRADO','SIN_REGISTRAR') NOT NULL
);
--- Describe Usuario;

--- Tabla Empleado
CREATE TABLE IF NOT EXISTS Empleado (
    idEmpleado INT AUTO_INCREMENT PRIMARY KEY NOT NULL,	
	tipoEmpleado ENUM('ADMINISTRADOR','VENDEDOR') NOT NULL,
	idUsuario INT NOT NULL,
    FOREIGN KEY (idUsuario) REFERENCES Usuario(idUsuario)
);
--- Describe Empleado;

--- Tabla Cliente
CREATE TABLE IF NOT EXISTS Cliente (
    idCliente INT AUTO_INCREMENT PRIMARY KEY NOT NULL,
    idUsuario INT NOT NULL,
    FOREIGN KEY (idUsuario) REFERENCES Usuario(idUsuario)
);
--- Describe Cliente;

--- Tabla Producto
CREATE TABLE IF NOT EXISTS Producto (
	idProducto INT AUTO_INCREMENT PRIMARY KEY NOT NULL,
    categoria ENUM('ROPA','ACCESORIOS', 'LIBRERIA', 'PAPELERIA') NOT NULL,
    nombreProducto VARCHAR(50) NOT NULL,
    precioUnitario FLOAT NOT NULL   
);
--- Describe Producto;

--- Tabla Stock
CREATE TABLE IF NOT EXISTS Stock (
    idStock INT AUTO_INCREMENT PRIMARY KEY NOT NULL,
    idProducto INT NOT NULL,
    fechaIngreso DATE NOT NULL,
    stock INT NOT NULL,
    FOREIGN KEY (idProducto) REFERENCES Producto(idProducto)
);
--- Describe Stock;

--- Tabla CarritoCompra
CREATE TABLE IF NOT EXISTS CarritoCompra (
    idCarritoCompra INT AUTO_INCREMENT PRIMARY KEY NOT NULL,
    fechaCreacion DATE NOT NULL,
    estado ENUM('VACIO','CON_PRODUCTOS', 'EN_PROCESO_DE_PAGO', 'PAGO_PENDIENTE', 'PAGO_EXITOSO') NOT NULL,
    idUsuario INT NOT NULL,
    FOREIGN KEY (idUsuario) REFERENCES Usuario(idUsuario)
);
--- Describe CarritoCompra;

--- Tabla ItemCarrito
CREATE TABLE IF NOT EXISTS ItemCarrito (
    idItemCarrito INT AUTO_INCREMENT PRIMARY KEY NOT NULL,
    cantidad INT NOT NULL,
    precioUnitario FLOAT NOT NULL,
    idCarritoCompra INT NOT NULL,
    idProducto INT NOT NULL,
    FOREIGN KEY (idCarritoCompra) REFERENCES CarritoCompra(idCarritoCompra),
    FOREIGN KEY (idProducto) REFERENCES Producto(idProducto)
);
--- Describe ItemCarrito

--- Tabla Factura
CREATE TABLE IF NOT EXISTS Factura (
    idFactura INT AUTO_INCREMENT PRIMARY KEY NOT NULL,
    fechaCompra DATE NOT NULL,
    totalCompra FLOAT NOT NULL,
    metodoPago ENUM('PSE', 'TARJETA_CREDITO', 'TARJETA_DEBITO', 'EFECTIVO', 'TRANSFERENCIA') NOT NULL,
    iva FLOAT NOT NULL,
    idEmpleado INT NOT NULL,
    idCliente INT NOT NULL,
    idCarritoCompra INT NOT NULL,
    FOREIGN KEY (idEmpleado) REFERENCES Empleado(idEmpleado),
    FOREIGN KEY (idCliente) REFERENCES Cliente(idCliente),
    FOREIGN KEY (idCarritoCompra) REFERENCES CarritoCompra(idCarritoCompra)
);
--- Describe Factura;



