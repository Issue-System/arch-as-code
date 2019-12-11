package net.nahknarmi.arch.model;

import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import static com.google.common.base.Preconditions.checkNotNull;

public class ArchitectureDataStructureImporter {

    public ArchitectureDataStructure load(File manifest) throws FileNotFoundException {
        checkNotNull(manifest, "Manifest must not be null.");
        return new Yaml().loadAs(new FileInputStream(manifest), ArchitectureDataStructure.class);
    }
}
