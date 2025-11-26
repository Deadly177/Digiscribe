package com.digiscrib.service;

import com.digiscrib.entity.RecognitionHistory;
import com.digiscrib.entity.User;
import com.digiscrib.repository.RecognitionHistoryRepository;
import com.digiscrib.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import com.digiscrib.config.PasswordConfig;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@Import({RecognitionHistoryService.class, PasswordConfig.class})
class RecognitionHistoryServiceTest {

    @Autowired
    private RecognitionHistoryRepository repository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RecognitionHistoryService service;

    @Test
    void saveRecognition_ShouldPersistWithFilename() {
        User user = userRepository.save(new User("alice", "alice@example.com", "pw"));
        RecognitionHistory history = service.saveRecognition(user, "drawing", "digit.png", 8, 0.92);

        assertThat(history.getId()).isNotNull();
        assertThat(history.getOriginalFilename()).isEqualTo("digit.png");
        assertThat(repository.count()).isEqualTo(1);
    }

    @Test
    void provideFeedback_ShouldUpdatePredictionAccuracy() {
        User user = userRepository.save(new User("bob", "bob@example.com", "pw"));
        RecognitionHistory saved = service.saveRecognition(user, "upload", "file.png", 5, 0.8);

        RecognitionHistory updated = service.provideFeedback(saved.getId(), 5);

        assertThat(updated.getActualDigit()).isEqualTo(5);
        assertThat(updated.getCorrectPrediction()).isTrue();
    }

    @Test
    void provideFeedback_ShouldThrowWhenHistoryMissing() {
        assertThrows(RuntimeException.class, () -> service.provideFeedback(999L, 1));
    }

    @Test
    void getAccuracy_ShouldReturnZeroWhenNoData() {
        assertThat(service.getAccuracy()).isZero();
    }

    @Test
    void getAccuracy_ShouldComputePercentage() {
        User user = userRepository.save(new User("carol", "carol@example.com", "pw"));
        service.saveRecognition(user, "drawing", "a.png", 1, 0.9);
        service.saveRecognition(user, "drawing", "b.png", 2, 0.9);
        service.saveRecognition(user, "drawing", "c.png", 3, 0.9);

        service.provideFeedback(repository.findAll().get(0).getId(), 1);
        service.provideFeedback(repository.findAll().get(1).getId(), 9); // incorrect
        service.provideFeedback(repository.findAll().get(2).getId(), 3);

        assertThat(service.getAccuracy()).isEqualTo(66.66666666666666);
    }
}
