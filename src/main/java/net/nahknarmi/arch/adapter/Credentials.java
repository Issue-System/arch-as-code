package net.nahknarmi.arch.adapter;

import io.vavr.control.Try;

import java.io.File;
import java.io.FileInputStream;
import java.util.Optional;

public abstract class Credentials {

    static Optional<FileInputStream> credentialsAsStream() {
        return Try.of(() -> new FileInputStream(new File("./.arch-as-code/structurizr/credentials.json")))
                .map(Optional::of)
                .getOrElse(Optional.empty());
    }
}
