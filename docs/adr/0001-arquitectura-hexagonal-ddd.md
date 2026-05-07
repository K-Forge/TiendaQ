# ADR-0001 — Arquitectura Hexagonal + DDD por bounded context

**Estado:** Aceptada
**Fecha:** Mayo 2026
**Autores:** Brian Vargas (PO)

---

## Contexto

El proyecto comenzo con una arquitectura en capas clasica (Controller → Service → Repository → Model). A medida que crecio el alcance (7 bounded contexts, integracion con Wompi, notificaciones, auditoria), quedaron claros los problemas:

- La logica de negocio estaba mezclada con Spring (anotaciones JPA y Spring en entidades de dominio).
- Los tests necesitaban levantar el contexto de Spring completo, haciendo el ciclo lento.
- Agregar un contexto nuevo (ej. Pagos) requeria tocar clases de otros contextos (ej. Factura).
- La integracion con Wompi (API externa) no tenia un punto de aislamiento claro.

---

## Decision

Adoptar **Arquitectura Hexagonal (Ports & Adapters) organizados por Bounded Context de DDD**.

Cada bounded context tiene su propia estructura de paquetes:

```
com.tiendaq.<contexto>/
├── domain/          # Entidades, Value Objects, Eventos de dominio, Puertos (interfaces)
├── application/     # Use cases (servicios de aplicacion)
└── infrastructure/  # Adaptadores: REST controllers, JPA repositories, clientes externos
```

Los 7 bounded contexts son:

| Contexto | Aggregate root principal |
|----------|--------------------------|
| identidad | Usuario |
| catalogo | Producto |
| inventario | StockEntry, StockLevel |
| carrito | Carrito |
| pedidos | Factura (con DetalleFactura) |
| pagos | IntentoPago |
| reportes | (read-model, sin aggregate propio) |

**Regla de dependencias:** domain no conoce Spring ni JPA. application solo conoce domain. infrastructure conoce domain y Spring/JPA.

---

## Consecuencias

**Positivas:**
- Los tests de domain y application corren sin Spring (`@SpringBootTest` solo para slices de infraestructura).
- Cada bounded context puede evolucionar sin tocar los demas.
- Agregar o reemplazar un adaptador externo (ej. cambiar Wompi por MercadoPago) no toca el dominio.

**Negativas:**
- Mayor cantidad de clases y paquetes que la arquitectura en capas clasica.
- Curva de aprendizaje para estudiantes acostumbrados a MVC de Spring.
- El refactor desde la arquitectura en capas actual requiere un sprint dedicado (US-068).

**Deuda tecnica aceptada:** El codigo actual no sigue esta estructura. El refactor se planifica como US-068 en el Sprint 1.

---

## Alternativas consideradas

| Alternativa | Razon de rechazo |
|-------------|-----------------|
| Capas clasicas (Controller/Service/Repository) | No aisle el dominio de Spring. Tests lentos. Acoplamiento entre contextos. |
| Microservicios | Excesivo para un equipo de 4 personas y alcance academico. Sin infraestructura de Kubernetes. |
| Modulos de Java (JPMS) | Complejidad adicional sin beneficio proporcional para el tamano del proyecto. |
