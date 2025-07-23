package com.wego.carparkapi.util;

import static org.junit.jupiter.api.Assertions.*;
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
class CarparkDataLoaderTest {

  @Mock
  private CarparkService carparkService;

  @InjectMocks
  private CarparkDataLoader carparkDataLoader;

  @Test
  void importOnStartup_WhenApplicationStarts_ShouldCallImportCarparkData() {
    carparkDataLoader.importOnStartup();
    verify(carparkService, times(1)).importCarparkDataFromCsv();
  }

}
