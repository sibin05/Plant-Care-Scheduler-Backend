package com.examly.plantcare.service;

import com.examly.plantcare.entity.Plant;
import com.examly.plantcare.entity.EnvironmentData;
import com.examly.plantcare.repository.EnvironmentDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Service
public class PlantScheduleService {

    @Autowired
    private EnvironmentDataRepository environmentDataRepository;

    public Map<String, Object> getPlantSchedule(Plant plant) {
        Map<String, Object> schedule = new HashMap<>();
        
        // Get latest environment data for intelligent scheduling
        EnvironmentData latestEnvData = getLatestEnvironmentData(plant.getId());
        
        LocalDateTime nextWatering = calculateNextWatering(plant, latestEnvData);
        LocalDateTime nextFertilizer = calculateNextFertilizer(plant, latestEnvData);
        LocalDateTime nextPruning = calculateNextPruning(plant);
        
        schedule.put("nextWatering", nextWatering);
        schedule.put("nextFertilizer", nextFertilizer);
        schedule.put("nextPruning", nextPruning);
        schedule.put("wateringDaysLeft", getDaysUntil(nextWatering));
        schedule.put("fertilizerDaysLeft", getDaysUntil(nextFertilizer));
        schedule.put("pruningDaysLeft", getDaysUntil(nextPruning));
        
        // Add optimal care times
        schedule.put("optimalWateringTime", getOptimalWateringTime());
        schedule.put("optimalFertilizerTime", getOptimalFertilizerTime());
        
        // Add care recommendations based on environment
        schedule.put("recommendations", getCareRecommendations(plant, latestEnvData));
        
        return schedule;
    }

    private LocalDateTime calculateNextWatering(Plant plant, EnvironmentData envData) {
        LocalDateTime lastWatered = plant.getLastWateredDate();
        if (lastWatered == null) {
            lastWatered = plant.getCreatedDate();
        }
        
        int wateringInterval = getSmartWateringInterval(plant, envData);
        LocalDateTime nextWatering = lastWatered.plusDays(wateringInterval);
        
        // Set optimal time (early morning)
        return nextWatering.with(LocalTime.of(7, 0));
    }

    private LocalDateTime calculateNextFertilizer(Plant plant, EnvironmentData envData) {
        LocalDateTime lastFertilized = plant.getLastFertilizedDate();
        if (lastFertilized == null) {
            lastFertilized = plant.getCreatedDate();
        }
        
        int fertilizerInterval = getSmartFertilizerInterval(plant, envData);
        LocalDateTime nextFertilizer = lastFertilized.plusDays(fertilizerInterval);
        
        // Set optimal time (morning)
        return nextFertilizer.with(LocalTime.of(8, 0));
    }

    private LocalDateTime calculateNextPruning(Plant plant) {
        LocalDateTime lastPruned = plant.getLastPrunedDate();
        if (lastPruned == null) {
            lastPruned = plant.getCreatedDate();
        }
        
        // Pruning every 30-45 days depending on plant health
        int pruningInterval = plant.getHealthStatus() == Plant.HealthStatus.EXCELLENT ? 45 : 30;
        return lastPruned.plusDays(pruningInterval).with(LocalTime.of(9, 0));
    }

    private int getSmartWateringInterval(Plant plant, EnvironmentData envData) {
        int baseInterval = 3; // Default 3 days
        
        // Adjust based on location
        String location = plant.getLocation();
        if (location != null && location.toLowerCase().contains("outdoor")) {
            baseInterval = 2;
        }
        
        // Adjust based on environment data
        if (envData != null) {
            // High temperature = more frequent watering
            if (envData.getTemperatureCelsius() != null && envData.getTemperatureCelsius() > 28) {
                baseInterval -= 1;
            }
            
            // Low humidity = more frequent watering
            if (envData.getHumidityPercentage() != null && envData.getHumidityPercentage() < 40) {
                baseInterval -= 1;
            }
            
            // High soil moisture = less frequent watering
            if (envData.getSoilMoisturePercentage() != null && envData.getSoilMoisturePercentage() > 70) {
                baseInterval += 1;
            }
        }
        
        // Adjust based on plant health
        if (plant.getHealthStatus() == Plant.HealthStatus.POOR || 
            plant.getHealthStatus() == Plant.HealthStatus.CRITICAL) {
            baseInterval -= 1;
        }
        
        return Math.max(1, Math.min(baseInterval, 7)); // Between 1-7 days
    }

    private int getSmartFertilizerInterval(Plant plant, EnvironmentData envData) {
        int baseInterval = 14; // Default 2 weeks
        
        // Adjust based on plant health
        switch (plant.getHealthStatus()) {
            case POOR:
            case CRITICAL:
                baseInterval = 10; // More frequent for unhealthy plants
                break;
            case EXCELLENT:
                baseInterval = 21; // Less frequent for healthy plants
                break;
            default:
                baseInterval = 14;
        }
        
        // Adjust based on environment (growing season)
        if (envData != null && envData.getTemperatureCelsius() != null) {
            if (envData.getTemperatureCelsius() > 20 && envData.getTemperatureCelsius() < 30) {
                baseInterval -= 3; // Growing season
            }
        }
        
        return Math.max(7, Math.min(baseInterval, 30)); // Between 1-4 weeks
    }

    private String getOptimalWateringTime() {
        return "Early morning (6-8 AM) for best absorption";
    }

    private String getOptimalFertilizerTime() {
        return "Morning (8-10 AM) after watering";
    }

    private List<String> getCareRecommendations(Plant plant, EnvironmentData envData) {
        List<String> recommendations = new ArrayList<>();
        
        if (envData != null) {
            // Temperature recommendations
            if (envData.getTemperatureCelsius() != null) {
                if (envData.getTemperatureCelsius() > 30) {
                    recommendations.add("🌡️ Temperature too high - provide shade or move to cooler location");
                } else if (envData.getTemperatureCelsius() < 15) {
                    recommendations.add("🌡️ Temperature too low - move to warmer location or provide protection");
                }
            }
            
            // Humidity recommendations
            if (envData.getHumidityPercentage() != null) {
                if (envData.getHumidityPercentage() < 40) {
                    recommendations.add("💧 Low humidity - consider using humidifier or water tray");
                } else if (envData.getHumidityPercentage() > 70) {
                    recommendations.add("💧 High humidity - ensure good air circulation");
                }
            }
            
            // Light recommendations
            if (envData.getLightLevelLux() != null) {
                if (envData.getLightLevelLux() < 1000) {
                    recommendations.add("☀️ Insufficient light - move to brighter location or add grow lights");
                } else if (envData.getLightLevelLux() > 50000) {
                    recommendations.add("☀️ Too much direct light - provide partial shade");
                }
            }
            
            // Soil moisture recommendations
            if (envData.getSoilMoisturePercentage() != null) {
                if (envData.getSoilMoisturePercentage() < 30) {
                    recommendations.add("🌱 Soil too dry - water immediately");
                } else if (envData.getSoilMoisturePercentage() > 80) {
                    recommendations.add("🌱 Soil too wet - reduce watering frequency and check drainage");
                }
            }
        }
        
        // Health-based recommendations
        switch (plant.getHealthStatus()) {
            case POOR:
                recommendations.add("🏥 Plant health is poor - check for pests and diseases");
                break;
            case CRITICAL:
                recommendations.add("🚨 Plant health is critical - immediate attention required");
                break;
        }
        
        if (recommendations.isEmpty()) {
            recommendations.add("✅ Plant conditions are optimal - continue current care routine");
        }
        
        return recommendations;
    }

    private EnvironmentData getLatestEnvironmentData(Long plantId) {
        try {
            return environmentDataRepository.findTopByPlant_IdOrderByRecordedDateDesc(plantId);
        } catch (Exception e) {
            return null;
        }
    }

    private long getDaysUntil(LocalDateTime futureDate) {
        LocalDateTime now = LocalDateTime.now();
        return java.time.Duration.between(now, futureDate).toDays();
    }
}