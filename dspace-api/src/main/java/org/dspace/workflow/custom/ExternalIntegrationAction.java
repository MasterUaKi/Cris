// Версия: 1.0.1
// Дата выпуска: 2026-04-22

package org.dspace.workflow.custom;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.dspace.authorize.AuthorizeException;
import org.dspace.content.Item;
import org.dspace.content.service.ItemService;
import org.dspace.core.Context;
import org.dspace.xmlworkflow.state.Step;
import org.dspace.xmlworkflow.state.actions.ActionResult;
import org.dspace.xmlworkflow.state.actions.processingaction.ProcessingAction; // ИСПРАВЛЕНО / KORRIGIERT
import org.springframework.beans.factory.annotation.Autowired;

import jakarta.servlet.http.HttpServletRequest; // ИСПРАВЛЕНО (javax -> jakarta) / KORRIGIERT
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class ExternalIntegrationAction extends ProcessingAction {
// ... остальной код оставляем без изменений / den restlichen Code unverändert lassen ...

    @Autowired
    protected ItemService itemService;

    @Override
    public void activate(Context c, XmlWorkflowItem wf) throws SQLException, IOException, AuthorizeException {
        // RU: Метод вызывается при поступлении объекта на этот шаг. 
        // В нашем случае предварительная активация не требуется.
        // DE: Diese Methode wird aufgerufen, wenn das Objekt diesen Schritt erreicht. 
        // In unserem Fall ist keine vorherige Aktivierung erforderlich.
    }

    @Override
    public ActionResult execute(Context c, XmlWorkflowItem wfi, Step step, HttpServletRequest request) 
            throws SQLException, AuthorizeException, IOException {
        
        Item item = wfi.getItem();
        
        // RU: Проверяем, является ли объект типом "Funding"
        // DE: Überprüfen, ob das Objekt vom Typ "Funding" ist
        String entityType = itemService.getMetadataFirstValue(item, "dspace", "entity", "type", Item.ANY);
        
        if ("Funding".equals(entityType)) {
            // RU: Получаем название для отправки (пример извлечения метаданных)
            // DE: Titel für den Versand abrufen (Beispiel für Metadatenextraktion)
            String title = itemService.getMetadataFirstValue(item, "dc", "title", null, Item.ANY);
            
            // RU: Выполняем HTTP POST запрос
            // DE: HTTP POST Anfrage ausführen
            sendToExternalSystem(title);
        }

        // RU: Действие завершено успешно, система должна перейти к следующему этапу (OUTCOME_COMPLETE)
        // DE: Aktion erfolgreich abgeschlossen, das System soll zum nächsten Schritt übergehen (OUTCOME_COMPLETE)
        return new ActionResult(ActionResult.TYPE.TYPE_OUTCOME, ActionResult.OUTCOME_COMPLETE);
    }

    @Override
    public List<String> getOptions() {
        // RU: Для автоматических действий (ProcessingAction) пользовательский интерфейс не требуется
        // DE: Für automatische Aktionen (ProcessingAction) ist keine Benutzeroberfläche erforderlich
        return null;
    }

    private void sendToExternalSystem(String title) {
        // RU: Основная логика отправки запроса
        // DE: Hauptlogik zum Senden der Anfrage
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            // RU: URL внешней системы (замени на реальный адрес API)
            // DE: URL des externen Systems (durch die reale API-Adresse ersetzen)
            HttpPost httpPost = new HttpPost("https://api.external-system.com/funding/import");
            
            // RU: Формируем JSON Payload
            // DE: JSON Payload erstellen
            String json = "{\"title\": \"" + title + "\"}";
            
            StringEntity entity = new StringEntity(json);
            httpPost.setEntity(entity);
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");
            
            try (CloseableHttpResponse response = client.execute(httpPost)) {
                // RU: Читаем и логируем ответ внешней системы
                // DE: Antwort des externen Systems lesen und protokollieren
                String responseBody = EntityUtils.toString(response.getEntity());
                System.out.println("External System Response: " + responseBody);
            }
        } catch (Exception e) {
            // RU: Логируем ошибку, если внешняя система недоступна
            // DE: Fehler protokollieren, falls das externe System nicht erreichbar ist
            e.printStackTrace();
        }
    }
}