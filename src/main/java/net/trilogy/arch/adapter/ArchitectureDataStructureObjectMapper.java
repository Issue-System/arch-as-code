package net.trilogy.arch.adapter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import net.trilogy.arch.adapter.out.serialize.C4EntitySerializer;
import net.trilogy.arch.adapter.out.serialize.C4ViewSerializer;
import net.trilogy.arch.adapter.out.serialize.DateSerializer;
import net.trilogy.arch.domain.c4.C4Component;
import net.trilogy.arch.domain.c4.C4Container;
import net.trilogy.arch.domain.c4.C4Person;
import net.trilogy.arch.domain.c4.C4SoftwareSystem;
import net.trilogy.arch.domain.c4.view.C4ComponentView;
import net.trilogy.arch.domain.c4.view.C4ContainerView;
import net.trilogy.arch.domain.c4.view.C4SystemView;

import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;
import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.NONE;
import static com.fasterxml.jackson.annotation.PropertyAccessor.*;

public class ArchitectureDataStructureObjectMapper {
    private final ObjectMapper mapper;

    public ArchitectureDataStructureObjectMapper() {
        this.mapper = new ObjectMapper(new YAMLFactory().disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER));
        this.mapper.setVisibility(FIELD, ANY);
        this.mapper.setVisibility(GETTER, NONE);
        this.mapper.setVisibility(IS_GETTER, NONE);
        this.mapper.registerModule(buildModule());
    }

    public void writeValue(@NotNull File resultFile, Object value) throws IOException {
        this.mapper.writeValue(resultFile, value);
    }

    public JsonNode readTree(@NotNull InputStream in) throws IOException {
        return this.mapper.readTree(in);
    }

    public <T> T readValue(@NotNull InputStream src, Class<T> valueType) throws IOException {
        return this.mapper.readValue(src, valueType);
    }

    private SimpleModule buildModule() {
        SimpleModule module = new SimpleModule();
        module.addSerializer(new C4EntitySerializer<>(C4Person.class))
                .addSerializer(new C4EntitySerializer<>(C4SoftwareSystem.class))
                .addSerializer(new C4EntitySerializer<>(C4Container.class))
                .addSerializer(new C4EntitySerializer<>(C4Component.class))
                .addSerializer(new C4ViewSerializer<>(C4ContainerView.class))
                .addSerializer(new C4ViewSerializer<>(C4ComponentView.class))
                .addSerializer(new C4ViewSerializer<>(C4SystemView.class))
                .addSerializer(new DateSerializer(Date.class));
        return module;
    }
}
