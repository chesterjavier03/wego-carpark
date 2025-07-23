package com.wego.carparkapi.model;

import com.opencsv.bean.CsvBindByName;
import lombok.Data;

/**
 * @author chesterjavier
 * @Date 7/23/25
 */
@Data
public class CarparkCsv {

  @CsvBindByName(column = "car_park_no")
  private String carParkNo;

  @CsvBindByName(column = "address")
  private String address;

  @CsvBindByName(column = "x_coord")
  private String xCoord;

  @CsvBindByName(column = "y_coord")
  private String yCoord;

  @CsvBindByName(column = "car_park_type")
  private String carParkType;

  @CsvBindByName(column = "type_of_parking_system")
  private String typeOfParkingSystem;

  @CsvBindByName(column = "short_term_parking")
  private String shortTermParking;

  @CsvBindByName(column = "freeParking")
  private String freeParking;

  @CsvBindByName(column = "night_parking")
  private String nightParking;

  @CsvBindByName(column = "car_park_decks")
  private String carParkDecks;

  @CsvBindByName(column = "gantry_height")
  private String gantryHeight;

  @CsvBindByName(column = "car_park_basement")
  private String carParkBasement;
}
