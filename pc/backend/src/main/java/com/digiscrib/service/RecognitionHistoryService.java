package com.digiscrib.service;

import com.digiscrib.entity.RecognitionHistory;
import com.digiscrib.entity.User;
import com.digiscrib.repository.RecognitionHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class RecognitionHistoryService {
    
    @Autowired
    private RecognitionHistoryRepository recognitionHistoryRepository;
    
    public RecognitionHistory saveRecognition(User user, String inputType, String filename, 
                                            Integer predictedDigit, Double confidence) {
        RecognitionHistory history = new RecognitionHistory(user, inputType, predictedDigit, confidence);
        history.setOriginalFilename(filename);
        return recognitionHistoryRepository.save(history);
    }
    
    public RecognitionHistory provideFeedback(Long historyId, Integer actualDigit) {
        Optional<RecognitionHistory> historyOpt = recognitionHistoryRepository.findById(historyId);
        if (historyOpt.isPresent()) {
            RecognitionHistory history = historyOpt.get();
            history.setActualDigit(actualDigit);
            history.setCorrectPrediction(history.getPredictedDigit().equals(actualDigit));
            return recognitionHistoryRepository.save(history);
        }
        throw new RuntimeException("Recognition history not found with id: " + historyId);
    }
    
    public List<RecognitionHistory> getUserHistory(Long userId) {
        return recognitionHistoryRepository.findByUserIdOrderByRecognitionTimeDesc(userId);
    }
    
    public long getTotalRecognitions() {
        return recognitionHistoryRepository.count();
    }
    
    public long getRecognitionsToday() {
        return recognitionHistoryRepository.countSince(LocalDateTime.now().minusDays(1));
    }
    
    public double getAccuracy() {
        long total = recognitionHistoryRepository.count();
        long correct = recognitionHistoryRepository.countCorrectPredictions();
        return total > 0 ? (double) correct / total * 100 : 0.0;
    }

    public long getFeedbackCount() {
        return recognitionHistoryRepository.countWithFeedback();
    }
}
