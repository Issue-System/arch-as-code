package net.trilogy.arch.adapter.architectureUpdateYaml;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import com.fasterxml.jackson.dataformat.yaml.YAMLParser;
import net.trilogy.arch.domain.architectureUpdate.ArchitectureUpdate;

import java.io.IOException;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;
import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.NONE;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static com.fasterxml.jackson.annotation.PropertyAccessor.FIELD;
import static com.fasterxml.jackson.annotation.PropertyAccessor.GETTER;
import static com.fasterxml.jackson.annotation.PropertyAccessor.IS_GETTER;

public class ArchitectureUpdateObjectMapper {
    private final ObjectMapper mapper;

    public ArchitectureUpdateObjectMapper() {
        this.mapper = new ObjectMapper(
                new YAMLFactory()
                        .configure(YAMLGenerator.Feature.SPLIT_LINES, false)
                        .disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER)
                        .configure(JsonParser.Feature.STRICT_DUPLICATE_DETECTION, true)
        );
        this.mapper.setVisibility(FIELD, ANY);
        this.mapper.setVisibility(GETTER, NONE);
        this.mapper.setVisibility(IS_GETTER, NONE);
        this.mapper.setSerializationInclusion(NON_NULL);
    }

    public String writeValueAsString(ArchitectureUpdate value) throws IOException {
        return this.mapper.writeValueAsString(value);
    }

    public ArchitectureUpdate readValue(String architectureAsString) throws JsonProcessingException {
        return this.mapper.readValue(architectureAsString, ArchitectureUpdate.class);
    }
}
