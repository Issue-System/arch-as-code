package net.trilogy.arch.adapter;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import net.trilogy.arch.adapter.out.serialize.DateSerializer;
import net.trilogy.arch.domain.ArchitectureDataStructure;

import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;
import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.NONE;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static com.fasterxml.jackson.annotation.PropertyAccessor.*;

public class ArchitectureDataStructureObjectMapper {
    private final ObjectMapper mapper;

    public ArchitectureDataStructureObjectMapper() {
        this.mapper = new ObjectMapper(new YAMLFactory().disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER));
        this.mapper.setVisibility(FIELD, ANY);
        this.mapper.setVisibility(GETTER, NONE);
        this.mapper.setVisibility(IS_GETTER, NONE);
        this.mapper.registerModule(buildModule());
        this.mapper.setSerializationInclusion(NON_NULL);
    }

    public void writeValue(@NotNull File resultFile, ArchitectureDataStructure value) throws IOException {
        this.mapper.writeValue(resultFile, value);
    }

    public JsonNode readTree(@NotNull InputStream in) throws IOException {
        return this.mapper.readTree(in);
    }

    public ArchitectureDataStructure readValue(@NotNull InputStream src) throws IOException {
        return this.mapper.readValue(src, ArchitectureDataStructure.class);
    }

    private SimpleModule buildModule() {
        SimpleModule module = new SimpleModule();
        module.addSerializer(new DateSerializer(Date.class));
        return module;
    }
}
