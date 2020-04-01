package net.trilogy.arch.adapter.in.google;

import net.trilogy.arch.domain.ArchitectureUpdate;

import java.io.IOException;


public class GoogleDocumentReader {
    private GoogleDocsApiInterface api;

    public GoogleDocumentReader(GoogleDocsAuthorizedApiFactory apiFactory) throws IOException {
        this.api = new GoogleDocsApiInterface(apiFactory);
    }

    public ArchitectureUpdate load(String url) throws IOException {
        return null;
    }
}
