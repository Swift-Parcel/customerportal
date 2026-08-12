# SwiftParcel Customer Portal API (Java Team)

Customer-facing backend for SwiftParcel: registration & authentication, pricing & quotes,
pickup scheduling, parcel tracking, delivery management, complaints/cases, and notification
preferences. It integrates with the C# Back-Office (system of record for parcels and cases)
over REST + webhooks.

---

## Tech Stack

- **Language / Framework:** Java 25, Spring Boot 4.1.0
- **Build tool:** Maven
- **Database:** PostgreSQL (schema managed with Flyway)
- **Cache:** Redis (optional caching layer)
- **Security:** Spring Security, JWT (access + refresh tokens), API-key filter for webhooks
- **API docs:** springdoc-openapi (Swagger UI)
- **Containerization:** Docker & Docker Compose

---

## Getting Started

### Prerequisites
- Docker Desktop / Docker Engine with Compose

### Run

```bash
docker compose -f compose.prod.yaml up --build
```
API available at http://localhost:8080

On startup, Flyway applies all migrations in `src/main/resources/db/migration`.

## Services & Ports

| Service | Host Port | Description               |
| :--- | :--- |:--------------------------|
| Customer Portal API | 8080 | Main Spring Boot REST API |
| PostgreSQL | 5432 | Database `customerportal` |

---

## API Documentation

With the application running:
- **Swagger UI:** http://localhost:8080/swagger-ui-custom.html
- **OpenAPI JSON:** http://localhost:8080/api-docs

---

## Authentication & Integrations

### Customer authentication (portal users)
JWT-based. Obtain a token via login, then send `Authorization: Bearer <accessToken>` on all
customer endpoints.

| Setting | Value |
| :--- | :--- |
| Login | `POST /api/customerportal/auth/login` |
| Refresh | `POST /api/customerportal/auth/refresh` |
| Logout | `POST /api/customerportal/auth/logout` |
| Access token TTL | 15 minutes |
| Refresh token TTL | 7 days |
| Issuer | `swiftparcel-customer-portal` |
| Secret | env `JWT_SECRET` (dev default in `application.yaml`) |

### Inbound webhooks (C# Back-Office → Java)
Webhook receivers live under `/api/webhooks/**` and are protected by an API-key filter
(separate from JWT).

- **Header:** `x-api-key`
- **Config key:** `app.key.api-key`
- **Dev value:** `550e8400-e29b-41d4-a716-446655440000`

### Outbound integration (Java → C# Back-Office)
Calls to the C# Back-Office authenticate with the shared secret agreed with the C# team.

- **Header:** `X-Api-Key`
- **Secret value:** `SwiftParcel_Java_Integration_Shared_Secret_2026!`
- **Base URL:** `http://localhost:3500` (config `app.backoffice.base-url`)
- **Config key:** `app.backoffice.api-key`


## Database Schema (owned tables)

The portal is greenfield and owns the following tables. It stores **references and thin caches**
for parcel/case data — the C# Back-Office remains the system of record for those.

| Domain | Table | Purpose |
| :--- | :--- | :--- |
| Identity | `customer` | Accounts, hashed credentials, profile, preferred language, default address |
| Identity | `address` | Customer/pickup addresses (city, postal code, country code) |
| Auth | `refresh_token` | Persisted refresh tokens for JWT session management |
| Pricing | `region` | Known regions (code, city, country, timezone) |
| Pricing | `route` | Zone multipliers (SAME_CITY / SAME_COUNTRY / CROSS_COUNTRY) |
| Pricing | `service_rate` | Base price, per-kg rate, surcharges, cut-off rules per service type |
| Pricing | `quote` | Persisted price snapshots hanging off a pickup request |
| Pickup | `pickup_request` | Pickup lifecycle (Draft → Quoted → Confirmed → Submitted → …) |
| Parcels | `parcel` | Local parcel reference / cached status linked to a customer |
| Cases | `complaint_cases` | Local case reference (case number → customer, cached status, feedback) |
| Delivery | `delivery_change_request` | Delivery-change lifecycle (Requested → Pending Review → Approved/Rejected) |
| Notifications | `notification_preference` | Per-customer notification toggles |
| Notifications | `notifications` | Generated notification records (sent/pending) |

---

## Seeded / Test Data

Applied automatically by Flyway on a clean database.

### Customers
| Email | Name | Language |
| :--- | :--- | :--- |
| diego.santos@gmail.com | Diego Santos | en |
| anna.kovacs@example.com | Anna Kovács | hu |
| marek.wojcik@example.pl | Marek Wójcik | pl |

> Passwords for all those users are `Test1234!`
### Zone multipliers (`route`)
| Route Type | Multiplier |
| :--- | :--- |
| SAME_CITY | 1.00 |
| SAME_COUNTRY | 1.20 |
| CROSS_COUNTRY | 1.80 |

### Service rates (`service_rate`)
| Service | Base | Per-kg | Surcharge | Rules |
| :--- | :--- | :--- | :--- | :--- |
| STANDARD | €5.00 | €1.50 | +€2.00 over 10 kg | — |
| EXPRESS | €12.00 | €2.50 | +€5.00 over 10 kg | ≥120 min before slot |
| SAME_DAY | €25.00 | €4.00 | +€10.00 over 5 kg | cut-off 10:00, no cross-country |

### Regions (`region`)
`HU-BUD` Budapest · `HU-DEB` Debrecen · `HU-SZE` Szeged · `AT-VIE` Vienna · `AT-GRZ` Graz ·
`CZ-PRG` Prague · `CZ-BRN` Brno · `PL-WAW` Warsaw · `PL-KRK` Krakow · `DE-BER` Berlin · `SK-BTS` Bratislava

---

## Design Decisions, Architecture & Trade-offs

**Ownership boundary.** The C# Back-Office is the system of record for parcels and cases. The portal
stores only references and thin caches (`parcel`, `complaint_cases`, `delivery_change_request`) plus
the data it genuinely owns (accounts, pricing, pickups, quotes, preferences). Read-heavy parcel/case
data is fetched live from C# rather than duplicated.

**Authentication split.** Customer endpoints use JWT (short-lived access token + persisted refresh
token). Webhook receivers under `/api/webhooks/**` use a separate `x-api-key` filter, so the two auth
schemes never collide and the security config stays declarative by path prefix.

**Integration auth.** Outbound calls to C# carry the shared `X-Api-Key` secret; inbound webhooks are
validated against the portal's own `x-api-key`. Webhooks are treated as best-effort, with C# lookups
as the fallback for anything a missed webhook would have delivered.

**Pricing is data-driven.** Base prices, per-kg rates, surcharges, and zone multipliers live in
`service_rate` / `route` so rates change without code edits.

**Quote as a snapshot.** A quote is persisted against a specific pickup (`quote.pickup_request_id`),
so pickups are quoted first (Draft → Quoted → Confirmed) and each quote is a frozen, time-limited
(24h) snapshot rather than a free-floating record.

**Delivery change.** Creation is synchronous: validate, call C# to open a `DELIVERY_CHANGE` case,
store the returned case number, and move the local record to `PENDING_REVIEW`. Resolution arrives by
webhook (`/api/webhooks/cases/delivery-change`); the customer to notify is resolved from the local
record by case number, since the webhook payload carries no email.

**Object-level authorization.** Customer identity is taken from the JWT principal, and resource
access is checked against ownership (e.g. a case's customer email) — not from client-supplied path or
body parameters — to prevent IDOR.


---
