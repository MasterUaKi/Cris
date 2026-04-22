/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.scripts;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.stereotype.Component;

@Component
public class D3IntegrationScript {

    private final ObjectMapper objectMapper;

    public D3IntegrationScript() {
        this.objectMapper = new ObjectMapper();
    }

    public String createDmsObject(Map<String, Object> payload, String token) {
        String url = "https://d3-server/api/dmsObject";

        try {
            HttpClient client = HttpClient.newHttpClient();
            String jsonPayload = objectMapper.writeValueAsString(payload);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Authorization", "Bearer " + token)
                    .header("Accept", "application/json")
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();

        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }
}