package net.nahknarmi.arch.adapter;

import com.google.gson.Gson;
import com.structurizr.Workspace;
import com.structurizr.api.StructurizrClient;
import com.structurizr.api.StructurizrClientException;

import java.io.InputStreamReader;
import java.util.Map;
import java.util.Objects;

public class StructurizrAdapter {
    private final Workspace workspace;
    private final ModelBuilder modelBuilder;

    private StructurizrAdapter(Workspace workspace, ModelBuilder modelBuilder) {
        this.workspace = workspace;
        this.modelBuilder = modelBuilder;
    }

    public static StructurizrAdapter load(long workspaceId) throws StructurizrClientException {
        Workspace workspace = buildClient().getWorkspace(workspaceId);
        return new StructurizrAdapter(workspace, new ModelBuilder());
    }

    public void publish() throws StructurizrClientException {
        Workspace workspace = new Workspace(this.workspace.getName(), this.workspace.getDescription());

        modelBuilder.buildModel(workspace);
        buildClient().putWorkspace(this.workspace.getId(), this.workspace);
    }

    public Workspace workspace() {
        return this.workspace;
    }

    @SuppressWarnings("unchecked")
    private static StructurizrClient buildClient() {
        InputStreamReader reader =
                new InputStreamReader(StructurizrQuickstart.class.getResourceAsStream("/structurizr/credentials.json"));
        Map<String, String> map = new Gson().fromJson(reader, Map.class);
        StructurizrClient structurizrClient = new StructurizrClient(map.get("api_key"), map.get("api_secret"));
        structurizrClient.setWorkspaceArchiveLocation(null);
        return structurizrClient;
    }
}
