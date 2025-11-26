package com.digiscrib.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "prediction_history")
public class PredictionHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    
    private Integer predictedDigit;
    private Integer actualDigit;
    
    private Double confidence;
    
    @Lob
    private String imagePath; // or store image data
    
    private LocalDateTime predictionTime;
    
    private Boolean isCorrect;
    
    // constructors, getters, setters
}