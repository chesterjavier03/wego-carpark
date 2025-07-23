package com.wego.carparkapi.controller;

import com.wego.carparkapi.dto.CarparkResponseDto;
import com.wego.carparkapi.service.CarparkService;
import jakarta.validation.constraints.Max;
import java.util.List;
import lombok.RequiredArgsConstructor;
import jakarta.validation.constraints.Min;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author chesterjavier
 * @Date 7/23/25
 */
@RestController
@RequestMapping("/carparks")
@RequiredArgsConstructor
@Validated
@Slf4j
public class CarparkController {

  private final CarparkService carparkService;

  @GetMapping("/nearest")
  public ResponseEntity<List<CarparkResponseDto>> getNearestCarparks(
      @RequestParam(value = "latitude") Double latitude,
      @RequestParam(value = "longitude") Double longitude,
      @RequestParam(value = "page", defaultValue = "1")
      @Min(value = 1, message = "Page must be at least 1")
      Integer page,
      @RequestParam(value = "per_page", defaultValue = "10")
      @Min(value = 1, message = "Per page must be at least 1")
      @Max(value = 1000, message = "Per page must not exceed 100")
      Integer perPage) {

    log.info("Finding nearest carparks for location: {}, {}, page: {}, perPage: {}",
        latitude, longitude, page, perPage);

    List<CarparkResponseDto> result = carparkService.findNearestCarparks(
        latitude, longitude, page, perPage);

    return ResponseEntity.ok(result);
  }

  @GetMapping("/import/csv")
  public ResponseEntity<String> importCarparkDataFromCsv() {
    log.info("Manual CSV import triggered...");
    try {
      carparkService.importCarparkDataFromCsv();
      return ResponseEntity.ok("CSV import completed successfully");
    } catch (Exception e) {
      return ResponseEntity.internalServerError()
          .body("CSV import failed: " + e.getMessage());
    }
  }
}
