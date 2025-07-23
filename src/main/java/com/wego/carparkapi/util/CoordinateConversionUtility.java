package com.wego.carparkapi.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author chesterjavier
 * @Date 7/23/25
 */
@Component
@Slf4j
public class CoordinateConversionUtility {

  private static final double ORIGIN_LATITUDE = Math.toRadians(1.366666); // 1°22'N
  private static final double ORIGIN_LONGITUDE = Math.toRadians(103.833333); // 103°50'E
  private static final double FALSE_NORTHING = 38744.572;
  private static final double FALSE_EASTING = 28001.642;
  private static final double SCALE_FACTOR = 1.0;

  private static final double WGS84_A = 6378137.0; // Semi-major axis
  private static final double WGS84_F = 1.0 / 298.257223563; // Flattening
  private static final double WGS84_E2 = 2 * WGS84_F - WGS84_F * WGS84_F;

  public double[] convertSvy21ToWgs84(double x, double y) {
    try {
      // Step 1: Convert SVY21 to geographic coordinates on SVY21 datum
      double[] geographic = svy21ToGeographic(x, y);
      double lat = geographic[0];
      double lon = geographic[1];

      // Step 2: Apply datum transformation (SVY21 uses WGS84 as base, so minimal transformation needed)
      // For Singapore, the difference between SVY21 and WGS84 is negligible for most applications

//       log.info("lat={}, lon={} result={}", lat, lon, new double[]{Math.toDegrees(lat), Math.toDegrees(lon)});

      return new double[]{Math.toDegrees(lat), Math.toDegrees(lon)};

    } catch (Exception e) {
      log.error("Error converting coordinates from SVY21 ({}, {}) to WGS84: {}", x, y, e.getMessage());
      // Return approximate conversion as fallback
      return approximateConversion(x, y);
    }
  }

  private double[] svy21ToGeographic(double x, double y) {
    // Adjust for false easting and northing
    double adjustedX = x - FALSE_EASTING;
    double adjustedY = y - FALSE_NORTHING;

    // Initial approximation
    double n = adjustedY / (WGS84_A * SCALE_FACTOR);
    double lat = ORIGIN_LATITUDE + n;

    // Iterative calculation for more accuracy
    for (int i = 0; i < 10; i++) {
      double sinLat = Math.sin(lat);
      double cosLat = Math.cos(lat);
      double tanLat = Math.tan(lat);

      double v = WGS84_A / Math.sqrt(1 - WGS84_E2 * sinLat * sinLat);
      double rho = WGS84_A * (1 - WGS84_E2) / Math.pow(1 - WGS84_E2 * sinLat * sinLat, 1.5);

      double eta2 = v / rho - 1;

      double m = WGS84_A * ((1 - WGS84_E2 / 4 - 3 * WGS84_E2 * WGS84_E2 / 64) * lat
          - (3 * WGS84_E2 / 8 + 3 * WGS84_E2 * WGS84_E2 / 32) * Math.sin(2 * lat)
          + (15 * WGS84_E2 * WGS84_E2 / 256) * Math.sin(4 * lat));

      double deltaLat = (adjustedY - SCALE_FACTOR * (m - WGS84_A * ((1 - WGS84_E2 / 4) * ORIGIN_LATITUDE
          - (3 * WGS84_E2 / 8) * Math.sin(2 * ORIGIN_LATITUDE)))) / (SCALE_FACTOR * rho);

      lat = ORIGIN_LATITUDE + deltaLat;

      if (Math.abs(deltaLat) < 1e-12) break;
    }

    double sinLat = Math.sin(lat);
    double cosLat = Math.cos(lat);
    double tanLat = Math.tan(lat);

    double v = WGS84_A / Math.sqrt(1 - WGS84_E2 * sinLat * sinLat);
    double eta2 = v * WGS84_E2 * cosLat * cosLat / (1 - WGS84_E2);

    double lon = ORIGIN_LONGITUDE + adjustedX / (SCALE_FACTOR * v * cosLat)
        - adjustedX * adjustedX * adjustedX * tanLat / (6 * SCALE_FACTOR * SCALE_FACTOR * SCALE_FACTOR * v * v * v * cosLat)
        * (1 + eta2);

    return new double[]{lat, lon};
  }

  private double[] approximateConversion(double x, double y) {
    // These are approximate conversion factors for Singapore area
    double lat = 1.366666 + (y - 38744.572) / 110000.0;
    double lon = 103.833333 + (x - 28001.642) / 111000.0;

    return new double[]{lat, lon};
  }

  public boolean isValidSingaporeCoordinates(double latitude, double longitude) {
    return latitude >= 1.0 && latitude <= 1.5 &&
        longitude >= 103.0 && longitude <= 104.5;
  }

}
