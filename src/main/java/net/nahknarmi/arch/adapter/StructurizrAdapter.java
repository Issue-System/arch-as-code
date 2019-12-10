package net.nahknarmi.arch.adapter;

import com.google.gson.Gson;
import com.structurizr.Workspace;
import com.structurizr.api.StructurizrClient;
import com.structurizr.api.StructurizrClientException;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

public class StructurizrAdapter {
    private final Workspace workspace;

    public StructurizrAdapter(long workspaceId) throws StructurizrClientException {
        StructurizrClient buildClient = buildClient();
        this.workspace = buildClient.getWorkspace(workspaceId);
    }

    public void publish(Workspace workspace) throws StructurizrClientException {
        buildClient().putWorkspace(workspace.getId(), workspace);
    }

    public Workspace workspace() {
        return this.workspace;
    }

    @SuppressWarnings("unchecked")
    private StructurizrClient buildClient() {
        String structurizrApiKey = System.getenv().get("STRUCTURIZR_API_KEY");
        String structurizrApiSecret = System.getenv().get("STRUCTURIZR_API_SECRET");
        StructurizrClient structurizrClient;

        if (structurizrApiKey != null && structurizrApiSecret != null) {
            structurizrClient = new StructurizrClient(structurizrApiKey, structurizrApiSecret);
        } else if (credentialsAsStream() != null) {
            InputStreamReader reader =
                    new InputStreamReader(credentialsAsStream());
            Map<String, String> map = new Gson().fromJson(reader, Map.class);
            structurizrClient = new StructurizrClient(map.get("api_key"), map.get("api_secret"));
        } else {
            throw new IllegalStateException("Structurizr credentials could not be found. See documentation on how to configure.");
        }
        structurizrClient.setWorkspaceArchiveLocation(null);

        return structurizrClient;
    }

    private InputStream credentialsAsStream() {
        return StructurizrPublisher.class.getResourceAsStream("/structurizr/credentials.json");
    }
}
