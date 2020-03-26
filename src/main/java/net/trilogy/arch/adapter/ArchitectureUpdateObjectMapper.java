package net.trilogy.arch.adapter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import net.trilogy.arch.domain.ArchitectureUpdate;

import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.IOException;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;
import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.NONE;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static com.fasterxml.jackson.annotation.PropertyAccessor.*;

public class ArchitectureUpdateObjectMapper {
    private final ObjectMapper mapper;

    public ArchitectureUpdateObjectMapper() {
        this.mapper = new ObjectMapper(new YAMLFactory().disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER));
        this.mapper.setVisibility(FIELD, ANY);
        this.mapper.setVisibility(GETTER, NONE);
        this.mapper.setVisibility(IS_GETTER, NONE);
        this.mapper.setSerializationInclusion(NON_NULL);
    }

    public void writeValue(@NotNull File resultFile, ArchitectureUpdate value) throws IOException {
        this.mapper.writeValue(resultFile, value);
    }
}
