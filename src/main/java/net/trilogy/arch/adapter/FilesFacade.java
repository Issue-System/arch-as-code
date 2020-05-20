package net.trilogy.arch.adapter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FilesFacade {
    public Path writeString(Path path, String toWrite) throws IOException {
        return Files.writeString(path, toWrite);
    }

    public String readString(Path path) throws IOException {
        return Files.readString(path);
    }

    public Path createDirectory(Path path) throws IOException {
        return Files.createDirectory(path);
    }
}
