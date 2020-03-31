package net.trilogy.arch.adapter.in.google;

import com.google.api.services.docs.v1.Docs;
import net.trilogy.arch.domain.ArchitectureUpdate;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class GoogleDocumentReader {
    private static final Pattern pattern = Pattern.compile("\\/document\\/d\\/([^\\/]+)");
    private Docs api;

    public GoogleDocumentReader(GoogleDocsAuthorizedApiFactory apiFactory) throws IOException {
        this.api = apiFactory.getAuthorizedDocsApi();
    }

    public ArchitectureUpdate load(String url) throws IOException {
        if (url == null || url.isBlank()) {
            throw new InvalidUrlException();
        }

        api.documents().get(getDocumentId(url));

        return ArchitectureUpdate.blank();
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
