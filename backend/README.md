# Children's Literacy Platform — Backend

Spring Boot 3.2 · Java 17 · PostgreSQL · JWT · Flyway

[![CI](https://github.com/YOUR_USERNAME/YOUR_REPO/actions/workflows/ci.yml/badge.svg)](https://github.com/YOUR_USERNAME/YOUR_REPO/actions)

## Quick Start (Local)

### Prerequisites
- Java 17+
- PostgreSQL 15+
- (Optional) Docker

### 1. Create database
```sql
CREATE DATABASE literacy_db;
```

### 2. Configure environment variables
Copy `.env.example` or set these in your shell:
```bash
export DB_URL=jdbc:postgresql://localhost:5432/literacy_db
export DB_USERNAME=postgres
export DB_PASSWORD=postgres
export JWT_SECRET=yourSuperSecretKeyAtLeast32CharsLong
export CORS_ORIGINS=http://localhost:5173
```

### 3. Run
```bash
./gradlew bootRun
```

Flyway will automatically run `V1__init.sql` on first start.

### 4. Swagger UI
Open: http://localhost:8080/swagger-ui.html

---

## Run Tests
```bash
./gradlew test
```
Tests use H2 in-memory DB (profile `test`) — no PostgreSQL needed.

---

## API Endpoints

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| POST | `/api/v1/auth/register` | Public | Register parent |
| POST | `/api/v1/auth/login` | Public | Login |
| POST | `/api/v1/auth/refresh` | Public | Refresh access token |
| POST | `/api/v1/auth/logout` | JWT | Logout (invalidates refresh token) |
| GET | `/api/v1/children` | JWT | List parent's children |
| POST | `/api/v1/children` | JWT | Create child profile |
| GET | `/api/v1/children/{id}` | JWT | Get child |
| PUT | `/api/v1/children/{id}` | JWT | Update child |
| DELETE | `/api/v1/children/{id}` | JWT | Delete child |
| GET | `/api/v1/children/{id}/progress` | JWT | Lesson history |
| GET | `/api/v1/children/{id}/badges` | JWT | Earned badges |
| GET | `/api/v1/units` | JWT | List units |
| POST | `/api/v1/units` | JWT | Create unit |
| GET | `/api/v1/lessons?unitId=&type=&published=&page=&size=` | JWT | List lessons (filtered, paginated) |
| POST | `/api/v1/lessons` | JWT | Create lesson |
| POST | `/api/v1/lessons/{id}/complete?childId=` | JWT | Complete lesson + XP + badges |
| GET | `/api/v1/lessons/{id}/exercises` | JWT | List exercises |
| POST | `/api/v1/exercises/{id}/submit` | JWT | Submit answer |
| GET | `/api/v1/leaderboard?ageGroup=3-5` | JWT | XP leaderboard |
| GET | `/api/v1/notifications` | JWT | Parent notifications |
| PATCH | `/api/v1/notifications/{id}/read` | JWT | Mark as read |
| GET | `/api/v1/admin/stats` | ADMIN | Platform stats |
| GET | `/api/v1/admin/logs?page=&size=` | ADMIN | Activity log (paginated) |

---

## Deployment (Railway / Render)

Set environment variables in the dashboard:
```
DB_URL=jdbc:postgresql://...
DB_USERNAME=...
DB_PASSWORD=...
JWT_SECRET=<random 64-char string>
CORS_ORIGINS=https://your-frontend.vercel.app
```

Flyway runs automatically on startup.

---

## Architecture

```
Controller → Service → Repository → PostgreSQL
                ↕
         GamificationService (XP, streak, badges)
                ↕
         NotificationService (in-app notifications)
```

- **Layered architecture**: zero business logic in controllers
- **JWT Auth**: access token (24h) + refresh token (7 days, stored in DB)
- **RBAC**: PARENT, CHILD, ADMIN roles enforced at SecurityConfig + @PreAuthorize
- **Flyway**: all schema changes via versioned SQL migrations
- **Ownership checks**: parents can only access their own children's data
