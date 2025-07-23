package com.wego.carparkapi.util;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * @author chesterjavier
 * @Date 7/23/25
 */
@ExtendWith(MockitoExtension.class)
class CoordinateConversionUtilityTest {

  @InjectMocks
  private CoordinateConversionUtility coordinateConversionUtility;

  @Test
  void convertSvy21ToWgs84_shouldReturnExpectedLatLon() {
    // Example SVY21 coordinates for a point in Singapore (approximate)
    double x = 30000.0;
    double y = 40000.0;
    double[] result = coordinateConversionUtility.convertSvy21ToWgs84(x, y);
    // Singapore latitude: ~1.2-1.4, longitude: ~103.6-104.0
    assertNotNull(result);
    assertEquals(2, result.length);
    assertTrue(result[0] > 1.0 && result[0] < 1.5, "Latitude should be within Singapore bounds");
    assertTrue(result[1] > 103.0 && result[1] < 104.5, "Longitude should be within Singapore bounds");
  }

  @Test
  void isValidSingaporeCoordinates_shouldReturnTrueForValid() {
    assertTrue(coordinateConversionUtility.isValidSingaporeCoordinates(1.3, 103.9));
  }

  @Test
  void isValidSingaporeCoordinates_shouldReturnFalseForInvalid() {
    assertFalse(coordinateConversionUtility.isValidSingaporeCoordinates(0.9, 103.9)); // latitude too low
    assertFalse(coordinateConversionUtility.isValidSingaporeCoordinates(1.6, 103.9)); // latitude too high
    assertFalse(coordinateConversionUtility.isValidSingaporeCoordinates(1.3, 102.9)); // longitude too low
    assertFalse(coordinateConversionUtility.isValidSingaporeCoordinates(1.3, 104.6)); // longitude too high
  }

}
