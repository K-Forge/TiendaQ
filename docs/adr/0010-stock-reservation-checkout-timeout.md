# ADR-0010 — Reservas de stock con timeout 10min

**Estado:** Aceptada
**Fecha:** Mayo 2026
**Autores:** Brian Vargas (PO)

---

## Contexto

El flujo de checkout tiene una ventana de tiempo entre "el usuario inicia el pago" y "Wompi confirma el pago". Durante esta ventana, el producto debe estar reservado para ese usuario, pero sin decrement permanente del stock hasta confirmacion real.

Sin reservas, dos usuarios pueden iniciar checkout del ultimo item disponible al mismo tiempo, ambos completan el pago y el sistema vende un item que no tiene.

Con reservas permanentes sin timeout, un usuario que abandona el checkout (cierra el navegador) bloquea el stock indefinidamente.

---

## Decision

Implementar **reservas de stock con timeout de 10 minutos** usando tabla dedicada + job de expiracion.

**Tabla:**

```sql
CREATE TABLE stock_reservation (
    id          BIGSERIAL PRIMARY KEY,
    producto_id BIGINT      NOT NULL REFERENCES producto(id),
    carrito_id  BIGINT      NOT NULL REFERENCES carrito(id),
    cantidad    INTEGER     NOT NULL CHECK (cantidad > 0),
    expires_at  TIMESTAMPTZ NOT NULL DEFAULT (NOW() + INTERVAL '10 minutes'),
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_stock_reservation_expires ON stock_reservation(expires_at);
CREATE INDEX idx_stock_reservation_producto ON stock_reservation(producto_id);
```

**Flujo de checkout:**

1. Usuario inicia checkout → `POST /api/pedidos/checkout`.
2. Use case verifica `StockLevel.disponible >= cantidad_solicitada`.
3. Use case crea `StockReservation` con `expires_at = NOW() + 10min`.
4. Use case decrementa `StockLevel.disponible` temporalmente.
5. Use case inicia `IntentoPago` con Wompi.
6. Wompi confirma via webhook → Use case consume la reserva (la elimina) y decrementa `StockLevel.real`.
7. Si Wompi rechaza → Use case libera la reserva y restaura `StockLevel.disponible`.

**Calculo de disponible:**

```sql
-- Disponible real para venta:
SELECT sl.cantidad - COALESCE(SUM(sr.cantidad), 0) AS disponible_real
FROM stock_level sl
LEFT JOIN stock_reservation sr
  ON sr.producto_id = sl.producto_id
  AND sr.expires_at > NOW()
WHERE sl.producto_id = ?
GROUP BY sl.cantidad;
```

**Job de expiracion:**

```java
@Scheduled(fixedRate = 60_000)  // cada 60 segundos
@Transactional
public void liberarReservasExpiradas() {
    List<StockReservation> expiradas = reservationRepo
        .findByExpiresAtBefore(Instant.now());

    expiradas.forEach(r -> {
        stockLevelRepo.incrementarDisponible(r.getProductoId(), r.getCantidad());
        reservationRepo.delete(r);
    });
}
```

---

## Consecuencias

**Positivas:**
- Elimina overselling: el stock no se vende dos veces.
- Timeout automatico: carritos abandonados no bloquean stock indefinidamente.
- Transaccional: la reserva y el decrement de disponible son atomicos.
- Visible para usuarios: se puede mostrar "quedan X unidades" usando el disponible real.

**Negativas:**
- Complejidad adicional: tabla extra, job de limpieza, dos campos de stock (`cantidad`, `disponible`).
- Si el job falla silenciosamente, reservas expiradas no se liberan hasta el siguiente ciclo.
- Timeout de 10min puede ser insuficiente si el proceso de pago es lento (red movil lenta).
- Necesita indices en `expires_at` para que el job sea eficiente a escala.

---

## Alternativas consideradas

| Alternativa | Razon de rechazo |
|-------------|-----------------|
| Sin reservas (decrement al confirmar) | Overselling posible en concurrencia |
| Decrement inmediato permanente (revert si falla) | Compensation transactions complejas; estado inconsistente si el revert falla |
| Queue/mensajeria (Kafka, RabbitMQ) | Infraestructura excesiva para free tier academico |
| Redis SETNX para reservas | Dependencia adicional (Redis); complejidad operacional en Render free tier |
| Timeout de 30min | Bloquea stock demasiado tiempo para productos de alta demanda |
