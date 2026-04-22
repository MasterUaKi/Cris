package org.dspace.app.scripts;

import java.util.Map;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

@Component
public class D3IntegrationScript {

    private final RestTemplate restTemplate;

    public D3IntegrationScript() {
        this.restTemplate = new RestTemplate();
    }

    /**
     * Создаёт объект в D3 через REST API
     * @param payload - данные Funding
     * @param token - Dutch Login / Bearer token
     * @return ответ D3
     */

    public String createDmsObject(Map<String, Object> payload, String token) {
        String url = "https://d3-server/api/dmsObject";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + token);

        HttpEntity<Map<String,Object>> request = new HttpEntity<>(payload, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
        return response.getBody();
    }
}
