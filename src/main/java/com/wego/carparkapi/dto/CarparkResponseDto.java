package com.wego.carparkapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author chesterjavier
 * @Date 7/23/25
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CarparkResponseDto {

  private String address;
  private Double latitude;
  private Double longitude;
  @JsonProperty("total_lots")
  private Integer totalLots;
  @JsonProperty("available_lots")
  private Integer availableLots;
  private Double distance;
}
