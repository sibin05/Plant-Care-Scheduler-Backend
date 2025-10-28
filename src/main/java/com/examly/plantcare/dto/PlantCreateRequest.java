package com.examly.plantcare.dto;

import com.examly.plantcare.entity.Plant;

import java.math.BigDecimal;
import java.time.LocalDate;

public class PlantCreateRequest {
    private String nickname;
    private String location;
    private String acquisitionDate;
    private String currentHeightCm;
    private String currentWidthCm;
    private String potSize;
    private String soilType;
    private String healthStatus;
    private String notes;

    // Getters and setters
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getAcquisitionDate() { return acquisitionDate; }
    public void setAcquisitionDate(String acquisitionDate) { this.acquisitionDate = acquisitionDate; }

    public String getCurrentHeightCm() { return currentHeightCm; }
    public void setCurrentHeightCm(String currentHeightCm) { this.currentHeightCm = currentHeightCm; }

    public String getCurrentWidthCm() { return currentWidthCm; }
    public void setCurrentWidthCm(String currentWidthCm) { this.currentWidthCm = currentWidthCm; }

    public String getPotSize() { return potSize; }
    public void setPotSize(String potSize) { this.potSize = potSize; }

    public String getSoilType() { return soilType; }
    public void setSoilType(String soilType) { this.soilType = soilType; }

    public String getHealthStatus() { return healthStatus; }
    public void setHealthStatus(String healthStatus) { this.healthStatus = healthStatus; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public Plant toPlant() {
        Plant plant = new Plant();
        plant.setNickname(this.nickname);
        plant.setLocation(this.location);
        
        if (this.acquisitionDate != null && !this.acquisitionDate.isEmpty()) {
            plant.setAcquisitionDate(LocalDate.parse(this.acquisitionDate));
        }
        
        if (this.currentHeightCm != null && !this.currentHeightCm.isEmpty()) {
            plant.setCurrentHeightCm(new BigDecimal(this.currentHeightCm));
        }
        
        if (this.currentWidthCm != null && !this.currentWidthCm.isEmpty()) {
            plant.setCurrentWidthCm(new BigDecimal(this.currentWidthCm));
        }
        
        plant.setPotSize(this.potSize);
        plant.setSoilType(this.soilType);
        
        if (this.healthStatus != null) {
            plant.setHealthStatus(Plant.HealthStatus.valueOf(this.healthStatus));
        }
        
        plant.setNotes(this.notes);
        return plant;
    }
}