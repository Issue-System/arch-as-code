package net.trilogy.arch.adapter.in.google;

import com.fasterxml.jackson.databind.JsonNode;
import net.trilogy.arch.domain.ArchitectureUpdate;

import java.io.IOException;
import java.util.Optional;


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

        JsonParser jsonParser = new JsonParser(response.asJson());

        return ArchitectureUpdate.builder()
                .milestone(jsonParser.getMilestone().orElse(""))
                .build();
    }


    private boolean isEmpty(GoogleDocsApiInterface.Response response) {
        return !response.asJson().hasNonNull("body");
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private static class JsonParser {
        private static final String MILESTONE_ROW_HEADER = "Milestone";

        private final JsonNode json;
        private Optional<JsonNode> metaDataTable;

        private JsonParser(JsonNode json) {
            this.json = json;
            this.metaDataTable = Optional.empty();
        }

        private Optional<JsonNode> getMetaDataTable() {
            if (metaDataTable.isPresent()) return metaDataTable;

            var contentJsonNode = this.json.get("body").get("content");
            for (JsonNode content : contentJsonNode) {
                if (!content.hasNonNull("table")) continue;

                var table = content.get("table");
                if (!table.hasNonNull("columns")) continue;
                if (table.get("columns").asInt() != 2) continue;

                if (!table.hasNonNull("tableRows")) continue;

                for (JsonNode row : table.get("tableRows")) {
                    if (!row.hasNonNull("tableCells")) continue;
                    if (row.get("tableCells").size() != 2) continue;

                    var firstCell = row.get("tableCells").get(0);
                    if (getText(firstCell).orElse("").equals(MILESTONE_ROW_HEADER)) {
                        this.metaDataTable = Optional.of(table);
                    }
                }
            }

            return this.metaDataTable;
        }

        private Optional<String> getMilestone() {
            var table = getMetaDataTable().orElseGet(() -> null);

            if (table == null) return Optional.empty();

            for (JsonNode row : table.get("tableRows")) {
                if (!row.hasNonNull("tableCells")) continue;
                if (row.get("tableCells").size() != 2) continue;

                var firstCell = row.get("tableCells").get(0);
                if (!getText(firstCell).orElse("").equals(MILESTONE_ROW_HEADER)) continue;

                var secondCell = row.get("tableCells").get(1);
                return getText(secondCell);
            }

            return Optional.empty();
        }

        private Optional<String> getText(JsonNode fromNode) {
            Optional<String> result = Optional.empty();

            if (!fromNode.hasNonNull("content")) return result;

            for (JsonNode contentItem : fromNode.get("content")) {
                if (!contentItem.hasNonNull("paragraph")) continue;

                var paragraph = contentItem.get("paragraph");

                if (!paragraph.hasNonNull("elements")) continue;

                for (JsonNode paragraphElement : paragraph.get("elements")) {
                    if (!paragraphElement.hasNonNull("textRun")) continue;
                    var textRun = paragraphElement.get("textRun");

                    result = result.or(() -> Optional.of(""))
                            .map(str -> str + textRun.get("content").textValue());
                }
            }

            return result.map(String::trim);
        }


    }
}
