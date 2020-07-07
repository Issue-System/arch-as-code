package net.trilogy.arch.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

public class Base64Converter {
    public static String toString(String path) throws IOException {
        byte[] inFileBytes = Files.readAllBytes(Paths.get(path));
        final byte[] encoded = Base64.getEncoder().encode(inFileBytes);

        return new String(encoded, "UTF-8");
    }
}
