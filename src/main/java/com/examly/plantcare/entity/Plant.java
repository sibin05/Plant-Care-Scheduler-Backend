package com.examly.plantcare.entity;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "plants")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Plant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nickname;

    @Column(length = 200)
    private String location;

    @Column(name = "acquisition_date")
    private LocalDate acquisitionDate;

    @Column(name = "current_height_cm", precision = 5, scale = 1)
    private BigDecimal currentHeightCm;

    @Column(name = "current_width_cm", precision = 5, scale = 1)
    private BigDecimal currentWidthCm;

    @Column(name = "pot_size", length = 50)
    private String potSize;

    @Column(name = "soil_type", length = 100)
    private String soilType;

    @Column(name = "last_watered_date")
    private LocalDateTime lastWateredDate;

    @Column(name = "last_fertilized_date")
    private LocalDateTime lastFertilizedDate;

    @Column(name = "last_pruned_date")
    private LocalDateTime lastPrunedDate;

    @Column(name = "last_repotted_date")
    private LocalDateTime lastRepottedDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "health_status", nullable = false)
    private HealthStatus healthStatus;

    @Lob
    private String notes;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "created_date", updatable = false)
    private LocalDateTime createdDate = LocalDateTime.now();

    @Column(name = "updated_date")
    private LocalDateTime updatedDate = LocalDateTime.now();

    @PreUpdate
    public void setUpdatedDate() {
        this.updatedDate = LocalDateTime.now();
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    @JsonBackReference(value = "user")
    private User owner;

    // In Plant
    @OneToMany(mappedBy = "plant", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference(value = "care-tasks")
    private List<CareTask> careTasks;

    @OneToMany(mappedBy = "plant", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference(value = "plant-environment")
    private List<EnvironmentData> environmentDataList = new ArrayList<>();

    @OneToMany(mappedBy = "plant", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference(value = "plant-health")
    private List<HealthRecord> healthRecords = new ArrayList<>();

    public enum HealthStatus {
        EXCELLENT, GOOD, FAIR, POOR, CRITICAL
    }

    // Manual getters and setters
    public Long getId() {
        return id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public LocalDate getAcquisitionDate() {
        return acquisitionDate;
    }

    public void setAcquisitionDate(LocalDate acquisitionDate) {
        this.acquisitionDate = acquisitionDate;
    }

    public BigDecimal getCurrentHeightCm() {
        return currentHeightCm;
    }

    public void setCurrentHeightCm(BigDecimal currentHeightCm) {
        this.currentHeightCm = currentHeightCm;
    }

    public BigDecimal getCurrentWidthCm() {
        return currentWidthCm;
    }

    public void setCurrentWidthCm(BigDecimal currentWidthCm) {
        this.currentWidthCm = currentWidthCm;
    }

    public String getPotSize() {
        return potSize;
    }

    public void setPotSize(String potSize) {
        this.potSize = potSize;
    }

    public String getSoilType() {
        return soilType;
    }

    public void setSoilType(String soilType) {
        this.soilType = soilType;
    }

    public HealthStatus getHealthStatus() {
        return healthStatus;
    }

    public void setHealthStatus(HealthStatus healthStatus) {
        this.healthStatus = healthStatus;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public LocalDateTime getLastWateredDate() {
        return lastWateredDate;
    }

    public void setLastWateredDate(LocalDateTime lastWateredDate) {
        this.lastWateredDate = lastWateredDate;
    }

    public LocalDateTime getLastFertilizedDate() {
        return lastFertilizedDate;
    }

    public void setLastFertilizedDate(LocalDateTime lastFertilizedDate) {
        this.lastFertilizedDate = lastFertilizedDate;
    }

    public LocalDateTime getLastPrunedDate() {
        return lastPrunedDate;
    }

    public void setLastPrunedDate(LocalDateTime lastPrunedDate) {
        this.lastPrunedDate = lastPrunedDate;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }
}