package com.digiscrib.controller;

import com.digiscrib.dto.MlAnalyticsOverview;
import com.digiscrib.service.MlGatewayService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/analytics")
@CrossOrigin(origins = "*")
public class AnalyticsController {

    private final MlGatewayService mlGatewayService;

    public AnalyticsController(MlGatewayService mlGatewayService) {
        this.mlGatewayService = mlGatewayService;
    }

    @GetMapping("/overview")
    public MlAnalyticsOverview overview() {
        return mlGatewayService.getAnalyticsOverview();
    }
}
