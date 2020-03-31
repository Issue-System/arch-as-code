package net.trilogy.arch.adapter.in.google;

import com.google.api.services.docs.v1.Docs;
import net.trilogy.arch.domain.ArchitectureUpdate;

public class GoogleDocumentReader {
    private GoogleDocsAuthorizer authorizer;
    private Docs docsApi;

    public GoogleDocumentReader(GoogleDocsAuthorizer authorizer) {
        this.authorizer = authorizer;
    }

    public ArchitectureUpdate load(String s) {
        if (s == null || s.isBlank()) {
            throw new InvalidUrlException();
        }

        return ArchitectureUpdate.blank();
    }

    class InvalidUrlException extends RuntimeException {
    }
}
