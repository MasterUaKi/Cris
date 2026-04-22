/**
 * The contents of this file are subject to the relevant license set forth
 * in the LICENSE file accompanying this file ("License").
 *
 * http://www.dspace.org/license/
 */
// Версия: 1.0.2
// Дата выпуска: 2026-04-22

package org.dspace.app.scripts;

import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

@Component
public class D3IntegrationScript {

    private final ObjectMapper objectMapper;

    public D3IntegrationScript() {
        this.objectMapper = new ObjectMapper();
    }

    public String createDmsObject(Map<String, Object> payload, String token) {
        String url = "https://d3-server/api/dmsObject";

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(url);
            String jsonPayload = objectMapper.writeValueAsString(payload);
            StringEntity entity = new StringEntity(jsonPayload, ContentType.APPLICATION_JSON);
            httpPost.setEntity(entity);
            httpPost.setHeader("Authorization", "Bearer " + token);
            httpPost.setHeader("Accept", "application/json");

            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                return EntityUtils.toString(response.getEntity());
            }

        } catch (IOException e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }
}