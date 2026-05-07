# ADR-0008 — RFC 7807 Problem Details para errores API

**Estado:** Aceptada
**Fecha:** Mayo 2026
**Autores:** Brian Vargas (PO)

---

## Contexto

El sistema actual retorna errores en formatos inconsistentes:

- Algunos endpoints retornan `{ "error": "mensaje" }`.
- Otros retornan el stack trace completo de Spring por defecto.
- Validaciones de `@Valid` generan `{ "errors": [...] }` de estructura diferente.
- El frontend Angular debe manejar multiples formatos de error, lo que complica el interceptor HTTP.

Un contrato de error uniforme reduce el codigo de manejo en el cliente y mejora la experiencia de debugging.

---

## Decision

Adoptar **RFC 7807 Problem Details** (`application/problem+json`) como unico formato de respuesta de error.

**Estructura base:**

```json
{
  "type": "https://tiendaq.com/errors/producto-no-encontrado",
  "title": "Producto no encontrado",
  "status": 404,
  "detail": "No existe producto con id 99",
  "instance": "/api/productos/99"
}
```

**Extension para errores de validacion:**

```json
{
  "type": "https://tiendaq.com/errors/validacion",
  "title": "Error de validacion",
  "status": 400,
  "detail": "Uno o mas campos son invalidos",
  "instance": "/api/productos",
  "violations": [
    { "field": "nombre", "message": "no puede estar vacio" },
    { "field": "precio", "message": "debe ser mayor que 0" }
  ]
}
```

**Implementacion con Spring 6 / Boot 4:**

Spring Boot 4 incluye soporte nativo para `ProblemDetail` (RFC 7807) via `ResponseEntityExceptionHandler`. Configuracion:

```java
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(RecursoNoEncontradoException.class)
    ProblemDetail handleNotFound(RecursoNoEncontradoException ex, HttpServletRequest req) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        pd.setType(URI.create("https://tiendaq.com/errors/recurso-no-encontrado"));
        pd.setInstance(URI.create(req.getRequestURI()));
        return pd;
    }
}
```

```yaml
# application.yml
spring:
  mvc:
    problemdetails:
      enabled: true   # Activa Problem Details para excepciones Spring MVC nativas
```

**Catalogo de tipos de error:**

| Tipo URI | Status | Cuando |
|----------|--------|--------|
| `.../recurso-no-encontrado` | 404 | Entidad no existe |
| `.../validacion` | 400 | @Valid falla |
| `.../acceso-denegado` | 403 | Sin permiso |
| `.../no-autenticado` | 401 | Token ausente o invalido |
| `.../conflict` | 409 | Optimistic lock, email duplicado |
| `.../stock-insuficiente` | 422 | Sin inventario disponible |
| `.../pago-rechazado` | 422 | Wompi retorna DECLINED |

---

## Consecuencias

**Positivas:**
- Formato estandar (RFC); compatible con cualquier cliente HTTP.
- Angular HttpClient interceptor maneja un solo formato.
- `type` URI es machine-readable; permite logging estructurado por tipo de error.
- Spring Boot 4 tiene soporte nativo: menos codigo boilerplate.

**Negativas:**
- Los URIs de tipo deben resolverse (aunque solo retornen documentacion en HTML).
- `violations` es una extension no estandar; clientes no-Java pueden no esperarla.

---

## Alternativas consideradas

| Alternativa | Razon de rechazo |
|-------------|-----------------|
| `{ "error": "mensaje" }` custom | No estandar; cada endpoint puede variar el formato |
| Spring default `DefaultErrorAttributes` | Incluye stack trace en dev, informacion inconsistente |
| GraphQL errors | No aplica; la API es REST |
| JSON:API errors | Mas verboso que RFC 7807; menor adopcion en ecosistema Spring |
