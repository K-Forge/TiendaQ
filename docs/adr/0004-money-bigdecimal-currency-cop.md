# ADR-0004 — BigDecimal + NUMERIC(15,2) para dinero en COP

**Estado:** Aceptada
**Fecha:** Mayo 2026
**Autores:** Brian Vargas (PO)

---

## Contexto

El sistema actual usa `double` para valores monetarios (precios, totales de factura, montos de pago). Esto causa:

- Errores de redondeo en aritmetica de punto flotante. Ejemplo: `0.1 + 0.2 = 0.30000000000000004` en Java.
- PostgreSQL con columnas `FLOAT8` / `DOUBLE PRECISION` tienen el mismo problema.
- IVA 19%: calcular `precio * 1.19` con `double` introduce errores que se acumulan en facturas con multiples items.
- COP no tiene centavos significativos en transacciones reales, pero la API Wompi trabaja en centavos (`amount_in_cents`), requiriendo conversion exacta.

---

## Decision

Usar `BigDecimal` en Java y `NUMERIC(15,2)` en PostgreSQL para todos los campos monetarios. Agregar columna `currency CHAR(3) DEFAULT 'COP'` en tablas con valores monetarios.

**Reglas de implementacion:**

```java
// Correcto: usar String en constructor para evitar perdida de precision
BigDecimal precio = new BigDecimal("19990.00");

// Incorrecto: double en constructor tiene el problema de precision
BigDecimal malo = new BigDecimal(19.99); // NO hacer esto

// Redondeo: siempre HALF_UP con 2 decimales
BigDecimal total = subtotal.multiply(new BigDecimal("1.19"))
    .setScale(2, RoundingMode.HALF_UP);

// Conversion a centavos para Wompi
long centavos = total.multiply(BigDecimal.valueOf(100))
    .setScale(0, RoundingMode.HALF_UP)
    .longValueExact();
```

**Esquema PostgreSQL:**

```sql
-- En todas las tablas con valores monetarios:
precio        NUMERIC(15,2) NOT NULL,
currency      CHAR(3)       NOT NULL DEFAULT 'COP'

-- Ejemplo en producto:
ALTER TABLE producto
  ALTER COLUMN precio TYPE NUMERIC(15,2),
  ADD COLUMN currency CHAR(3) NOT NULL DEFAULT 'COP';
```

**Mapeo JPA:**

```java
@Column(precision = 15, scale = 2, nullable = false)
private BigDecimal precio;

@Column(length = 3, nullable = false)
private String currency = "COP";
```

---

## Consecuencias

**Positivas:**
- Aritmetica exacta. Sin errores de redondeo en calculos de IVA y totales.
- `NUMERIC(15,2)` almacena exactamente lo que se ve; sin sorpresas en reportes.
- Columna `currency` permite futura extension a USD u otras divisas sin cambio de esquema.
- Conversion a centavos para Wompi es determinista.

**Negativas:**
- `BigDecimal` es mas verboso que `double`. Operaciones requieren metodos explicitos de redondeo.
- Migracion Flyway necesaria para convertir columnas existentes de `DOUBLE PRECISION` a `NUMERIC(15,2)`.
- Serialization JSON: configurar `ObjectMapper` para no usar notacion cientifica.

```java
// En configuracion Spring:
objectMapper.enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);
objectMapper.disable(SerializationFeature.WRITE_BIGDECIMAL_AS_PLAIN);
```

---

## Alternativas consideradas

| Alternativa | Razon de rechazo |
|-------------|-----------------|
| `double` / `DOUBLE PRECISION` | Errores de redondeo inaceptables para valores monetarios |
| Entero en centavos (`long`) | Wompi usa centavos internamente, pero el dominio y UI trabajan con pesos; doble conversion introduce complejidad |
| `javax.money.MonetaryAmount` (JSR 354) | Dependencia adicional, sin soporte nativo en Spring Data JPA, complejidad excesiva para alcance del proyecto |
