package net.nahknarmi.arch.adapter;

import com.google.gson.Gson;
import com.structurizr.Workspace;
import com.structurizr.api.StructurizrClient;

import java.io.InputStreamReader;
import java.util.Map;

public class StructurizrQuickstart {

    public static void main(String[] args) throws Exception {
        // a Structurizr workspace is the wrapper for a software architecture model, views and documentation
//        Workspace workspace = new Workspace("Foo Barr", "This is a model of my software system.");
//        new ModelBuilder(workspace).buildModel(workspace);
//
//        uploadWorkspaceToStructurizr(workspace);

    }



    private static void uploadWorkspaceToStructurizr(Workspace workspace) throws Exception {
        Map<String, String> map = new Gson().fromJson(new InputStreamReader(StructurizrQuickstart.class.getResourceAsStream("/structurizr/credentials.json")), Map.class);

        StructurizrClient structurizrClient = new StructurizrClient( map.get("api_key"), map.get("api_secret"));
        structurizrClient.putWorkspace(Long.parseLong(map.get("workspace_id")), workspace);
    }

}
