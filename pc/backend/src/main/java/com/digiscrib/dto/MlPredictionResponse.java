package com.digiscrib.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MlPredictionResponse {

    @JsonProperty("predicted_digit")
    private Integer predictedDigit;

    private Double confidence;

    @JsonProperty("confidence_distribution")
    private List<Double> confidenceDistribution;

    @JsonProperty("processing_time")
    private Integer processingTime;

    @JsonProperty("model_used")
    private String modelUsed;

    @JsonProperty("model_id")
    private String modelId;

    public Integer getPredictedDigit() {
        return predictedDigit;
    }

    public void setPredictedDigit(Integer predictedDigit) {
        this.predictedDigit = predictedDigit;
    }

    public Double getConfidence() {
        return confidence;
    }

    public void setConfidence(Double confidence) {
        this.confidence = confidence;
    }

    public List<Double> getConfidenceDistribution() {
        return confidenceDistribution;
    }

    public void setConfidenceDistribution(List<Double> confidenceDistribution) {
        this.confidenceDistribution = confidenceDistribution;
    }

    public Integer getProcessingTime() {
        return processingTime;
    }

    public void setProcessingTime(Integer processingTime) {
        this.processingTime = processingTime;
    }

    public String getModelUsed() {
        return modelUsed;
    }

    public void setModelUsed(String modelUsed) {
        this.modelUsed = modelUsed;
    }

    public String getModelId() {
        return modelId;
    }

    public void setModelId(String modelId) {
        this.modelId = modelId;
    }
}
