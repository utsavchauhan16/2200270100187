

# 📦 URL Shortener Microservice

A robust Spring Boot microservice for shortening long URLs, tracking redirection analytics, and securely logging usage using token-based authentication. Built for Campus Hiring Evaluation compliance.

---

## 🚀 Features

✅ Generate short URLs
✅ Redirect to original long URL
✅ Set custom validity (in minutes)
✅ Track analytics (redirect count, last accessed)
✅ Client authentication via Bearer token (stored in DB)
✅ External logging with Authorization header
✅ MySQL + JPA persistence
✅ Spring Boot RESTful APIs
✅ Logs each request to evaluation server

---

## ⚙️ Technologies Used

* Java 17
* Spring Boot 3+
* Spring Web + Spring Data JPA
* MySQL
* RestTemplate
* HikariCP
* Hibernate
* Lombok
* External Auth API + Logging API

---

## 🗄 Database Schema

* Table: auth\_token
  Stores bearer token and expiry

* Table: url\_mapping
  Stores original and shortened URLs

Example:

```sql
CREATE TABLE auth_token (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  token VARCHAR(1000),
  expires_at DATETIME(6)
);

CREATE TABLE url_mapping (
  shortcode VARCHAR(20) PRIMARY KEY,
  original_url TEXT,
  created_at DATETIME,
  expiry DATETIME,
  redirect_count INT,
  last_accessed DATETIME
);
```

---

## 🔐 External Authentication

At app startup and when token expires, this service makes a POST request to:

POST [http://20.244.56.144/evaluation-service/auth](http://20.244.56.144/evaluation-service/auth)

With JSON body:

```json
{
  "email": "utsavrajput16002@gmail.com",
  "name": "utsav chauhan",
  "rollNo": "2200270100187",
  "accessCode": "secret",
  "clientID": "secret",
  "clientSecret": "secret"
}
```

Token is saved to DB with expiration and reused on future requests.

---

## 🧾 API Endpoints

### 1. Shorten URL

POST /shorturls

Request:

```json
{
  "url": "https://example.com",
  "validity": 30,
  "shortcode": "custom12"
}
```

Response:

```json
{
  "shortLink": "http://localhost:8080/custom12",
  "expiry": "2025-07-15T14:30:00"
}
```

—

### 2. Redirect to Long URL

GET /{shortcode}

Redirects to original URL if not expired. Returns 404 if not found or expired.

—

### 3. Analytics

GET /shorturls/{shortcode}/stats

Response:

```json
{
  "shortcode": "custom12",
  "originalUrl": "https://example.com",
  "createdAt": "...",
  "expiry": "...",
  "redirectCount": 3,
  "lastAccessed": "..."
}
```

—

## 🪵 Logging System

Every request is logged to:

POST [http://20.244.56.144/evaluation-service/logs](http://20.244.56.144/evaluation-service/logs)

With Authorization: Bearer {access\_token}

and body:

```json
{
  "log": "Log(stack=UrlShortener, level=INFO, package=/shorturls, message=POST http://localhost:8080/shorturls)"
}
```

—

## 🧪 Test with Postman

Required Headers:

* Content-Type: application/json

—

## 🛠 Setup

1. Clone repo
2. Configure MySQL in application.properties
3. Run the app:
   ./mvnw spring-boot\:run

MySQL config:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/url_db
spring.datasource.username=root
spring.datasource.password=yourpassword
spring.jpa.hibernate.ddl-auto=update
```

—

## ✅ Evaluation Ready

* Token-based client auth ✅
* Secure logging ✅
* URL analytics ✅
* API documentation ✅
* Postman-compatible ✅
* No console logging ❌ (only custom logging middleware)
* Lightweight, testable service ✅

—

## 👨‍💻 Author

Utsav Chauhan
Email: [utsavrajput16002@gmail.com](mailto:utsavrajput16002@gmail.com)
Roll No: 2200270100187
