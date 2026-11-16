# Order Event Service

Ćwiczenie z przetwarzania zdarzeń w Spring Boot i Kafce na przykładzie zamówień. API przyjmuje zamówienie i zmiany jego statusu, Kafka przekazuje zdarzenia do przetworzenia, a PostgreSQL przechowuje historię. Treść przykładowych powiadomień e-mail trafia do logów.

[Diagram przepływu zdarzeń](diagramAplikacji.md)

## API

| Metoda i ścieżka | Działanie |
| --- | --- |
| `POST /api/orders` | Przyjęcie zamówienia. |
| `PATCH /api/orders/{shipmentNumber}/status/packed-and-shipped` | Oznaczenie przesyłki jako spakowanej i wysłanej. |
| `PATCH /api/orders/{shipmentNumber}/status/out-for-delivery` | Oznaczenie przesyłki jako będącej w doręczeniu. |
| `GET /api/orders/{shipmentNumber}/events` | Historia zdarzeń dla przesyłki. |
| `GET /api/orders/events` | Lista zapisanych zdarzeń. |
| `GET /api/admin/settings` i `PUT /api/admin/settings` | Odczyt i zmiana ustawień w trakcie działania. |

## Uruchomienie

Potrzebujesz JDK 17, Dockera i Docker Compose. W PowerShell:

```powershell
docker compose up -d
.\mvnw.cmd spring-boot:run
```

Na Linuxie lub macOS użyj `./mvnw spring-boot:run` po uruchomieniu kontenerów. Swagger UI jest dostępny pod `http://localhost:8081/swagger-ui.html`. Testy jednostkowe obsługi zdarzeń uruchomisz poleceniem `.\mvnw.cmd test`.
