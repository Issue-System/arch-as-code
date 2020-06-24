package net.trilogy.arch.adapter.architectureYaml;

import net.trilogy.arch.facade.FilesFacade;
import net.trilogy.arch.domain.ArchitectureDataStructure;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.IOException;

public class ArchitectureDataStructureWriter {
    public File export(ArchitectureDataStructure dataStructure) throws IOException {
        File tempFile = File.createTempFile("arch-as-code", ".yml");

        return export(dataStructure, tempFile);
    }

    public File export(ArchitectureDataStructure dataStructure, File writeFile) throws IOException {
        ArchitectureDataStructureObjectMapper mapper = new ArchitectureDataStructureObjectMapper();
        new FilesFacade().writeString(writeFile.toPath(), mapper.writeValueAsString(dataStructure));
        return writeFile;
    }

}
