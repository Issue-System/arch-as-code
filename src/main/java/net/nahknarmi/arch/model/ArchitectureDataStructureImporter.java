package net.nahknarmi.arch.model;

import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;

import static com.google.common.base.Preconditions.checkNotNull;

public class ArchitectureDataStructureImporter {

    public ArchitectureDataStructure load(InputStream inputStream) {
        checkNotNull(inputStream);
        Yaml yaml = new Yaml();
        return yaml.loadAs(inputStream, ArchitectureDataStructure.class);
    }
}
