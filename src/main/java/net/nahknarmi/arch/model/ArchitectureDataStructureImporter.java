package net.nahknarmi.arch.model;

import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;

import static com.google.common.base.Preconditions.checkNotNull;

public class ArchitectureDataStructureImporter {

    public ArchitectureDataStructure load(InputStream manifest) {
        checkNotNull(manifest, "InputStream must not be null.");
        return new Yaml().loadAs(manifest, ArchitectureDataStructure.class);
    }
}
