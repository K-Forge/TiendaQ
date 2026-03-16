-- ====================
-- SCRIPT DE ELIMINACIÓN DE DATOS - PostgreSQL
-- Base de datos: tiendaq
-- Elimina todos los datos de las tablas para permitir una reinserción limpia.
-- ====================

-- Limpieza de tablas usando TRUNCATE con CASCADE
-- CASCADE maneja automáticamente las dependencias entre tablas

TRUNCATE TABLE
    DetalleFactura,
    ItemCarrito,
    Factura,
    CarritoCompra,
    Stock,
    Producto,
    Empleado,
    Cliente,
    Usuario
RESTART IDENTITY CASCADE;

-- Mensaje de confirmación (opcional)
DO $$
BEGIN
    RAISE NOTICE 'Datos de TiendaQ eliminados correctamente.';
END $$;
