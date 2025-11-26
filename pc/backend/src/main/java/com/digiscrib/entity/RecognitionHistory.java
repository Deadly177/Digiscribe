package com.digiscrib.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "recognition_history")
public class RecognitionHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String inputType; // "drawing" or "upload"

    private String originalFilename;

    private Integer predictedDigit;

    private Double confidence;

    private Integer actualDigit; // For feedback

    private Boolean correctPrediction;

    private LocalDateTime recognitionTime;

    private String imagePath; // Store processed image path

    @PrePersist
    protected void onCreate() {
        recognitionTime = LocalDateTime.now();
    }

    // Constructors
    public RecognitionHistory() {}

    public RecognitionHistory(User user, String inputType, Integer predictedDigit, Double confidence) {
        this.user = user;
        this.inputType = inputType;
        this.predictedDigit = predictedDigit;
        this.confidence = confidence;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public String getInputType() { return inputType; }
    public void setInputType(String inputType) { this.inputType = inputType; }
    public String getOriginalFilename() { return originalFilename; }
    public void setOriginalFilename(String originalFilename) { this.originalFilename = originalFilename; }
    public Integer getPredictedDigit() { return predictedDigit; }
    public void setPredictedDigit(Integer predictedDigit) { this.predictedDigit = predictedDigit; }
    public Double getConfidence() { return confidence; }
    public void setConfidence(Double confidence) { this.confidence = confidence; }
    public Integer getActualDigit() { return actualDigit; }
    public void setActualDigit(Integer actualDigit) { this.actualDigit = actualDigit; }
    public Boolean getCorrectPrediction() { return correctPrediction; }
    public void setCorrectPrediction(Boolean correctPrediction) { this.correctPrediction = correctPrediction; }
    public LocalDateTime getRecognitionTime() { return recognitionTime; }
    public void setRecognitionTime(LocalDateTime recognitionTime) { this.recognitionTime = recognitionTime; }
    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }
}