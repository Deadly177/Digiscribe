package com.digiscrib.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import java.util.Map;

@Service
public class TranslationService {
    
    private final RestTemplate restTemplate = new RestTemplate();
    
    public String translateToChinese(String text) {
        try {
            if (text == null || text.trim().isEmpty()) {
                return text;
            }
            
            String url = UriComponentsBuilder.fromHttpUrl("https://api.mymemory.translated.net/get")
                    .queryParam("q", text)
                    .queryParam("langpair", "en|zh")
                    .toUriString();
            
            Map response = restTemplate.getForObject(url, Map.class);
            
            if (response != null && response.containsKey("responseData")) {
                Map responseData = (Map) response.get("responseData");
                String translatedText = (String) responseData.get("translatedText");
                return translatedText != null ? translatedText : text;
            }
            
            return text; // Return original if translation fails
            
        } catch (Exception e) {
            System.err.println("Translation failed: " + e.getMessage());
            return text; // Return original text on failure
        }
    }
}