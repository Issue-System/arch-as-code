package net.trilogy.arch.adapter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FilesFacade {
    public Path writeString(File file, String credentialJsonString) throws IOException {
        return Files.writeString(file.toPath(), credentialJsonString);
    }
}
