package com.digiscrib.dto;

public class FeedbackRequest {
    private Long historyId;
    private Integer actualDigit;

    // Constructors
    public FeedbackRequest() {}
    public FeedbackRequest(Long historyId, Integer actualDigit) {
        this.historyId = historyId;
        this.actualDigit = actualDigit;
    }

    // Getters and Setters
    public Long getHistoryId() { return historyId; }
    public void setHistoryId(Long historyId) { this.historyId = historyId; }
    public Integer getActualDigit() { return actualDigit; }
    public void setActualDigit(Integer actualDigit) { this.actualDigit = actualDigit; }
}