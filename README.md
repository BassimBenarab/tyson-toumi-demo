# Tyson Toumi – Watch Configurator

Et ordrestyringssystem til urproducenten **Tyson Toumi**, der lader kunder sammensætte skræddersyede ure ud fra udvalgte komponenter.

## Funktioner

- **Ur-konfigurator**: Kunder vælger en del fra hver kategori (urskive, kasse, lænke, urværk, visere, krone, glas) og indsender en ordre
- **Ordrestyring**: Admin kan se alle ordrer og opdatere deres status (Modtaget → I produktion → Klar → Leveret)
- **Lagerstyring**: Admin kan se, opdatere og slette dele
- **Validering**: Både frontend og backend validerer brugerinput med brugerorienterede fejlbeskeder

## Teknologier

| Lag | Teknologi |
|-----|-----------|
| Backend | Spring Boot 4.0, Spring Web, Spring Data JPA |
| Database | MySQL 8.4 |
| Test | JUnit 5, Mockito, H2 (in-memory) |
| Container | Docker, Docker Compose |
| CI/CD | GitHub Actions → GHCR |
| Frontend | HTML, CSS, Vanilla JavaScript |

## Kom i gang

### Krav
- Docker og Docker Compose installeret

### Start applikationen
```bash
docker compose up --build
```

Applikationen starter på [http://localhost:8080](http://localhost:8080)

Frontend-filer åbnes direkte i browseren fra `frontend/`-mappen.

### Endpoints

| Metode | URL | Beskrivelse |
|--------|-----|-------------|
| GET | `/api/parts` | Alle dele |
| GET | `/api/parts/grouped` | Dele grupperet efter kategori |
| POST | `/api/parts` | Opret ny del |
| PATCH | `/api/parts/{id}/stock` | Opdater lagerstatus |
| DELETE | `/api/parts/{id}` | Slet del |
| GET | `/api/orders` | Alle ordrer |
| POST | `/api/orders` | Opret ny ordre |
| PATCH | `/api/orders/{id}/status` | Opdater ordrestatus |

## Kør tests

```bash
mvn test
```

Tests kører med H2 in-memory database (kræver ikke MySQL).

## CI/CD

| Workflow | Trigger | Handling |
|----------|---------|----------|
| `ci.yaml` | Pull request til `main` | Compiler og tester koden |
| `publish.yaml` | Push til `main` | Tester, bygger Docker image og pusher til GHCR |

Images tagges med commit-SHA og `latest`.

## Projektstruktur

```
src/main/java/com/tysontoumi/
├── controller/      REST API endpoints
├── service/         Forretningslogik
├── repository/      Spring Data JPA interfaces
├── model/           JPA entiteter og enums
├── dto/             Data Transfer Objects
├── exception/       Fejlhåndtering
└── config/          Seed-data ved opstart

frontend/
├── index.html       Ur-konfigurator (kundevisning)
├── admin.html       Admin-panel
├── style.css        Fælles styling
├── app.js           Konfigurator-logik
└── admin.js         Admin-logik
```

## Gruppemedlemmer

| Navn | GitHub-brugernavn |
|------|------------------|
| Bassim Benarab | bassimbenarab |
