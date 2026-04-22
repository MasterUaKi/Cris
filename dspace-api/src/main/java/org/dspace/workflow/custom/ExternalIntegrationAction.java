/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.workflow.custom;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.SQLException;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import org.dspace.authorize.AuthorizeException;
import org.dspace.content.Item;
import org.dspace.content.service.ItemService;
import org.dspace.core.Context;
import org.dspace.xmlworkflow.state.Step;
import org.dspace.xmlworkflow.state.actions.ActionResult;
import org.dspace.xmlworkflow.state.actions.processingaction.ProcessingAction;
import org.dspace.xmlworkflow.storedcomponents.XmlWorkflowItem;
import org.springframework.beans.factory.annotation.Autowired;

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
        try {
            HttpClient client = HttpClient.newHttpClient();
            String json = "{\"title\": \"" + title + "\"}";

            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.external-system.com/funding/import"))
                    .header("Accept", "application/json")
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = client.send(req, HttpResponse.BodyHandlers.ofString());
            System.out.println("External System Response: " + response.body());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}