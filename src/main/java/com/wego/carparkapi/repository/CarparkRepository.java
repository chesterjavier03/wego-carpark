package com.wego.carparkapi.repository;

import com.wego.carparkapi.model.Carpark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author chesterjavier
 * @Date 7/23/25
 */
@Repository
public interface CarparkRepository extends JpaRepository<Carpark, Long> {

}
