package com.digiscrib.service;

import com.digiscrib.dto.MlPredictionResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

/**
 * Integration-style tests that mock the Python ML HTTP API.
 * Covers valid calls, invalid input (400), and large payload handling.
 */
class MlGatewayServiceIntegrationTest {

    private final RestTemplate restTemplate = new RestTemplate();
    private final MockRestServiceServer server = MockRestServiceServer.createServer(restTemplate);
    private final MlGatewayService service = new MlGatewayService(restTemplate, "http://ml");

    @Test
    void predict_validPayload_returnsPrediction() {
        server.expect(once(), requestTo("http://ml/api/predict"))
            .andExpect(method(POST))
            .andExpect(jsonPath("$.image").value("imgdata"))
            .andRespond(withSuccess("""
                {"predicted_digit":2,"confidence":0.77}
            """, MediaType.APPLICATION_JSON));

        MlPredictionResponse resp = service.predict("imgdata");

        assertThat(resp.getPredictedDigit()).isEqualTo(2);
        assertThat(resp.getConfidence()).isEqualTo(0.77);
        server.verify();
    }

    @Test
    void predict_invalidPayload_returns400() {
        server.expect(once(), requestTo("http://ml/api/predict"))
            .andExpect(method(POST))
            .andRespond(request -> org.springframework.test.web.client.response.MockRestResponseCreators
                .withBadRequest()
                .body("{\"error\":\"invalid input\"}")
                .contentType(MediaType.APPLICATION_JSON)
                .createResponse(request));

        assertThrows(HttpClientErrorException.class, () -> service.predict("bad"));
        server.verify();
    }

    @Test
    void predict_largePayload_stillPostsAndParsesResponse() {
        String large = "x".repeat(5_000); // simulate big base64/image payload
        server.expect(once(), requestTo("http://ml/api/predict"))
            .andExpect(method(POST))
            .andRespond(withSuccess("""
                {"predicted_digit":9,"confidence":0.5}
            """, MediaType.APPLICATION_JSON));

        MlPredictionResponse resp = service.predict(large);

        assertThat(resp.getPredictedDigit()).isEqualTo(9);
        assertThat(resp.getConfidence()).isEqualTo(0.5);
        server.verify();
    }
}
