# URL Shortener (Spring Boot + PostgreSQL)

Сервис сокращения ссылок на Java/Spring.

## Стек

- Java 17
- Spring Boot 4
- PostgreSQL
- Redis
- Gradle
- nginx

## Быстрый старт

1. Поднять приложение + PostgreSQL + Redis + nginx:

```bash
docker compose up -d
```

2. Проверить логи приложения:

```bash
docker compose logs -f app
```

Приложение будет доступно по адресу http://localhost (nginx проксирует на порт 8080).

## Docker

Поднять весь стек (app + postgres + redis + nginx):

```bash
docker compose up -d --build
```

Остановить и удалить контейнеры:

```bash
docker compose down
```

## API

### 1) Создать короткую ссылку

`POST /api/urls`

Тело запроса:

```json
{
  "url": "https://example.com/some/very/long/path",
  "length": 10
}
```

`length` - опциональное поле, диапазон `4..16`.  
Код теперь генерируется по схеме `sequence/id -> base62`.  
Если `length` больше длины base62-кода, слева добавляются `0`.  
Если `length` меньше длины base62-кода, сервис вернет `400 Bad Request`.  

Пример ответа:

```json
{
  "code": "a1B2c3D",
  "originalUrl": "https://example.com/some/very/long/path",
  "shortUrl": "/a1B2c3D"
}
```

### 2) Переход по короткой ссылке

`GET /{code}`

Возвращает `302 Found` с заголовком `Location` на оригинальный URL.  
Если срок жизни ссылки истек, сервис возвращает `410 Gone`.

## Фоновая очистка ##
Просроченные ссылки удаляются из PostgreSQL каждое первое число месяца в 00:00.

Задача выполняется с помощью @Scheduled. При нескольких экземплярах используется ShedLock для предотвращения дублирования.
## Переменные окружения

- `SERVER_PORT` (default: `8080`)
- `SPRING_DATASOURCE_URL` (default: `jdbc:postgresql://localhost:5432/url_shortener`)
- `SPRING_DATASOURCE_USERNAME` (default: `postgres`)
- `SPRING_DATASOURCE_PASSWORD` (default: `1111`)
- `SPRING_DATA_REDIS_HOST` (default: `localhost`)
- `SPRING_DATA_REDIS_PORT` (default: `6379`)
- `APP_SHORT_URL_TTL` (default: `30d`)
