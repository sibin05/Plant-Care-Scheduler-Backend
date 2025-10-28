package com.examly.plantcare.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.examly.plantcare.entity.User;
import com.examly.plantcare.entity.Plant;
import com.examly.plantcare.entity.Species;
import com.examly.plantcare.entity.EnvironmentData;
import com.examly.plantcare.entity.HealthRecord;
import com.examly.plantcare.entity.CareTask;
import com.examly.plantcare.repository.UserRepository;
import com.examly.plantcare.repository.PlantRepository;
import com.examly.plantcare.repository.SpeciesRepository;
import com.examly.plantcare.repository.EnvironmentDataRepository;
import com.examly.plantcare.repository.HealthRecordRepository;
import com.examly.plantcare.repository.CareTaskRepository;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PlantRepository plantRepository;
    
    @Autowired
    private SpeciesRepository speciesRepository;
    
    @Autowired
    private EnvironmentDataRepository environmentDataRepository;
    
    @Autowired
    private HealthRecordRepository healthRecordRepository;
    
    @Autowired
    private CareTaskRepository careTaskRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Skip clearing - just add sample data if tables are empty
        System.out.println("Checking for existing data...");
        
        // Create species data if none exists
        if (speciesRepository.count() == 0) {
            createSpeciesData();
        }
        
        // Create a test user if none exists
        if (userRepository.count() == 0) {
            User testUser = new User();
            testUser.setUsername("admin");
            testUser.setEmail("admin@test.com");
            testUser.setPasswordHash(passwordEncoder.encode("password"));
            testUser.setRole(User.Role.ADMIN);
            testUser.setGardeningExperience(User.GardeningExperience.EXPERT);
            testUser.setLocation("Test Location");
            testUser.setTimezone("UTC");
            testUser.setIsActive(true);
            testUser.setEmailVerified(true);
            
            userRepository.save(testUser);
            System.out.println("Test user created: username=admin, password=password");
        }
        
        // Create sample plants if none exist
        if (plantRepository.count() == 0) {
            User adminUser = userRepository.findByUsername("admin").orElse(null);
            if (adminUser != null) {
                Plant plant1 = new Plant();
                plant1.setNickname("My Red Rose");
                plant1.setLocation("Garden");
                plant1.setHealthStatus(Plant.HealthStatus.GOOD);
                plant1.setNotes("Beautiful red roses");
                plant1.setOwner(adminUser);
                plantRepository.save(plant1);
                
                Plant plant2 = new Plant();
                plant2.setNickname("Living Room Monstera");
                plant2.setLocation("Living Room");
                plant2.setHealthStatus(Plant.HealthStatus.EXCELLENT);
                plant2.setNotes("Large beautiful leaves");
                plant2.setOwner(adminUser);
                plantRepository.save(plant2);
                
                System.out.println("Sample plants created");
            }
        }
        
        if (plantRepository.count() > 0) {
            createSampleData();
        } else {
            System.out.println("No plants found - skipping sample data creation");
        }
    }
    
    private void createSampleData() {
        List<Plant> plants = plantRepository.findAll();
        System.out.println("Found " + plants.size() + " plants for sample data creation");
        if (plants.isEmpty()) {
            System.out.println("No plants found - cannot create sample data");
            return;
        }
        
        // Add sample environment data only if none exists
        if (environmentDataRepository.count() == 0) {
            for (Plant plant : plants) {
                EnvironmentData envData = new EnvironmentData();
                envData.setPlant(plant);
                envData.setLocationId("Sensor_" + plant.getId());
                envData.setTemperatureCelsius(22.5 + (plant.getId() * 2));
                envData.setHumidityPercentage(65.0 + plant.getId());
                envData.setLightLevelLux((int)(1500 + (plant.getId() * 100)));
                envData.setSoilMoisturePercentage(45.0 + plant.getId());
                envData.setRecordedDate(LocalDateTime.now().minusDays(plant.getId()));
                envData.setDataSource(EnvironmentData.DataSource.SENSOR);
                environmentDataRepository.save(envData);
            }
            System.out.println("Sample environment data created");
        }
        
        // Always create fresh health records for testing
        healthRecordRepository.deleteAll();
        for (Plant plant : plants) {
            HealthRecord healthRecord = new HealthRecord();
            healthRecord.setPlant(plant);
            healthRecord.setOverallHealth(HealthRecord.OverallHealth.GOOD);
            healthRecord.setSymptoms("No visible issues");
            healthRecord.setNotes("Regular checkup - plant looks healthy");
            healthRecord.setRecoveryStatus(HealthRecord.RecoveryStatus.NOT_APPLICABLE);
            healthRecord.setAssessmentDate(LocalDateTime.now().minusDays(plant.getId()));
            healthRecordRepository.save(healthRecord);
        }
        System.out.println("Sample health records created: " + healthRecordRepository.count());
        
        // Always create fresh care tasks for testing
        careTaskRepository.deleteAll();
        for (Plant plant : plants) {
            // Watering task
            CareTask waterTask = new CareTask();
            waterTask.setPlant(plant);
            waterTask.setTaskType(CareTask.TaskType.WATERING);
            waterTask.setScheduledDate(LocalDateTime.now().plusDays(1));
            waterTask.setStatus(CareTask.Status.PENDING);
            waterTask.setPriority(CareTask.Priority.HIGH);
            waterTask.setNotes("Regular watering schedule");
            waterTask.setCreatedDate(LocalDateTime.now());
            careTaskRepository.save(waterTask);
            
            // Fertilizing task
            CareTask fertTask = new CareTask();
            fertTask.setPlant(plant);
            fertTask.setTaskType(CareTask.TaskType.FERTILIZING);
            fertTask.setScheduledDate(LocalDateTime.now().plusDays(7));
            fertTask.setStatus(CareTask.Status.PENDING);
            fertTask.setPriority(CareTask.Priority.MEDIUM);
            fertTask.setNotes("Monthly fertilizing");
            fertTask.setCreatedDate(LocalDateTime.now());
            careTaskRepository.save(fertTask);
        }
        System.out.println("Sample care tasks created: " + careTaskRepository.count());
    }
    
    private void createSpeciesData() {
        Species species1 = new Species();
        species1.setCommonName("Monstera Deliciosa");
        species1.setScientificName("Monstera deliciosa");
        species1.setFamilyName("Araceae");
        speciesRepository.save(species1);
        
        Species species2 = new Species();
        species2.setCommonName("Snake Plant");
        species2.setScientificName("Sansevieria trifasciata");
        species2.setFamilyName("Asparagaceae");
        speciesRepository.save(species2);
        
        System.out.println("Sample species data created");
    }
}