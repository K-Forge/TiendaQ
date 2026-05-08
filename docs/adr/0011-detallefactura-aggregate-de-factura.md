# ADR-0011 — DetalleFactura como entidad dentro del aggregate Factura

**Estado:** Aceptada
**Fecha:** Mayo 2026
**Autores:** Brian Vargas (PO)

---

## Contexto

`DetalleFactura` (los items de una factura: producto, cantidad, precio unitario, subtotal) existe en el esquema SQL pero no tiene entidad JPA ni controlador REST en el codigo actual. Se debio decidir si:

1. Crear un controlador `DetalleFacturaController` con endpoints CRUD propios.
2. Tratar `DetalleFactura` como parte del aggregate `Factura`, sin endpoint propio.

En DDD, un **aggregate** es un cluster de entidades y value objects tratados como una unidad de consistencia. La **aggregate root** controla el acceso a sus miembros. Los miembros del aggregate no se acceden directamente desde fuera del aggregate.

---

## Decision

`DetalleFactura` es una **entidad dentro del aggregate `Factura`**. `Factura` es la aggregate root.

**Consecuencias del modelo:**
- No existe `DetalleFacturaController` ni endpoints `/api/detalles`.
- `DetalleFactura` se crea junto con la `Factura` en un solo request `POST /api/pedidos/checkout`.
- `DetalleFactura` no se puede crear, actualizar ni eliminar de forma independiente.
- El repositorio `FacturaRepository` carga la `Factura` con sus `DetalleFactura` via `@OneToMany(cascade = ALL, orphanRemoval = true)`.

**Implementacion:**

```java
@Entity
public class Factura {
    @Id @GeneratedValue
    private Long id;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "factura_id")
    private List<DetalleFactura> detalles = new ArrayList<>();

    public void agregarDetalle(Producto producto, int cantidad, BigDecimal precioUnitario) {
        DetalleFactura detalle = new DetalleFactura(producto, cantidad, precioUnitario);
        this.detalles.add(detalle);
        this.total = this.total.add(detalle.getSubtotal());
    }
}

@Entity
public class DetalleFactura {
    @Id @GeneratedValue
    private Long id;

    // Sin ManyToOne a Factura aqui: la FK factura_id se gestiona por el @JoinColumn en Factura
    private Long productoId;        // ID desnormalizado para referencia historica
    private String nombreProducto;  // Snapshot del nombre al momento de la compra
    private BigDecimal precioUnitario;
    private Integer cantidad;
    private BigDecimal subtotal;
}
```

Nota: `nombreProducto` se captura como snapshot porque el nombre del producto puede cambiar despues de la compra. La factura debe reflejar los datos al momento de la transaccion.

---

## Consecuencias

**Positivas:**
- Consistencia garantizada: no puede existir un `DetalleFactura` sin su `Factura`.
- La factura siempre tiene todos sus items en una sola operacion atomica.
- Modelo de dominio correcto segun DDD: `Factura` es la unidad de consistencia.
- Menos endpoints: API mas simple, menos superficie de error.
- Snapshot de precio: la factura es inmutable e historicamente correcta aunque el producto cambie.

**Negativas:**
- No es posible agregar items a una factura despues de creada (correcto por diseno: facturas cerradas).
- Si se necesita una factura con 100 items, el request inicial debe enviarlos todos.
- Consultar los detalles de una factura requiere cargar la factura completa.

---

## Alternativas consideradas

| Alternativa | Razon de rechazo |
|-------------|-----------------|
| `DetalleFacturaController` con CRUD propio | Permite estados inconsistentes (detalle sin factura, factura con totales incorrectos) |
| `DetalleFactura` como Value Object (sin ID) | PostgreSQL necesita PK para la tabla; JPA requiere ID para persistencia |
| Factura como JSON blob en columna | No relacional; dificil de consultar con SQL |
| Factura + Detalles como eventos (event sourcing) | Complejidad excesiva para MVP academico |
