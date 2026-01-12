package com.example.order_event_service.settings;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * ============================================================================
 * SYSTEM SETTINGS – RUNTIME STEROWANIE WYDAJNOŚCIĄ
 * ============================================================================
 *
 * Ta encja reprezentuje JEDYNY rekord konfiguracyjny w systemie (ID = 1),
 * który pełni rolę centralnego punktu sterowania wydajnością aplikacji.
 *
 * Rekord:
 * - jest tworzony automatycznie przy starcie aplikacji,
 * - NIE może być usunięty ani dodany ponownie,
 * - może być jedynie odczytywany i aktualizowany.
 *
 * Ustawienia są:
 * - zapisywane w bazie danych,
 * - okresowo cache’owane w pamięci aplikacji,
 * - używane przez konsumenta Kafki do dynamicznego throttlingu.
 *
 * Dzięki temu system może reagować na zmienne obciążenie
 * (np. wzrost z 1 mln do 5 mln eventów dziennie)
 * BEZ restartu aplikacji i BEZ zmian w kodzie.
 */
@Entity
@Table(name = "system_settings")
@Getter
@Setter
public class SystemSettings {

    /**
     * Stałe ID = 1.
     * Gwarantuje, że w systemie istnieje dokładnie jeden rekord ustawień.
     */
    @Id
    private Long id = 1L;

    /**
     * RUNTIME PARAMETR
     * ----------------
     * Określa maksymalną liczbę eventów przetwarzanych RÓWNOLEGLE
     * przez konsumenta Kafki.
     *
     * Technicznie:
     * - używany do sterowania Semaphore w konsumencie,
     * - działa jako "kurek" chroniący bazę danych.
     *
     * Wpływ na wydajność:
     * - zwiększenie → większa przepustowość,
     * - zmniejszenie → mniejsze obciążenie DB.
     *
     * Może być zmieniany w runtime (bez restartu).
     */
    private int consumerConcurrency = 2;

    /**
     * RUNTIME PARAMETR
     * ----------------
     * Globalny przełącznik wysyłki e-maili.
     *
     * Pozwala administratorowi:
     * - natychmiast wyłączyć wysyłkę e-maili,
     * - chronić zewnętrzny system (SMTP / API),
     * - zachować przetwarzanie eventów bez notyfikacji.
     *
     * Zmiana działa natychmiast.
     */
    private boolean emailEnabled = true;

    /**
     * RUNTIME PARAMETR
     * ----------------
     * Limit wysyłanych e-maili na sekundę.
     *
     * Implementowany jako RateLimiter (np. Semaphore / Guava).
     *
     * Wpływ na wydajność:
     * - chroni system e-mailowy przed przeciążeniem,
     * - zapobiega efektowi "mail storm".
     *
     * Może być dynamicznie zmieniany w runtime.
     */
    private int emailRatePerSecond = 5;

    /**
     * Pole wersji dla mechanizmu optimistic locking.
     *
     * Nie jest wymagane przy pessimistic locku,
     * ale stanowi dodatkowe zabezpieczenie spójności danych
     * w przypadku nieprawidłowego użycia encji.
     */
    @Version
    private Long version;


    /**
    ============================================================================
    PARAMETRY NIERUNTIME – KONFIGURACJA KAFKI I INFRASTRUKTURY
    ============================================================================

    Poniższe parametry NIE są zarządzane przez SystemSettings,
    ponieważ Kafka oraz pule połączeń DB inicjalizują je przy starcie aplikacji.

    Zmiana ich wymaga restartu aplikacji lub zmiany infrastruktury.

    Kafka Producer (transport danych):
    - batch.size        → wielkość paczek eventów
    - linger.ms         → czas zbierania batcha
    - compression.type  → kompresja danych
    - acks              → poziom potwierdzeń

    Kafka Topic (infrastruktura):
    - liczba partycji   → główny mechanizm skalowania Kafki

    Baza danych:
    - hikari.maximum-pool-size
    - hikari.minimum-idle
    - connection-timeout

    Te parametry definiują MAKSYMALNY throughput systemu,
    ale nie służą do reagowania na chwilowe skoki ruchu.
    */


    /**
    ============================================================================
    WPŁYW RUNTIME SETTINGS Z BAZY DANYCH NA WYDAJNOŚĆ SYSTEMU
    ============================================================================

    Runtime settings pełnią rolę mechanizmu BACKPRESSURE.

    Zasada działania:
    1. Kafka może przyjąć bardzo duży wolumen eventów.
    2. Konsument pobiera je zgodnie z liczbą partycji.
    3. Semaphore (consumerConcurrency) ogranicza realne przetwarzanie.
    4. Baza danych jest chroniona przed przeciążeniem.
    5. Administrator może dynamicznie sterować przepustowością systemu.

    Efekt:
    - brak utraty danych,
    - brak restartów aplikacji,
    - kontrolowane zużycie zasobów,
    - skalowanie z 1 mln do 5 mln eventów dziennie
      bez zmian w kodzie.
    */
}
