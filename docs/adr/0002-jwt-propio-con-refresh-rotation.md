# ADR-0002 — JWT propio con access 15min + refresh 7d + rotacion

**Estado:** Aceptada
**Fecha:** Mayo 2026
**Autores:** Brian Vargas (PO)

---

## Contexto

El sistema requiere autenticacion stateless para la API REST. Se evaluaron tres estrategias:

- Session HTTP (stateful): incompatible con SPA Angular y deploy en Render free tier (sin sticky sessions).
- OAuth2 social login (Google/GitHub): fuera de alcance academico, requiere app registrada y dominio verificado.
- JWT propio: conocido por el equipo, stateless, compatible con Angular HttpClient + interceptor.

El riesgo principal con JWT clasico (access token de larga duracion) es que un token robado es valido hasta expiracion. La solucion es mantener access tokens cortos y rotar refresh tokens en cada uso.

---

## Decision

Implementar **JWT propio** con las siguientes caracteristicas:

| Parametro | Valor |
|-----------|-------|
| Access token TTL | 15 minutos |
| Refresh token TTL | 7 dias |
| Algoritmo firma | HS256 (secreto en env var `JWT_SECRET`) |
| Rotacion | Cada uso de refresh genera nuevo refresh token; el anterior queda invalido |
| Logout | Insertar `jti` del refresh token en `revoked_token` |
| Limpieza | Job `@Scheduled` diario elimina filas con `expires_at < NOW()` |

Tabla de soporte:

```sql
CREATE TABLE revoked_token (
    jti       UUID PRIMARY KEY,
    user_id   BIGINT NOT NULL REFERENCES usuario(id),
    expires_at TIMESTAMPTZ NOT NULL
);

CREATE INDEX idx_revoked_token_expires ON revoked_token(expires_at);
```

Flujo de refresh:

1. Cliente envia refresh token en `POST /api/auth/refresh`.
2. API verifica firma y que `jti` no este en `revoked_token`.
3. API inserta `jti` anterior en `revoked_token`.
4. API emite nuevo par access + refresh.
5. Si el mismo refresh token llega dos veces (replay), el segundo intento falla → sesion comprometida detectada.

---

## Consecuencias

**Positivas:**
- Access token corto reduce ventana de exposicion ante robo.
- Rotacion de refresh permite deteccion de replay attacks.
- Implementacion 100% en Spring Security + `io.jsonwebtoken:jjwt`, sin dependencias externas de identidad.
- Logout real sin estado servidor (solo blacklist minima de `jti`s).

**Negativas:**
- Tabla `revoked_token` crece con cada logout; requiere job de limpieza.
- Si el cliente pierde el refresh token (cierre inesperado de app), debe re-autenticarse.
- HS256 con secreto compartido: si el secreto se filtra, todos los tokens son vulnerables. Mitigacion: secreto en env var, rotacion semestral recomendada.

---

## Alternativas consideradas

| Alternativa | Razon de rechazo |
|-------------|-----------------|
| Access token 24h sin refresh | Ventana de exposicion muy grande ante robo de token |
| OAuth2 con Keycloak | Complejidad operacional excesiva para deploy academico en free tier |
| Session HTTP | Incompatible con SPA stateless y Render free tier |
| JWT con blacklist completa (todos los tokens) | Overhead de DB en cada request; anula beneficio stateless |
