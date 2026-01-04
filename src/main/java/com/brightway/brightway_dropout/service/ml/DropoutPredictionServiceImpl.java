package com.brightway.brightway_dropout.service.ml;

import com.brightway.brightway_dropout.dto.ml.PredictionRequestDTO;
import com.brightway.brightway_dropout.dto.ml.PredictionRequestItem;
import com.brightway.brightway_dropout.dto.ml.PredictionResponseDTO;
import com.brightway.brightway_dropout.dto.ml.PredictionResultItem;
import com.brightway.brightway_dropout.dto.ml.StudentFeaturesDTO;
import com.brightway.brightway_dropout.dto.ml.TopFactor;
import com.brightway.brightway_dropout.dto.prediction.response.BatchPredictionResponseDTO;
import com.brightway.brightway_dropout.dto.prediction.response.PredictionItemResponseDTO;
import com.brightway.brightway_dropout.dto.prediction.response.RiskFactorDTO;
import com.brightway.brightway_dropout.dto.prediction.response.SinglePredictionResponseDTO;
import com.brightway.brightway_dropout.enumeration.ERiskLevel;
import com.brightway.brightway_dropout.enumeration.EStudentStatus;
import com.brightway.brightway_dropout.exception.MLServiceException;
import com.brightway.brightway_dropout.model.DropoutPrediction;
import com.brightway.brightway_dropout.model.Student;
import com.brightway.brightway_dropout.model.StudentMLFeatures;
import com.brightway.brightway_dropout.repository.IDropoutPredictionRepository;
import com.brightway.brightway_dropout.repository.IStudentMLFeaturesRepository;
import com.brightway.brightway_dropout.repository.IStudentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DropoutPredictionServiceImpl implements IDropoutPredictionService {
    
    @Autowired
    private IStudentRepository studentRepository;
    
    @Autowired
    private IDropoutPredictionRepository dropoutPredictionRepository;
    
    @Autowired
    private IStudentMLFeaturesRepository studentMLFeaturesRepository;
    
    @Autowired
    private IMLFeatureCalculationService featureCalculationService;
    
    @Autowired
    private IMLServiceClient mlServiceClient;
    
    private final AtomicBoolean isRunning = new AtomicBoolean(false);
    
    @Override
    @Scheduled(cron = "${ml.prediction.schedule:0 0 0 */14 * *}") // Every 2 weeks at midnight
    public void runScheduledPredictions() {
        log.info("Scheduled prediction job triggered");
        executeCore();
    }
    
    @Override
    @Transactional
    public BatchPredictionResponseDTO runManualPredictions() {
        log.info("Manual prediction job triggered by admin");
        return executeManualCore();
    }
    
    @Override
    @Transactional
    public SinglePredictionResponseDTO runPredictionForStudent(UUID studentId) {
        log.info("Running prediction for single student: {}", studentId);
        
        try {
            Student student = studentRepository.findById(studentId)
                    .orElseThrow(() -> new IllegalArgumentException("Student not found: " + studentId));
            
            PredictionResponseDTO mlResponse = runPredictionsForStudents(List.of(student));
            
            // Convert to frontend DTO
            if (mlResponse.getPredictions().isEmpty()) {
                throw new MLServiceException("No prediction result returned from ML service");
            }
            
            PredictionResultItem result = mlResponse.getPredictions().get(0);
            StudentFeaturesDTO features = featureCalculationService.calculateFeatures(student);
            
            return convertToSinglePredictionResponse(student, result, features);
            
        } catch (MLServiceException e) {
            log.error(" ML service error for student {}: {}", studentId, e.getMessage());
            throw e;
        } catch (IllegalArgumentException e) {
            log.error(" Student not found: {}", studentId);
            throw e;
        } catch (Exception e) {
            log.error(" Failed to run prediction for student {}: {}", studentId, e.getMessage(), e);
            throw new RuntimeException("Failed to run prediction: " + e.getMessage(), e);
        }
    }
    
    @Override
    @Transactional
    public PredictionResponseDTO runPredictionsForStudents(List<Student> students) {
        log.info("Running predictions for {} students", students.size());
        
        try {
            // Calculate features
            List<StudentFeaturesDTO> featuresList = featureCalculationService.calculateFeaturesForAll(students);
            
            // Build request
            List<PredictionRequestItem> requestItems = new ArrayList<>();
            for (int i = 0; i < students.size(); i++) {
                Student student = students.get(i);
                StudentFeaturesDTO features = featuresList.get(i);
                
                PredictionRequestItem item = new PredictionRequestItem();
                item.setStudentId(student.getId());
                item.setFeatures(features);
                requestItems.add(item);
            }
            
            PredictionRequestDTO request = new PredictionRequestDTO(requestItems);
            
            // Call ML service - may throw MLServiceException
            PredictionResponseDTO response = mlServiceClient.predictBatch(request);
            
            if (response == null || response.getPredictions() == null) {
                throw new MLServiceException("ML service returned invalid response");
            }
            
            // Save predictions and add top factors
            savePredictionsWithTopFactors(students, response, featuresList);
            
            return response;
            
        } catch (MLServiceException e) {
            log.error("ML service error: {}", e.getMessage());
            throw e;  // Re-throw to controller
        } catch (Exception e) {
            log.error("Failed to process predictions: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to process predictions: " + e.getMessage(), e);
        }
    }
    
    private void executeCore() {
        // Prevent duplicate runs
        if (!isRunning.compareAndSet(false, true)) {
            log.warn("Prediction job already running, skipping");
            return;
        }
        
        try {
            log.info("Starting batch dropout prediction job");
            
            // Get all active students
            List<Student> activeStudents = studentRepository.findAllByActive(true);
            
            if (activeStudents.isEmpty()) {
                log.warn("No active students found");
                return;
            }
            
            log.info("Found {} active students", activeStudents.size());
            
            // Run predictions
            PredictionResponseDTO response = runPredictionsForStudents(activeStudents);
            
            // Log results
            long highRiskCount = response.getPredictions().stream()
                    .filter(p -> "HIGH".equals(p.getRiskLevel()) || "CRITICAL".equals(p.getRiskLevel()))
                    .count();
            
            log.info("Batch prediction completed successfully");
            log.info("Processed {} students in {}ms", response.getProcessedCount(), response.getProcessingTimeMs());
            log.warn("{} students flagged as HIGH/CRITICAL risk", highRiskCount);
            
        } catch (MLServiceException e) {
            log.error("ML service error: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Batch prediction failed", e);
            throw new RuntimeException("Batch prediction failed: " + e.getMessage(), e);
        } finally {
            isRunning.set(false);
        }
    }
    
    private void savePredictionsWithTopFactors(
            List<Student> students,
            PredictionResponseDTO response,
            List<StudentFeaturesDTO> featuresList) {
        
        List<DropoutPrediction> predictions = new ArrayList<>();
        List<StudentMLFeatures> mlFeaturesList = new ArrayList<>();
        
        for (int i = 0; i < students.size(); i++) {
            Student student = students.get(i);
            StudentFeaturesDTO features = featuresList.get(i);
            
            // Find matching prediction result
            PredictionResultItem result = response.getPredictions().stream()
                    .filter(p -> matchesStudent(p.getStudentId(), student))
                    .findFirst()
                    .orElse(null);
            
            if (result == null) {
                log.warn("No prediction result found for student: {}", student.getId());
                continue;
            }
            
            // Prepare ML features for saving (kept in memory)
            mlFeaturesList.add(createMLFeatures(student, features));
            
            // Create prediction entity
            DropoutPrediction prediction = new DropoutPrediction();
            prediction.setStudent(student);
            prediction.setProbability(result.getProbability().floatValue());
            prediction.setRiskLevel(ERiskLevel.valueOf(result.getRiskLevel()));
            
            // Set top factor details
            if (result.getTopFactors() != null && !result.getTopFactors().isEmpty()) {
                TopFactor primaryFactor = result.getTopFactors().get(0);
                prediction.setTopFactor(primaryFactor.getFactor());
                prediction.setFactorMessage(primaryFactor.getMessage());
            }
            
            prediction.setStatus("ACTIVE");
            prediction.setPredictedAt(LocalDateTime.now());
            
            predictions.add(prediction);
        }
        
        // Batch save predictions first
        dropoutPredictionRepository.saveAll(predictions);
        log.info("Saved {} predictions to database", predictions.size());
        
        // Save ML features only after successful predictions save
        studentMLFeaturesRepository.saveAll(mlFeaturesList);
        log.info("Saved {} ML features to database", mlFeaturesList.size());
    }
    
    private boolean matchesStudent(UUID resultId, Student student) {
        return student.getId().equals(resultId);
    }
    
    private StudentMLFeatures createMLFeatures(Student student, StudentFeaturesDTO features) {
        StudentMLFeatures mlFeatures = new StudentMLFeatures();
        mlFeatures.setStudent(student);
        
        // Attendance features
        mlFeatures.setAttendanceRate(features.getAttendanceRate());
        mlFeatures.setDaysAbsent(features.getDaysAbsent());
        mlFeatures.setConsecutiveAbsences(features.getConsecutiveAbsences());
        
        // Grade features
        mlFeatures.setAverageMarks(features.getAverageMarks());
        mlFeatures.setFailingCoursesCount(features.getFailingCoursesCount());
        mlFeatures.setLowestGrade(features.getLowestGrade());
        
        // Behavior features
        mlFeatures.setIncidentCount(features.getIncidentCount());
        mlFeatures.setSeverityScore(features.getSeverityScore());
        mlFeatures.setDaysSinceLastIncident(features.getDaysSinceLastIncident());
        
        // Student features
        mlFeatures.setWeeksEnrolled(features.getWeeksEnrolled());
        mlFeatures.setAge(features.getAge());
        mlFeatures.setGenderEncoded(features.getGenderEncoded());
        
        LocalDateTime now = LocalDateTime.now();
        mlFeatures.setCalculatedAt(now);
        mlFeatures.setFeatureDate(now);
        
        return mlFeatures;
    }
    
    private BatchPredictionResponseDTO executeManualCore() {
        if (!isRunning.compareAndSet(false, true)) {
            log.warn("Prediction job already running, skipping");
            throw new IllegalStateException("Prediction job already running");
        }
        
        try {
            log.info("Starting batch dropout prediction job");
            
            List<Student> activeStudents = studentRepository.findAllByActive(true);
            
            if (activeStudents.isEmpty()) {
                log.warn("No active students found");
                return new BatchPredictionResponseDTO(0, List.of());
            }
            
            log.info("Found {} active students", activeStudents.size());
            
            PredictionResponseDTO mlResponse = runPredictionsForStudents(activeStudents);
            
            return convertToBatchPredictionResponse(activeStudents, mlResponse);
            
        } catch (MLServiceException e) {
            log.error("ML service error: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Batch prediction failed", e);
            throw e;
        } finally {
            isRunning.set(false);
        }
    }
    
    private BatchPredictionResponseDTO convertToBatchPredictionResponse(
            List<Student> students, 
            PredictionResponseDTO mlResponse) {
        
        List<PredictionItemResponseDTO> items = new ArrayList<>();
        
        for (Student student : students) {
            PredictionResultItem result = mlResponse.getPredictions().stream()
                    .filter(p -> matchesStudent(p.getStudentId(), student))
                    .findFirst()
                    .orElse(null);
            
            if (result != null) {
                DropoutPrediction savedPrediction = dropoutPredictionRepository
                        .findTopByStudentIdOrderByPredictedAtDesc(student.getId())
                        .orElse(null);
                
                PredictionItemResponseDTO item = new PredictionItemResponseDTO(
                        student.getId(),
                        student.getUser().getName(),
                        result.getProbability(),
                        result.getRiskLevel(),
                        savedPrediction != null ? savedPrediction.getTopFactor() : null,
                        savedPrediction != null ? savedPrediction.getPredictedAt() : LocalDateTime.now()
                );
                
                items.add(item);
            }
        }
        
        return new BatchPredictionResponseDTO(students.size(), items);
    }
    
    private SinglePredictionResponseDTO convertToSinglePredictionResponse(
            Student student, 
            PredictionResultItem result,
            StudentFeaturesDTO features) {
        
        DropoutPrediction savedPrediction = dropoutPredictionRepository
                .findTopByStudentIdOrderByPredictedAtDesc(student.getId())
                .orElse(null);
        
        return new SinglePredictionResponseDTO(
                student.getId(),
                student.getUser().getName(),
                result.getProbability(),
                result.getRiskLevel(),
                savedPrediction != null ? savedPrediction.getTopFactor() : null,
                savedPrediction != null ? savedPrediction.getPredictedAt() : LocalDateTime.now()
        );
    }
}