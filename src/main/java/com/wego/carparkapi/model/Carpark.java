package com.wego.carparkapi.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author chesterjavier
 * @Date 7/23/25
 */
@Entity
@Table(name = "carparks", indexes = {
    @Index(name = "idx_carpark_location", columnList = "latitude, longitude"),
    @Index(name = "idx_carpark_code", columnList = "carparkNumber")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Carpark {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name ="carpark_number", unique = true, nullable = false)
  private String carparkNumber;

  @Column(name = "address", nullable = false)
  private String address;

  @Column(name = "x_coord")
  private Double xCoord;

  @Column(name = "y_coord")
  private Double yCoord;

  @Column(name = "latitude", nullable = false)
  private Double latitude;

  @Column(name = "longitude", nullable = false)
  private Double longitude;

  @Column(name = "carpark_type")
  private String carparkType;

  @Column(name = "type_of_parking_system")
  private String typeOfParkingSystem;

  @Column(name = "short_term_parking")
  private String shortTermParking;

  @Column(name = "free_parking")
  private String freeParking;

  @Column(name = "night_parking")
  private String nightParking;

  @Column(name = "carpark_decks")
  private Integer carparkDecks;

  @Column(name = "gantry_height")
  private Double gantryHeight;

  @Column(name = "carpark_basement")
  private String carparkBasement;

  @Column(name = "total_lots")
  private Integer totalLots;

  @Column(name = "available_lots")
  private Integer availableLots;

  @Column(name = "last_updated")
  private LocalDateTime lastUpdated;

  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  @PrePersist
  protected void onCreate() {
    createdAt = LocalDateTime.now();
    updatedAt = LocalDateTime.now();
  }

  @PreUpdate
  protected void onUpdate() {
    updatedAt = LocalDateTime.now();
  }
}
