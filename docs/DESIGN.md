# Documento de Diseno de Software (SDD) — TiendaQ

**Version:** 3.0 | **Fecha:** Mayo 2026 | **Estado:** Aprobado

---

## 1. Arquitectura general (C4 Nivel 1 — Contexto)

```
┌──────────────────────────────────────────────────────────────┐
│                      SISTEMA TIENDAQ                         │
│                                                              │
│  ┌─────────────┐    ┌────────────────────────────────────┐   │
│  │   Cliente   │───▶│           Angular SPA              │   │
│  │  (browser)  │    │        localhost:4200              │   │
│  └─────────────┘    └────────────────┬───────────────────┘   │
│                                      │ HTTP/JSON             │
│  ┌─────────────┐    ┌────────────────▼───────────────────┐   │
│  │  Empleado   │───▶│         Spring Boot API            │   │
│  │  (browser)  │    │        localhost:8080              │   │
│  └─────────────┘    └─────┬──────────────────┬───────────┘   │
│                           │                  │               │
│              ┌────────────▼──┐    ┌──────────▼──────────┐    │
│              │  PostgreSQL   │    │   Wompi Sandbox      │    │
│              │  :5432        │    │   sandbox.wompi.co  │    │
│              └───────────────┘    └─────────────────────┘    │
│                                                              │
│  Sistema externo: Gmail SMTP (kforge.dev@gmail.com)          │
└──────────────────────────────────────────────────────────────┘
```

---

## 2. Arquitectura del backend (C4 Nivel 2 — Contenedores)

```
com.tiendaq/
├── identidad/
│   ├── domain/          # Usuario, Cliente, Empleado, puertos
│   ├── application/     # RegistroUseCase, LoginUseCase, LogoutUseCase
│   └── infrastructure/  # UsuarioJpaRepository, JwtTokenAdapter, GmailSmtpAdapter
│                          AuthController
├── catalogo/
│   ├── domain/          # Producto, Categoria, puertos
│   ├── application/     # CrearProductoUseCase, ActualizarProductoUseCase, etc.
│   └── infrastructure/  # ProductoJpaRepository, ProductoController
├── inventario/
│   ├── domain/          # StockLevel, StockEntry, StockReservation, puertos
│   ├── application/     # RegistrarStockUseCase, ReservarStockUseCase
│   └── infrastructure/  # StockJpaRepository, StockController, StockExpirationJob
├── carrito/
│   ├── domain/          # Carrito, ItemCarrito, puertos
│   ├── application/     # AgregarItemUseCase, ActualizarItemUseCase
│   └── infrastructure/  # CarritoJpaRepository, CarritoController, CarritoCleanupJob
├── pedidos/
│   ├── domain/          # Factura, DetalleFactura, puertos
│   ├── application/     # CheckoutUseCase, ConsultarFacturaUseCase
│   └── infrastructure/  # FacturaJpaRepository, PedidoController
├── pagos/
│   ├── domain/          # IntentoPago, PagoPort, puertos
│   ├── application/     # ProcesarPagoUseCase, ProcesarWebhookUseCase
│   └── infrastructure/  # WompiPagoAdapter, PagoController (webhook endpoint)
└── reportes/
    ├── application/     # ResumenVentasQuery, StockBajoQuery
    └── infrastructure/  # ReporteController, queries SQL nativas
```

**Regla de dependencias (hexagonal):**
- `domain` no conoce Spring ni JPA. Solo interfaces (puertos) y objetos de valor.
- `application` conoce solo `domain`. Orquesta casos de uso via puertos.
- `infrastructure` conoce `domain` + Spring/JPA. Implementa adaptadores.

---

## 3. Componentes clave (C4 Nivel 3)

### 3.1 Flujo de checkout

```
Cliente HTTP
    │
    ▼
PedidoController.checkout(CheckoutRequest)
    │ valida JWT rol CLIENTE
    ▼
CheckoutUseCase.ejecutar(clienteId, carritoId)
    ├── CarritoRepositoryPort.obtener(carritoId)       [validar ACTIVO]
    ├── StockRepositoryPort.verificarDisponible(items)  [cada item]
    ├── StockReservationPort.reservar(items, 10min)    [atomico]
    ├── FacturaRepositoryPort.crear(factura)            [con DetalleFactura]
    └── PagoPort.iniciarPago(intentoPago)              ──▶ WompiPagoAdapter
                                                              │
                                                              ▼
                                                       Wompi Sandbox API
```

### 3.2 Procesamiento de webhook

```
Wompi POST /api/pagos/webhook
    │
    ▼
PagoController.webhook(WebhookPayload)
    ├── verificar firma HMAC-SHA256 (WOMPI_EVENTS_SECRET)
    ├── verificar idempotencia: processed_webhook.contains(transaction_id)?
    │   └── Si existe: retornar 200 sin procesar
    └── ProcesarWebhookUseCase.ejecutar(payload)
          ├── actualizar IntentoPago.estado
          ├── Si APPROVED:
          │     ├── Factura.estado = PAGADA
          │     ├── StockReservationPort.consumir(reservas)
          │     └── NotificacionPort.enviarConfirmacion(cliente)
          └── Si DECLINED:
                ├── Factura.estado = CANCELADA
                └── StockReservationPort.liberar(reservas)
```

---

## 4. Diagrama de paquetes por bounded context

```
                    ┌─────────────────────────────────────┐
                    │           PUERTOS (interfaces)       │
                    │   (dentro de domain/)                │
                    │                                     │
                    │  ProductoRepositoryPort             │
                    │  PagoPort                           │
                    │  TokenPort                          │
                    │  NotificacionPort                   │
                    └──────────────┬──────────────────────┘
                                   │ implementados por
                    ┌──────────────▼──────────────────────┐
                    │        ADAPTADORES (infraestructura) │
                    │                                     │
                    │  ProductoJpaRepository              │
                    │  WompiPagoAdapter                   │
                    │  JwtTokenAdapter                    │
                    │  GmailSmtpAdapter                   │
                    └─────────────────────────────────────┘
```

---

## 5. Modelo de datos (resumen — ver DATABASE.md para detalle)

| Tabla | Bounded Context | Aggregate root |
|-------|----------------|----------------|
| `usuario` | Identidad | Si |
| `cliente` | Identidad | No (perfil) |
| `empleado` | Identidad | No (perfil) |
| `producto` | Catalogo | Si |
| `categoria` | Catalogo | Si |
| `stock_level` | Inventario | Si |
| `stock_entry` | Inventario | Si |
| `stock_reservation` | Inventario | Si |
| `carrito` | Carrito | Si |
| `item_carrito` | Carrito | No |
| `factura` | Pedidos | Si |
| `detalle_factura` | Pedidos | No |
| `intento_pago` | Pagos | Si |
| `processed_webhook` | Pagos | No (tabla soporte) |
| `revoked_token` | Identidad | No (tabla soporte) |
| `audit_log` | Transversal | No |

---

## 6. Maquinas de estado

### 6.1 Estado de Factura

```
CREADA ──────────────────────▶ PAGADA
  │
  │ (cancelacion pre-pago o webhook DECLINED)
  ▼
CANCELADA
```

### 6.2 Estado de IntentoPago

```
PENDIENTE ──▶ APROBADO
    │
    └────────▶ RECHAZADO
    │
    └────────▶ CANCELADO (timeout o cancelacion manual)
```

### 6.3 Estado de Carrito

```
ACTIVO ──▶ PROCESANDO ──▶ COMPLETADO
  │              │
  │              └────────▶ ACTIVO (si pago rechazado)
  │
  └────────▶ EXPIRADO (job 30 dias)
```

---

## 7. Seguridad — cadena de filtros Spring

```
Request HTTP
     │
     ▼
┌─────────────────────────────┐
│       CorsFilter            │  Valida Origin, preflight OPTIONS
└─────────────┬───────────────┘
              │
              ▼
┌─────────────────────────────┐
│      RateLimitFilter        │  Bucket4j: 5/15min login, 3/h registro, 10/h checkout
└─────────────┬───────────────┘
              │
              ▼
┌─────────────────────────────┐
│  JwtAuthenticationFilter   │  Lee Bearer token, verifica firma + expiracion
│                             │  Si valido: setea SecurityContext
└─────────────┬───────────────┘
              │
              ▼
┌─────────────────────────────┐
│  @ControllerAdvice          │  Captura excepciones, retorna RFC 7807 ProblemDetail
│  GlobalExceptionHandler     │
└─────────────┬───────────────┘
              │
              ▼
┌─────────────────────────────┐
│    @RestController          │  Logica de negocio via use cases
│    (por bounded context)    │
└─────────────────────────────┘
```

**Configuracion de filtros:**

```java
@Bean
SecurityFilterChain chain(HttpSecurity http) throws Exception {
    return http
        .csrf(AbstractHttpConfigurer::disable)
        .sessionManagement(s -> s.sessionCreationPolicy(STATELESS))
        .cors(c -> c.configurationSource(corsSource()))
        .addFilterBefore(rateLimitFilter, UsernamePasswordAuthenticationFilter.class)
        .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/api/auth/**", "/api/catalogo/productos", "/api/catalogo/categorias").permitAll()
            .requestMatchers("/api/admin/**", "/api/inventario/**", "/api/reportes/**").hasRole("EMPLEADO")
            .anyRequest().authenticated()
        )
        .build();
}
```

---

## 8. Frontend — arquitectura Angular

Ver [FRONTEND.md](FRONTEND.md) para detalle completo.

**Resumen:**
- Angular 21 standalone components. Sin NgModules.
- Estado global via Angular Signals (`signal()`, `computed()`, `effect()`).
- PrimeNG como biblioteca de componentes UI con tema K-Forge.
- Atomic Design: Atoms → Molecules → Organisms → Templates → Pages.
- Container/Presentational pattern: Pages = containers, Organisms/Molecules = presentationals.
- Interceptor HTTP para adjuntar Bearer token y manejar 401 (refresh automatico).

---

## 9. Decisiones de diseno — referencias a ADRs

| Decision | ADR |
|----------|-----|
| Hexagonal + DDD bounded contexts | [ADR-0001](adr/0001-arquitectura-hexagonal-ddd.md) |
| JWT access 15min + refresh 7d + rotacion | [ADR-0002](adr/0002-jwt-propio-con-refresh-rotation.md) |
| Wompi sandbox como pasarela | [ADR-0003](adr/0003-pasarela-pago-wompi-sandbox.md) |
| BigDecimal + NUMERIC(15,2) para dinero | [ADR-0004](adr/0004-money-bigdecimal-currency-cop.md) |
| Flyway en lugar de ddl-auto | [ADR-0005](adr/0005-flyway-vs-ddl-auto.md) |
| Cliente/Empleado como perfiles 1:1 | [ADR-0006](adr/0006-cliente-empleado-perfiles-no-herencia.md) |
| Soft-delete diferenciado por entidad | [ADR-0007](adr/0007-soft-delete-por-entidad.md) |
| RFC 7807 Problem Details para errores | [ADR-0008](adr/0008-error-envelope-rfc-7807-problem-details.md) |
| Optimistic locking con @Version | [ADR-0009](adr/0009-optimistic-locking-version-column.md) |
| Reservas de stock con timeout 10min | [ADR-0010](adr/0010-stock-reservation-checkout-timeout.md) |
| DetalleFactura como entidad del aggregate Factura | [ADR-0011](adr/0011-detallefactura-aggregate-de-factura.md) |
