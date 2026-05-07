# Estrategia de Testing — TiendaQ

---

## Piramide de testing

```
         /\
        /E2E\          Playwright — flujos criticos completos
       /──────\
      / Integr \       Testcontainers — repositorios + casos de uso
     /──────────\
    / Unitarios  \     JUnit 5 + Vitest — domain, use cases, components
   /______________\
```

| Nivel | Tecnologia | Que cubre | Velocidad |
|-------|-----------|-----------|-----------|
| Unitario (backend) | JUnit 5 + Mockito | Domain entities, use cases (mocks de puertos) | Rapido (<1s por test) |
| Unitario (frontend) | Vitest + Angular Testing Library | Components, signals, servicios | Rapido (<2s por suite) |
| Integracion (backend) | Testcontainers + PostgreSQL real | Repositorios JPA, Flyway, use cases completos | Medio (5-30s) |
| E2E | Playwright | Flujos de usuario criticos (login, checkout, admin) | Lento (1-5min) |

**Cobertura objetivo:**
- Domain layer: >= 80%
- Application layer (use cases): >= 60%
- Infrastructure (repositorios): cubierta por tests de integracion
- Frontend components presentationals: >= 70%

---

## Testing de Backend

### Unitarios — Domain

Los tests de dominio no levantan Spring. Solo JUnit 5 + clases del dominio:

```java
// CheckoutUseCaseTest.java
class CheckoutUseCaseTest {

    // Mocks de puertos (interfaces del dominio)
    CarritoRepositoryPort carritoRepo = mock(CarritoRepositoryPort.class);
    StockReservationPort stockPort = mock(StockReservationPort.class);
    FacturaRepositoryPort facturaRepo = mock(FacturaRepositoryPort.class);
    PagoPort pagoPort = mock(PagoPort.class);

    CheckoutUseCase useCase = new CheckoutUseCase(carritoRepo, stockPort, facturaRepo, pagoPort);

    @Test
    void checkoutFallaSiCarritoNoEstaActivo() {
        var carrito = Carrito.builder().estado(EstadoCarrito.COMPLETADO).build();
        when(carritoRepo.obtener(1L)).thenReturn(Optional.of(carrito));

        assertThrows(EstadoInvalidoException.class, () ->
            useCase.ejecutar(new CheckoutCommand(1L, 1L))
        );

        verify(stockPort, never()).reservar(any());
    }

    @Test
    void checkoutCalculaTotalConIva19Pct() {
        // dado un carrito con item de $10.000
        // cuando ejecuto checkout
        // entonces total = $11.900 (HALF_UP)
    }
}
```

### Integracion con Testcontainers

```java
@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
class ProductoRepositoryTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
        .withDatabaseName("tiendaq_test");

    @DynamicPropertySource
    static void props(DynamicPropertyRegistry r) {
        r.add("spring.datasource.url", postgres::getJdbcUrl);
        r.add("spring.datasource.username", postgres::getUsername);
        r.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired ProductoRepository repo;

    @Test
    void softDeleteNoAparenceEnConsulta() {
        var producto = repo.save(Producto.builder().nombre("Test").precio(new BigDecimal("1000")).build());
        repo.deleteById(producto.getId());  // soft delete via @SQLDelete

        assertThat(repo.findById(producto.getId())).isEmpty();
        // pero existe en DB: verificar con query nativa
    }

    @Test
    void optimisticLockLanzaExcepcionEnConflicto() {
        // dos transacciones actualizan el mismo producto
        // la segunda lanza OptimisticLockException
    }
}
```

### Slice tests para controladores

```java
@WebMvcTest(ProductoController.class)
class ProductoControllerTest {

    @Autowired MockMvc mvc;
    @MockBean CrearProductoUseCase crearUseCase;

    @Test
    @WithMockUser(roles = "EMPLEADO")
    void crearProductoRetorna201() throws Exception {
        mvc.perform(post("/api/catalogo/productos")
                .contentType(APPLICATION_JSON)
                .content("""
                    {"nombre":"Cuaderno","precio":5000,"categoriaId":1}
                """))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").exists());
    }

    @Test
    @WithMockUser(roles = "CLIENTE")
    void crearProductoSinRolEmpleadoRetorna403() throws Exception {
        mvc.perform(post("/api/catalogo/productos")
                .contentType(APPLICATION_JSON)
                .content("{}"))
            .andExpect(status().isForbidden());
    }
}
```

---

## Testing de Frontend

### Setup Vitest + Angular

```typescript
// vitest.config.ts
import { defineConfig } from 'vitest/config';
import angular from '@analogjs/vite-plugin-angular';

export default defineConfig({
  plugins: [angular()],
  test: {
    globals: true,
    environment: 'jsdom',
    setupFiles: ['src/test-setup.ts'],
    coverage: {
      provider: 'v8',
      include: ['src/app/**/*.ts'],
      exclude: ['src/app/**/*.routes.ts', 'src/app/**/*.spec.ts']
    }
  }
});
```

### Test de componente presentational

```typescript
// product-card.component.spec.ts
import { render, screen } from '@testing-library/angular';
import { ProductCardComponent } from './product-card.component';

describe('ProductCardComponent', () => {
  it('muestra nombre y precio formateado', async () => {
    await render(ProductCardComponent, {
      componentInputs: {
        producto: { id: 1, nombre: 'Cuaderno', precio: 5000, currency: 'COP' }
      }
    });

    expect(screen.getByText('Cuaderno')).toBeTruthy();
    expect(screen.getByText('$5.000')).toBeTruthy();
  });

  it('emite evento agregarAlCarrito al hacer click', async () => {
    const { fixture } = await render(ProductCardComponent, {
      componentInputs: { producto: { id: 1, nombre: 'Test', precio: 1000 } }
    });

    const spy = jest.spyOn(fixture.componentInstance.agregarAlCarrito, 'emit');
    screen.getByRole('button', { name: /agregar/i }).click();

    expect(spy).toHaveBeenCalledWith(1);
  });
});
```

### Test de signal store

```typescript
// catalogo.store.spec.ts
import { TestBed } from '@angular/core/testing';
import { catalogoStore } from './catalogo.store';

describe('catalogoStore', () => {
  it('productosFiltrados retorna todos sin filtro activo', () => {
    catalogoStore.productos.set([
      { id: 1, nombre: 'A', categoria: 'utiles' },
      { id: 2, nombre: 'B', categoria: 'libros' }
    ]);
    catalogoStore.categoria.set(null);

    expect(catalogoStore.productosFiltrados()).toHaveLength(2);
  });

  it('productosFiltrados filtra por categoria', () => {
    catalogoStore.categoria.set('utiles');
    expect(catalogoStore.productosFiltrados()).toHaveLength(1);
  });
});
```

---

## E2E con Playwright

```typescript
// e2e/checkout.spec.ts
import { test, expect } from '@playwright/test';

test.describe('Flujo checkout', () => {
  test.beforeEach(async ({ page }) => {
    // Login con usuario de prueba
    await page.goto('/login');
    await page.fill('[data-testid=email]', 'cliente@test.com');
    await page.fill('[data-testid=password]', 'Test1234!');
    await page.click('[data-testid=btn-login]');
    await expect(page).toHaveURL('/catalogo');
  });

  test('agrega producto al carrito y llega a checkout', async ({ page }) => {
    await page.goto('/catalogo');
    await page.click('[data-testid=btn-agregar-0]');
    await page.click('[data-testid=nav-carrito]');
    await expect(page.locator('[data-testid=cart-total]')).toBeVisible();
    await page.click('[data-testid=btn-checkout]');
    await expect(page).toHaveURL('/checkout');
  });
});
```

```typescript
// playwright.config.ts
export default {
  testDir: 'e2e',
  use: { baseURL: 'http://localhost:4200' },
  webServer: [
    { command: 'cd app/backend/tiendaq && ./mvnw spring-boot:run', port: 8080 },
    { command: 'cd app/frontend && bun start', port: 4200 }
  ]
};
```

---

## Comandos

```bash
# Backend — tests unitarios e integracion
cd app/backend/tiendaq
./mvnw test                    # todos los tests
./mvnw test -pl :tiendaq -Dtest=CheckoutUseCaseTest  # test especifico
./mvnw verify -Pcoverage       # con reporte Jacoco (target/site/jacoco/)

# Frontend — tests unitarios
cd app/frontend
bun run test                   # Vitest watch mode
bun run test:ci                # Vitest una sola vez (CI)
bun run test:coverage          # Con reporte de cobertura

# E2E
bun run e2e                    # Playwright headless
bun run e2e:ui                 # Playwright con UI
```

---

## Datos de prueba

Para tests de integracion y E2E, usar los seeds de `app/database/INSERTS.sql`.

Usuarios de prueba predefinidos:

| Email | Password | Rol |
|-------|----------|-----|
| `cliente@test.com` | `Test1234!` | CLIENTE |
| `empleado@test.com` | `Test1234!` | EMPLEADO |

Tarjetas de prueba Wompi sandbox:

| Numero | Resultado |
|--------|-----------|
| `4242 4242 4242 4242` | APPROVED |
| `4000 0000 0000 0002` | DECLINED |
