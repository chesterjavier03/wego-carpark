package com.wego.carparkapi.util;

import com.wego.carparkapi.service.CarparkService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * @author chesterjavier
 * @Date 7/23/25
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CarparkDataLoader {

  private final CarparkService carparkService;

  /**
   * Triggered on ApplicationReadyEvent when application runs
   */
  @EventListener(ApplicationReadyEvent.class)
  public void importOnStartup() {
    log.info("Starting data import....");
    carparkService.importCarparkDataFromCsv();
  }
}
