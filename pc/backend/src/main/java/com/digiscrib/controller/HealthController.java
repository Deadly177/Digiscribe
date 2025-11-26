package com.digiscrib.controller;

import com.digiscrib.service.MlGatewayService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.management.ManagementFactory;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class HealthController {

    private final MlGatewayService mlGatewayService;

    public HealthController(MlGatewayService mlGatewayService) {
        this.mlGatewayService = mlGatewayService;
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("status", "healthy");
        payload.put("timestamp", Instant.now().toString());
        payload.put("uptimeMs", ManagementFactory.getRuntimeMXBean().getUptime());
        payload.put("mlService", mlGatewayService.health());
        return ResponseEntity.ok(payload);
    }
}
