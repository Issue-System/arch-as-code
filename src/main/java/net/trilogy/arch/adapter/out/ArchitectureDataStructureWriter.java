package net.trilogy.arch.adapter.out;

import net.trilogy.arch.adapter.ArchitectureDataStructureObjectMapper;
import net.trilogy.arch.domain.ArchitectureDataStructure;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.IOException;

public class ArchitectureDataStructureWriter {
    private static final Log logger = LogFactory.getLog(ArchitectureDataStructureWriter.class);

    public File export(ArchitectureDataStructure dataStructure) throws IOException {
        File tempFile = File.createTempFile("arch-as-code", ".yml");
        ArchitectureDataStructureObjectMapper mapper = new ArchitectureDataStructureObjectMapper();
        mapper.writeValue(tempFile, dataStructure);
        logger.info(String.format("Architecture data structure written to - %s", tempFile.getAbsolutePath()));
        return tempFile;
    }

}
