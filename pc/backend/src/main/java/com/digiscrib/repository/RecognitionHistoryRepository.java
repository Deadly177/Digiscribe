package com.digiscrib.repository;

import com.digiscrib.entity.RecognitionHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RecognitionHistoryRepository extends JpaRepository<RecognitionHistory, Long> {
    List<RecognitionHistory> findByUserIdOrderByRecognitionTimeDesc(Long userId);
    
    @Query("SELECT COUNT(r) FROM RecognitionHistory r WHERE r.recognitionTime >= :startDate")
    long countSince(LocalDateTime startDate);
    
    @Query("SELECT r.predictedDigit, COUNT(r) FROM RecognitionHistory r GROUP BY r.predictedDigit")
    List<Object[]> countPredictionsByDigit();
    
    @Query("SELECT AVG(r.confidence) FROM RecognitionHistory r")
    Double findAverageConfidence();
    
    @Query("SELECT COUNT(r) FROM RecognitionHistory r WHERE r.correctPrediction = true")
    long countCorrectPredictions();

    @Query("SELECT COUNT(r) FROM RecognitionHistory r WHERE r.actualDigit IS NOT NULL")
    long countWithFeedback();
}
