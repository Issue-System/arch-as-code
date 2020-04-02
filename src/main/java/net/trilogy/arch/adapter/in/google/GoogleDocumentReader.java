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
        var response = api.getDocument(url);

        if (isEmpty(response)) {
            return ArchitectureUpdate.blank();
        }

        return ArchitectureUpdate.builder()
                .milestone(parseMilestone(response))
                .build();
    }

    private String parseMilestone(GoogleDocsApiInterface.Response fromResponse) {
        var contentJsonNode = fromResponse.asJson().get("body").get("content");

        for (JsonNode content : contentJsonNode) {
            if ( ! content.hasNonNull("table")) continue;

            var table = content.get("table");

            if( ! table.hasNonNull("columns")) continue;
            if(table.get("columns").asInt() != 2) continue;

            if( ! table.hasNonNull("tableRows")) continue;

            for(JsonNode row : table.get("tableRows")) {
                if( ! row.hasNonNull("tableCells")) continue;
                if(row.get("tableCells").size() != 2) continue;

                var firstCell = row.get("tableCells").get(0);
                if(!getText(firstCell).equals("Milestone")) continue;

                var secondCell = row.get("tableCells").get(1);
                return getText(secondCell);
            }
        }

        return "";
    }

    private String getText(JsonNode fromNode) {
        String result = "";

        if( ! fromNode.hasNonNull("content")) return result;

        for(JsonNode contentItem : fromNode.get("content")) {
            if( ! contentItem.hasNonNull("paragraph")) continue;

            var paragraph = contentItem.get("paragraph");

            if( ! paragraph.hasNonNull("elements")) continue;

            for(JsonNode paragraphElement : paragraph.get("elements")) {
                if( ! paragraphElement.hasNonNull("textRun")) continue;
                var textRun = paragraphElement.get("textRun");
                result = result + textRun.get("content").textValue();
            }
        }

        return result.trim();
    }

    private boolean isEmpty(GoogleDocsApiInterface.Response response) {
        return !response.asJson().hasNonNull("body");
    }
}
