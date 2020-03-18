package net.trilogy.arch.adapter.in;

import net.trilogy.arch.adapter.ArchitectureDataStructureObjectMapper;
import net.trilogy.arch.domain.ArchitectureDataStructure;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public class ArchitectureDataStructureReader {

    public ArchitectureDataStructure load(File manifest) throws IOException {
        checkNotNull(manifest, "Manifest must not be null.");
        checkArgument(manifest.exists(), String.format("Manifest file does not exist - %s.", manifest.getAbsolutePath()));


        return new ArchitectureDataStructureObjectMapper().readValue(new FileInputStream(manifest), ArchitectureDataStructure.class);

    }
}
