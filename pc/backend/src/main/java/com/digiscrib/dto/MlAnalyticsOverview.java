package com.digiscrib.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MlAnalyticsOverview {
    private Integer totalPredictions;
    private Integer activeModels;
    private Integer totalModels;
    private Double averageAccuracy;
    private Integer totalTrainingSamples;

    public Integer getTotalPredictions() {
        return totalPredictions;
    }

    public void setTotalPredictions(Integer totalPredictions) {
        this.totalPredictions = totalPredictions;
    }

    public Integer getActiveModels() {
        return activeModels;
    }

    public void setActiveModels(Integer activeModels) {
        this.activeModels = activeModels;
    }

    public Integer getTotalModels() {
        return totalModels;
    }

    public void setTotalModels(Integer totalModels) {
        this.totalModels = totalModels;
    }

    public Double getAverageAccuracy() {
        return averageAccuracy;
    }

    public void setAverageAccuracy(Double averageAccuracy) {
        this.averageAccuracy = averageAccuracy;
    }

    public Integer getTotalTrainingSamples() {
        return totalTrainingSamples;
    }

    public void setTotalTrainingSamples(Integer totalTrainingSamples) {
        this.totalTrainingSamples = totalTrainingSamples;
    }
}
