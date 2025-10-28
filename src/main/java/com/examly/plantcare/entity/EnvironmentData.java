package com.examly.plantcare.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "environment_data")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnvironmentData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plant_id")
    @JsonIgnore
    private Plant plant;

    private String locationId;
    private String sensorId;

    private Double temperatureCelsius;
    private Double humidityPercentage;
    private Integer lightLevelLux;
    private Double soilMoisturePercentage;
    private Double phLevel;

    private LocalDateTime recordedDate = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    private DataSource dataSource;

    @Transient
    private Long plantId;

    public enum DataSource {
        MANUAL, SENSOR, WEATHER_API
    }
    
    // Manual setters to fix Lombok issues
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    
    public DataSource getDataSource() {
        return dataSource;
    }

    public Long getPlantId() {
        return plantId != null ? plantId : (plant != null ? plant.getId() : null);
    }

    public void setPlantId(Long plantId) {
        this.plantId = plantId;
    }

    public Plant getPlant() {
        return plant;
    }

    public void setPlant(Plant plant) {
        this.plant = plant;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public void setRecordedDate(LocalDateTime recordedDate) {
        this.recordedDate = recordedDate;
    }
    
    public LocalDateTime getRecordedDate() {
        return recordedDate;
    }
    
    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }
    
    public void setTemperatureCelsius(Double temperatureCelsius) {
        this.temperatureCelsius = temperatureCelsius;
    }
    
    public void setHumidityPercentage(Double humidityPercentage) {
        this.humidityPercentage = humidityPercentage;
    }
    
    public void setLightLevelLux(Integer lightLevelLux) {
        this.lightLevelLux = lightLevelLux;
    }
    
    public void setSoilMoisturePercentage(Double soilMoisturePercentage) {
        this.soilMoisturePercentage = soilMoisturePercentage;
    }
    
    public Long getId() {
        return id;
    }
    
    public String getLocationId() {
        return locationId;
    }
    
    public Double getTemperatureCelsius() {
        return temperatureCelsius;
    }
    
    public Double getHumidityPercentage() {
        return humidityPercentage;
    }
    
    public Integer getLightLevelLux() {
        return lightLevelLux;
    }
    
    public Double getSoilMoisturePercentage() {
        return soilMoisturePercentage;
    }
}
