package com.wego.carparkapi.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.wego.carparkapi.dto.CarparkResponseDto;
import com.wego.carparkapi.service.CarparkService;
import com.wego.carparkapi.util.GlobalExceptionHandler;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.MethodArgumentNotValidException;

/**
 * @author chesterjavier
 * @Date 7/23/25
 */
@ExtendWith(MockitoExtension.class)
@Slf4j
class CarparkControllerTest {

  private MockMvc mockMvc;

  @Mock
  private CarparkService carparkService;

  @InjectMocks
  private CarparkController carparkController;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.standaloneSetup(carparkController)
        .setControllerAdvice(new GlobalExceptionHandler())
        .build();
  }

  @Test
  void getNearestCarparks_WithValidParams_ShouldReturnOk() throws Exception {
    List<CarparkResponseDto> mockResponse = Arrays.asList(
        new CarparkResponseDto( "BLK 270/271 ALBERT CENTRE BASEMENT CAR PARK", 1.3, 103.8, 10, 100, null),
        new CarparkResponseDto( "BLK 98A ALJUNIED CRESCENT", 1.32, 103.88,  5, 50, null)
    );

    when(carparkService.findNearestCarparks(anyDouble(), anyDouble(), anyInt(), anyInt()))
        .thenReturn(mockResponse);

    mockMvc.perform(get("/carparks/nearest")
            .param("latitude", "1.3")
            .param("longitude", "103.8")
            .param("page", "1")
            .param("per_page", "10"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(2))
        .andExpect(jsonPath("$[0].address").value("BLK 270/271 ALBERT CENTRE BASEMENT CAR PARK"))
        .andExpect(jsonPath("$[0].latitude").value(1.3))
        .andExpect(jsonPath("$[0].longitude").value(103.8))
        .andExpect(jsonPath("$[1].address").value("BLK 98A ALJUNIED CRESCENT"))
        .andExpect(jsonPath("$[1].latitude").value(1.32))
        .andExpect(jsonPath("$[1].longitude").value(103.88));

    verify(carparkService, times(1)).findNearestCarparks(1.3, 103.8, 1, 10);
  }

  @Test
  void importCarparkData_ShouldReturnSuccessMessage() {
    ResponseEntity<String> response = carparkController.importCarparkDataFromCsv();

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals("CSV import completed successfully", response.getBody());
    verify(carparkService, times(1)).importCarparkDataFromCsv();
  }

  @Test
  void importCarparkData_WhenExceptionThrown_ShouldReturnErrorResponse() {
    doThrow(new RuntimeException("Test exception")).when(carparkService).importCarparkDataFromCsv();

    ResponseEntity<String> response = carparkController.importCarparkDataFromCsv();

    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    assertTrue(response.getBody().startsWith("CSV import failed: Test exception"));
  }

  @Test
  void updateAvailability_ShouldReturnSuccessMessage() {
    ResponseEntity<String> response = carparkController.updateAvailability();

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals("Availability update completed successfully", response.getBody());
    verify(carparkService, times(1)).updateCarparkAvailability();
  }

  @Test
  void updateAvailability_WhenExceptionThrown_ShouldReturnErrorResponse() {
    doThrow(new RuntimeException("Test exception")).when(carparkService).updateCarparkAvailability();

    ResponseEntity<String> response = carparkController.updateAvailability();

    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    assertTrue(response.getBody().startsWith("Availability update failed: Test exception"));
  }

  @Test
  void health_ShouldReturnOkWithCount() {
    when(carparkService.getCarparksWithAvailabilityCount()).thenReturn(150L);

    ResponseEntity<String> response = carparkController.health();

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals("Service is healthy. 150 carparks with availability found.", response.getBody());
    verify(carparkService, times(1)).getCarparksWithAvailabilityCount();
  }
}
