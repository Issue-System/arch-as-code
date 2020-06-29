package net.trilogy.arch.domain;

import lombok.Data;

@Data
public class DocumentationSection {
    private final String elementId;
    private final String title;
    private final Integer order;
    private final Format format;
    private final String content;

    public enum Format {
        MARKDOWN,
        ASCIIDOC
    }

}
