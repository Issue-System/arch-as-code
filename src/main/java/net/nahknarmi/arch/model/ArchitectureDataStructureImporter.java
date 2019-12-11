package net.nahknarmi.arch.model;

import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;

import static com.google.common.base.Preconditions.checkNotNull;

public class ArchitectureDataStructureImporter {

    public ArchitectureDataStructure load(InputStream inputStream) {
        checkNotNull(inputStream, "InputStream must not be null.");
        return new Yaml().loadAs(inputStream, ArchitectureDataStructure.class);
    }
}
