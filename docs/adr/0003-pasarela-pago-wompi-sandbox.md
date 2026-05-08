# ADR-0003 — Pasarela de pago Wompi sandbox

**Estado:** Aceptada
**Fecha:** Mayo 2026
**Autores:** Brian Vargas (PO)

---

## Contexto

TiendaQ necesita procesar pagos electronicos en pesos colombianos (COP). Requisitos del MVP:

- Procesar pagos con tarjeta credito/debito en COP.
- Sin necesidad de cuenta bancaria comercial registrada (alcance academico).
- API REST disponible con documentacion en espanol.
- Sandbox gratuito para pruebas.
- Webhook para confirmacion asincrona de pagos.

El equipo es estudiantes en Colombia, Bogota, sin acceso a cuentas Stripe o PayPal colombianas verificadas.

---

## Decision

Usar **Wompi sandbox** como unica pasarela de pago para el MVP.

Integracion:

```
POST https://sandbox.wompi.co/v1/transactions
Headers:
  Authorization: Bearer {public_key}
Body:
  amount_in_cents, currency, customer_email, payment_method, reference
```

**Flujo de pago:**

1. Backend crea `IntentoPago` con estado `PENDIENTE`.
2. Backend llama Wompi API para crear transaccion.
3. Wompi retorna `transaction_id` + estado inicial.
4. Wompi envia webhook `POST /api/pagos/webhook` con resultado final.
5. Backend verifica firma del webhook con `WOMPI_EVENTS_SECRET`.
6. Si `status = APPROVED`, backend confirma `IntentoPago` y crea `Factura`.

**Idempotencia de webhook:**

```sql
CREATE TABLE processed_webhook (
    transaction_id VARCHAR(100) PRIMARY KEY,
    processed_at   TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
```

Antes de procesar cualquier webhook, verificar que `transaction_id` no exista en esta tabla. Si existe, retornar 200 sin reprocesar.

**Variables de entorno requeridas:**

```
WOMPI_PUBLIC_KEY=pub_test_xxx
WOMPI_PRIVATE_KEY=prv_test_xxx
WOMPI_EVENTS_SECRET=test_events_xxx
```

---

## Consecuencias

**Positivas:**
- Cero costo para ambiente sandbox.
- Documentacion en espanol, orientada al mercado colombiano.
- Webhook nativo permite flujo asincrono real.
- Tarjetas de prueba disponibles para todos los estados (APPROVED, DECLINED, etc.).

**Negativas:**
- Wompi sandbox puede tener latencia o downtime sin SLA.
- Migracion a produccion requiere cuenta comercial colombiana verificada (RUT, camara de comercio).
- API cambia entre versiones; el cliente esta acoplado a la API v1 actual.

**Deuda tecnica aceptada:** El adaptador `WompiPagoAdapter` implementa `PagoPort` del dominio. Cambiar a MercadoPago en el futuro solo requiere reemplazar el adaptador, no tocar el dominio.

---

## Alternativas consideradas

| Alternativa | Razon de rechazo |
|-------------|-----------------|
| Stripe | Requiere cuenta bancaria US o Colombia verificada; documentacion principalmente en ingles |
| PayU | API mas compleja; sin sandbox gratuito abierto |
| MercadoPago | Viable, pero el equipo no tiene familiaridad; Wompi es mas simple para COP |
| Pago simulado (mock) | No cumple el objetivo academico de integracion real con pasarela |
