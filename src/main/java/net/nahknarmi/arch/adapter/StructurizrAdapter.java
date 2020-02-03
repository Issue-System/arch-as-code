package net.nahknarmi.arch.adapter;

import com.google.gson.Gson;
import com.structurizr.Workspace;
import com.structurizr.api.StructurizrClient;
import com.structurizr.api.StructurizrClientException;

import java.io.InputStreamReader;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;
import static net.nahknarmi.arch.adapter.Credentials.credentialsAsStream;

public class StructurizrAdapter {
    private final WorkspaceIdFinder workspaceIdFinder;

    public StructurizrAdapter(WorkspaceIdFinder workspaceIdFinder) {
        this.workspaceIdFinder = workspaceIdFinder;
    }

    public Workspace load(long workspaceId) throws StructurizrClientException {
        StructurizrClient buildClient = buildClient();
        return buildClient.getWorkspace(workspaceId);
    }

    /**
     * It will use following order to determine which workspace id to use:
     *  - from environment variable
     *  - from ./.arch-as-code/structurizr/credentials.json
     *  - from workspace configured in passed in workspace
     * @param workspace
     * @throws StructurizrClientException
     */
    public void publish(Workspace workspace) throws StructurizrClientException {
        checkNotNull(workspace, "Workspace must not be null!");
        Long workspaceId = workspaceIdFinder.workspaceId().orElse(workspace.getId());

        buildClient().putWorkspace(workspaceId, workspace);
    }

    @SuppressWarnings("unchecked")
    private StructurizrClient buildClient() {
        String structurizrApiKey = System.getenv().get("STRUCTURIZR_API_KEY");
        String structurizrApiSecret = System.getenv().get("STRUCTURIZR_API_SECRET");
        StructurizrClient structurizrClient;

        if (structurizrApiKey != null && structurizrApiSecret != null) {
            structurizrClient = new StructurizrClient(structurizrApiKey, structurizrApiSecret);
        } else if (credentialsAsStream().isPresent()) {
            InputStreamReader reader =
                    new InputStreamReader(credentialsAsStream().get());
            Map<String, String> map = new Gson().fromJson(reader, Map.class);
            structurizrClient = new StructurizrClient(map.get("api_key"), map.get("api_secret"));
        } else {
            throw new IllegalStateException("Structurizr credentials could not be found. See documentation on how to configure.");
        }
        structurizrClient.setWorkspaceArchiveLocation(null);

        return structurizrClient;
    }

}
