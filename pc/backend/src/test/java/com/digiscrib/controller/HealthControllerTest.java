package com.digiscrib.controller;

import com.digiscrib.service.MlGatewayService;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class HealthControllerTest {

    private final MockMvc mockMvc = MockMvcBuilders
        .standaloneSetup(new HealthController(new StubMlGatewayService()))
        .build();

    private static class StubMlGatewayService extends MlGatewayService {
        StubMlGatewayService() {
            super(new RestTemplate(), "http://stub");
        }

        @Override
        public Map<String, Object> health() {
            return Map.of("status", "ok");
        }
    }

    @Test
    void health_ReturnsPlatformAndMlStatus() throws Exception {
        mockMvc.perform(get("/api/health"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("healthy"))
            .andExpect(jsonPath("$.mlService.status").value("ok"))
            .andExpect(jsonPath("$.timestamp").exists())
            .andExpect(jsonPath("$.uptimeMs").exists());
    }
}
