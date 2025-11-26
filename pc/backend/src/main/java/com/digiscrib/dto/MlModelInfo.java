package com.digiscrib.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MlModelInfo {

    private String id;
    private String name;
    private String version;
    private String status;
    private Double accuracy;

    @JsonProperty("prediction_count")
    private Integer predictionCount;

    @JsonProperty("training_samples")
    private Integer trainingSamples;

    @JsonProperty("last_trained")
    private String lastTrained;

    private String architecture;

    @JsonProperty("training_progress")
    private Integer trainingProgress;

    @JsonProperty("current_epoch")
    private Integer currentEpoch;

    @JsonProperty("total_epochs")
    private Integer totalEpochs;

    @JsonProperty("model_path")
    private String modelPath;

    @JsonProperty("created_at")
    private String createdAt;

    @JsonProperty("model_size_mb")
    private Double modelSizeMb;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Double getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(Double accuracy) {
        this.accuracy = accuracy;
    }

    public Integer getPredictionCount() {
        return predictionCount;
    }

    public void setPredictionCount(Integer predictionCount) {
        this.predictionCount = predictionCount;
    }

    public Integer getTrainingSamples() {
        return trainingSamples;
    }

    public void setTrainingSamples(Integer trainingSamples) {
        this.trainingSamples = trainingSamples;
    }

    public String getLastTrained() {
        return lastTrained;
    }

    public void setLastTrained(String lastTrained) {
        this.lastTrained = lastTrained;
    }

    public String getArchitecture() {
        return architecture;
    }

    public void setArchitecture(String architecture) {
        this.architecture = architecture;
    }

    public Integer getTrainingProgress() {
        return trainingProgress;
    }

    public void setTrainingProgress(Integer trainingProgress) {
        this.trainingProgress = trainingProgress;
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

    public String getModelPath() {
        return modelPath;
    }

    public void setModelPath(String modelPath) {
        this.modelPath = modelPath;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public Double getModelSizeMb() {
        return modelSizeMb;
    }

    public void setModelSizeMb(Double modelSizeMb) {
        this.modelSizeMb = modelSizeMb;
    }
}
