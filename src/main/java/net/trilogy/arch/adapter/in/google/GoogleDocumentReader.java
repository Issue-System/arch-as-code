package net.trilogy.arch.adapter.in.google;

import com.fasterxml.jackson.databind.JsonNode;
import io.vavr.collection.Traversable;
import net.trilogy.arch.domain.ArchitectureUpdate;
import net.trilogy.arch.domain.ArchitectureUpdate.P1;
import net.trilogy.arch.domain.Jira;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


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
                .p1(extractP1(jsonParser))
                .build();
    }

    private P1 extractP1(JsonParser jsonParser) {
        return P1.builder().jira(
                new Jira(
                        jsonParser.getP1JiraTicket().orElse(""),
                        jsonParser.getP1JiraLink().orElse("")
                )
        ).build();
    }


    private boolean isEmpty(GoogleDocsApiInterface.Response response) {
        return !response.asJson().hasNonNull("body");
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private static class JsonParser {
        private static final String MILESTONE_ROW_HEADER = "Milestone";
        private static final String P1_JIRA_TICKET_ROW_HEADER = "P1 Jira Ticket";

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
                    String text = getCombinedText(getTextRuns(firstCell));
                    if (text.equalsIgnoreCase(MILESTONE_ROW_HEADER)) {
                        this.metaDataTable = Optional.of(table);
                    }
                }
            }

            return this.metaDataTable;
        }

        private Optional<JsonNode> getFromMetaDataTable(String rowHeading) {
            var table = getMetaDataTable().orElseGet(() -> null);

            if (table == null) return Optional.empty();

            for (JsonNode row : table.get("tableRows")) {
                if (!row.hasNonNull("tableCells")) continue;
                if (row.get("tableCells").size() != 2) continue;

                var firstCell = row.get("tableCells").get(0);
                List<JsonNode> textRuns = getTextRuns(firstCell);
                boolean isTextSameAsHeading = getCombinedText(textRuns).equalsIgnoreCase(rowHeading);

                if (!isTextSameAsHeading) continue;

                var secondCell = row.get("tableCells").get(1);
                return Optional.of(secondCell);
            }

            return Optional.empty();
        }

        private Optional<String> getMilestone() {
            Optional<JsonNode> fromMetaDataTable = getFromMetaDataTable(MILESTONE_ROW_HEADER);
            Optional<List<JsonNode>> jsonNodes = fromMetaDataTable
                    .map(this::getTextRuns);
            Optional<String> s = jsonNodes
                    .map(this::getCombinedText);
            return s;
        }

        private String getCombinedText(List<JsonNode> fromTextRuns) {
            return fromTextRuns.stream()
                    .map(this::getTextFrom)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.joining(""))
                    .trim();
        }

        private List<JsonNode> getTextRuns(JsonNode fromNode) {
            List<JsonNode> textRuns = new ArrayList<>();

            if (!fromNode.hasNonNull("content")) return textRuns;

            for (JsonNode contentItem : fromNode.get("content")) {
                if (!contentItem.hasNonNull("paragraph")) continue;

                var paragraph = contentItem.get("paragraph");

                if (!paragraph.hasNonNull("elements")) continue;

                for (JsonNode paragraphElement : paragraph.get("elements")) {
                    if (!paragraphElement.hasNonNull("textRun")) continue;
                    var textRun = paragraphElement.get("textRun");
                    textRuns.add(textRun);
                }
            }
            return textRuns;
        }

        private Optional<String> getTextFrom(JsonNode textRun) {
            if (!textRun.hasNonNull("content")) return Optional.empty();
            return Optional.of(textRun.get("content").textValue());
        }

        public Optional<String> getP1JiraTicket() {
            return getFromMetaDataTable(P1_JIRA_TICKET_ROW_HEADER)
                    .map(this::getTextRuns)
                    .map(this::getCombinedText);
        }

        public Optional<String> getP1JiraLink() {
            Optional<JsonNode> cellContents = getFromMetaDataTable(P1_JIRA_TICKET_ROW_HEADER);
            return Optional.empty();
        }
    }
}
