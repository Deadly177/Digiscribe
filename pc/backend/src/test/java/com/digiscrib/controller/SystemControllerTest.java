package com.digiscrib.controller;

import com.digiscrib.dto.LoginRequest;
import com.digiscrib.dto.MlAnalyticsOverview;
import com.digiscrib.dto.MlModelInfo;
import com.digiscrib.dto.TrainingConfigRequest;
import com.digiscrib.entity.User;
import com.digiscrib.service.MlGatewayService;
import com.digiscrib.service.UserService;
import com.digiscrib.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Lightweight system-style tests using standalone MockMvc with stubbed dependencies.
 * Exercises login, model create/activate, and analytics overview flows without external services.
 */
class SystemControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private StubGatewayService stubGatewayService;
    private StubAuthDeps stubAuthDeps;

    @BeforeEach
    void setup() {
        objectMapper = new ObjectMapper();
        stubGatewayService = new StubGatewayService();
        stubAuthDeps = new StubAuthDeps();

        // Instantiate controllers and inject stubs
        AuthController authController = new AuthController();
        ReflectionTestUtils.setField(authController, "authenticationManager", stubAuthDeps.authenticationManager);
        ReflectionTestUtils.setField(authController, "userService", stubAuthDeps.userService);
        ReflectionTestUtils.setField(authController, "jwtUtil", stubAuthDeps.jwtUtil);

        ModelController modelController = new ModelController(stubGatewayService);
        AnalyticsController analyticsController = new AnalyticsController(stubGatewayService);

        mockMvc = MockMvcBuilders.standaloneSetup(authController, modelController, analyticsController).build();
    }

    @Test
    void adminLogin_success() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setUsername("admin");
        request.setPassword("admin123");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").value("jwt-token"))
            .andExpect(jsonPath("$.username").value("admin"))
            .andExpect(jsonPath("$.role").value("ADMIN"))
            .andExpect(jsonPath("$.message").value("Login successful"));
    }

    @Test
    void adminLogin_invalidCredentials_returnsBadRequest() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setUsername("admin");
        request.setPassword("wrong");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Invalid credentials"));
    }

    @Test
    void modelCreate_startsConfigFlow() throws Exception {
        TrainingConfigRequest cfg = new TrainingConfigRequest();
        cfg.setName("model-a");
        cfg.setArchitecture("cnn_simple");
        cfg.setEpochs(1);
        cfg.setLearningRate(0.001);
        cfg.setBatchSize(32);

        mockMvc.perform(post("/api/models/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cfg)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value("mid-1"))
            .andExpect(jsonPath("$.name").value("model-a"))
            .andExpect(jsonPath("$.status").value("idle"));
    }

    @Test
    void modelActivate_switchesModel() throws Exception {
        mockMvc.perform(post("/api/models/mid-1/activate"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("Activated mid-1"));
    }

    @Test
    void analyticsOverview_returnsMetrics() throws Exception {
        mockMvc.perform(get("/api/analytics/overview"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.totalPredictions").value(10))
            .andExpect(jsonPath("$.activeModels").value(1))
            .andExpect(jsonPath("$.totalModels").value(2))
            .andExpect(jsonPath("$.averageAccuracy").value(95.0))
            .andExpect(jsonPath("$.totalTrainingSamples").value(1000));
    }

    // -------- Stub helpers (no Mockito to avoid bytecode agent issues on JDK 25) --------

    private static class StubAuthDeps {
        AuthenticationManager authenticationManager;
        UserService userService;
        JwtUtil jwtUtil;

        StubAuthDeps() {
            this.authenticationManager = authentication -> {
                String username = authentication.getName();
                String password = (String) authentication.getCredentials();
                if ("admin".equals(username) && "admin123".equals(password)) {
                    User u = new User();
                    u.setUsername("admin");
                    u.setEmail("admin@digiscrib.com");
                    u.setRole("ADMIN");
                    return new UsernamePasswordAuthenticationToken(u, null, u.getAuthorities());
                }
                throw new RuntimeException("Invalid credentials");
            };

            this.userService = new UserService(null) {
                @Override
                public void updateLastLogin(String username) {
                    // no-op
                }
            };

            this.jwtUtil = new JwtUtil() {
                @Override
                public String generateToken(String username) {
                    return "jwt-token";
                }
            };
        }
    }

    private static class StubGatewayService extends MlGatewayService {

        StubGatewayService() {
            super(new org.springframework.web.client.RestTemplate(), "http://ml");
        }

        @Override
        public MlModelInfo createModel(TrainingConfigRequest configRequest) {
            MlModelInfo info = new MlModelInfo();
            info.setId("mid-1");
            info.setName(configRequest.getName());
            info.setStatus("idle");
            return info;
        }

        @Override
        public Map<String, Object> activateModel(String modelId) {
            return Map.of("message", "Activated " + modelId);
        }

        @Override
        public MlAnalyticsOverview getAnalyticsOverview() {
            MlAnalyticsOverview overview = new MlAnalyticsOverview();
            overview.setActiveModels(1);
            overview.setTotalModels(2);
            overview.setTotalPredictions(10);
            overview.setAverageAccuracy(95.0);
            overview.setTotalTrainingSamples(1000);
            return overview;
        }
    }
}
