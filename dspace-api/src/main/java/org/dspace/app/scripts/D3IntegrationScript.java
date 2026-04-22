// Версия: 1.0.1
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

    // Для преобразования Map в JSON строку используется стандартный ObjectMapper
    private final ObjectMapper objectMapper;

    public D3IntegrationScript() {
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Создаёт объект в D3 через REST API
     * @param payload - данные Funding
     * @param token - Dutch Login / Bearer token
     * @return ответ D3
     */
    public String createDmsObject(Map<String, Object> payload, String token) {
        String url = "https://d3-server/api/dmsObject";

        // Используем try-with-resources для автоматического закрытия клиента
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(url);

            // Преобразуем объект Map в JSON
            String jsonPayload = objectMapper.writeValueAsString(payload);

            // Устанавливаем тело запроса и заголовки
            StringEntity entity = new StringEntity(jsonPayload, ContentType.APPLICATION_JSON);
            httpPost.setEntity(entity);
            httpPost.setHeader("Authorization", "Bearer " + token);
            httpPost.setHeader("Accept", "application/json");

            // Выполняем HTTP POST запрос
            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                // Извлекаем и возвращаем тело ответа
                return EntityUtils.toString(response.getEntity());
            }

        } catch (IOException e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }
}