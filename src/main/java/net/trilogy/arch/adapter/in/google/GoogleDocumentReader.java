package net.trilogy.arch.adapter.in.google;

import com.fasterxml.jackson.databind.JsonNode;
import net.trilogy.arch.domain.ArchitectureUpdate;

import java.io.IOException;


public class GoogleDocumentReader {
    private GoogleDocsApiInterface api;

    public GoogleDocumentReader(GoogleDocsApiInterface api) {
        this.api = api;
    }

    public ArchitectureUpdate load(String url) throws IOException {
        final JsonNode json = api.getDocument(url).asJson();

        // Parse Json
        String milestone = json.get("body").get("content").toString();
        json.at("");
        return ArchitectureUpdate.builder()
                .milestone(milestone)
                .build();
    }
}
