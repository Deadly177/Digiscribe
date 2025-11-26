package com.digiscrib.service;

import com.digiscrib.dto.MlModelInfo;
import com.digiscrib.dto.MlPredictionResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class MlGatewayServiceTest {

    private final MockRestServiceServer mockServer;
    private final MlGatewayService service;

    MlGatewayServiceTest() {
        RestTemplate restTemplate = new RestTemplate();
        this.mockServer = MockRestServiceServer.createServer(restTemplate);
        this.service = new MlGatewayService(restTemplate, "http://ml");
    }

    @Test
    void predict_ShouldPostImagePayloadAndReturnBody() {
        mockServer.expect(once(), requestTo("http://ml/api/predict"))
            .andExpect(method(POST))
            .andExpect(jsonPath("$.image").value("encoded-image"))
            .andRespond(withSuccess("""
                {"predicted_digit":7,"confidence":0.9}
            """, MediaType.APPLICATION_JSON));

        MlPredictionResponse result = service.predict("encoded-image");

        assertThat(result.getPredictedDigit()).isEqualTo(7);
        assertThat(result.getConfidence()).isEqualTo(0.9);
        mockServer.verify();
    }

    @Test
    void getModels_ShouldReturnListFromGateway() {
        mockServer.expect(once(), requestTo("http://ml/api/models"))
            .andExpect(method(GET))
            .andRespond(withSuccess("""
                [{"id":"model-1","status":"ACTIVE"}]
            """, MediaType.APPLICATION_JSON));

        List<MlModelInfo> models = service.getModels();
        assertThat(models).hasSize(1);
        assertThat(models.get(0).getId()).isEqualTo("model-1");
        assertThat(models.get(0).getStatus()).isEqualTo("ACTIVE");
        mockServer.verify();
    }

    @Test
    void health_ShouldReturnFallbackWhenMlServiceDown() {
        mockServer.expect(once(), requestTo("http://ml/health"))
            .andExpect(method(GET))
            .andRespond(withServerError());

        Map<String, Object> health = service.health();

        assertThat(health.get("status")).isEqualTo("error");
        assertThat(health.get("message")).isInstanceOf(String.class);
        mockServer.verify();
    }
}
