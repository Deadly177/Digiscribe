package com.digiscrib.repository;

import com.digiscrib.entity.PredictionHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PredictionHistoryRepository extends JpaRepository<PredictionHistory, Long> {
    List<PredictionHistory> findByUserIdOrderByPredictionTimeDesc(Long userId);
    Long countByUserIdAndIsCorrect(Long userId, Boolean isCorrect);
}