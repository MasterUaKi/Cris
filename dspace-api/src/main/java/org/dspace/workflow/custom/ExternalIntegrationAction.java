/**
 * The contents of this file are subject to the relevant license set forth
 * in the LICENSE file accompanying this file ("License").
 *
 * http://www.dspace.org/license/
 */
// Версия: 1.0.3
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
import org.dspace.xmlworkflow.state.actions.processingaction.ProcessingAction;
import org.dspace.xmlworkflow.storedcomponents.XmlWorkflowItem;
import org.springframework.beans.factory.annotation.Autowired;

import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class ExternalIntegrationAction extends ProcessingAction {

    @Autowired
    protected ItemService itemService;

    @Override
    public void activate(Context c, XmlWorkflowItem wf) throws SQLException, IOException, AuthorizeException {
    }

    @Override
    public ActionResult execute(Context c, XmlWorkflowItem wfi, Step step, HttpServletRequest request) 
            throws SQLException, AuthorizeException, IOException {
        
        Item item = wfi.getItem();
        String entityType = itemService.getMetadataFirstValue(item, "dspace", "entity", "type", Item.ANY);
        
        if ("Funding".equals(entityType)) {
            String title = itemService.getMetadataFirstValue(item, "dc", "title", null, Item.ANY);
            sendToExternalSystem(title);
        }

        return new ActionResult(ActionResult.TYPE.TYPE_OUTCOME, ActionResult.OUTCOME_COMPLETE);
    }

    @Override
    public List<String> getOptions() {
        return null;
    }

    private void sendToExternalSystem(String title) {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost("https://api.external-system.com/funding/import");
            String json = "{\"title\": \"" + title + "\"}";
            StringEntity entity = new StringEntity(json);
            httpPost.setEntity(entity);
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");
            
            try (CloseableHttpResponse response = client.execute(httpPost)) {
                String responseBody = EntityUtils.toString(response.getEntity());
                System.out.println("External System Response: " + responseBody);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}