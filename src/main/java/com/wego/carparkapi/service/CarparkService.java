package com.wego.carparkapi.service;

import com.opencsv.bean.CsvToBeanBuilder;
import com.wego.carparkapi.dto.CarparkAvailabilityResponseDto;
import com.wego.carparkapi.dto.CarparkAvailabilityResponseDto.AvailabilityItem;
import com.wego.carparkapi.dto.CarparkResponseDto;
import com.wego.carparkapi.model.Carpark;
import com.wego.carparkapi.model.CarparkCsv;
import com.wego.carparkapi.repository.CarparkRepository;
import com.wego.carparkapi.util.CoordinateConversionUtility;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.io.InputStreamReader;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * @author chesterjavier
 * @Date 7/23/25
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CarparkService {

  private final CarparkRepository carparkRepository;
  private final CoordinateConversionUtility coordinateConversionUtility;
  private final WebClient webClient;

  @Value("${app.carpark.csv.file-path}")
  private String csvFilePath;

  @Value("${app.carpark.api.url}")
  private String carparkApiUrl;

  @Value("${app.carpark.api.timeout:1000}")
  private int apiTimeout;

  @Transactional(readOnly = true)
  public List<CarparkResponseDto> findNearestCarparks(Double latitude, Double longitude,
      @Min(value = 1, message = "Page must be at least 1") Integer page,
      @Min(value = 1, message = "Per page must be at least 1")
      @Max(value = 100, message = "Per page must not exceed 100")
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

  @Transactional
  public void importCarparkDataFromCsv() {
    log.info("Starting CSV import from: {}", csvFilePath);

    try {
      ClassPathResource resource = new ClassPathResource(csvFilePath);

      List<CarparkCsv> csvModels = new CsvToBeanBuilder<CarparkCsv>(
          new InputStreamReader(resource.getInputStream()))
          .withType(CarparkCsv.class)
          .withIgnoreLeadingWhiteSpace(true)
          .build()
          .parse();

      log.info("Parsed {} records from CSV", csvModels.size());

      int imported = 0;
      int errors = 0;

      for (CarparkCsv csvModel : csvModels) {
        try {
          Optional<Carpark> existingCarpark = carparkRepository.findByCarparkNumber(csvModel.getCarParkNo());

          Carpark carpark;

          if (existingCarpark.isPresent()) {
            carpark = existingCarpark.get();
            updateCarparkFromCsv(carpark, csvModel);
          } else {
            carpark = createCarparkFromCsv(csvModel);
          }

          if (carpark != null) {
            carparkRepository.save(carpark);
            imported++;
          }

        } catch (Exception e) {
          log.error("Error processing carpark {}: {}", csvModel.getCarParkNo(), e.getMessage());
          errors++;
        }
      }

    } catch (Exception e) {
      log.error("Error while importing csv file: {}", e.getLocalizedMessage());
      throw new RuntimeException("CSV import failed", e);
    }
  }

  private void updateCarparkFromCsv(Carpark carpark, CarparkCsv csvModel) {
    carpark.setAddress(csvModel.getAddress());
    carpark.setCarparkType(csvModel.getCarParkType());
    carpark.setTypeOfParkingSystem(csvModel.getTypeOfParkingSystem());
    carpark.setShortTermParking(csvModel.getShortTermParking());
    carpark.setFreeParking(csvModel.getFreeParking());
    carpark.setNightParking(csvModel.getNightParking());
    carpark.setCarparkDecks(parseInteger(csvModel.getCarParkDecks()));
    carpark.setGantryHeight(parseDouble(csvModel.getGantryHeight()));
    carpark.setCarparkBasement(csvModel.getCarParkBasement());
  }

  private Carpark createCarparkFromCsv(CarparkCsv csvModel) {
    try {
      double xCoord = parseDouble(csvModel.getXCoord());
      double yCoord = parseDouble(csvModel.getYCoord());

      double[] wgs84 = coordinateConversionUtility.convertSvy21ToWgs84(xCoord, yCoord);

      if (!coordinateConversionUtility.isValidSingaporeCoordinates(wgs84[0], wgs84[1])) {
        log.warn("Invalid coordinates for carpark {}: lat={}, lon={}",
            csvModel.getCarParkNo(), wgs84[0], wgs84[1]);
        return null;
      }

      return Carpark.builder()
          .carparkNumber(csvModel.getCarParkNo())
          .address(csvModel.getAddress())
          .xCoord(xCoord)
          .yCoord(yCoord)
          .latitude(wgs84[0])
          .longitude(wgs84[1])
          .carparkType(csvModel.getCarParkType())
          .typeOfParkingSystem(csvModel.getTypeOfParkingSystem())
          .shortTermParking(csvModel.getShortTermParking())
          .freeParking(csvModel.getFreeParking())
          .nightParking(csvModel.getNightParking())
          .carparkDecks(parseInteger(csvModel.getCarParkDecks()))
          .gantryHeight(parseDouble(csvModel.getGantryHeight()))
          .carparkBasement(csvModel.getCarParkBasement())
          .totalLots(0)
          .availableLots(0)
          .build();
    } catch (Exception e) {
      log.error("Error creating carpark from CSV model {}: {}", csvModel.getCarParkNo(), e.getMessage());
      return null;
    }
  }

  private Double parseDouble(String value) {
    if (value == null || value.trim().isEmpty()) {
      return null;
    }
    try {
      return Double.parseDouble(value.trim());
    } catch (NumberFormatException e) {
      return null;
    }
  }

  private Integer parseInteger(String value) {
    if (value == null || value.trim().isEmpty()) {
      return null;
    }
    try {
      return Integer.parseInt(value.trim());
    } catch (NumberFormatException e) {
      return null;
    }
  }

  @Transactional
  public void updateCarparkAvailability() {
    log.info("Fetching carpark availability from API: {}", carparkApiUrl);

    try {
      Mono<CarparkAvailabilityResponseDto> responseMono = webClient
          .get()
          .uri(carparkApiUrl)
          .retrieve()
          .bodyToMono(CarparkAvailabilityResponseDto.class)
          .timeout(Duration.ofMillis(apiTimeout))
          .onErrorResume(TimeoutException.class, ex -> Mono.empty());

      CarparkAvailabilityResponseDto response = responseMono.block();

      if (response != null && response.getItems() != null && !response.getItems().isEmpty()) {
        processAvailabilityData(response.getItems().get(0));
      } else {
        log.warn("No availability data received from API");
      }

    } catch (Exception e) {
      log.error("Failed to fetch carpark availability: {}", e.getMessage(), e);
      throw new RuntimeException("Failed to update carpark availability", e);
    }
  }

  private void processAvailabilityData(AvailabilityItem availabilityItem) {
    LocalDateTime updateTime = LocalDateTime.now();
    int updated = 0;
    int notFound = 0;

    for (CarparkAvailabilityResponseDto.CarparkData carparkData : availabilityItem.getCarparkData()) {
      try {
        Optional<Carpark> carparkOpt = carparkRepository.findByCarparkNumber(carparkData.getCarparkNumber());

        if (carparkOpt.isPresent()) {
          Carpark carpark = carparkOpt.get();

          CarparkAvailabilityResponseDto.CarparkInfo carInfo = carparkData.getCarparkInfo().stream()
              .filter(info -> "C".equals(info.getLotType()))
              .findFirst()
              .orElse(carparkData.getCarparkInfo().get(0));

          carpark.setTotalLots(parseInteger(carInfo.getTotalLots()));
          carpark.setAvailableLots(parseInteger(carInfo.getLotsAvailable()));
          carpark.setLastUpdated(updateTime);

          carparkRepository.save(carpark);
          updated++;

        } else {
          log.debug("Carpark not found in database: {}", carparkData.getCarparkNumber());
          notFound++;
        }

      } catch (Exception e) {
        log.error("Error updating carpark {}: {}", carparkData.getCarparkNumber(), e.getMessage());
      }
    }

    log.info("Availability update completed. Updated: {}, Not found: {}", updated, notFound);
  }

  @Transactional(readOnly = true)
  public long getCarparksWithAvailabilityCount() {
    return carparkRepository.findAllWithAvailableLots(Pageable.unpaged()).getTotalElements();
  }
}
