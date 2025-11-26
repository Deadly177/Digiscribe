package com.digiscrib.controller;

import com.digiscrib.dto.TranslationRequest;
import com.digiscrib.service.TranslationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/translation")
@CrossOrigin(origins = "*")
public class TranslationController {
    
    @Autowired
    private TranslationService translationService;
    
    @PostMapping("/translate")
    public ResponseEntity<?> translate(@RequestBody TranslationRequest request) {
        try {
            String translatedText = translationService.translateToChinese(request.getText());
            
            Map<String, String> response = new HashMap<>();
            response.put("original", request.getText());
            response.put("translated", translatedText);
            response.put("targetLanguage", request.getTargetLanguage());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("message", "Translation failed: " + e.getMessage()));
        }
    }
}