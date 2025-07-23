package com.wego.carparkapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Data;

/**
 * @author chesterjavier
 * @Date 7/23/25
 */
@Data
public class CarparkAvailabilityResponseDto {

  @JsonProperty("items")
  private List<AvailabilityItem> items;

  @Data
  public static class AvailabilityItem {

    @JsonProperty("timestamp")
    private String timestamp;

    @JsonProperty("carpark_data")
    private List<CarparkData> carparkData;
  }

  @Data
  public static class CarparkData {

    @JsonProperty("carpark_number")
    private String carparkNumber;

    @JsonProperty("carpark_info")
    private List<CarparkInfo> carparkInfo;
  }

  @Data
  public static class CarparkInfo {

    @JsonProperty("total_lots")
    private String totalLots;

    @JsonProperty("lot_type")
    private String lotType;

    @JsonProperty("lots_available")
    private String lotsAvailable;

  }

}
