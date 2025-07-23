package com.wego.carparkapi.util;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.wego.carparkapi.service.CarparkService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * @author chesterjavier
 * @Date 7/23/25
 */
@ExtendWith(MockitoExtension.class)
class AvailabilityUpdateServiceTest {

  @Mock
  private CarparkService dataImportService;

  @InjectMocks
  private AvailabilityUpdateService availabilityUpdateService;

  @Test
  void updateAvailability_shouldCallDataImportService() {
    // When
    availabilityUpdateService.updateAvailability();
    // Then
    verify(dataImportService, times(1)).updateCarparkAvailability();
  }

  @Test
  void updateAvailability_shouldLogErrorOnException() {
    // Given
    doThrow(new RuntimeException("Test exception")).when(dataImportService).updateCarparkAvailability();
    // When/Then: Should not throw, but should log
    assertDoesNotThrow(() -> availabilityUpdateService.updateAvailability());
    verify(dataImportService, times(1)).updateCarparkAvailability();
  }

}
