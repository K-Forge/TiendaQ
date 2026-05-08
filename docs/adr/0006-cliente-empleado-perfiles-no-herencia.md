# ADR-0006 — Cliente/Empleado como perfiles 1:1 (sin herencia JPA)

**Estado:** Aceptada
**Fecha:** Mayo 2026
**Autores:** Brian Vargas (PO)

---

## Contexto

El modelo tiene tres tipos de personas en el sistema: usuarios basicos, clientes (que compran) y empleados (que administran). El diseno inicial uso `@Inheritance(JOINED)` con `Usuario` como superclase y `Cliente`/`Empleado` como subclases.

Problemas identificados:

- `@Inheritance(JOINED)` genera JOINs implicitos en cada query a `Usuario`, incluso cuando no se necesita la informacion de cliente o empleado.
- Hibernate genera columnas `DTYPE` o tablas `usuario_id` con semantica confusa.
- Un `Usuario` no puede ser simultaneamente `Cliente` y `Empleado` (uso academico: profesor que tambien compra en la tienda).
- Tests de integracion fallan con `@SpringBootTest` por la forma en que Hibernate carga la jerarquia.
- El SQL schema actual en `SCRIPTS_POSTGRES.sql` ya modela `cliente` y `empleado` como tablas separadas con FK `id_usuario`, no como herencia.

---

## Decision

Modelar `Cliente` y `Empleado` como **perfiles 1:1** de `Usuario`. Cada uno es una entidad JPA separada con FK `id_usuario UNIQUE`.

**Esquema:**

```sql
CREATE TABLE usuario (
    id            BIGSERIAL PRIMARY KEY,
    nombre        VARCHAR(100) NOT NULL,
    apellido      VARCHAR(100) NOT NULL,
    email         VARCHAR(150) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    rol           tipo_rol    NOT NULL,
    deleted_at    TIMESTAMPTZ
);

CREATE TABLE cliente (
    id_usuario    BIGINT PRIMARY KEY REFERENCES usuario(id),
    telefono      VARCHAR(20),
    direccion     VARCHAR(255),
    tipo_documento tipo_documento NOT NULL,
    numero_documento VARCHAR(20) NOT NULL
);

CREATE TABLE empleado (
    id_usuario    BIGINT PRIMARY KEY REFERENCES usuario(id),
    cargo         VARCHAR(100),
    fecha_ingreso DATE
);
```

**Entidades JPA:**

```java
@Entity
public class Cliente {
    @Id
    private Long idUsuario;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    // campos propios...
}
```

**Regla de dominio:** Un `Usuario` con rol `CLIENTE` debe tener un perfil `Cliente`. Un `Usuario` con rol `EMPLEADO` debe tener un perfil `Empleado`. La validacion ocurre en el use case de registro, no en JPA.

---

## Consecuencias

**Positivas:**
- Queries a `Usuario` sin JOIN a `cliente`/`empleado` a menos que se necesiten.
- Un mismo `Usuario` puede tener ambos perfiles (profesor que compra).
- Esquema SQL limpio y legible. Alineado con `SCRIPTS_POSTGRES.sql` existente.
- Sin magia de Hibernate con `DTYPE` ni tabla de discriminadores.

**Negativas:**
- El use case de registro debe crear explicitamente el perfil correspondiente en una transaccion.
- Queries que necesiten datos de usuario + cliente requieren JOIN explicito o `@EntityGraph`.
- Sin la jerarquia JPA, no se puede hacer `instanceOf` polimorfismo en codigo.

---

## Alternativas consideradas

| Alternativa | Razon de rechazo |
|-------------|-----------------|
| `@Inheritance(JOINED)` | JOINs implicitos, falla con perfiles multiples, discordante con schema SQL actual |
| `@Inheritance(SINGLE_TABLE)` | Columnas nulas en todas las filas; una tabla gigante con campos dispersos |
| Tabla unica con columnas opcionales | Mismo problema que SINGLE_TABLE; schema ilegible |
| Roles via tabla `usuario_rol` M:N | Sobre-ingenieria para MVP; el sistema tiene solo 2 roles funcionales |
