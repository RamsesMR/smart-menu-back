# 🍽️ Smart Menu Back

### API REST Backend para sistema de gestión de menús digitales con recomendaciones nutricionales inteligentes

---

## 📋 Descripción general

**Smart Menu Back** es una API REST backend desarrollada con **Java 21** y **Spring Boot 4.0.1**, diseñada para dar soporte al sistema de menú digital de un restaurante. El proyecto gestiona toda la lógica de negocio del lado del servidor: autenticación de usuarios, gestión del catálogo de productos, categorización de la carta, control de pedidos en tiempo real y un motor de recomendaciones nutricionales personalizadas.

El sistema está pensado para negocios del sector de la restauración que necesiten digitalizar su carta y su proceso de pedidos, permitiendo que clientes (p. ej., tablets de mesa) puedan consultar el menú y realizar pedidos, mientras que el personal y la empresa gestiona el catálogo, el estado de los pedidos y los usuarios.

El backend actúa como una capa de datos y lógica centralizada, lista para ser consumida por cualquier cliente frontend (Angular, React, app móvil, etc.).

---

## ✅ Funcionalidades principales

- **Autenticación y autorización** basada en JWT con roles diferenciados (`EMPRESA`, `CLIENTE`, `EMPLEADO`).
- **Registro e inicio de sesión** de usuarios con contraseñas cifradas en BCrypt.
- **Gestión completa de productos** (CRUD): alta, consulta, modificación y baja de platos del menú, con cálculo automático de IVA.
- **Gestión de categorías** (CRUD): organización de la carta por secciones (Entrantes, Platos principales, Bebidas, etc.) con orden configurable.
- **Gestión de pedidos**: creación, consulta (por mesa, por usuario, por estado), modificación y eliminación de pedidos. Cada pedido incluye líneas de pedido con nombre y precio histórico del momento de la compra.
- **Cambio de estado de pedido** (`RECIBIDO → PREPARANDO → LISTO → ENTREGADO / CANCELADO`) mediante un endpoint PATCH dedicado, con generación automática de código de pedido.
- **Gestión de restaurantes**: consulta del restaurante o lista de restaurantes disponibles.
- **Gestión de usuarios** (solo `EMPRESA`): listado, consulta, actualización del perfil biométrico y eliminación. Los endpoints de usuario devuelven siempre DTOs, nunca datos sensibles.
- **Motor de recomendaciones nutricionales**: algoritmo propio que estima las calorías diarias mediante la fórmula de **Mifflin-St Jeor** y genera hasta 3 propuestas de menú (entrante + principal + postre + bebida opcional) personalizadas por tipo de dieta, objetivo nutricional, alergenos y perfil biométrico del usuario.
- **Documentación interactiva** de la API con **Swagger UI / OpenAPI 3** con soporte de autenticación Bearer JWT.
- **CORS configurado** para entornos de desarrollo (`localhost:4200`) y producción (`lakritas.com`).
- **Empaquetado WAR** para despliegue en servidor Tomcat externo.

---

## 🏛️ Arquitectura del proyecto

El proyecto sigue una **arquitectura en capas** (Layered Architecture), patrón estándar en aplicaciones Spring Boot. Cada capa tiene una responsabilidad bien definida y se comunica únicamente con la capa inmediatamente inferior:

```
┌─────────────────────────────────────────────────────────────┐
│                     CLIENTE (Frontend)                      │
│              HTTP Request con Bearer JWT                    │
└─────────────────────────┬───────────────────────────────────┘
                          │
┌─────────────────────────▼───────────────────────────────────┐
│              CAPA DE SEGURIDAD (Security)                   │
│  JwtAuthenticationFilter → SecurityConfig → CorsConfig      │
│  Valida el token JWT e inyecta el contexto de seguridad     │
└─────────────────────────┬───────────────────────────────────┘
                          │
┌─────────────────────────▼───────────────────────────────────┐
│           CAPA DE PRESENTACIÓN (Controllers)                │
│  @RestController → recibe la petición HTTP, valida IDs,     │
│  delega al servicio y construye la ResponseEntity           │
└─────────────────────────┬───────────────────────────────────┘
                          │
┌─────────────────────────▼───────────────────────────────────┐
│              CAPA DE NEGOCIO (Services)                     │
│  Interfaces + Implementaciones (@Service)                   │
│  Contiene la lógica de negocio: cálculo de IVA,            │
│  algoritmo de recomendación, mapeo a DTOs, etc.             │
└─────────────────────────┬───────────────────────────────────┘
                          │
┌─────────────────────────▼───────────────────────────────────┐
│           CAPA DE PERSISTENCIA (Repositories)               │
│  Interfaces MongoRepository → acceso a MongoDB              │
│  Consultas derivadas por convención de nomenclatura         │
└─────────────────────────┬───────────────────────────────────┘
                          │
┌─────────────────────────▼───────────────────────────────────┐
│                  BASE DE DATOS (MongoDB)                    │
│  Colecciones: usuario, producto, categoria, pedido,         │
│  restaurante                                                │
└─────────────────────────────────────────────────────────────┘
```

### Flujo de una petición típica

1. El cliente envía una petición HTTP con el token JWT en la cabecera `Authorization: Bearer <token>`.
2. El filtro `JwtAuthenticationFilter` intercepta la petición, valida el token con `JwtSecurityService` y, si es válido, carga el `UserDetails` desde `UsuarioServiceImpl` e inyecta la autenticación en el `SecurityContextHolder`.
3. `SecurityConfig` evalúa las reglas de autorización según el rol del usuario y el método HTTP. Si no está autorizado, responde con `401 Unauthorized`.
4. La petición llega al `@RestController` correspondiente, que valida el formato del `ObjectId` y delega en el `@Service`.
5. El servicio aplica la lógica de negocio (cálculo de IVA, generación de recomendaciones, mapeo a DTO) y llama al `MongoRepository`.
6. El repositorio ejecuta la consulta en MongoDB y devuelve el documento.
7. La respuesta recorre las capas en sentido inverso hasta ser serializada como JSON por Jackson (con serialización personalizada de `ObjectId`).

### DTOs y protección de datos

Los DTOs actúan como objetos de transferencia entre capas: el endpoint de usuario devuelve únicamente `UsuarioDto` (nombre + rol) para no exponer contraseñas ni datos biométricos. Las respuestas de autenticación incluyen un `AuthResponseDto` con el token y el DTO del usuario.

---

## 📁 Estructura del proyecto

```bash
smart-menu-back-002-bryan/
├── DB/                                          # Datos de ejemplo para importar en MongoDB
│   ├── smart_menu.categoria.json
│   ├── smart_menu.pedido.json
│   ├── smart_menu.producto.json
│   ├── smart_menu.restaurante.json
│   └── smart_menu.usuario.json
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── gestion/
│   │   │       ├── SmartMenuBackApplication.java       # Clase principal @SpringBootApplication
│   │   │       ├── ServletInitializer.java             # Inicializador para despliegue WAR
│   │   │       ├── config/
│   │   │       │   └── JacksonConfig.java              # Serialización personalizada de ObjectId
│   │   │       ├── model/
│   │   │       │   ├── collections/                    # Entidades / documentos MongoDB
│   │   │       │   │   ├── Categoria.java
│   │   │       │   │   ├── LineaPedido.java
│   │   │       │   │   ├── Pedido.java
│   │   │       │   │   ├── Producto.java
│   │   │       │   │   ├── Restaurante.java
│   │   │       │   │   ├── Usuario.java
│   │   │       │   │   └── DTO/                        # Objetos de transferencia de datos
│   │   │       │   │       ├── AuthResponseDto.java
│   │   │       │   │       ├── EstadoUpdateDto.java
│   │   │       │   │       ├── MenuSuggestion.java
│   │   │       │   │       ├── RecommendationRequest.java
│   │   │       │   │       ├── RecommendationResponse.java
│   │   │       │   │       ├── RegisterRequestDto.java
│   │   │       │   │       ├── UsuarioDto.java
│   │   │       │   │       └── UsuarioLoginDto.java
│   │   │       │   ├── enums/
│   │   │       │   │   ├── DietType.java               # NORMAL | VEGETARIANA | VEGANA
│   │   │       │   │   ├── EstadoPedido.java           # RECIBIDO | PREPARANDO | LISTO | ENTREGADO | CANCELADO
│   │   │       │   │   ├── GoalType.java               # PERDER_PESO | MANTENER | GANAR_MUSCULO
│   │   │       │   │   ├── Rol.java                    # EMPRESA | CLIENTE | EMPLEADO
│   │   │       │   │   └── Sexo.java                   # HOMBRE | MUJER
│   │   │       │   ├── repository/                     # Acceso a datos (Spring Data MongoDB)
│   │   │       │   │   ├── CategoriaRepository.java
│   │   │       │   │   ├── PedidoRepository.java
│   │   │       │   │   ├── ProductoRepository.java
│   │   │       │   │   ├── RestauranteRepository.java
│   │   │       │   │   └── UsuarioRepository.java
│   │   │       │   ├── restcontroller/                 # Capa de presentación (API REST)
│   │   │       │   │   ├── AuthRestController.java
│   │   │       │   │   ├── CategoriaRestController.java
│   │   │       │   │   ├── PedidoRestController.java
│   │   │       │   │   ├── ProductoRestController.java
│   │   │       │   │   ├── RecommendationRestController.java
│   │   │       │   │   ├── RestauranteRestController.java
│   │   │       │   │   └── UsuarioRestController.java
│   │   │       │   └── service/                        # Lógica de negocio
│   │   │       │       ├── CategoriaService.java       # Interface
│   │   │       │       ├── CategoriaServiceImpl.java
│   │   │       │       ├── PedidoService.java
│   │   │       │       ├── PedidoServiceImpl.java
│   │   │       │       ├── ProductoService.java
│   │   │       │       ├── ProductoServiceImpl.java
│   │   │       │       ├── RecommendationService.java  # Motor de recomendaciones (sin interfaz)
│   │   │       │       ├── RestauranteService.java
│   │   │       │       ├── RestauranteServiceImpl.java
│   │   │       │       ├── UsuarioService.java
│   │   │       │       └── UsuarioServiceImpl.java     # Implementa UserDetailsService
│   │   │       └── security/
│   │   │           ├── CorsConfig.java                 # Política CORS
│   │   │           ├── JwtAuthenticationFilter.java    # Filtro JWT (OncePerRequestFilter)
│   │   │           ├── JwtSecurityService.java         # Generación y validación de tokens
│   │   │           ├── OpenApiConfig.java              # Swagger con Bearer Auth
│   │   │           └── SecurityConfig.java             # Cadena de filtros y reglas de autorización
│   │   └── resources/
│   │       └── application.properties                  # Configuración de la aplicación
│   └── test/
│       └── java/
│           └── gestion/
│               └── SmartMenuBackApplicationTests.java
├── pom.xml                                              # Dependencias y build Maven
└── mvnw / mvnw.cmd                                      # Maven Wrapper
```

---

## 🛠️ Tecnologías utilizadas

| Tecnología | Versión | Propósito |
|---|---|---|
| Java | 21 | Lenguaje de programación |
| Spring Boot | 4.0.1 | Framework principal |
| Spring Data MongoDB | (BOM Boot) | Persistencia en MongoDB |
| Spring Security | (BOM Boot) | Autenticación y autorización |
| JJWT (io.jsonwebtoken) | 0.11.5 | Generación y validación de tokens JWT |
| SpringDoc OpenAPI | 2.6.0 | Documentación interactiva Swagger UI |
| Lombok | (BOM Boot) | Reducción de código boilerplate |
| Spring Validation | (BOM Boot) | Validación de beans |
| MongoDB | — | Base de datos NoSQL documental |
| Maven | (Wrapper) | Gestión de dependencias y build |

---

## 🔐 Seguridad

### Mecanismo de autenticación

La seguridad del sistema se basa en **JSON Web Tokens (JWT)** con firma **HMAC-SHA256**. El flujo es el siguiente:

1. El usuario realiza una petición `POST /auth/login` o `POST /auth/register`.
2. Si las credenciales son válidas, el servidor genera un JWT firmado que incluye el email del usuario y sus roles en los claims, con una expiración de **3600000 ms (1 hora)**.
3. El cliente debe incluir el token en todas las peticiones protegidas mediante la cabecera `Authorization: Bearer <token>`.
4. El filtro `JwtAuthenticationFilter` valida el token en cada petición y establece el contexto de seguridad.

Las contraseñas se almacenan cifradas con **BCrypt** y nunca se devuelven en ningún endpoint (anotadas con `@JsonIgnore`).

### Roles y control de acceso

| Rol | Descripción |
|---|---|
| `EMPRESA` | Administrador del sistema. Acceso total a CRUD de productos, categorías, usuarios y gestión de pedidos. |
| `CLIENTE` | Usuario de mesa/tablet. Puede consultar carta, crear pedidos y acceder a recomendaciones. |
| `EMPLEADO` | Definido en el enum pero sin reglas de acceso específicas en el código analizado. |

### Matriz de acceso por endpoint

| Endpoint | Método | Acceso |
|---|---|---|
| `/auth/login`, `/auth/register` | `POST` | Público |
| `/restaurante/**` | `GET` | Público |
| `/swagger-ui/**`, `/v3/api-docs/**` | `GET` | Público |
| `/producto/**` | `GET` | `EMPRESA` + `CLIENTE` |
| `/producto/**` | `POST`, `PUT`, `DELETE` | Solo `EMPRESA` |
| `/categoria/**` | `GET` | `EMPRESA` + `CLIENTE` |
| `/categoria/**` | `POST`, `PUT`, `DELETE` | Solo `EMPRESA` |
| `/pedido/**` | `GET`, `POST`, `PUT`, `PATCH`, `DELETE` | `EMPRESA` + `CLIENTE` |
| `/recommendations/**` | `POST` | `EMPRESA` + `CLIENTE` |
| `/usuario/**` | Todos | Solo `EMPRESA` |

### Política CORS

El servidor permite peticiones desde los siguientes orígenes:

- `http://localhost:4200` y `http://127.0.0.1:4200` (desarrollo Angular)
- `http://lakritas.com`, `https://lakritas.com`, `http://www.lakritas.com`, `https://www.lakritas.com` (producción)

---

## 📦 Modelos de datos

### `Usuario`
Implementa `UserDetails` de Spring Security directamente, lo que permite usarlo como sujeto de autenticación sin adaptadores adicionales.

| Campo | Tipo | Descripción |
|---|---|---|
| `id` | `ObjectId` | Identificador único MongoDB |
| `nombre` | `String` | Nombre del usuario |
| `email` | `String` | Email (usado como `username`) |
| `contrasena` | `String` | Contraseña cifrada con BCrypt (`@JsonIgnore`) |
| `rol` | `Rol` | `EMPRESA`, `CLIENTE` o `EMPLEADO` |
| `pesoKg` | `Double` | Peso en kg (perfil biométrico) |
| `alturaCm` | `Integer` | Altura en cm |
| `edad` | `Integer` | Edad en años |
| `objetivo` | `GoalType` | `PERDER_PESO`, `MANTENER`, `GANAR_MUSCULO` |
| `dieta` | `DietType` | `NORMAL`, `VEGETARIANA`, `VEGANA` |

### `Producto`
Incluye lógica de dominio propia: el método `calcularIva()` deriva automáticamente `importeIva` y `precioConIva` a partir del precio base y el tipo de IVA. Este método es invocado desde el servicio en cada inserción o actualización.

| Campo | Tipo | Descripción |
|---|---|---|
| `id` | `ObjectId` | Identificador único |
| `categoriaId` | `ObjectId` | FK a la colección `categoria` |
| `restauranteId` | `ObjectId` | FK a la colección `restaurante` |
| `nombre` | `String` | Nombre del plato |
| `descripcion` | `String` | Descripción del plato |
| `precio` | `BigDecimal` | Precio base sin IVA |
| `tipoIva` | `BigDecimal` | Porcentaje de IVA (ej: `10`) |
| `importeIva` | `BigDecimal` | Importe calculado del IVA |
| `precioConIva` | `BigDecimal` | Precio final con IVA |
| `imagen` | `String` | URL de la imagen del plato |
| `disponible` | `boolean` | Disponibilidad en carta |
| `tags` | `List<String>` | Etiquetas: `ENTRANTE`, `PRINCIPAL`, `POSTRE`, `BEBIDA`, `VEGETARIANO`, `VEGANO`, `LIGERO`, `ALTO_PROTEINA`, etc. |
| `alergenos` | `List<String>` | Lista de alérgenos |
| `kcal` | `Integer` | Calorías |
| `proteinas` | `BigDecimal` | Proteínas en gramos |
| `grasas` | `BigDecimal` | Grasas en gramos |
| `carbohidratos` | `BigDecimal` | Carbohidratos en gramos |

### `Pedido`

| Campo | Tipo | Descripción |
|---|---|---|
| `id` | `ObjectId` | Identificador único |
| `usuarioId` | `ObjectId` | FK al usuario que realizó el pedido |
| `codigo` | `String` | Código legible (ej: `M1-A3F2`) generado automáticamente |
| `mesaId` | `String` | Identificador de la mesa |
| `estado` | `EstadoPedido` | Estado del pedido |
| `nota` | `String` | Nota general del pedido |
| `lineasPedido` | `List<LineaPedido>` | Listado de productos pedidos con snapshot de precio |
| `totalPedido` | `BigDecimal` | Importe total del pedido |
| `fechaCreacion` | `LocalDateTime` | Fecha y hora de creación (formato ISO) |

### `LineaPedido` (embebido en `Pedido`)

| Campo | Tipo | Descripción |
|---|---|---|
| `productoId` | `ObjectId` | Referencia al producto |
| `nombreActual` | `String` | Nombre en el momento del pedido (snapshot) |
| `precioActual` | `BigDecimal` | Precio en el momento del pedido (snapshot) |
| `cantidad` | `int` | Unidades pedidas |
| `nota` | `String` | Nota específica de esta línea |

### `Categoria`

| Campo | Tipo | Descripción |
|---|---|---|
| `id` | `ObjectId` | Identificador único |
| `nombre` | `String` | Nombre de la categoría |
| `descripcion` | `String` | Descripción |
| `orden` | `int` | Orden de presentación en la carta |
| `activo` | `boolean` | Si está activa y visible |

### `Restaurante`

| Campo | Tipo | Descripción |
|---|---|---|
| `id` | `ObjectId` | Identificador único |
| `nombre` | `String` | Nombre del restaurante |
| `telefono` | `String` | Teléfono de contacto |
| `direccion` | `String` | Dirección física |
| `activo` | `boolean` | Si está operativo |

---

## 🤖 Motor de recomendaciones nutricionales

`RecommendationService` implementa un algoritmo de recomendación de menús personalizado sin dependencias externas de IA. El proceso es el siguiente:

1. **Estimación de calorías objetivo** mediante la fórmula de **Mifflin-St Jeor** (TMB × factor de actividad moderada 1.55), dividida entre 3 comidas, con ajuste según el objetivo:
   - `PERDER_PESO`: −20% de calorías por comida.
   - `GANAR_MUSCULO`: +15% de calorías por comida.
   - `MANTENER`: sin ajuste.

2. **Filtrado del catálogo** por:
   - Disponibilidad del producto.
   - Tipo de dieta (`VEGANO`, `VEGETARIANO` o sin restricción).
   - Alérgenos a evitar.
   - Tipo de plato (solo productos con tag `ENTRANTE`, `PRINCIPAL`, `POSTRE` o `BEBIDA`).

3. **Composición de hasta 3 propuestas de menú** combinando: 1 plato principal + 1 entrante (opcional) + 1 postre (opcional) + 1 bebida (si se solicita), priorizando los productos cuyas calorías se aproximan al objetivo por fracción de comida.

4. **Respuesta** incluye el objetivo calórico calculado y los menús propuestos con su desglose nutricional total (kcal, proteínas, grasas, carbohidratos) y una justificación textual (`reason`).

---

## 🌐 Endpoints de la API

### Autenticación (`/auth`)

| Método | Ruta | Descripción | Auth |
|---|---|---|---|
| `POST` | `/auth/login` | Inicio de sesión. Devuelve JWT + UsuarioDto | No |
| `POST` | `/auth/register` | Registro de nuevo usuario | No |

### Categorías (`/categoria`)

| Método | Ruta | Descripción | Rol |
|---|---|---|---|
| `GET` | `/categoria` | Lista todas las categorías | EMPRESA / CLIENTE |
| `GET` | `/categoria/{id}` | Obtiene una categoría por ID | EMPRESA / CLIENTE |
| `POST` | `/categoria` | Crea una nueva categoría | EMPRESA |
| `PUT` | `/categoria/{id}` | Actualiza una categoría existente | EMPRESA |
| `DELETE` | `/categoria/{id}` | Elimina una categoría | EMPRESA |

### Productos (`/producto`)

| Método | Ruta | Descripción | Rol |
|---|---|---|---|
| `GET` | `/producto` | Lista todos los productos | EMPRESA / CLIENTE |
| `GET` | `/producto/{id}` | Obtiene un producto por ID | EMPRESA / CLIENTE |
| `GET` | `/producto/disponibles?restauranteId={id}` | Lista productos disponibles de un restaurante | EMPRESA / CLIENTE |
| `POST` | `/producto` | Crea un producto (calcula IVA automáticamente) | EMPRESA |
| `PUT` | `/producto/{id}` | Actualiza un producto (recalcula IVA) | EMPRESA |
| `DELETE` | `/producto/{id}` | Elimina un producto | EMPRESA |

### Pedidos (`/pedido`)

| Método | Ruta | Descripción | Rol |
|---|---|---|---|
| `GET` | `/pedido` | Lista todos los pedidos | EMPRESA / CLIENTE |
| `GET` | `/pedido/{id}` | Obtiene un pedido por ID | EMPRESA / CLIENTE |
| `GET` | `/pedido/mesa/{mesaId}` | Pedidos de una mesa | EMPRESA / CLIENTE |
| `GET` | `/pedido/usuario/{usuarioId}` | Pedidos de un usuario | EMPRESA / CLIENTE |
| `POST` | `/pedido` | Crea un pedido (genera código automáticamente) | EMPRESA / CLIENTE |
| `PUT` | `/pedido/{id}` | Actualiza un pedido completo | EMPRESA / CLIENTE |
| `PATCH` | `/pedido/{id}/estado` | Cambia únicamente el estado del pedido | EMPRESA / CLIENTE |
| `DELETE` | `/pedido/{id}` | Elimina un pedido | EMPRESA / CLIENTE |

### Restaurantes (`/restaurante`)

| Método | Ruta | Descripción | Auth |
|---|---|---|---|
| `GET` | `/restaurante` | Lista todos los restaurantes | No |
| `GET` | `/restaurante/{id}` | Obtiene un restaurante por ID | No |

### Usuarios (`/usuario`)

| Método | Ruta | Descripción | Rol |
|---|---|---|---|
| `GET` | `/usuario` | Lista todos los usuarios (devuelve DTOs) | EMPRESA |
| `GET` | `/usuario/{id}` | Obtiene un usuario por ID (devuelve DTO) | EMPRESA |
| `PUT` | `/usuario/{id}` | Actualiza perfil (incluido perfil biométrico) | EMPRESA |
| `DELETE` | `/usuario/{id}` | Elimina un usuario | EMPRESA |

### Recomendaciones (`/recommendations`)

| Método | Ruta | Descripción | Rol |
|---|---|---|---|
| `POST` | `/recommendations` | Genera recomendaciones de menú personalizadas | EMPRESA / CLIENTE |

**Body de ejemplo para `/recommendations`:**
```json
{
  "restauranteId": "696ba6825fe46fff9ddceb06",
  "edad": 30,
  "pesoKg": 75.0,
  "alturaCm": 175,
  "sexo": "HOMBRE",
  "dieta": "NORMAL",
  "objetivo": "MANTENER",
  "alergenosEvitar": ["gluten"],
  "kcalObjetivo": null,
  "incluirBebida": true
}
```

---

## ⚙️ Configuración y puesta en marcha

### Requisitos previos

- **Java 21** o superior.
- **Maven 3.9+** (o usar el Maven Wrapper incluido `./mvnw`).
- **MongoDB** en ejecución local en `localhost:27017` (o ajustar la URI en `application.properties`).

### Variables de configuración (`application.properties`)

```properties
spring.application.name=Smart_Menu_Back

# Puerto del servidor
server.port=9002

# Conexión MongoDB local
spring.mongodb.uri=mongodb://127.0.0.1:27017/smart_menu

# Para producción (requiere usuario/contraseña):
# spring.mongodb.uri=mongodb://userapp3:userapp3@127.0.0.1:27017/smart_menu?authSource=app3

# JWT
jwt.secret=TU_JWT
jwt.expiration=3600000
```

> ⚠️ **Importante**: La clave secreta JWT (`jwt.secret`) incluida en el repositorio es la de desarrollo. En producción debe sustituirse por una clave segura y gestionarse mediante variables de entorno o un vault de secretos.

### Importar datos de ejemplo

Los archivos JSON en la carpeta `DB/` pueden importarse directamente en MongoDB con `mongoimport`:

```bash
mongoimport --db smart_menu --collection restaurante --file DB/smart_menu.restaurante.json --jsonArray
mongoimport --db smart_menu --collection categoria   --file DB/smart_menu.categoria.json   --jsonArray
mongoimport --db smart_menu --collection producto    --file DB/smart_menu.producto.json    --jsonArray
mongoimport --db smart_menu --collection usuario     --file DB/smart_menu.usuario.json     --jsonArray
mongoimport --db smart_menu --collection pedido      --file DB/smart_menu.pedido.json      --jsonArray
```

### Compilar y ejecutar

```bash
# Clonar el repositorio
git clone <url-del-repositorio>
cd smart-menu-back-002-bryan

# Compilar y ejecutar (modo desarrollo)
./mvnw spring-boot:run

# Generar el WAR para despliegue
./mvnw clean package -DskipTests

# El artefacto generado estará en:
# target/Smart_Menu_Back-0.0.1-SNAPSHOT.war
```

La API estará disponible en: `http://localhost:9002`

### Documentación Swagger UI

Una vez iniciada la aplicación, la documentación interactiva está disponible en:

```
http://localhost:9002/swagger-ui.html
```

Para probar endpoints protegidos, hacer clic en **Authorize** e introducir el token obtenido del login con el formato:

```
Bearer <tu_token_jwt>
```

---

## 🧪 Pruebas

El proyecto incluye la clase de prueba base `SmartMenuBackApplicationTests` generada por Spring Initializr. Las dependencias de test incluidas en el `pom.xml` son:

- `spring-boot-starter-data-mongodb-test`
- `spring-boot-starter-security-test`
- `spring-boot-starter-webmvc-test`

> ⚠️ **Pendiente de completar**: No se han detectado tests unitarios ni de integración implementados más allá de la clase generada automáticamente. Se recomienda añadir tests para los servicios y controladores principales.

---

## 📝 Notas técnicas adicionales

- **Serialización de `ObjectId`**: Se configura un `ObjectMapper` personalizado en `JacksonConfig` que serializa los `ObjectId` de MongoDB como `String` hexadecimal en todas las respuestas JSON, evitando el problema de serialización por defecto del tipo BSON.
- **Snapshot de precios en pedidos**: `LineaPedido` almacena `nombreActual` y `precioActual` en el momento del pedido, garantizando integridad histórica aunque el producto cambie de precio posteriormente.
- **Generación de código de pedido**: El controlador de pedidos genera un código legible del tipo `M1-A3F2` (prefijo de mesa + 4 caracteres aleatorios en mayúsculas) que facilita la identificación en cocina.
- **Empaquetado WAR**: La clase `ServletInitializer` extiende `SpringBootServletInitializer`, lo que permite desplegar la aplicación en un Tomcat externo además de como JAR embebido.
- **Patrón Interface/Impl en servicios**: Todos los servicios (excepto `RecommendationService`) siguen el patrón interfaz/implementación, lo que facilita la inyección de dependencias y la posibilidad de crear implementaciones alternativas o mocks en pruebas.
- **`UsuarioServiceImpl` como `UserDetailsService`**: La misma clase de servicio implementa tanto `UsuarioService` como `UserDetailsService` de Spring Security, centralizando la lógica de carga de usuarios y evitando duplicidades.

---

## 👤 Autor

Desarrollado por **BRYAN, RAMSES, TANIMARA** como proyecto backend para el sistema **Smart Menu**.

---

*Documentación generada mediante análisis estático del código fuente.*
