package net.nahknarmi.arch.adapter;

import com.google.common.base.Optional;
import com.google.gson.Gson;
import com.structurizr.Workspace;
import com.structurizr.api.StructurizrClient;
import com.structurizr.api.StructurizrClientException;
import com.structurizr.util.WorkspaceUtils;

import java.io.File;
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

    public void upload() throws Exception {
        buildClient().putWorkspace(this.workspace.getId(), WorkspaceUtils.loadWorkspaceFromJson(new File("/Users/ikhan/scratch/trilogy/google-docs-spike/src/main/resources/structurizr/49328.json")));
    }

    @SuppressWarnings("unchecked")
    private static StructurizrClient buildClient() {
        String structurizrApiKey = System.getenv().get("STRUCTURIZR_API_KEY");
        String structurizrApiSecret = System.getenv().get("STRUCTURIZR_API_SECRET");
        StructurizrClient structurizrClient;

        printEnvs();

        System.err.println(String.format("----------%s ----------%s", structurizrApiKey, structurizrApiSecret));

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

    private static void printEnvs() {
        Map<String, String> map = System.getenv();
        for (Map.Entry <String, String> entry: map.entrySet()) {
            System.out.println("Variable Name:- " + entry.getKey() + " Value:- " + entry.getValue());
        }
    }
}
