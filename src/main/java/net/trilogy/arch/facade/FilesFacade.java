package net.trilogy.arch.facade;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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

    public File createTempFile(String prefix, String suffix) throws IOException {
        return File.createTempFile(prefix, suffix);
    }

    public FileOutputStream newFileOutputStream(String name) throws FileNotFoundException {
        return new FileOutputStream(name);
    }
}
