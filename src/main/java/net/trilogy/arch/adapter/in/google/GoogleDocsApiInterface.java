package net.trilogy.arch.adapter.in.google;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.services.docs.v1.Docs;
import com.google.api.services.docs.v1.model.Document;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GoogleDocsApiInterface {
    private static final Pattern pattern = Pattern.compile("\\/document\\/d\\/([^\\/]+)");
    private Docs api;

    public GoogleDocsApiInterface(GoogleDocsAuthorizedApiFactory apiFactory) throws IOException {
        this.api = apiFactory.getAuthorizedDocsApi();
    }

    public JsonNode getDocument(String url) throws IOException {
        if (url == null || url.isBlank()) {
            throw new InvalidUrlException();
        }

        Document doc = api.documents().get(getDocumentId(url)).execute();

        // TODO FUTURE: Don't do this.
        final ObjectMapper mapper = new ObjectMapper();
        final String jsonString = mapper.writeValueAsString(doc);
        final JsonNode jsonNode = mapper.readValue(jsonString, JsonNode.class);

        return jsonNode;
    }

    private static String getDocumentId(String documentUrl) {
        Matcher matcher = pattern.matcher(documentUrl);

        if (!matcher.find()) {
            throw new InvalidUrlException();
        }

        return matcher.group(1);
    }

    static class InvalidUrlException extends RuntimeException {
    }
}
