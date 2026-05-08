# Arquitectura Frontend — TiendaQ

**Framework:** Angular 21.2.0
**Lenguaje:** TypeScript ~5.9.2
**Estilos:** SCSS
**UI:** PrimeNG con tema K-Forge
**Gestion de estado:** Angular Signals
**Package manager:** pnpm (dependencias) + Bun (scripts)

---

## Estructura de directorios

```
app/frontend/src/
├── app/
│   ├── core/                     # Servicios singleton, interceptors, guards
│   │   ├── auth/
│   │   │   ├── auth.service.ts   # login, logout, refresh token
│   │   │   ├── auth.guard.ts     # guard rutas autenticadas
│   │   │   └── auth.interceptor.ts  # adjunta Bearer token, maneja 401
│   │   └── services/
│   │       ├── api.service.ts    # wrapper HttpClient
│   │       └── notification.service.ts
│   ├── shared/                   # Atomic Design: Atoms + Molecules
│   │   ├── atoms/
│   │   │   ├── button/
│   │   │   ├── input/
│   │   │   ├── badge/
│   │   │   └── spinner/
│   │   └── molecules/
│   │       ├── product-card/
│   │       ├── cart-item/
│   │       └── price-display/
│   ├── features/                 # Bounded contexts del frontend
│   │   ├── catalogo/
│   │   │   ├── organisms/        # ProductGrid, CategoryFilter
│   │   │   ├── templates/        # CatalogoTemplate
│   │   │   └── pages/            # CatalogoPage (container)
│   │   ├── carrito/
│   │   │   ├── organisms/        # CartSidebar, CartSummary
│   │   │   └── pages/            # CarritoPage
│   │   ├── checkout/
│   │   │   ├── organisms/        # CheckoutForm, PaymentStatus
│   │   │   └── pages/            # CheckoutPage, ConfirmacionPage
│   │   ├── pedidos/
│   │   │   ├── organisms/        # FacturaList, FacturaDetail
│   │   │   └── pages/            # HistorialPage
│   │   ├── auth/
│   │   │   ├── organisms/        # LoginForm, RegisterForm
│   │   │   └── pages/            # LoginPage, RegisterPage
│   │   └── admin/
│   │       ├── organisms/        # ProductoForm, StockPanel, ReporteTable
│   │       └── pages/            # AdminCatalogoPage, InventarioPage
│   ├── app.routes.ts             # Rutas principales con lazy loading
│   └── app.ts                    # Root component
├── environments/
│   ├── environment.ts            # dev: localhost:8080
│   └── environment.prod.ts       # prod: URL de Render
└── styles/
    ├── _variables.scss           # Design tokens K-Forge
    ├── _typography.scss
    └── main.scss
```

---

## Atomic Design

| Nivel | Ubicacion | Descripcion | Ejemplo |
|-------|-----------|-------------|---------|
| **Atoms** | `shared/atoms/` | Componente basico sin logica de negocio | `<tq-button>`, `<tq-input>` |
| **Molecules** | `shared/molecules/` | Composicion de atoms con minima logica | `<tq-product-card>`, `<tq-price-display>` |
| **Organisms** | `features/*/organisms/` | Seccion completa de UI con logica de presentacion | `<tq-product-grid>`, `<tq-cart-sidebar>` |
| **Templates** | `features/*/templates/` | Layout sin datos reales | `<tq-catalogo-template>` |
| **Pages** | `features/*/pages/` | Container: carga datos, llama servicios, pasa props | `CatalogoPage`, `CheckoutPage` |

**Container/Presentational pattern:**
- **Pages (containers):** Inyectan servicios, llaman API, manejan estado via signals. Pasan datos como `@Input()` a organisms.
- **Organisms/Molecules (presentationals):** Sin dependencias de servicios. Solo `@Input()` + `@Output()`. Faciles de testear con Vitest.

---

## Angular Signals

```typescript
// catalogo.store.ts (signal store por feature)
export const catalogoStore = {
  productos: signal<Producto[]>([]),
  categoria: signal<string | null>(null),
  cargando: signal(false),

  productosFiltrados: computed(() =>
    catalogoStore.productos().filter(p =>
      !catalogoStore.categoria() || p.categoria === catalogoStore.categoria()
    )
  )
};

// catalogo.page.ts (container)
@Component({
  template: `
    <tq-product-grid
      [productos]="store.productosFiltrados()"
      [cargando]="store.cargando()"
      (agregarAlCarrito)="onAgregar($event)"
    />
  `
})
export class CatalogoPage {
  store = catalogoStore;

  constructor(private catalogoSvc: CatalogoService) {
    effect(() => {
      this.catalogoSvc.listar(this.store.categoria()).subscribe(
        productos => this.store.productos.set(productos)
      );
    });
  }
}
```

---

## Autenticacion — Interceptor + Guard

```typescript
// auth.interceptor.ts
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const auth = inject(AuthService);
  const token = auth.accessToken();

  const authed = token
    ? req.clone({ setHeaders: { Authorization: `Bearer ${token}` } })
    : req;

  return next(authed).pipe(
    catchError(err => {
      if (err.status === 401) {
        return auth.refresh().pipe(
          switchMap(newToken => next(
            req.clone({ setHeaders: { Authorization: `Bearer ${newToken}` } })
          ))
        );
      }
      return throwError(() => err);
    })
  );
};

// auth.guard.ts
export const authGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  const router = inject(Router);
  return auth.isAuthenticated() ? true : router.createUrlTree(['/login']);
};

// empleado.guard.ts
export const empleadoGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  const router = inject(Router);
  return auth.hasRole('EMPLEADO') ? true : router.createUrlTree(['/']);
};
```

---

## Rutas con lazy loading

```typescript
// app.routes.ts
export const routes: Routes = [
  { path: '', redirectTo: 'catalogo', pathMatch: 'full' },
  {
    path: 'catalogo',
    loadComponent: () => import('./features/catalogo/pages/catalogo.page')
      .then(m => m.CatalogoPage)
  },
  {
    path: 'carrito',
    loadComponent: () => import('./features/carrito/pages/carrito.page')
      .then(m => m.CarritoPage),
    canActivate: [authGuard]
  },
  {
    path: 'checkout',
    loadComponent: () => import('./features/checkout/pages/checkout.page')
      .then(m => m.CheckoutPage),
    canActivate: [authGuard]
  },
  {
    path: 'pedidos',
    loadComponent: () => import('./features/pedidos/pages/historial.page')
      .then(m => m.HistorialPage),
    canActivate: [authGuard]
  },
  {
    path: 'admin',
    loadChildren: () => import('./features/admin/admin.routes')
      .then(m => m.adminRoutes),
    canActivate: [authGuard, empleadoGuard]
  },
  { path: 'login', loadComponent: () => import('./features/auth/pages/login.page').then(m => m.LoginPage) },
  { path: 'registro', loadComponent: () => import('./features/auth/pages/register.page').then(m => m.RegisterPage) }
];
```

---

## Design tokens K-Forge (SCSS)

```scss
// _variables.scss
:root {
  --color-primary:     #EAB308;  // K-Forge yellow
  --color-primary-dark:#CA8A04;
  --color-surface:     #0F0F0F;  // dark background
  --color-surface-alt: #1A1A1A;
  --color-text:        #F5F5F5;
  --color-text-muted:  #9CA3AF;
  --color-danger:      #EF4444;
  --color-success:     #22C55E;

  --radius-sm:  4px;
  --radius-md:  8px;
  --radius-lg:  12px;

  --spacing-xs: 4px;
  --spacing-sm: 8px;
  --spacing-md: 16px;
  --spacing-lg: 24px;
  --spacing-xl: 48px;
}
```

---

## PrimeNG — configuracion

```typescript
// app.ts
import { providePrimeNG } from 'primeng/config';
import Aura from '@primeng/themes/aura';

bootstrapApplication(App, {
  providers: [
    providePrimeNG({
      theme: {
        preset: Aura,
        options: {
          darkModeSelector: '.dark',  // clase en <html>
          cssLayer: { name: 'primeng', order: 'tailwind-base, primeng, tailwind-utilities' }
        }
      }
    })
  ]
});
```

---

## Comandos de desarrollo

```bash
# Instalar dependencias
cd app/frontend
pnpm install

# Iniciar servidor de desarrollo (puerto 4200)
bun start

# Build de produccion
bun run build

# Tests (Vitest)
bun run test

# Tests E2E (Playwright)
bun run e2e
```

---

## Variables de entorno

```typescript
// environment.ts (dev)
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080/api'
};

// environment.prod.ts
export const environment = {
  production: true,
  apiUrl: 'https://tiendaq-api.onrender.com/api'
};
```
