package com.digiscrib.controller;

import com.digiscrib.dto.RecognitionRequest;
import com.digiscrib.service.MNISTRecognitionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/mobile")
@CrossOrigin(origins = "*")
public class MobileController {

    private final MNISTRecognitionService recognitionService;

    public MobileController(MNISTRecognitionService recognitionService) {
        this.recognitionService = recognitionService;
    }

    @PostMapping("/predict")
    public ResponseEntity<?> predictDigit(@RequestBody RecognitionRequest request) {
        Map<String, Object> response = new HashMap<>();
        try {
            MNISTRecognitionService.RecognitionResult result =
                recognitionService.predictDigit(request.getImageData());

            response.put("success", true);
            response.put("predicted_digit", result.getDigit());
            response.put("confidence", result.getConfidence());
            response.put("confidence_distribution", result.getConfidenceDistribution());
            response.put("processing_time_ms", result.getProcessingTimeMs());
            response.put("model_used", result.getModelUsed());
            response.put("model_id", result.getModelId());
            response.put("message", "Prediction successful");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/status")
    public Map<String, String> status() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "online");
        response.put("message", "MNIST Backend is running");
        return response;
    }
}
