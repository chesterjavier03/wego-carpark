package com.wego.carparkapi.repository;

import com.wego.carparkapi.model.Carpark;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * @author chesterjavier
 * @Date 7/23/25
 */
@Repository
public interface CarparkRepository extends JpaRepository<Carpark, Long> {

  @Query(value = """
      SELECT c.*,
           (6371 * acos(cos(radians(:latitude))
           * cos(radians(c.latitude))
           * cos(radians(c.longitude) - radians(:longitude))
           + sin(radians(:latitude))
           * sin(radians(c.latitude)))) AS distance
    FROM carparks c
    WHERE c.available_lots > 0
    ORDER BY distance
    """, nativeQuery = true)
  Page<Carpark> findNearestCarparksWithAvailability(@Param("latitude") Double latitude, @Param("longitude") Double longitude, Pageable pageable);

  Optional<Carpark> findByCarparkNumber(String carparkNumber);
}
