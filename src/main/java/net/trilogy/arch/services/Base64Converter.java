package net.trilogy.arch.services;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;

public class Base64Converter {
    public static String toString(Path path) throws IOException {
        byte[] inFileBytes = Files.readAllBytes(path);
        final byte[] encoded = Base64.getEncoder().encode(inFileBytes);

        return new String(encoded, "UTF-8");
    }

    public static String toString(String string) {
        byte[] bytes = string.getBytes();
        String encoded = Base64.getEncoder().encodeToString(bytes);

        return encoded;
    }


    public static Boolean toFile(String encodedString, Path outputStreamPath) throws IOException {
        final byte[] decoded = Base64.getDecoder().decode(encodedString);
        final FileOutputStream outputStream = new FileOutputStream(outputStreamPath.toString());

        outputStream.write(decoded);
        outputStream.flush();
        outputStream.close();

        return true;
    }
}
