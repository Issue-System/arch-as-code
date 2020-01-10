package net.nahknarmi.arch.adapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Optional;

import static java.util.Optional.empty;

public abstract class Credentials {

    static Optional<InputStream> credentialsAsStream() {
        try {
            return Optional.of(new FileInputStream(new File("./.arch-as-code/structurizr/credentials.json")));
        } catch (FileNotFoundException e) {
            return empty();
        }
    }
}
