package com.digiscrib.service;

import com.digiscrib.dto.MlAnalyticsOverview;
import com.digiscrib.dto.MlModelInfo;
import com.digiscrib.entity.SystemLog;
import com.digiscrib.entity.User;
import com.digiscrib.repository.SystemLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AdminService {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private RecognitionHistoryService recognitionHistoryService;
    
    @Autowired
    private SystemLogRepository systemLogRepository;

    @Autowired
    private MlGatewayService mlGatewayService;
    
    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        long totalPredictions = recognitionHistoryService.getTotalRecognitions();
        long todaysPredictions = recognitionHistoryService.getRecognitionsToday();
        double accuracy = recognitionHistoryService.getAccuracy();
        long feedbackCount = recognitionHistoryService.getFeedbackCount();
        
        stats.put("totalPredictions", totalPredictions);
        stats.put("accuracy", accuracy);
        stats.put("activeModels", getActiveModelCount());
        stats.put("feedbackCount", feedbackCount);
        stats.put("predictionChange", calculatePercentageChange(todaysPredictions, totalPredictions));
        stats.put("accuracyChange", Math.round((accuracy - 95.0) * 10.0) / 10.0); // relative to baseline
        stats.put("feedbackChange", calculatePercentageChange(feedbackCount, Math.max(totalPredictions, 1)));
        stats.put("serverTime", LocalDateTime.now().toString());
        
        return stats;
    }
    
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }
    
    public List<SystemLog> getSystemLogs() {
        return systemLogRepository.findByTimestampBetweenOrderByTimestampDesc(
            LocalDateTime.now().minusDays(7), LocalDateTime.now());
    }
    
    public Map<String, Object> getAnalytics() {
        Map<String, Object> analytics = new HashMap<>();
        analytics.put("recognitionStats", getRecognitionStats());
        analytics.put("systemHealth", getSystemHealth());
        analytics.put("mlOverview", fetchMlOverview());
        analytics.put("digitAccuracy", fetchDigitAccuracy());
        
        return analytics;
    }
    
    private Map<String, Object> getUserGrowthData() {
        Map<String, Object> growth = new HashMap<>();
        growth.put("lastWeek", 15);
        growth.put("lastMonth", 45);
        growth.put("total", userService.getUserCount());
        return growth;
    }
    
    private Map<String, Object> getRecognitionStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("total", recognitionHistoryService.getTotalRecognitions());
        stats.put("today", recognitionHistoryService.getRecognitionsToday());
        stats.put("accuracy", recognitionHistoryService.getAccuracy());
        stats.put("feedbackCount", recognitionHistoryService.getFeedbackCount());
        return stats;
    }
    
    private Map<String, Object> getSystemHealth() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "Healthy");
        health.put("uptime", "99.8%");
        health.put("lastIncident", "None");
        health.put("activeUsers", 5);
        return health;
    }

    private Map<String, Object> fetchMlOverview() {
        try {
            MlAnalyticsOverview overview = mlGatewayService.getAnalyticsOverview();
            Map<String, Object> payload = new HashMap<>();
            if (overview != null) {
                payload.put("totalPredictions", overview.getTotalPredictions());
                payload.put("activeModels", overview.getActiveModels());
                payload.put("totalModels", overview.getTotalModels());
                payload.put("averageAccuracy", overview.getAverageAccuracy());
                payload.put("totalTrainingSamples", overview.getTotalTrainingSamples());
            }
            return payload;
        } catch (Exception ex) {
            Map<String, Object> fallback = new HashMap<>();
            fallback.put("totalPredictions", 0);
            fallback.put("activeModels", getActiveModelCount());
            fallback.put("totalModels", 0);
            fallback.put("averageAccuracy", 0);
            fallback.put("totalTrainingSamples", 0);
            fallback.put("error", ex.getMessage());
            return fallback;
        }
    }

    private List<?> fetchDigitAccuracy() {
        try {
            return mlGatewayService.getAccuracyByDigit();
        } catch (Exception ex) {
            return List.of();
        }
    }

    private int getActiveModelCount() {
        try {
            List<MlModelInfo> models = mlGatewayService.getModels();
            return (int) models.stream().filter(model -> "active".equalsIgnoreCase(model.getStatus())).count();
        } catch (Exception ex) {
            return 0;
        }
    }

    private double calculatePercentageChange(long portion, long total) {
        if (total <= 0) {
            return 0.0;
        }
        return Math.round(((double) portion / total) * 1000.0) / 10.0;
    }
    
    public void logSystemEvent(String level, String message, String source, String username) {
        SystemLog log = new SystemLog(level, message, source);
        log.setUsername(username);
        systemLogRepository.save(log);
    }
}
