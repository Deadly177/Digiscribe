package com.digiscrib.service;

import com.digiscrib.dto.MlPredictionResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MNISTRecognitionService {

    private final MlGatewayService mlGatewayService;

    public MNISTRecognitionService(MlGatewayService mlGatewayService) {
        this.mlGatewayService = mlGatewayService;
    }

    public RecognitionResult predictDigit(String imagePayload) {
        MlPredictionResponse mlResponse = mlGatewayService.predict(imagePayload);
        if (mlResponse == null || mlResponse.getPredictedDigit() == null) {
            throw new RuntimeException("ML service did not return a prediction");
        }
        return new RecognitionResult(
            mlResponse.getPredictedDigit(),
            mlResponse.getConfidence(),
            mlResponse.getConfidenceDistribution(),
            mlResponse.getProcessingTime(),
            mlResponse.getModelUsed(),
            mlResponse.getModelId()
        );
    }

    public static class RecognitionResult {
        private final int digit;
        private final double confidence;
        private final List<Double> confidenceDistribution;
        private final Integer processingTimeMs;
        private final String modelUsed;
        private final String modelId;

        public RecognitionResult(int digit,
                                 double confidence,
                                 List<Double> confidenceDistribution,
                                 Integer processingTimeMs,
                                 String modelUsed,
                                 String modelId) {
            this.digit = digit;
            this.confidence = confidence;
            this.confidenceDistribution = confidenceDistribution;
            this.processingTimeMs = processingTimeMs;
            this.modelUsed = modelUsed;
            this.modelId = modelId;
        }

        public int getDigit() {
            return digit;
        }

        public double getConfidence() {
            return confidence;
        }

        public List<Double> getConfidenceDistribution() {
            return confidenceDistribution;
        }

        public Integer getProcessingTimeMs() {
            return processingTimeMs;
        }

        public String getModelUsed() {
            return modelUsed;
        }

        public String getModelId() {
            return modelId;
        }
    }
}
