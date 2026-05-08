# ADR-0009 — Optimistic locking con columna @Version

**Estado:** Aceptada
**Fecha:** Mayo 2026
**Autores:** Brian Vargas (PO)

---

## Contexto

El sistema tiene tres escenarios de concurrencia critica:

1. **Producto:** Dos empleados actualizan precio o stock al mismo tiempo. El segundo sobrescribe el cambio del primero sin saberlo (lost update).
2. **Carrito:** Usuario tiene la app abierta en dos pestanas. Agrega un item desde cada una. El segundo POST puede leer un estado obsoleto del carrito.
3. **StockLevel:** Dos checkouts simultaneos para el mismo producto pueden ambos ver `disponible=5` y ambos reservar, dejando el stock en negativo.

Pessimistic locking (`SELECT FOR UPDATE`) resuelve esto pero bloquea filas durante toda la transaccion, reduciendo throughput y arriesgando deadlocks.

---

## Decision

Usar **optimistic locking con `@Version`** en las entidades con concurrencia critica: `Producto`, `Carrito`, `StockLevel`.

**Implementacion:**

```java
@Entity
public class Producto {
    @Id @GeneratedValue
    private Long id;

    @Version
    private Integer version;

    private BigDecimal precio;
    // ...
}
```

Hibernate agrega automaticamente `AND version = ?` en cada `UPDATE` y lanza `OptimisticLockException` si la fila fue modificada por otra transaccion en el interim.

**Esquema SQL (Flyway):**

```sql
ALTER TABLE producto    ADD COLUMN version INTEGER NOT NULL DEFAULT 0;
ALTER TABLE carrito     ADD COLUMN version INTEGER NOT NULL DEFAULT 0;
ALTER TABLE stock_level ADD COLUMN version INTEGER NOT NULL DEFAULT 0;
```

**Manejo de conflicto:**

```java
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(OptimisticLockException.class)
    ProblemDetail handleConflict(OptimisticLockException ex, HttpServletRequest req) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(
            HttpStatus.CONFLICT,
            "El recurso fue modificado por otra operacion. Reintente."
        );
        pd.setType(URI.create("https://tiendaq.com/errors/conflict"));
        return pd;
    }
}
```

El cliente Angular captura `409 Conflict` y muestra un mensaje al usuario para que recargue y reintente.

---

## Consecuencias

**Positivas:**
- Sin bloqueos de fila: alta concurrencia sin deadlocks.
- Hibernate maneja el mecanismo automaticamente con `@Version`.
- Deteccion exacta: solo falla si hubo conflicto real, no de manera preventiva.
- Bajo overhead: solo un campo entero por tabla.

**Negativas:**
- El cliente debe manejar `409 Conflict` y propagar el `version` actual en requests PUT.
- En escenarios de alta contention (flash sale), muchos reintentos pueden degradar UX.
- Las entidades JPA deben incluir `version` en los DTOs de respuesta para que el cliente lo devuelva en actualizaciones.

---

## Alternativas consideradas

| Alternativa | Razon de rechazo |
|-------------|-----------------|
| Pessimistic locking (`SELECT FOR UPDATE`) | Bloqueos de larga duracion, riesgo de deadlock, throughput reducido |
| Sin locking (status quo) | Lost updates silenciosos en concurrencia; datos inconsistentes |
| `@Version` solo en `StockLevel` | Carrito y Producto tambien tienen escenarios de lost update documentados |
| Serializable isolation level | Nivel mas restrictivo de PostgreSQL; overhead alto; abortos frecuentes |
