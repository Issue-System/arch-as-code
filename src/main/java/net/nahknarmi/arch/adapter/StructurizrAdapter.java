package net.nahknarmi.arch.adapter;

import com.google.gson.Gson;
import com.structurizr.Workspace;
import com.structurizr.api.StructurizrClient;
import com.structurizr.api.StructurizrClientException;
import net.nahknarmi.arch.Bootstrap;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public class StructurizrAdapter {
    private final long workspaceId;

    public StructurizrAdapter(long workspaceId) {
        checkArgument(workspaceId > 0, String.format("Workspace id (%d) must be greater than 0.", workspaceId));
        this.workspaceId = workspaceId;
    }

    public Workspace load() throws StructurizrClientException {
        StructurizrClient buildClient = buildClient();
        return buildClient.getWorkspace(workspaceId);
    }

    public void publish(Workspace workspace) throws StructurizrClientException {
        checkNotNull(workspace, "Workspace must not be null!");
        buildClient().putWorkspace(workspace.getId(), workspace);
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
        return Bootstrap.class.getResourceAsStream("/structurizr/credentials.json");
    }
}
