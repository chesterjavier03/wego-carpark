package com.wego.carparkapi.util;

import com.wego.carparkapi.service.CarparkService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * @author chesterjavier
 * @Date 7/23/25
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AvailabilityUpdateService {

  private final CarparkService carparkService;

  /**
   * Update every 5 minutes
   */
  @Scheduled(fixedRate = 300000)
  public void updateAvailability() {
    log.info("Updating availability...");
    try {
      carparkService.updateCarparkAvailability();
    } catch (Exception e) {
      log.error("Availability update failed: {}", e.getMessage());
    }
  }
}
