package com.digiscrib.service;

import com.digiscrib.dto.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MlGatewayService {

    private final RestTemplate restTemplate;
    private final String mlServiceUrl;

    public MlGatewayService(RestTemplate restTemplate, @Value("${ml.service.url}") String mlServiceUrl) {
        this.restTemplate = restTemplate;
        this.mlServiceUrl = mlServiceUrl;
    }

    public MlPredictionResponse predict(Object imagePayload) {
        Map<String, Object> request = new HashMap<>();
        request.put("image", imagePayload);
        request.put("timestamp", Instant.now().toString());
        ResponseEntity<MlPredictionResponse> response =
            restTemplate.postForEntity(mlServiceUrl + "/api/predict", request, MlPredictionResponse.class);
        return response.getBody();
    }

    public List<MlModelInfo> getModels() {
        ResponseEntity<List<MlModelInfo>> response = restTemplate.exchange(
            mlServiceUrl + "/api/models",
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<MlModelInfo>>() {}
        );
        return response.getBody();
    }

    public MlModelInfo createModel(TrainingConfigRequest configRequest) {
        ResponseEntity<MlModelInfo> response =
            restTemplate.postForEntity(mlServiceUrl + "/api/models/create", configRequest, MlModelInfo.class);
        return response.getBody();
    }

    public Map<String, Object> activateModel(String modelId) {
        ResponseEntity<Map> response =
            restTemplate.postForEntity(mlServiceUrl + "/api/models/" + modelId + "/activate", null, Map.class);
        return response.getBody();
    }

    public Map<String, Object> trainModel(String modelId) {
        ResponseEntity<Map> response =
            restTemplate.postForEntity(mlServiceUrl + "/api/models/" + modelId + "/train", null, Map.class);
        return response.getBody();
    }

    public TrainingProgressResponse getTrainingProgress(String modelId) {
        ResponseEntity<TrainingProgressResponse> response = restTemplate.getForEntity(
            mlServiceUrl + "/api/models/" + modelId + "/training-progress",
            TrainingProgressResponse.class
        );
        return response.getBody();
    }

    public Map<String, Object> deleteModel(String modelId) {
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
            mlServiceUrl + "/api/models/" + modelId,
            HttpMethod.DELETE,
            HttpEntity.EMPTY,
            new ParameterizedTypeReference<Map<String, Object>>() {}
        );
        return response.getBody();
    }

    public List<MlAccuracyByDigit> getAccuracyByDigit() {
        ResponseEntity<List<MlAccuracyByDigit>> response = restTemplate.exchange(
            mlServiceUrl + "/api/models/accuracy-by-digit",
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<MlAccuracyByDigit>>() {}
        );
        return response.getBody();
    }

    public MlAnalyticsOverview getAnalyticsOverview() {
        ResponseEntity<MlAnalyticsOverview> response =
            restTemplate.getForEntity(mlServiceUrl + "/api/analytics/overview", MlAnalyticsOverview.class);
        return response.getBody();
    }

    public MlModelInfo getModelDetails(String modelId) {
        ResponseEntity<MlModelInfo> response =
            restTemplate.getForEntity(mlServiceUrl + "/api/models/" + modelId + "/details", MlModelInfo.class);
        return response.getBody();
    }

    public Map<String, Object> health() {
        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(mlServiceUrl + "/health", Map.class);
            return response.getBody();
        } catch (RestClientException ex) {
            Map<String, Object> fallback = new HashMap<>();
            fallback.put("status", "error");
            fallback.put("message", ex.getMessage());
            return fallback;
        }
    }
}
