# 📦 Order Event Service

Skalowalna aplikacja do obsługi masowych zdarzeń zamówień z e-commerce  
**Kafka + Spring Boot + PostgreSQL + Runtime Performance Management**

---

## 🎯 Realizacja Wymagań Zadania


### ✅ Wymagania Funkcjonalne

| Wymaganie | Status | Implementacja |
|-----------|--------|---------------|
| Responsywność przy dużym wolumenie | ✅ | Asynchroniczne przetwarzanie przez Kafka |
| Audit log wszystkich żądań | ✅ | Append-only model w PostgreSQL (order_event_audit) |
| Mock wysyłki e-mail | ✅ | Dedykowane handlery z logowaniem pełnej treści wiadomości |
| Kontrola wydajności | ✅ | Runtime settings w bazie danych (bez restartu aplikacji) |


### ✅ Wymagania Techniczne

| Technologia | Wersja | Zastosowanie |
|-------------|--------|--------------|
| Java | 17 | Backend aplikacji |
| Spring Boot | 3.x | Framework webowy + Kafka integration |
| Apache Kafka | Latest | Message broker (producer/consumer) |
| PostgreSQL | 15 | Relacyjna baza danych |
| Docker | - | Konteneryzacja całego stacku |
| JUnit 5 + Mockito | - | Testy jednostkowe (5 handlerów) |

---

## 🛠️ Technologie

**Stack:**

- Java 17
- Spring Boot 3
- Apache Kafka
- PostgreSQL
- Docker
- JUnit 5
- Mockito

---

## 🧭 Diagram Architektury

📐 **Pełny diagram architektury aplikacji (Mermaid):**  
👉 https://github.com/przemyslawwlodarczyk90/order-event-service/blob/master/diagramAplikacji.md

ℹ️ Diagram jest zapisany w formacie **Mermaid** – kod źródłowy można skopiować i uruchomić w trybie pełnoekranowym np. w **https://mermaid.live** (obsługa zoomu i eksport do SVG/PNG).


---

## 🏗️ Architektura Rozwiązania
```
┌─────────────────┐
│   REST API      │  POST /api/orders
│  (Controller)   │  PATCH /api/orders/{id}/status/*
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│ Kafka Producer  │  Wysyła event do topicu "order-events"
└────────┬────────┘
         │
         ▼
    ╔═══════════╗
    ║   KAFKA   ║  Topic: order-events (async processing)
    ║  BROKER   ║  
    ╚═══════════╝
         │
         ▼
┌─────────────────┐
│ Kafka Consumer  │  @KafkaListener + runtime-configurable concurrency
└────────┬────────┘
         │
         ├─────────────────────┐
         ▼                     ▼
┌─────────────────┐   ┌─────────────────┐
│  Order Handler  │   │  Email Handler  │
│   (Business)    │   │  (Notification) │
└────────┬────────┘   └─────────────────┘
         │
         ▼
┌─────────────────┐
│   PostgreSQL    │  order_event_audit (append-only)
│  (Audit Log)    │  + system_settings (runtime config)
└─────────────────┘
```

---

## 🚀 Kluczowe Cechy Rozwiązania

### 1️⃣ Asynchroniczne Przetwarzanie z Kafka

REST API natychmiast zwraca **202 Accepted** po wysłaniu eventu do Kafki. Rzeczywiste przetwarzanie odbywa się w tle przez Kafka Consumer.
```java
@PostMapping
public ResponseEntity<Void> receiveOrderEvent(@Valid @RequestBody OrderRequestDto request) {
    producer.sendOrderEvent(request.getShipmentNumber(), request);
    return ResponseEntity.accepted().build(); // 🔥 Natychmiastowa odpowiedź
}
```

**Efekt:** Pełna responsywność aplikacji niezależnie od wolumenu żądań.

---

### 2️⃣ Append-Only  Audit Log

Każda zmiana statusu zamówienia = **nowy rekord** w bazie. Brak UPDATE - tylko INSERT.

**Przykład historii zamówienia SHIP-123:**

| ID | shipment_number | status_code | received_at |
|----|-----------------|-------------|-------------|
| 1 | SHIP-123 | 10 (ORDER_ACCEPTED) | 2025-01-10 10:00:00 |
| 2 | SHIP-123 | 30 (PACKED_AND_SHIPPED) | 2025-01-10 14:30:00 |
| 3 | SHIP-123 | 80 (OUT_FOR_DELIVERY) | 2025-01-11 08:15:00 |
```sql
-- Query: Pełna historia zamówienia
SELECT * FROM order_event_audit 
WHERE shipment_number = 'SHIP-123' 
ORDER BY received_at ASC;
```

---

### 3️⃣ Walidacja Biznesowa

**Dozwolone przejścia statusów:**
```
ORDER_ACCEPTED (10)
    ↓
PACKED_AND_SHIPPED (30)
    ↓
OUT_FOR_DELIVERY (80)
```

**Reguły:**
- ❌ Nie można utworzyć zamówienia z statusem wysyłkowym
- ❌ Nie można przejść do OUT_FOR_DELIVERY bez PACKED_AND_SHIPPED
- ✅ Duplikat statusu = brak INSERT (idempotentność)

**Obsługa błędów w Kafka Consumer:**
```java
catch (InvalidOrderStatusTransitionException ex) {
    // BŁĄD BIZNESOWY - event jest niepoprawny
    log.warn("Business rule violation – event ignored");
    // Offset Kafki idzie dalej, event nie blokuje kolejki
}
catch (Exception ex) {
    // BŁĄD TECHNICZNY - Kafka robi retry
    throw ex;
}
```

---

### 4️⃣ Zarządzanie Wydajnością - Runtime

**Parametry dostępne w `/api/admin/settings`:**

| Parametr | Opis | Efekt |
|----------|------|-------|
| consumerConcurrency | Liczba równolegle przetwarzanych eventów | Kontrola obciążenia bazy danych |
| emailEnabled | Włączenie/wyłączenie notyfikacji | Natychmiastowe wstrzymanie wysyłki |
| emailRatePerSecond | Parametr przygotowany pod przyszły rate limiting | Konfiguracja dla limitu e-maili/s |

**Zmiana ustawień BEZ restartu aplikacji:**
```bash
curl -X PUT http://localhost:8080/api/admin/settings \
  -H "Content-Type: application/json" \
  -d '{
    "consumerConcurrency": 10,
    "emailEnabled": false,
    "emailRatePerSecond": 20
  }'
```

System odświeża konfigurację co 5 sekund z bazy danych.

---

### 5️⃣ Mock Wysyłki E-mail

**Dedykowane handlery dla każdego typu powiadomienia:**
- `OrderAcceptedEmailHandler` → Potwierdzenie przyjęcia
- `PackedAndShippedEmailHandler` → Informacja o wysyłce
- `OutForDeliveryEmailHandler` → Paczka w drodze

**Przykładowy log:**
```
Email content (MOCK):
----------------------------------------
To: customer@example.com

Dear Customer,

Your order with number SHIP-123 has been successfully registered.

Sender country code: DE
Recipient country code: PL
Current order status: 10

Thank you for your order.
----------------------------------------
```

---

## 🧪 Pokrycie Testami

### Testy Jednostkowe (JUnit 5 + Mockito)

| Handler | Test Coverage |
|---------|---------------|
| CreateOrderEventHandler | ✅ Poprawne utworzenie<br>✅ Duplikat zamówienia<br>✅ Niedozwolony status początkowy |
| PackAndShipOrderHandler | ✅ Poprawna zmiana statusu<br>✅ Idempotentność (już packed)<br>✅ Brak zamówienia |
| OutForDeliveryOrderHandler | ✅ Poprawna zmiana statusu<br>✅ Błędny poprzedni status<br>✅ Brak zamówienia |
| GetOrderEventsHandler | ✅ Zwracanie historii<br>✅ Brak zamówienia |
| GetAllOrderEventsHandler | ✅ Zwracanie wszystkich eventów |

**Przykład testu:**
```java
@Test
void shouldCreateOrderEvent_whenValidRequest() {
    // given
    OrderRequestDto request = new OrderRequestDto();
    request.setShipmentNumber("SHIP-123");
    request.setStatusCode(10);
    
    // when
    OrderEvent result = handler.handle(request);
    
    // then
    assertNotNull(result);
    assertEquals("SHIP-123", result.getShipmentNumber());
    verify(repository).save(any(OrderEvent.class));
}
```

---

## 📋 API - Endpointy

### 1. Przyjmowanie Nowego Zamówienia
```http
POST /api/orders
Content-Type: application/json

{
  "shipmentNumber": "SHIP-123456",
  "recipientEmail": "customer@example.com",
  "recipientCountryCode": "PL",
  "senderCountryCode": "DE",
  "statusCode": 10
}

Response: 202 Accepted
```

### 2. Aktualizacja Statusu: Packed & Shipped
```http
PATCH /api/orders/SHIP-123456/status/packed-and-shipped

Response: 202 Accepted
```

### 3. Aktualizacja Statusu: Out for Delivery
```http
PATCH /api/orders/SHIP-123456/status/out-for-delivery

Response: 202 Accepted
```

### 4. Historia Zamówienia (Audit)
```http
GET /api/orders/SHIP-123456/events

Response: 200 OK
[
  {
    "id": 1,
    "shipmentNumber": "SHIP-123456",
    "statusCode": 10,
    "receivedAt": "2025-01-13T10:00:00Z"
  },
  {
    "id": 2,
    "shipmentNumber": "SHIP-123456",
    "statusCode": 30,
    "receivedAt": "2025-01-13T14:30:00Z"
  }
]
```

### 5. Wszystkie Eventy (Global Audit)
```http
GET /api/orders/events

Response: 200 OK
```

### 6. Zarządzanie Wydajnością (Admin)
```http
GET /api/admin/settings
PUT /api/admin/settings
```

---

## 🐳 Uruchomienie Lokalne (Docker Compose)

**Wymagania:**
- Docker
- Docker Compose

**Kroki:**
```bash
# 1. Sklonuj repozytorium
git clone <repository-url>
cd order-event-service

# 2. Uruchom cały stack
docker-compose up -d

# Stack zawiera:
# - PostgreSQL (port 5432)
# - Zookeeper (port 2181)
# - Kafka (port 9092)
# - Spring Boot App (port 8080)

# 3. Sprawdź logi aplikacji
docker-compose logs -f app

# 4. Testuj API
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "shipmentNumber": "SHIP-TEST-001",
    "recipientEmail": "test@example.com",
    "recipientCountryCode": "PL",
    "senderCountryCode": "DE",
    "statusCode": 10
  }'
```

---

## 📊 Struktura Projektu
```
src/main/java/com/example/order_event_service/
├── controller/
│   ├── AdminSettingsController.java       # Runtime settings
│   ├── OrderEventQueryController.java     # GET endpoints (audit)
│   ├── OrderReceiverController.java       # POST endpoint
│   └── OrderStatusUpdateController.java   # PATCH endpoints
├── service/
│   ├── OrderEventFacade.java              # Orchestration
│   └── handler/
│       ├── CreateOrderEventHandler.java
│       ├── PackAndShipOrderHandler.java
│       ├── OutForDeliveryOrderHandler.java
│       ├── GetOrderEventsHandler.java
│       └── GetAllOrderEventsHandler.java
├── kafka/
│   ├── OrderEventKafkaProducer.java
│   └── OrderEventKafkaConsumer.java
├── notification/
│   ├── EmailNotificationFacade.java
│   └── handler/
│       ├── OrderAcceptedEmailHandler.java
│       ├── PackedAndShippedEmailHandler.java
│       └── OutForDeliveryEmailHandler.java
├── settings/
│   ├── SystemSettings.java                # Entity
│   ├── SystemSettingsService.java         # Business logic
│   ├── SettingsCache.java                 # Memory cache
│   └── SettingsInitializer.java           # Startup init
├── entity/
│   └── OrderEvent.java                    # Audit log entity
├── repository/
│   ├── OrderEventRepository.java
│   └── SystemSettingsRepository.java
├── dto/
│   ├── OrderRequestDto.java
│   ├── OrderStatusUpdateDto.java
│   └── SettingsDto.java
├── domain/
│   └── OrderStatus.java                   # Enum (10, 30, 80)
├── util/
│   └── Validator.java                     # Business rules
└── exception/
    ├── InvalidOrderStatusTransitionException.java
    └── OrderNotFoundException.java
```

---

## 📝 Podsumowanie Realizacji

| Obszar | Rozwiązanie |
|--------|-------------|
| Responsywność | Kafka + async processing (202 Accepted) |
| Audit | Append-only PostgreSQL (pełna historia) |
| E-mail | Mock handlery z logowaniem treści |
| Wydajność | Runtime settings (bez restartu!) |
| Walidacja | State machine + idempotentność |
| Błędy | Rozróżnienie biznesowe vs techniczne |
| Testy | JUnit 5 + Mockito (5 handlerów, 13 testów) |
| Infrastruktura | Docker Compose (app + Kafka + PostgreSQL) |

---

## 🎓 Kluczowe Decyzje Projektowe

### Dlaczego Kafka?
- Persystencja eventów (replay możliwy)
- Naturalne backpressure
- Skalowalność przez partycje

### Dlaczego Append-Only?
- Pełna historia zamówienia
- Brak konfliktów UPDATE
- Event-driven persistence (inspired by Event Sourcing)

### Dlaczego Runtime Settings?
- Zmiana bez restartu aplikacji
- Reakcja na skoki ruchu
- Persystencja konfiguracji

### Dlaczego rozróżnienie błędów?
- Błędne eventy nie blokują kolejki
- Techniczne problemy mają retry
- Stabilność systemu

---

## 📄 Licencja

MIT License - projekt edukacyjny
