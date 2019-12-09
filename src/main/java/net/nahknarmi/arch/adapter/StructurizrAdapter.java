package net.nahknarmi.arch.adapter;

import com.google.gson.Gson;
import com.structurizr.Workspace;
import com.structurizr.api.StructurizrClient;
import com.structurizr.api.StructurizrClientException;
import com.structurizr.util.WorkspaceUtils;

import java.io.File;
import java.io.InputStreamReader;
import java.util.Map;

public class StructurizrAdapter {
    private final Workspace workspace;
    private final StructurizrClient buildClient;

    public StructurizrAdapter(long workspaceId) throws StructurizrClientException {
        this.buildClient = buildClient();
        this.workspace = buildClient.getWorkspace(workspaceId);
    }

    public void publish(Workspace workspace) throws StructurizrClientException {
        buildClient().putWorkspace(workspace.getId(), workspace);
    }

    public Workspace workspace() {
        return this.workspace;
    }

    //TODO: Modify to read from products directory and batch update all products instead of hardcoding
    public void upload() throws Exception {
        String path = getClass().getResource("/structurizr/49328.json").getPath();
        buildClient().putWorkspace(this.workspace.getId(), WorkspaceUtils.loadWorkspaceFromJson(new File(path)));
    }

    @SuppressWarnings("unchecked")
    private StructurizrClient buildClient() {
        String structurizrApiKey = System.getenv().get("STRUCTURIZR_API_KEY");
        String structurizrApiSecret = System.getenv().get("STRUCTURIZR_API_SECRET");
        StructurizrClient structurizrClient;

        if (structurizrApiKey != null && structurizrApiSecret != null) {
            structurizrClient = new StructurizrClient(structurizrApiKey, structurizrApiSecret);
        } else {
            InputStreamReader reader =
                    new InputStreamReader(StructurizrQuickstart.class.getResourceAsStream("/structurizr/credentials.json"));
            Map<String, String> map = new Gson().fromJson(reader, Map.class);
            structurizrClient = new StructurizrClient(map.get("api_key"), map.get("api_secret"));
        }
        structurizrClient.setWorkspaceArchiveLocation(null);

        return structurizrClient;
    }
}
