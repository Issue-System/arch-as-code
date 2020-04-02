package net.trilogy.arch.adapter.in.google;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
class GoogleDocsJsonParser {
    private static final String MILESTONE_ROW_HEADER = "Milestone";
    private static final String P1_JIRA_TICKET_ROW_HEADER = "P1 Jira Ticket";

    private final JsonNode json;
    private Optional<JsonNode> metaDataTable;

    GoogleDocsJsonParser(JsonNode json) {
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
            List<TextRun> textRuns = getTextRuns(firstCell);
            boolean isTextSameAsHeading = getCombinedText(textRuns).equalsIgnoreCase(rowHeading);

            if (!isTextSameAsHeading) continue;

            var secondCell = row.get("tableCells").get(1);
            return Optional.of(secondCell);
        }

        return Optional.empty();
    }

    public Optional<String> getMilestone() {
        return getFromMetaDataTable(MILESTONE_ROW_HEADER)
                .map(this::getTextRuns)
                .map(GoogleDocsJsonParser::getCombinedText);
    }

    private static String getCombinedText(List<TextRun> runs) {
        return runs.stream()
                .map(TextRun::getTextFrom)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.joining(""))
                .trim();
    }

    private List<TextRun> getTextRuns(JsonNode fromNode) {
        List<TextRun> textRuns = new ArrayList<>();

        if (!fromNode.hasNonNull("content")) return textRuns;

        for (JsonNode contentItem : fromNode.get("content")) {
            if (!contentItem.hasNonNull("paragraph")) continue;

            var paragraph = contentItem.get("paragraph");

            if (!paragraph.hasNonNull("elements")) continue;

            for (JsonNode paragraphElement : paragraph.get("elements")) {
                if (!paragraphElement.hasNonNull("textRun")) continue;
                var textRunNode = paragraphElement.get("textRun");
                textRuns.add(new TextRun(textRunNode));
            }
        }
        return textRuns;
    }

    public Optional<String> getP1JiraTicket() {
        return getFromMetaDataTable(P1_JIRA_TICKET_ROW_HEADER)
                .map(this::getTextRuns)
                .map(GoogleDocsJsonParser::getCombinedText);
    }

    public Optional<String> getP1JiraLink() {
        Optional<JsonNode> cellContents = getFromMetaDataTable(P1_JIRA_TICKET_ROW_HEADER);
        return Optional.empty();
    }

    private static class TextRun {
        private final JsonNode node;

        private TextRun(JsonNode node) {this.node = node;}

        private JsonNode getNode() {return node;}

        private Optional<String> getTextFrom() {
            if (!getNode().hasNonNull("content")) return Optional.empty();
            return Optional.of(getNode().get("content").textValue());
        }
    }
}
