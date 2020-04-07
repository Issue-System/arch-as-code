package net.trilogy.arch.adapter.in.google;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toSet;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
class GoogleDocsJsonParser {
    private static final String MILESTONE_ROW_HEADER = "Milestone";
    private static final String P1_JIRA_TICKET_ROW_HEADER = "P1 Jira Ticket";
    private static final String P2_LINK_ROW_HEADER = "P2 Spec Links";
    private static final String EXECUTIVE_SUMMARY_COLUMN_HEADER = "Executive Summary";

    private final JsonNode json;
    private Optional<JsonNode> metaDataTable;
    private Optional<JsonNode> content;

    GoogleDocsJsonParser(JsonNode json) {
        this.json = json;
        this.metaDataTable = Optional.empty();
        this.content = Optional.empty();
    }

    private Optional<JsonNode> getContent() {
        if (this.content.isPresent()) return content;

        if (!this.json.hasNonNull("body")) return Optional.empty();
        if (!this.json.get("body").hasNonNull("content")) return Optional.empty();

        this.content = Optional.of(this.json.get("body").get("content"));
        return this.content;
    }

    private Optional<JsonNode> getMetaDataTable() {
        if (metaDataTable.isPresent()) return metaDataTable;

        List<JsonNode> tables = getTablesWithNColumns(2);

        for (JsonNode table : tables) {
            for (JsonNode row : table.get("tableRows")) {
                if (!row.hasNonNull("tableCells")) continue;
                if (row.get("tableCells").size() != 2) continue;

                var firstCell = row.get("tableCells").get(0);
                String text = getCombinedText(getTextRuns(firstCell));
                if (text.equalsIgnoreCase(MILESTONE_ROW_HEADER)) {
                    this.metaDataTable = Optional.of(table);
                    return this.metaDataTable;
                }
            }
        }

        return Optional.empty();
    }

    private Optional<JsonNode> getFromMetaDataTable(String rowHeading) {
        var table = getMetaDataTable().orElse(null);

        if (table == null) return Optional.empty();

        for (JsonNode row : table.get("tableRows")) {
            if (!row.hasNonNull("tableCells")) continue;
            if (row.get("tableCells").size() != 2) continue;

            var firstCell = row.get("tableCells").get(0);
            List<TextRun> textRuns = getTextRuns(firstCell);
            boolean doesTextContainHeadingWeWant = getCombinedText(textRuns).toLowerCase().contains(rowHeading.toLowerCase());

            if (!doesTextContainHeadingWeWant) continue;

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
                .collect(joining(""))
                .trim();
    }

    private static Set<String> getAllLinks(List<TextRun> runs) {
        return runs.stream()
                .map(TextRun::getLinkFrom)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(toSet());
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
        return getFromMetaDataTable(P1_JIRA_TICKET_ROW_HEADER)
                .map(this::getTextRuns)
                .map(GoogleDocsJsonParser::getAllLinks)
                .map(s -> String.join(", ", s));
    }

    public Optional<String> getP2Link() {
        return getFromMetaDataTable(P2_LINK_ROW_HEADER)
                .map(this::getTextRuns)
                .map(GoogleDocsJsonParser::getAllLinks)
                .map(theSetOfLinks ->
                        theSetOfLinks.stream()
                                .filter(theLink -> !theLink.contains("jira"))
                                .collect(toSet())
                )
                .map(s -> String.join(", ", s));
    }

    private List<JsonNode> getTablesWithNColumns(int numColumns) {
        List<JsonNode> tablesFound = new ArrayList<>();

        if(getContent().isEmpty()) return tablesFound;

        for(JsonNode contentItem : getContent().get()) {
            if (!contentItem.hasNonNull("table")) continue;

            var table = contentItem.get("table");
            if (!table.hasNonNull("columns")) continue;
            if (table.get("columns").asInt() != numColumns) continue;

            if (!table.hasNonNull("tableRows")) continue;

            tablesFound.add(table);
        }

        return tablesFound;
    }

    public Optional<String> getExecutiveSummary() {
        List<JsonNode> tables = getTablesWithNColumns(1);

        for (JsonNode table : tables) {
            if (table.get("tableRows").size() != 2) continue;

            JsonNode firstRow = table.get("tableRows").get(0);
            if (!firstRow.hasNonNull("tableCells")) continue;
            if (firstRow.get("tableCells").size() != 1) continue;
            JsonNode firstCell = firstRow.get("tableCells").get(0);

            JsonNode secondRow = table.get("tableRows").get(1);
            if (!secondRow.hasNonNull("tableCells")) continue;
            if (secondRow.get("tableCells").size() != 1) continue;
            JsonNode secondCell = secondRow.get("tableCells").get(0);

            boolean isTheFirstRowTheExecutiveSummaryHeader = getCombinedText(getTextRuns(firstCell)).toLowerCase().contains(EXECUTIVE_SUMMARY_COLUMN_HEADER.toLowerCase());
            if (!isTheFirstRowTheExecutiveSummaryHeader) continue;

            return Optional.of(getCombinedText(getTextRuns(secondCell)));
        }

        return Optional.empty();
    }

    private static class TextRun {
        private final JsonNode node;

        private TextRun(JsonNode node) {
            this.node = node;
        }

        private JsonNode getNode() {
            return node;
        }

        private Optional<String> getTextFrom() {
            if (!getNode().hasNonNull("content")) return Optional.empty();
            String content = getNode().get("content").textValue();

            // TODO FUTURE: Keep special characters (like ’ (which is different from '))
            content = content.replaceAll("\\P{Print}", "");

            return Optional.of(content);
        }

        private Optional<String> getLinkFrom() {
            if (!getNode().hasNonNull("textStyle")) return Optional.empty();
            if (!getNode().get("textStyle").hasNonNull("link")) return Optional.empty();
            if (!getNode().get("textStyle").get("link").hasNonNull("url")) return Optional.empty();

            return Optional.of(getNode().get("textStyle").get("link").get("url").textValue());
        }
    }
}
