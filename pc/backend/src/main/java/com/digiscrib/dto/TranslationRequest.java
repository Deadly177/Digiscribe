package com.digiscrib.dto;

public class TranslationRequest {
    private String text;
    private String targetLanguage = "zh"; // Chinese

    // Constructors
    public TranslationRequest() {}
    public TranslationRequest(String text, String targetLanguage) {
        this.text = text;
        this.targetLanguage = targetLanguage;
    }

    // Getters and Setters
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
    public String getTargetLanguage() { return targetLanguage; }
    public void setTargetLanguage(String targetLanguage) { this.targetLanguage = targetLanguage; }
}