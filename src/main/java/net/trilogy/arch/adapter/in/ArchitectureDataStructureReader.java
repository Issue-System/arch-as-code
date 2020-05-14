package net.trilogy.arch.adapter.in;

import net.trilogy.arch.adapter.ArchitectureDataStructureObjectMapper;
import net.trilogy.arch.adapter.FilesFacade;
import net.trilogy.arch.domain.ArchitectureDataStructure;

import java.io.File;
import java.io.IOException;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public class ArchitectureDataStructureReader {

    final private FilesFacade filesFacade;

    public ArchitectureDataStructure load(File manifest) throws IOException {
        checkNotNull(manifest, "Manifest must not be null.");
        checkArgument(manifest.exists(), String.format("Manifest file does not exist - %s.", manifest.getAbsolutePath()));

        final String archAsString = filesFacade.readString(manifest.toPath());
        return new ArchitectureDataStructureObjectMapper().readValue(archAsString);
    }

    public ArchitectureDataStructureReader(FilesFacade filesFacade) {
        this.filesFacade = filesFacade;
    }

}
