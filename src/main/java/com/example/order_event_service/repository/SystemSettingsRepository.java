package com.example.order_event_service.repository;

import com.example.order_event_service.settings.SystemSettings;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SystemSettingsRepository extends JpaRepository<SystemSettings, Long> {

    /**
     * Pobiera rekord ustawień i nakłada blokadę PESSIMISTIC_WRITE.
     * Dzięki temu baza danych "zarezerwuje" ten wiersz dla aktualnej transakcji,
     * a inne próby edycji będą musiały poczekać na jej zakończenie.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM SystemSettings s WHERE s.id = 1")
    Optional<SystemSettings> findAndLockById1();
}