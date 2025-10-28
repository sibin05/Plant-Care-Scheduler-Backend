package com.examly.plantcare.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "species")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Species {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "common_name", nullable = false, length = 200)
    private String commonName;

    @Column(name = "scientific_name", nullable = false, unique = true, length = 200)
    private String scientificName;

    @Column(name = "family_name", length = 100)
    private String familyName;

    @Enumerated(EnumType.STRING)
    @Column(name = "care_difficulty")
    private CareDifficulty careDifficulty;

    @Enumerated(EnumType.STRING)
    @Column(name = "light_requirements")
    private LightRequirements lightRequirements;

    private Integer waterFrequencyDays;
    private Integer humidityMin;
    private Integer humidityMax;

    private BigDecimal temperatureMinCelsius;
    private BigDecimal temperatureMaxCelsius;

    private BigDecimal soilPhMin;
    private BigDecimal soilPhMax;

    @Enumerated(EnumType.STRING)
    @Column(name = "growth_rate")
    private GrowthRate growthRate;

    private Integer maxHeightCm;
    private Integer fertilizerFrequencyDays;
    private Integer pruningFrequencyDays;
    private Integer repottingFrequencyMonths;

    @ElementCollection
    @Column(name = "issue")
    private List<String> commonIssues = new ArrayList<>();

    @ElementCollection
    @Column(name = "tip")
    private List<String> careTips = new ArrayList<>();


    @Column(name = "created_date", updatable = false)
    private LocalDateTime createdDate = LocalDateTime.now();

    public enum CareDifficulty { EASY, MODERATE, DIFFICULT, EXPERT }
    public enum LightRequirements { LOW, MEDIUM, HIGH, DIRECT }
    public enum GrowthRate { SLOW, MODERATE, FAST }

    // Manual getters and setters to fix Lombok issues
    public LightRequirements getLightRequirements() {
        return lightRequirements;
    }

    public Integer getWaterFrequencyDays() {
        return waterFrequencyDays;
    }

    public Integer getFertilizerFrequencyDays() {
        return fertilizerFrequencyDays;
    }

    public Integer getPruningFrequencyDays() {
        return pruningFrequencyDays;
    }

    public Integer getRepottingFrequencyMonths() {
        return repottingFrequencyMonths;
    }

    public BigDecimal getSoilPhMin() {
        return soilPhMin;
    }

    public BigDecimal getSoilPhMax() {
        return soilPhMax;
    }

    public BigDecimal getTemperatureMinCelsius() {
        return temperatureMinCelsius;
    }

    public BigDecimal getTemperatureMaxCelsius() {
        return temperatureMaxCelsius;
    }

    public List<String> getCommonIssues() {
        return commonIssues;
    }

    public void setCareTips(List<String> careTips) {
        this.careTips = careTips;
    }
    
    // Manual setters to fix Lombok issues
    public void setCommonName(String commonName) {
        this.commonName = commonName;
    }
    
    public void setScientificName(String scientificName) {
        this.scientificName = scientificName;
    }
    
    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }
    
    public void setCareDifficulty(CareDifficulty careDifficulty) {
        this.careDifficulty = careDifficulty;
    }
    
    public void setLightRequirements(LightRequirements lightRequirements) {
        this.lightRequirements = lightRequirements;
    }
    
    public void setGrowthRate(GrowthRate growthRate) {
        this.growthRate = growthRate;
    }
}
