package com.digiscrib.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TrainingConfigRequest {
    private String name;
    private String architecture = "cnn_simple";
    private Integer epochs = 5;

    @JsonProperty("learning_rate")
    private Double learningRate = 0.001;

    @JsonProperty("batch_size")
    private Integer batchSize = 128;

    @JsonProperty("use_pretrained")
    private Boolean usePretrained = true;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArchitecture() {
        return architecture;
    }

    public void setArchitecture(String architecture) {
        this.architecture = architecture;
    }

    public Integer getEpochs() {
        return epochs;
    }

    public void setEpochs(Integer epochs) {
        this.epochs = epochs;
    }

    public Double getLearningRate() {
        return learningRate;
    }

    public void setLearningRate(Double learningRate) {
        this.learningRate = learningRate;
    }

    public Integer getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(Integer batchSize) {
        this.batchSize = batchSize;
    }

    public Boolean getUsePretrained() {
        return usePretrained;
    }

    public void setUsePretrained(Boolean usePretrained) {
        this.usePretrained = usePretrained;
    }
}
