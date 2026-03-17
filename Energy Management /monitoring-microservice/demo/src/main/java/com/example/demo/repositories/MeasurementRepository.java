package com.example.demo.repositories;

import com.example.demo.entities.Measurement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface MeasurementRepository extends JpaRepository<Measurement, UUID> {

    List<Measurement> findByDeviceIdAndTimestampBetween(UUID deviceId, LocalDateTime start, LocalDateTime end);

    List<Measurement> findByDeviceId(UUID deviceId);

    @Query("SELECT SUM(m.hourlyConsumption) FROM Measurement m " +
            "WHERE m.deviceId = :deviceId AND m.timestamp >= :startTime")
    Double getTotalConsumptionForDeviceSince(@Param("deviceId") UUID deviceId,
                                             @Param("startTime") LocalDateTime startTime);

}