package net.trilogy.arch.adapter.architectureYaml;

import net.trilogy.arch.domain.ArchitectureDataStructure;
import net.trilogy.arch.domain.DocumentationSection;
import net.trilogy.arch.facade.FilesFacade;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class ArchitectureDataStructureWriter {
    public File export(ArchitectureDataStructure dataStructure) throws IOException {
        File tempFile = File.createTempFile("arch-as-code", ".yml");

        return export(dataStructure, tempFile);
    }

    public File export(ArchitectureDataStructure dataStructure, File writeFile) throws IOException {
        ArchitectureDataStructureObjectMapper mapper = new ArchitectureDataStructureObjectMapper();
        new FilesFacade().writeString(writeFile.toPath(), mapper.writeValueAsString(dataStructure));

        final Path documentation = Path.of(writeFile.getParent()).resolve("documentation");
        if (!documentation.toFile().exists()) new FilesFacade().createDirectory(documentation);

        for (DocumentationSection doc : dataStructure.getDocumentation()) {
            final File docFile = documentation.resolve(doc.getTitle() + ".md").toFile();
            new FilesFacade().writeString(docFile.toPath(), doc.getContent());
        }

        return writeFile;
    }

}
