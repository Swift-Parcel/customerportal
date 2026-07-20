# SwiftParcel Customer Portal API (Java Team)

## Project Overview

This is one half of the SwiftParcel internship project. Two teams are building two backend applications that must integrate with each other:

| Team | Application | Tech | Role |
|------|-------------|------|------|
| **C# Team** | Back-Office API | C# / .NET | Backend for handlers, supervisors, admins. Owns case & parcel data. |
| **Java Team (you)** | Customer Portal API | Java / Spring Boot | Backend for senders & recipients. Owns customer accounts, pricing, pickup scheduling, and delivery preferences. |

Both applications are **REST APIs only** — no frontend work. A frontend may be built separately by mentors after your API design phase. Treat this like working with an off-shore frontend team: they will consume your APIs with minimal coordination, so your endpoints must be well-documented, self-explanatory, and return clear error messages.

**Both teams share a 3–4 week timeline.** You must agree on API contracts with the C# team early (ideally end of week 1) so integration work can proceed in parallel.

## Background

SwiftParcel is a mid-size parcel delivery company operating across Central Europe handling ~50,000 parcels/day. Customers (both senders and recipients) currently have no self-service option — they must call or email customer support for everything: tracking a parcel, changing a delivery date, filing a complaint.

Your team is building the **Customer Portal API** — a backend that exposes REST endpoints for customer self-service capabilities. This reduces the load on the customer support team and gives customers faster, 24/7 access to their shipment information through whatever frontend is built on top.

---

## Your Database

Unlike the C# team (who must fix a legacy database), you are building **greenfield** — design your own database schema from scratch. You own the following data domains:

| Domain | What You Store | Why It's Yours |
|--------|---------------|----------------|
| **Customer Accounts** | Registration, credentials (hashed!), profiles, preferences | The C# backend has no concept of customer login — authentication is your responsibility. |
| **Pricing & Quotes** | Pricing zones, weight tiers, service type rates, surcharges, generated quotes | Pricing logic lives in your app. The C# backend only stores the final declared value after pickup is confirmed. |
| **Pickup Requests** | Draft pickups, scheduled pickups, pickup time slots, pickup history | Pickups go through a lifecycle in your app (draft → quoted → confirmed → sent to C# backend). You only notify the C# backend once a pickup is confirmed. |
| **Delivery Preferences** | Default safe-place instructions, preferred time slots, address book | Customer self-service preferences — the C# backend doesn't need these until a delivery is scheduled. |
| **Notification Settings** | Per-customer preferences: which events to notify, via which channel | You decide when and how to notify. You may call the C# backend to check for status changes, then notify based on customer preferences. |
| **Delivery Change Requests** | Request lifecycle: created → sent to backend → approved/rejected | You own the customer-facing lifecycle. The C# backend creates a case for it; you poll the case status and update your local record. |

**Parcel tracking data and case/complaint data still comes from the C# backend** — you don't duplicate it. You call their APIs and may cache responses briefly for performance.

Design your schema properly from day one: use appropriate data types, foreign keys, constraints, indexes. This is a greenfield project — there is no excuse for a messy schema.

---

## Application Requirements

Requirements are split into **Required** (must be completed) and **Optional** (stretch goals if time permits).

### REQUIRED — Customer Accounts & Authentication

1. **Registration** — Endpoint for customer registration: email (unique), full name, phone number, password. Store credentials securely (hashed + salted — never plaintext). Upon successful registration, also create the customer record in the C# backend via API so they appear in the back-office system.

2. **Login / Logout** — Authentication endpoints. Use JWT tokens for session management. Implement token expiry and refresh.

3. **Profile Management** — Endpoints to view and update customer profile: name, phone number, default address, preferred language.

### REQUIRED — Parcel Tracking

4. **Track by Tracking Number** — Public endpoint (no authentication required) that accepts a tracking number (format: `SP-XXXXXXXX`) and returns the current parcel status and tracking history. Data comes from the C# backend.

5. **My Parcels** — Authenticated endpoint returning all parcels associated with the customer's email (as sender or recipient). Each entry includes: tracking number, status, sender/recipient, created date, and service type.

6. **Parcel Detail** — Authenticated endpoint returning full parcel details: current status, tracking history (status changes with timestamps), estimated delivery date, sender, recipient, weight, service type, and declared value.

### REQUIRED — Pricing & Quotes

7. **Price Calculator** — Endpoint that calculates a shipping price quote. Input: origin region, destination region, weight (kg), dimensions (cm), service type (Standard / Express / Same-Day). Output: calculated price in EUR with breakdown.

   **Pricing logic (you implement this):**

   | Service Type | Base Price | Per-kg Rate | Surcharges |
       |-------------|------------|-------------|------------|
   | Standard | €5.00 | €1.50/kg | +€2.00 if weight > 10kg |
   | Express | €12.00 | €2.50/kg | +€5.00 if weight > 10kg |
   | Same-Day | €25.00 | €4.00/kg | +€10.00 if weight > 5kg, not available cross-country |

   **Zone-based pricing:**

   | Route | Multiplier |
       |-------|-----------|
   | Same city (e.g., Budapest → Budapest) | 1.0x |
   | Same country (e.g., Budapest → Debrecen) | 1.2x |
   | Cross-country (e.g., Budapest → Vienna) | 1.8x |

   The pricing tables (base prices, per-kg rates, surcharges, zone multipliers) must be stored in your database so they can be changed without code modifications.

8. **Quote History** — Endpoint returning customer's past quotes (last 30 days). A quote can be converted into a pickup request within 24 hours of creation — after that, the price may have changed and a new quote is needed.

### REQUIRED — Pickup Requests

9. **Request Pickup** — Endpoint for creating a pickup request. Input:
    - Sender address (default from profile, editable)
    - Recipient name and address
    - Parcel weight (kg) and dimensions (L×W×H in cm)
    - Service type: Standard, Express, Same-Day
    - Declared value (EUR)
    - Preferred pickup date and time slot (Morning 08–12, Afternoon 12–17, Evening 17–20)

   **Pickup lifecycle (you manage this in your database):**
    ```
    Draft → Quoted → Confirmed → Submitted to C# Backend → Tracking Number Assigned → Picked Up
    ```
    - **Draft:** Customer has filled in the data but not yet requested a quote.
    - **Quoted:** Price has been calculated. Customer has 24 hours to confirm.
    - **Confirmed:** Customer accepted the price. Payment is captured (simulated — just mark as paid).
    - **Submitted:** You send the pickup details to the C# backend, which creates the parcel record and returns a tracking number.
    - **Tracking Number Assigned:** Tracking number received from C# backend, stored and returned to customer.
    - **Picked Up:** C# backend notifies you that the courier has collected the parcel.

10. **Pickup Validation Rules** (business logic you enforce):
    - Same-Day service: pickup must be requested before 10:00 AM on the same day.
    - Same-Day service: not available for cross-country routes.
    - Express service: pickup must be requested at least 2 hours before the time slot starts.
    - Maximum parcel weight: 30kg. Maximum single dimension: 120cm.
    - Declared value cannot exceed €5,000.
    - A customer cannot have more than 5 unconfirmed (Draft/Quoted) pickup requests at a time.

### REQUIRED — Delivery Management

11. **View Delivery Schedule** — Endpoint returning the estimated delivery date and time slot for a given in-transit parcel. Data comes from the C# backend.

12. **Request Delivery Date Change** — Endpoint for recipients to request a different delivery date/time slot. The request goes through a lifecycle in your database:
    ```
    Requested → Sent to C# Backend → Pending Review → Approved / Rejected
    ```
    You send the request to the C# backend, which creates a `DELIVERY_CHANGE` case. You periodically poll the case status (or receive a webhook) and update your local record when it's resolved.

    **Validation:** A customer can have only one pending delivery change request per parcel. If the previous one was rejected, they may submit a new one.

13. **Confirm Delivery** — Endpoint for recipients to confirm they received a parcel. Sends a confirmation to the C# backend which updates the parcel status.

### REQUIRED — Complaints & Cases

14. **File a Complaint** — Endpoint for creating a complaint:
    - Tracking number(s) — one or more, validated against `SP-XXXXXXXX` format
    - Case type: Lost parcel, Damaged parcel, Delayed delivery, Wrong address, Billing issue, Other
    - Description — free text

    Submitted to the C# backend, which creates a case in the back-office system.

15. **List My Cases** — Endpoint returning all customer's cases: case number, type, status, created date, last update. Data from C# backend.

16. **Case Detail & Messaging** — Endpoint returning case details and customer-visible notes. Separate endpoint for adding a customer message to an existing case.

17. **Case Feedback** — Endpoint for submitting satisfaction score (1–5) and optional comment after resolution. Store locally that feedback was submitted (prevent double submission even if the C# backend is temporarily down).

### REQUIRED — Notification Preferences

18. **Notification Settings** — Endpoints to view and update notification preferences per customer:

    | Event | Default |
        |-------|---------|
    | Parcel status changed | On |
    | Delivery arriving today | On |
    | Case status updated | On |
    | Delivery change request resolved | On |
    | Pickup confirmed / tracking assigned | On |
    | Price quote expiring soon (20h mark) | Off |

    Notification channel: Email only (for required scope). Store preferences in your database.

19. **Notification Generation** — When a relevant event occurs (you detect this by polling the C# backend for changes, or via webhook from the C# app), check the customer's preferences and generate a notification record. For the required scope, just store the notification in the database with a `sent/pending` status — actually sending emails is optional.

### REQUIRED — Integration with Back-Office (C# Team)

Your application communicates with the C# team's Back-Office system. Both teams design the API contracts together during week 1.

**Data you read from the C# backend (you call them):**

- Parcel status, tracking history, and delivery schedule for a given tracking number
- Case status and customer-visible notes for a given case number
- List of parcels associated with a customer email

**Actions you trigger on the C# backend (you call them):**

- Submit a confirmed pickup request (send parcel details, receive tracking number back)
- Confirm delivery for a parcel (recipient confirms receipt)
- Create a new complaint/case (customer files a complaint)
- Add a customer note to an existing case
- Submit satisfaction feedback on a resolved case
- Request a delivery date/time change (C# creates a `DELIVERY_CHANGE` case)

**Events the C# backend sends to you (they call you):**

- Parcel status changed (picked up, in transit, delivered, etc.) — you update cached data and trigger notifications based on customer preferences.
- Case status changed (resolved, closed, etc.) — you update delivery change request status if applicable and trigger notifications.
- Delivery change request resolved (approved/rejected) — you update your local record and notify the customer.

**Integration ground rules:**
- Both teams agree on API contracts (request/response schemas, endpoint paths, authentication) by end of **week 1**. You design them together.
- Use JSON over HTTP for all integration.
- Authentication between the two apps: use a shared API key in a request header.
- If the C# backend is unavailable, return appropriate error responses. For pickup submissions, queue them locally and retry when the backend is back.
- Webhooks are best-effort — design your system to work even if a webhook is missed (use polling as a fallback).

---

### OPTIONAL — Redis Caching

20. **Caching with Redis** *(optional)* — Introduce Redis as a caching layer to reduce calls to the C# backend and improve response times. Candidates for caching:
    - Parcel status/tracking data (most frequently requested, changes infrequently relative to reads)
    - Case status lookups (customers query case status repeatedly)
    - Pricing tables (rarely change, read on every quote request)
    - Customer session data (offload from JWT verification on every request)

    Implement cache invalidation when you receive webhooks from the C# backend (parcel/case status changed → invalidate that entry). Use TTL-based expiry as a fallback for missed webhooks.

### OPTIONAL — Real-Time Events

21. **Event Streaming** *(optional)* — Expose a WebSocket or Server-Sent Events endpoint that pushes real-time updates to connected clients (parcel status changes, case updates) instead of requiring them to poll.

22. **Notification Delivery** *(optional)* — Actually send emails (or simulate sending) for generated notifications. Implement retry logic for failed deliveries.

    *Microservice note: Real-time event distribution and notification delivery are natural candidates for a dedicated "Notification Delivery Service." It would maintain connections with consumers, receive events from your main app (or a message queue), and deliver them via the appropriate channel (WebSocket, email, push). This separates stateful connection management and delivery retry logic from stateless business logic.*

### OPTIONAL — Enhanced Delivery Preferences

23. **Delivery Time Slot Selection** *(optional)* — When requesting a delivery change, provide available time slots (Morning, Afternoon, Evening) and accept customer's selection.

24. **Safe Place Instructions** *(optional)* — Endpoint for customers to set a default "safe place" for deliveries (e.g., "Leave with neighbor at #5"). Stored in your database, sent with delivery info to the C# backend when relevant.

### OPTIONAL — Address Book

25. **Saved Addresses** *(optional)* — Endpoints for CRUD on frequently used recipient addresses. Auto-suggest capability when creating pickup requests. Stored in your database.

### OPTIONAL — Localization

26. **Multi-Language Support** *(optional)* — API responses support HU, DE, CZ, PL, EN. Accept-Language header determines response language. All error messages and notification texts should be translatable.

### OPTIONAL — Reporting (Customer-Side)

27. **Shipping Statistics** *(optional)* — Endpoints providing customer-specific analytics: total parcels sent/received, spending by month, most-used routes, average delivery time by service type.

### OPTIONAL — Pricing as a Service

28. **Pricing Engine Extraction** *(optional)* — Refactor the pricing logic so it could function as an independent service. Expose a clean, separate API for price calculation that is decoupled from the pickup request flow. This means: the pricing engine has its own controller, its own data access, and could be deployed independently.

    *Microservice note: In a production architecture, the Pricing Service would be fully independent — owning its own database tables (rates, zones, surcharges), exposing a pricing API, and being called by both the Customer Portal and potentially other internal services (e.g., batch pricing for business customers, partner integrations). Think about: what's the contract? How do you version pricing rules? What happens if the pricing service is down — can you still accept pickups with cached prices?*

---

## Business Rules

- Customers are identified by email (unique).
- Tracking numbers follow the format `SP-XXXXXXXX` — validate before sending to backend.
- Only the sender or recipient of a parcel can file a complaint about it (match by email).
- Delivery change requests can only be made for parcels with status `in_transit`.
- Delivery confirmation can only be submitted by the recipient.
- Satisfaction feedback: once per case, only after status is `Resolved` or `Closed`.
- Pickup requests require all fields — no partial submissions.
- Quotes expire after 24 hours.
- Same-Day service unavailable for cross-country routes and after 10:00 AM.
- Max parcel weight: 30kg. Max single dimension: 120cm. Max declared value: €5,000.
- Max 5 unconfirmed pickup requests per customer.
- One pending delivery change request per parcel at a time.

## Technical Guidelines

- **Framework:** Java 21+ with Spring Boot
- **Build tool:** Maven
- **Database:** PostgreSQL. Design the schema yourself — proper types, FKs, constraints, indexes from the start.
- **API communication:** Use `RestTemplate` or `WebClient` to call the C# backend
- **Security:** Hash passwords (BCrypt). Never store plaintext. Validate all input. Sanitize all output.
- **Error handling:** Never expose stack traces to API consumers. Log errors internally. Return structured error responses with appropriate HTTP status codes.

## Your Task

**Phase 1 — Setup & API Design (Week 1)**
- Set up Spring Boot project (controllers, services, repositories, HTTP client)
- Design your database schema (customer accounts, pricing, pickups, delivery preferences, notifications)
- **Agree on API contracts with the C# team** — design endpoint paths, request/response schemas, and authentication together
- Build mock responses for C# endpoints so you can develop in parallel
- Implement customer registration, login, profile (JWT-based auth)

**Phase 2 — Core Features (Weeks 2–3)**
- Implement pricing engine and quote system
- Implement pickup request lifecycle (draft → confirmed → submitted)
- Implement parcel tracking endpoints
- Implement delivery management (schedule, change requests, confirmation)
- Implement complaint filing and case endpoints
- Implement notification preferences and notification generation
- Connect to real C# backend APIs as they become available

**Phase 3 — Integration & Polish (Week 3–4)**
- End-to-end integration testing with the C# team
- Handle edge cases: backend unavailable, expired quotes, invalid inputs, race conditions
- Tackle optional features if time permits
- Bug fixes

## Deliverables

1. Working Customer Portal API with all required features
2. Database schema DDL (your own tables)
3. API documentation (clear enough for the FE team and C# team to consume without asking questions)
4. Integration verified end-to-end with the C# backend
5. Brief document of design decisions, architecture, and trade-offs

## Notes

- Local infra setup is provided by mentors, if something is missing ask for it (Redis, DB, etc.)
- Quantity and Quality of deliverables are hand-in-hand, but a good working application outweighs a missing documentation
- FE may be implemented by mentors after the design phase ends (as mentioned in the overview)
