package com.example.zonezero.repository;

import com.example.zonezero.model.Alert;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AlertRepository extends JpaRepository<Alert, Long> {
    List<Alert> findByRegionIgnoreCase(String region);
}
