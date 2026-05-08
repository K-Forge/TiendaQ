# ADR-0007 — Soft-delete diferenciado por entidad

**Estado:** Aceptada
**Fecha:** Mayo 2026
**Autores:** Brian Vargas (PO)

---

## Contexto

El sistema necesita eliminar registros sin perder integridad referencial ni historial de transacciones:

- Un `Usuario` eliminado tiene facturas, carritos e intentos de pago asociados.
- Un `Producto` descontinuado puede seguir apareciendo en facturas historicas.
- Un `Carrito` abandonado puede eliminarse fisicamente despues de cierto tiempo (no tiene valor historico).

Una politica uniforme de hard-delete o soft-delete para todas las entidades no es correcta: algunas necesitan auditoria perpetua, otras pueden eliminarse fisicamente.

---

## Decision

Aplicar **soft-delete diferenciado por entidad** segun su rol en el sistema:

| Entidad | Estrategia | Razon |
|---------|-----------|-------|
| `Usuario` | Soft (`deleted_at`) | Preservar historial de compras y facturas |
| `Producto` | Soft (`deleted_at`) | Preservar referencia en facturas historicas |
| `Carrito` | Hard despues de 30 dias inactivo | Sin valor historico; limpieza por job |
| `ItemCarrito` | Hard (cascada con `Carrito`) | Depende del carrito |
| `StockEntry` | Soft | Auditoria de movimientos de inventario |
| `Factura` | Nunca eliminar | Documento fiscal; solo cancelar con estado |
| `IntentoPago` | Nunca eliminar | Auditoria de transacciones |

**Implementacion Hibernate para soft-delete:**

```java
@Entity
@SQLDelete(sql = "UPDATE usuario SET deleted_at = NOW() WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class Usuario {
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
    // ...
}
```

**Job de limpieza de carritos:**

```java
@Scheduled(cron = "0 0 2 * * *")  // 2am diario
public void limpiarCarritosAbandonados() {
    carritoRepository.deleteByUpdatedAtBefore(
        LocalDateTime.now().minusDays(30)
    );
}
```

**Indices para filtrado eficiente:**

```sql
-- Filtrar solo registros activos es el caso comun; parcial index excluye soft-deleted
CREATE INDEX idx_usuario_activo ON usuario(email) WHERE deleted_at IS NULL;
CREATE INDEX idx_producto_activo ON producto(nombre) WHERE deleted_at IS NULL;
```

---

## Consecuencias

**Positivas:**
- Integridad referencial preservada: facturas apuntan a usuarios/productos aunque esten "eliminados".
- Auditoria implicita: `deleted_at` registra cuando ocurrio la eliminacion.
- `@Where` de Hibernate filtra automaticamente sin cambiar queries existentes.
- Limpieza fisica de carritos mantiene la tabla liviana.

**Negativas:**
- `@Where` de Hibernate es un filtro global: si se necesita ver registros eliminados (panel admin), requiere una query nativa o repositorio separado.
- El campo `deleted_at` debe incluirse en indices parciales para que el filtro sea eficiente.
- Job de carritos necesita monitoreo: si falla silenciosamente, la tabla crece.

---

## Alternativas consideradas

| Alternativa | Razon de rechazo |
|-------------|-----------------|
| Hard-delete uniforme | Rompe integridad referencial en facturas; pierde historial |
| Soft-delete uniforme (todas las entidades) | Carritos y items de carrito acumulan basura sin valor historico |
| Tabla de auditoria separada para todo | Complejidad alta; auditoria real se cubre con `audit_log`, no con copias de entidades |
| Archivado a tabla historica | Over-engineering para escala academica |
