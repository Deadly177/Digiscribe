package com.digiscrib.repository;

import com.digiscrib.entity.MnistImage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MnistImageRepository extends JpaRepository<MnistImage, Long> {
    List<MnistImage> findByUserId(Long userId);
    List<MnistImage> findByPredictedLabel(Integer label);
}