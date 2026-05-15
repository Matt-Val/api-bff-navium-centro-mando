# api-bff-navium-centro-mando

Backend for Frontend (BFF) para el Centro de Mando de Navium. Este servicio agrega datos de agendamientos, contenedores y andenes para exponer un tablero de operaciones consumido por el frontend.

## Alcance

- Orquesta llamadas a tres microservicios: agendamientos, contenedores y andenes.
- Consolida la informacion en una vista unica del dashboard.
- Aplica JWT obligatorio para las rutas del dashboard.
- Expone documentacion OpenAPI con Swagger UI.

## Requisitos

- Java 17
- Maven (o usar el wrapper `mvnw`)

## Configuracion

Las propiedades base estan en `src/main/resources/application.properties`.

- `server.port`: puerto de la app (por defecto `8082`).
- `navium.web.allowed-origins`: origenes permitidos para CORS.
- `jwt.secret`: clave usada por la libreria de seguridad.
- `navium.api.contenedores.url`: URL del ms de contenedores.
- `navium.api.agendamientos.url`: URL del ms de agendamientos.
- `navium.api.andenes.url`: URL del ms de andenes.

TimeLimiter y CircuitBreaker se configuran con Resilience4j para los clientes HTTP.


La app quedara disponible en `http://localhost:8082`.

## Endpoints

- `GET /api/dashboard/operaciones`
	- Resumen del tablero de operaciones.
	- Requiere JWT valido (filtro `JwtAuthorizationFilter`).


## Swagger

La UI de Swagger esta disponible en:

`http://localhost:8082/swagger-ui.html`
