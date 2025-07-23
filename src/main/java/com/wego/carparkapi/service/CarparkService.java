package com.wego.carparkapi.service;

import com.wego.carparkapi.dto.CarparkResponseDto;
import com.wego.carparkapi.model.Carpark;
import com.wego.carparkapi.repository.CarparkRepository;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author chesterjavier
 * @Date 7/23/25
 */
@Service
@AllArgsConstructor
@Slf4j
public class CarparkService {

  private final CarparkRepository carparkRepository;

  @Transactional(readOnly = true)
  public List<CarparkResponseDto> findNearestCarparks(Double latitude, Double longitude,
      @Min(value = 1, message = "Page must be at least 1") Integer page,
      @Min(value = 1, message = "Per page must be at least 1")
      @Max(value = 1000, message = "Per page must not exceed 100")
      Integer perPage) {
    log.debug("Finding nearest carparks for location: {}, {}, page: {}, perPage: {}",
        latitude, longitude, page, perPage);

    Pageable pageable = PageRequest.of(page - 1, perPage);

    Page<Carpark> carparks = carparkRepository.findNearestCarparksWithAvailability(
        latitude, longitude, pageable);

    log.debug("Found {} carparks on page {} of {}",
        carparks.getNumberOfElements(), page, carparks.getTotalPages());

    return carparks.getContent().stream()
        .map(this::convertToResponseDto)
        .collect(Collectors.toList());
  }

  private CarparkResponseDto convertToResponseDto(Carpark carpark) {
    return CarparkResponseDto.builder()
        .address(carpark.getAddress())
        .latitude(carpark.getLatitude())
        .longitude(carpark.getLongitude())
        .totalLots(carpark.getTotalLots())
        .availableLots(carpark.getAvailableLots())
        .build();
  }
}
