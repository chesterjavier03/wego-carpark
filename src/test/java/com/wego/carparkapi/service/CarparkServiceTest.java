package com.wego.carparkapi.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.wego.carparkapi.dto.CarparkResponseDto;
import com.wego.carparkapi.model.Carpark;
import com.wego.carparkapi.repository.CarparkRepository;
import com.wego.carparkapi.util.CoordinateConversionUtility;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

/**
 * @author chesterjavier
 * @Date 7/23/25
 */
@ExtendWith(MockitoExtension.class)
class CarparkServiceTest {

  @Mock
  private CarparkRepository carparkRepository;

  @InjectMocks
  private CarparkService carparkService;

  private Carpark testCarpark1;
  private Carpark testCarpark2;

  @BeforeEach
  void setUp() {
    testCarpark1 = Carpark.builder()
        .id(1L)
        .carparkNumber("HG12")
        .address("BLK 401-413, 460-463 HOUGANG AVENUE 10")
        .latitude(1.37429)
        .longitude(103.896)
        .totalLots(693)
        .availableLots(182)
        .lastUpdated(LocalDateTime.now())
        .build();

    testCarpark2 = Carpark.builder()
        .id(2L)
        .carparkNumber("HG13")
        .address("BLK 351-357 HOUGANG AVENUE 7")
        .latitude(1.37234)
        .longitude(103.899)
        .totalLots(249)
        .availableLots(143)
        .lastUpdated(LocalDateTime.now())
        .build();
  }

  @Test
  void findNearestCarparks_ShouldReturnCarparks_WhenValidInput() {
    // Given
    double latitude = 1.37326;
    double longitude = 103.897;
    int page = 1;
    int perPage = 10;

    List<Carpark> carparks = Arrays.asList(testCarpark1, testCarpark2);
    Page<Carpark> carparkPage = new PageImpl<>(carparks, PageRequest.of(0, 10), 2);

    when(carparkRepository.findNearestCarparksWithAvailability(eq(latitude), eq(longitude), any(
        Pageable.class)))
        .thenReturn(carparkPage);

    // When
    List<CarparkResponseDto> result = carparkService.findNearestCarparks(latitude, longitude, page, perPage);

    // Then
    assertNotNull(result);
    assertEquals(2, result.size());

    CarparkResponseDto dto1 = result.get(0);
    assertEquals(testCarpark1.getAddress(), dto1.getAddress());
    assertEquals(testCarpark1.getLatitude(), dto1.getLatitude());
    assertEquals(testCarpark1.getLongitude(), dto1.getLongitude());
    assertEquals(testCarpark1.getTotalLots(), dto1.getTotalLots());
    assertEquals(testCarpark1.getAvailableLots(), dto1.getAvailableLots());

    verify(carparkRepository).findNearestCarparksWithAvailability(eq(latitude), eq(longitude), any(Pageable.class));
  }

  @Test
  void findNearestCarparks_ShouldReturnEmptyList_WhenNoCarparksFound() {
    // Given
    double latitude = 1.37326;
    double longitude = 103.897;
    int page = 1;
    int perPage = 10;

    Page<Carpark> emptyPage = new PageImpl<>(Arrays.asList(), PageRequest.of(0, 10), 0);

    when(carparkRepository.findNearestCarparksWithAvailability(eq(latitude), eq(longitude), any(Pageable.class)))
        .thenReturn(emptyPage);

    // When
    List<CarparkResponseDto> result = carparkService.findNearestCarparks(latitude, longitude, page, perPage);

    // Then
    assertNotNull(result);
    assertTrue(result.isEmpty());
  }

  @Test
  void findNearestCarparks_ShouldReturnAllAvailable_WhenPerPageExceedsAvailable() {
    double latitude = 1.37326;
    double longitude = 103.897;
    int page = 1;
    int perPage = 10;
    List<Carpark> carparks = Arrays.asList(testCarpark1, testCarpark2);
    Page<Carpark> carparkPage = new PageImpl<>(carparks, PageRequest.of(0, 10), 2);
    when(carparkRepository.findNearestCarparksWithAvailability(eq(latitude), eq(longitude), any(Pageable.class)))
        .thenReturn(carparkPage);
    List<CarparkResponseDto> result = carparkService.findNearestCarparks(latitude, longitude, page, perPage);
    assertEquals(2, result.size());
  }

  @Test
  void findNearestCarparks_ShouldReturnEmpty_WhenPageTooHigh() {
    double latitude = 1.37326;
    double longitude = 103.897;
    int page = 2;
    int perPage = 2;
    Page<Carpark> emptyPage = new PageImpl<>(Arrays.asList(), PageRequest.of(1, 2), 0);
    when(carparkRepository.findNearestCarparksWithAvailability(eq(latitude), eq(longitude), any(Pageable.class)))
        .thenReturn(emptyPage);
    List<CarparkResponseDto> result = carparkService.findNearestCarparks(latitude, longitude, page, perPage);
    assertTrue(result.isEmpty());
  }

  @Test
  void getCarparksWithAvailabilityCount_ShouldReturnCount() {
    // Given
    Page<Carpark> carparkPage = new PageImpl<>(Arrays.asList(testCarpark1, testCarpark2),
        PageRequest.of(0, 10), 2);
    when(carparkRepository.findAllWithAvailableLots(any(Pageable.class)))
        .thenReturn(carparkPage);

    // When
    long count = carparkService.getCarparksWithAvailabilityCount();

    // Then
    assertEquals(2L, count);
    verify(carparkRepository).findAllWithAvailableLots(any(Pageable.class));
  }

  @Test
  void getCarparksWithAvailabilityCount_ShouldReturnZeroWhenNoneAvailable() {
    Page<Carpark> emptyPage = new PageImpl<>(Arrays.asList(), PageRequest.of(0, 10), 0);
    when(carparkRepository.findAllWithAvailableLots(any(Pageable.class)))
        .thenReturn(emptyPage);
    long count = carparkService.getCarparksWithAvailabilityCount();
    assertEquals(0L, count);
  }

}
