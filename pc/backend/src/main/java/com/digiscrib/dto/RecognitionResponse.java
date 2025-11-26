package com.digiscrib.dto;

public class RecognitionResponse {
    private Integer predictedDigit;
    private Double confidence;
    private Long historyId;
    private String message;
    private java.util.List<Double> confidenceDistribution;
    private Integer processingTimeMs;
    private String modelUsed;
    private String modelId;

    // Constructors
    public RecognitionResponse() {}
    public RecognitionResponse(Integer predictedDigit, Double confidence, Long historyId, String message) {
        this.predictedDigit = predictedDigit;
        this.confidence = confidence;
        this.historyId = historyId;
        this.message = message;
    }

    // Getters and Setters
    public Integer getPredictedDigit() { return predictedDigit; }
    public void setPredictedDigit(Integer predictedDigit) { this.predictedDigit = predictedDigit; }
    public Double getConfidence() { return confidence; }
    public void setConfidence(Double confidence) { this.confidence = confidence; }
    public Long getHistoryId() { return historyId; }
    public void setHistoryId(Long historyId) { this.historyId = historyId; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public java.util.List<Double> getConfidenceDistribution() { return confidenceDistribution; }
    public void setConfidenceDistribution(java.util.List<Double> confidenceDistribution) { this.confidenceDistribution = confidenceDistribution; }
    public Integer getProcessingTimeMs() { return processingTimeMs; }
    public void setProcessingTimeMs(Integer processingTimeMs) { this.processingTimeMs = processingTimeMs; }
    public String getModelUsed() { return modelUsed; }
    public void setModelUsed(String modelUsed) { this.modelUsed = modelUsed; }
    public String getModelId() { return modelId; }
    public void setModelId(String modelId) { this.modelId = modelId; }
}
