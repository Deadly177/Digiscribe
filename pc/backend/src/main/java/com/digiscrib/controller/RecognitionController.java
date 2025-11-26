package com.digiscrib.controller;

import com.digiscrib.dto.*;
import com.digiscrib.entity.RecognitionHistory;
import com.digiscrib.entity.User;
import com.digiscrib.service.MNISTRecognitionService;
import com.digiscrib.service.RecognitionHistoryService;
import com.digiscrib.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/recognition")
@CrossOrigin(origins = "*")
public class RecognitionController {
    
    @Autowired
    private MNISTRecognitionService recognitionService;
    
    @Autowired
    private RecognitionHistoryService historyService;
    
    @Autowired
    private UserService userService;
    
    @PostMapping("/predict")
    public ResponseEntity<?> predictDigit(
            @RequestBody RecognitionRequest request,
            @AuthenticationPrincipal String username) {
        
        try {
            User user = null;
            if (username != null && !"anonymousUser".equalsIgnoreCase(username)) {
                user = userService.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            }
            
            // Perform digit recognition
            MNISTRecognitionService.RecognitionResult result = 
                recognitionService.predictDigit(request.getImageData());
            
            // Save to history
            RecognitionHistory history = null;
            if (user != null) {
                history = historyService.saveRecognition(
                    user,
                    request.getInputType(),
                    null,
                    result.getDigit(),
                    result.getConfidence()
                );
            }

            RecognitionResponse response = new RecognitionResponse(
                result.getDigit(),
                result.getConfidence(),
                history != null ? history.getId() : null,
                "Recognition successful"
            );
            response.setConfidenceDistribution(result.getConfidenceDistribution());
            response.setProcessingTimeMs(result.getProcessingTimeMs());
            response.setModelUsed(result.getModelUsed());
            response.setModelId(result.getModelId());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new RecognitionResponse(null, null, null, "Recognition failed: " + e.getMessage()));
        }
    }
    
    @PostMapping("/feedback")
    public ResponseEntity<?> provideFeedback(@RequestBody FeedbackRequest request) {
        try {
            RecognitionHistory history = historyService.provideFeedback(
                request.getHistoryId(),
                request.getActualDigit()
            );
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Feedback saved successfully");
            response.put("correct", history.getCorrectPrediction());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("message", "Failed to save feedback: " + e.getMessage()));
        }
    }
    
    @GetMapping("/history")
    public ResponseEntity<?> getUserHistory(@AuthenticationPrincipal String username) {
        try {
            User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
            
            return ResponseEntity.ok(historyService.getUserHistory(user.getId()));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("message", "Failed to fetch history: " + e.getMessage()));
        }
    }
}
