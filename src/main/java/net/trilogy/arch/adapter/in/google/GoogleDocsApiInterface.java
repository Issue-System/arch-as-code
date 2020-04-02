package net.trilogy.arch.adapter.in.google;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.services.docs.v1.Docs;
import com.google.api.services.docs.v1.model.Document;
import lombok.Getter;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GoogleDocsApiInterface {
    private static final Pattern pattern = Pattern.compile("\\/document\\/d\\/([^\\/]+)");
    private Docs api;

    public GoogleDocsApiInterface(GoogleDocsAuthorizedApiFactory apiFactory) throws IOException {
        this.api = apiFactory.getAuthorizedDocsApi();
    }

    public Response getDocument(String url) throws IOException {
        if (url == null || url.isBlank()) {
            throw new InvalidUrlException();
        }

        Document doc = api.documents().get(getDocumentId(url)).execute();

        // TODO FUTURE: We wouldn't normally do this, but we're forced to while we wait for Google
        // to resolve this issue, https://issuetracker.google.com/issues/152645656
        final ObjectMapper mapper = new ObjectMapper();
        final String jsonString = mapper.writeValueAsString(doc);
        final JsonNode jsonNode = mapper.readValue(jsonString, JsonNode.class);

        return new Response(jsonNode, doc);
    }

    private static String getDocumentId(String documentUrl) {
        Matcher matcher = pattern.matcher(documentUrl);

        if (!matcher.find()) {
            throw new InvalidUrlException();
        }

        return matcher.group(1);
    }

    public static class Response {
        private final JsonNode json;
        private final Document document;

        public Response(JsonNode json, Document document) {
            this.json = json;
            this.document = document;
        }

        public JsonNode asJson() {
            return this.json;
        }

        public Document asDocument() {
            return this.document;
        }
    }

    static class InvalidUrlException extends RuntimeException {}
}
