package net.trilogy.arch.domain;

import com.google.common.io.Files;
import lombok.Data;

import java.io.File;

@Data
public class DocumentationImage {
    private final String name;
    private final String type;
    private final String content;

    public static Boolean isImage(File image) {
        if (image == null) return false;
        if (image.isDirectory()) return false;

        String fileExtension = Files.getFileExtension(image.getName());

        // Supported Structurizr image types
        return fileExtension.equals("png") ||
                fileExtension.equals("jpg") ||
                fileExtension.equals("jpeg") ||
                fileExtension.equals("gif");
    }
}
