# URL Shortener REST API

Spring Boot + PostgreSQL (Spring JDBC). Layers: controller -> service -> repository.

## Run
1. Install Java 17+ and PostgreSQL. Make sure PostgreSQL is running.
2. Create the database once (pgAdmin or psql): `CREATE DATABASE urlshortener;`
3. Put your PostgreSQL password in `src/main/resources/application.properties`.
4. Run `UrlShortenerApplication` from IntelliJ (or `mvn spring-boot:run`).
   The `urls` table is created automatically.

## Test in Postman
| Request | Expected |
|---|---|
| POST `http://localhost:8080/shorten`, Body > raw > JSON: `{"url": "https://www.google.com"}` | 201 with `shortCode` and `shortUrl` |
| GET `http://localhost:8080/{shortCode}` (turn OFF "Automatically follow redirects") | 302 with a Location header |
| GET `http://localhost:8080/stats/{shortCode}` | JSON with `clickCount` going up |
| GET `http://localhost:8080/doesnotexist` | 404 with error message |
| POST `/shorten` with `{"url": "abc"}` | 400 with error message |

## Endpoints
- `POST /shorten`
- `GET /{code}`
- `GET /stats/{code}`
