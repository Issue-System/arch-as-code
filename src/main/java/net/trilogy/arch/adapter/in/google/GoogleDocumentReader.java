package net.trilogy.arch.adapter.in.google;

import net.trilogy.arch.domain.ArchitectureUpdate;
import net.trilogy.arch.domain.ArchitectureUpdate.P1;
import net.trilogy.arch.domain.Jira;

import java.io.IOException;


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
                .p1(extractP1(jsonParser))
                .build();
    }

    private P1 extractP1(GoogleDocsJsonParser jsonParser) {
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

}
