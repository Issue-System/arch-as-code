package net.nahknarmi.arch.adapter;

import com.google.gson.Gson;

import java.io.InputStreamReader;
import java.util.Map;
import java.util.Optional;

import static net.nahknarmi.arch.adapter.Credentials.credentialsAsStream;

public class WorkspaceIdFinder {

    @SuppressWarnings("unchecked")
    public Optional<Long> workspaceId() {
        String workspaceId = System.getenv().get("STRUCTURIZR_WORKSPACE_ID");
        if (workspaceId != null) {
            return Optional.of(Long.parseLong(workspaceId));
        } else if (credentialsAsStream().isPresent()) {
            InputStreamReader reader =
                    new InputStreamReader(credentialsAsStream().get());
            Map<String, String> map = new Gson().fromJson(reader, Map.class);
            return Optional.of(Long.parseLong(map.get("workspace_id")));
        } else {
            return Optional.empty();
        }
    }
}
