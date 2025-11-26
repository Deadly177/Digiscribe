package com.digiscrib.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TrainingProgressResponse {
    @JsonProperty("model_id")
    private String modelId;
    private String status;
    private Integer percentage;
    @JsonProperty("current_epoch")
    private Integer currentEpoch;
    @JsonProperty("total_epochs")
    private Integer totalEpochs;
    @JsonProperty("current_loss")
    private Double currentLoss;
    @JsonProperty("current_accuracy")
    private Double currentAccuracy;

    public String getModelId() {
        return modelId;
    }

    public void setModelId(String modelId) {
        this.modelId = modelId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getPercentage() {
        return percentage;
    }

    public void setPercentage(Integer percentage) {
        this.percentage = percentage;
    }

    public Integer getCurrentEpoch() {
        return currentEpoch;
    }

    public void setCurrentEpoch(Integer currentEpoch) {
        this.currentEpoch = currentEpoch;
    }

    public Integer getTotalEpochs() {
        return totalEpochs;
    }

    public void setTotalEpochs(Integer totalEpochs) {
        this.totalEpochs = totalEpochs;
    }

    public Double getCurrentLoss() {
        return currentLoss;
    }

    public void setCurrentLoss(Double currentLoss) {
        this.currentLoss = currentLoss;
    }

    public Double getCurrentAccuracy() {
        return currentAccuracy;
    }

    public void setCurrentAccuracy(Double currentAccuracy) {
        this.currentAccuracy = currentAccuracy;
    }
}
