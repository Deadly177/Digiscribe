package com.digiscrib.dto;

public class RecognitionRequest {
    private String imageData; // Base64 encoded image
    private String inputType; // "drawing" or "upload"

    // Constructors
    public RecognitionRequest() {}
    public RecognitionRequest(String imageData, String inputType) {
        this.imageData = imageData;
        this.inputType = inputType;
    }

    // Getters and Setters
    public String getImageData() { return imageData; }
    public void setImageData(String imageData) { this.imageData = imageData; }
    public String getInputType() { return inputType; }
    public void setInputType(String inputType) { this.inputType = inputType; }
}