package net.trilogy.arch.adapter.architectureYaml;

import net.trilogy.arch.facade.FilesFacade;
import net.trilogy.arch.domain.ArchitectureDataStructure;

import java.io.File;
import java.io.IOException;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public class ArchitectureDataStructureReader {

    final private FilesFacade filesFacade;

    public ArchitectureDataStructure load(File manifest) throws IOException {
        final String archAsString = filesFacade.readString(manifest.toPath());
        return new ArchitectureDataStructureObjectMapper().readValue(archAsString);
    }

    public ArchitectureDataStructureReader(FilesFacade filesFacade) {
        this.filesFacade = filesFacade;
    }

}
