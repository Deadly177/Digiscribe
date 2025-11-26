package com.digiscrib.controller;

import com.digiscrib.dto.*;
import com.digiscrib.service.MlGatewayService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/models")
@CrossOrigin(origins = "*")
public class ModelController {

    private final MlGatewayService mlGatewayService;

    public ModelController(MlGatewayService mlGatewayService) {
        this.mlGatewayService = mlGatewayService;
    }

    @GetMapping
    public List<MlModelInfo> getModels() {
        return mlGatewayService.getModels();
    }

    @PostMapping("/create")
    public MlModelInfo createModel(@RequestBody TrainingConfigRequest configRequest) {
        return mlGatewayService.createModel(configRequest);
    }

    @PostMapping("/{modelId}/activate")
    public Map<String, Object> activateModel(@PathVariable String modelId) {
        return mlGatewayService.activateModel(modelId);
    }

    @PostMapping("/{modelId}/train")
    public Map<String, Object> trainModel(@PathVariable String modelId) {
        return mlGatewayService.trainModel(modelId);
    }

    @GetMapping("/{modelId}/training-progress")
    public TrainingProgressResponse getTrainingProgress(@PathVariable String modelId) {
        return mlGatewayService.getTrainingProgress(modelId);
    }

    @DeleteMapping("/{modelId}")
    public Map<String, Object> deleteModel(@PathVariable String modelId) {
        return mlGatewayService.deleteModel(modelId);
    }

    @GetMapping("/accuracy-by-digit")
    public List<MlAccuracyByDigit> accuracyByDigit() {
        return mlGatewayService.getAccuracyByDigit();
    }

    @GetMapping("/{modelId}/details")
    public MlModelInfo getDetails(@PathVariable String modelId) {
        return mlGatewayService.getModelDetails(modelId);
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        return ResponseEntity.ok(mlGatewayService.health());
    }
}
