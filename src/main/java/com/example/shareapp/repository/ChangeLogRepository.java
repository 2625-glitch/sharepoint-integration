package com.example.shareapp.repository;

import com.example.shareapp.model.ChangeLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ChangeLogRepository extends JpaRepository<ChangeLog, Long> {
    @Query("SELECT c FROM ChangeLog c WHERE c.itemId = :itemId")
    Optional<ChangeLog> findByItemId(String itemId);

}
