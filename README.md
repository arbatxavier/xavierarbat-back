# xavierarbat-back

REST API backend for a multidisciplinary artist portfolio. Built with Kotlin, Spring Boot 4, and PostgreSQL.

Supports i18n (es/ca/en) via JSONB fields and `Accept-Language` header.

## Tech Stack

- **Kotlin** 2.2 + **Spring Boot** 4.0
- **Spring Data JPA** + **Hibernate** 7
- **PostgreSQL** 16 (via Docker)
- **Spring Security** 7 (API Key authentication)
- **SpringDoc OpenAPI** 3.0 (Swagger UI live docs)

## Prerequisites

- Java 17+
- Docker Desktop (for PostgreSQL)

## Getting Started

### 1. Start PostgreSQL

```bash
docker-compose up -d
```

This starts a PostgreSQL 16 container on port `5432` with:
- **User:** `user`
- **Password:** `password`
- **Database:** `xavierarbat_db`

### 2. Create your seed data

Copy the example file and customize it with your own data:

```bash
cp src/main/resources/data.sql.example src/main/resources/data.sql
```

Edit `data.sql` with your real contacts, projects, and blog posts. This file is in `.gitignore` and will not be committed.

The schema is created automatically by Hibernate on startup (`ddl-auto=update`). A reference schema is available at `src/main/resources/schema.sql`.

### 3. Run the application

```bash
./gradlew bootRun
```

The API starts at `http://localhost:8080`.

### 4. API Documentation (Swagger UI)

Interactive API documentation is available at:

- **Local:** [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **Production:** [https://api.xavierarbat.com/swagger-ui.html](https://api.xavierarbat.com/swagger-ui.html)

The raw OpenAPI 3.1 spec (JSON) is at `/v3/api-docs`.

### 5. Run tests

```bash
./gradlew test
```

> Tests require PostgreSQL to be running (`docker-compose up -d`).

## API Endpoints

### Public (no authentication required)

| Method | Path | Description |
|--------|------|-------------|
| `GET` | `/api/v1/projects` | List all projects |
| `GET` | `/api/v1/projects/{slug}` | Get project detail |
| `GET` | `/api/v1/blogs` | List all blog posts |
| `GET` | `/api/v1/blogs/{slug}` | Get blog post detail |
| `GET` | `/api/v1/contacts` | List all contacts |
| `GET` | `/api/v1/images/{folder}` | List images in folder |
| `GET` | `/uploads/{folder}/{filename}` | Serve an image file |

All GET endpoints accept an `Accept-Language` header (`en`, `es`, `ca`). Defaults to `en`.

### Protected (requires `X-API-Key` header)

| Method | Path | Description |
|--------|------|-------------|
| `POST` | `/api/v1/projects` | Create a project |
| `PUT` | `/api/v1/projects/{slug}` | Update a project |
| `DELETE` | `/api/v1/projects/{slug}` | Delete a project |
| `POST` | `/api/v1/blogs` | Create a blog post |
| `PUT` | `/api/v1/blogs/{slug}` | Update a blog post |
| `DELETE` | `/api/v1/blogs/{slug}` | Delete a blog post |
| `POST` | `/api/v1/contacts` | Create a contact |
| `PUT` | `/api/v1/contacts/{name}` | Update a contact |
| `DELETE` | `/api/v1/contacts/{name}` | Delete a contact |
| `POST` | `/api/v1/images/{folder}` | Upload an image (multipart) |
| `DELETE` | `/api/v1/images/{folder}/{filename}` | Delete an image |

### Example requests

```bash
# List projects in Spanish
curl -H "Accept-Language: es" http://localhost:8080/api/v1/projects

# Create a blog post (requires API key)
curl -X POST http://localhost:8080/api/v1/blogs \
  -H "X-API-Key: change-me-in-production" \
  -H "Content-Type: application/json" \
  -d '{
    "slug": "my-post",
    "date": "2026-04-27",
    "title": {"en": "My Post", "es": "Mi Post", "ca": "El Meu Post"},
    "description": {"en": "Short desc.", "es": "Desc corta.", "ca": "Desc curta."},
    "content": {"en": "Full **markdown** content.", "es": "Contenido en **markdown**.", "ca": "Contingut en **markdown**."}
  }'

# Upload an image (requires API key)
curl -X POST http://localhost:8080/api/v1/images/projects \
  -H "X-API-Key: change-me-in-production" \
  -F "file=@/path/to/image.jpg"

# Serve an uploaded image (public)
curl http://localhost:8080/uploads/projects/image.jpg
```

## Configuration

Key settings in `src/main/resources/application.properties`:

| Property | Default | Env Variable | Description |
|----------|---------|--------------|-------------|
| `app.api-key` | `change-me-in-production` | `API_KEY` | API key for protected endpoints |
| `spring.datasource.url` | `localhost:5432` | `DB_HOST` | Database host |
| `spring.datasource.username` | `user` | `DB_USER` | Database user |
| `spring.datasource.password` | `password` | `DB_PASSWORD` | Database password |
| `app.uploads.path` | `/app/uploads` | `UPLOADS_PATH` | Path for uploaded images |

In production, override via environment variables:

```bash
DB_HOST=localhost DB_USER=myuser DB_PASSWORD=strong-password API_KEY=your-secret-key ./gradlew bootRun
```

## Security

- **CORS**: Only `https://xavierarbat.com` and `http://localhost:3000` can make requests from browsers
- **API Key**: `POST`/`PUT`/`DELETE` endpoints require `X-API-Key` header
- **Rate Limiting**: 60 requests per minute per IP address

## i18n

All text fields (title, description, content, display) are stored as JSONB maps:

```json
{"es": "Texto en español", "ca": "Text en català", "en": "Text in English"}
```

The API resolves the language from the `Accept-Language` header with fallback: requested lang -> `en` -> first available.

## Image Uploads

Images are stored on a persistent volume and served as static files.

### Storage structure

```
/app/uploads/
├── projects/    # Project images
├── blogs/       # Blog post images
└── contacts/    # Contact avatars/icons
```

In production (Coolify), the volume maps `/var/www/xavierarbat/uploads` (host) to `/app/uploads` (container), so images persist across deployments.

For local development, set the `UPLOADS_PATH` environment variable:

```bash
UPLOADS_PATH=./uploads ./gradlew bootRun
```

### Upload constraints

| Constraint | Value |
|------------|-------|
| Max file size | 20 MB |
| Allowed types | `image/jpeg`, `image/png`, `image/webp`, `image/gif`, `image/svg+xml` |
| Filename handling | Original name preserved, sanitized (unsafe chars replaced with `_`) |
| Collision handling | Numeric suffix added (`image_1.jpg`, `image_2.jpg`, ...) |

### Usage examples

```bash
# Upload an image to the projects folder
curl -X POST https://api.xavierarbat.com/api/v1/images/projects \
  -H "X-API-Key: your-secret-key" \
  -F "file=@my-artwork.jpg"
# Response: {"url": "/uploads/projects/my-artwork.jpg"}

# Access the image (public, no auth needed)
# https://api.xavierarbat.com/uploads/projects/my-artwork.jpg

# List all images in the projects folder
curl https://api.xavierarbat.com/api/v1/images/projects
# Response: ["/uploads/projects/my-artwork.jpg", "/uploads/projects/another.png"]

# Delete an image
curl -X DELETE https://api.xavierarbat.com/api/v1/images/projects/my-artwork.jpg \
  -H "X-API-Key: your-secret-key"
```

### Using image URLs in entities

When creating a project or blog, reference the uploaded image path:

```json
{
  "slug": "my-project",
  "date": "2026-04-27",
  "image": "/uploads/projects/my-artwork.jpg",
  "title": {"en": "My Project"},
  "description": {"en": "Description"},
  "content": {"en": "Content"},
  "altImages": ["/uploads/projects/detail-1.jpg", "/uploads/projects/detail-2.jpg"]
}
```

## Project Structure

```
src/main/kotlin/com/xavierarbat/xavierarbatback/
├── config/          # CORS, Security, Rate Limiter, Error Handler, JPA Converters, OpenAPI
├── controller/      # REST controllers (Blog, Contact, Project, Image)
├── domain/          # JPA entities and enums
├── dto/             # Data Transfer Objects and mappers
├── exception/       # Custom exceptions
├── repository/      # Spring Data JPA repositories
└── service/         # Business logic (including image storage)
```
