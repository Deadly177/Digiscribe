package com.digiscrib.service;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.SocketTimeoutException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;

class MlGatewayServiceEdgeTest {

    private final RestTemplate restTemplate = new RestTemplate();
    private final MockRestServiceServer server = MockRestServiceServer.createServer(restTemplate);
    private final MlGatewayService service = new MlGatewayService(restTemplate, "http://ml");

    @Test
    void predict_returnsServerError_propagatesException() {
        server.expect(once(), requestTo("http://ml/api/predict"))
            .andExpect(method(POST))
            .andRespond(withServerError().contentType(MediaType.APPLICATION_JSON));

        assertThrows(RestClientException.class, () -> service.predict("bad-image"));
        server.verify();
    }

    @Test
    void predict_timeout_propagatesException() {
        server.expect(once(), requestTo("http://ml/api/predict"))
            .andExpect(method(POST))
            .andRespond(request -> { throw new SocketTimeoutException("timeout"); });

        assertThrows(Exception.class, () -> service.predict("img"));
        server.verify();
    }
}
