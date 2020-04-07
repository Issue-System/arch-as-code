package net.trilogy.arch.adapter.in.google;

import net.trilogy.arch.domain.ArchitectureUpdate;
import net.trilogy.arch.domain.ArchitectureUpdate.P1;
import net.trilogy.arch.domain.ArchitectureUpdate.P2;
import net.trilogy.arch.domain.Jira;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;


public class GoogleDocumentReader {
    private GoogleDocsApiInterface api;

    public GoogleDocumentReader(GoogleDocsApiInterface api) {
        this.api = api;
    }

    public ArchitectureUpdate load(String url) throws IOException {
        var response = api.fetch(url);

        if (isEmpty(response)) {
            return ArchitectureUpdate.blank();
        }

        GoogleDocsJsonParser jsonParser = new GoogleDocsJsonParser(response.asJson());

        return ArchitectureUpdate.builder()
                .milestone(jsonParser.getMilestone().orElse(""))
                .p1(extractP1(jsonParser, url))
                .p2(extractP2(jsonParser))
                .decisions(extractDecisions(jsonParser))
                .build();
    }

    private List<ArchitectureUpdate.Decision> extractDecisions(GoogleDocsJsonParser jsonParser) {
        List<String> decisionStrings = jsonParser.getDecisions();
        return decisionStrings
                .stream()
                .map(string -> new ArchitectureUpdate.Decision(ArchitectureUpdate.DecisionType.ITD, string))
                .collect(Collectors.toList());
    }

    private P2 extractP2(GoogleDocsJsonParser jsonParser) {
        return P2.builder()
                .link(jsonParser.getP2Link().orElse(""))
                .build();
    }

    private P1 extractP1(GoogleDocsJsonParser jsonParser, String url) {
        return P1.builder()
                .link(url)
                .executiveSummary(jsonParser.getExecutiveSummary().orElse(""))
                .jira(new Jira(
                                jsonParser.getP1JiraTicket().orElse(""),
                                jsonParser.getP1JiraLink().orElse("")
                        )
                ).build();
    }


    private boolean isEmpty(GoogleDocsApiInterface.Response response) {
        return !response.asJson().hasNonNull("body");
    }

}
