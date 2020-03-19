package net.trilogy.arch.adapter.out.serialize;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import lombok.NonNull;
import net.trilogy.arch.domain.c4.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.util.Optional;

import static java.util.Optional.ofNullable;

public class C4EntitySerializer<T extends BaseEntity> extends StdSerializer<T> {
    private static final Log log = LogFactory.getLog(C4EntitySerializer.class);

    public C4EntitySerializer(Class<T> t) {
        super(t);
    }

    @Override
    public void serialize(T value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();

        baseEntityWrite(value, gen);

        gen.writeEndObject();
    }

    protected void baseEntityWrite(T value, JsonGenerator gen) throws IOException {
        gen.writeStringField("id", value.getId());
        writeAlias(value, gen);
        writeParentIdentity(value, gen);
        gen.writeStringField("name", value.getName());
        gen.writeStringField("description", value.getDescription());

        writeTags(value, gen);
        writeRelationships(value, gen);

        writeLocation(value, gen);
        writeUrl(value, gen);
        writeTechnology(value, gen);
    }

    private void writeAlias(T value, JsonGenerator gen) throws IOException {
        ofNullable(value.getAlias())
                .ifPresent(alias -> wrappedWriteStringField(gen, "alias", alias));
    }

    private void writeParentIdentity(T value, JsonGenerator gen) throws IOException {
        if (value instanceof C4Container) {
            if (((C4Container) value).getSystemId() != null) {
                gen.writeStringField("systemId", ((C4Container) value).getSystemId());
            }
            if (((C4Container) value).getSystemAlias() != null) {
                gen.writeStringField("systemAlias", ((C4Container) value).getSystemAlias());
            }
        }
        if (value instanceof C4Component) {
            if (((C4Component) value).getContainerId() != null) {
                gen.writeStringField("containerId", ((C4Component) value).getContainerId());
            }
            if (((C4Component) value).getContainerAlias() != null) {
                gen.writeStringField("containerAlias", ((C4Component) value).getContainerAlias());
            }
        }
    }

    private void writeTags(T value, JsonGenerator gen) throws IOException {
        gen.writeFieldName("tags");
        gen.writeStartArray();
        value.getTags().forEach(t -> {
            try {
                gen.writeString(t.getTag());
            } catch (IOException e) {
                log.error("Failed to write tags.", e);
                throw new IllegalStateException("Failed to write tags.", e);
            }
        });
        gen.writeEndArray();
    }

    private void writeRelationships(T value, JsonGenerator gen) throws IOException {
        gen.writeFieldName("relationships");
        gen.writeStartArray();
        value.getRelationships().forEach(r -> {
            try {
                @NonNull String with = r.getWithId();
                @NonNull C4Action action = r.getAction();

                gen.writeStartObject();
                gen.writeStringField("id", r.getId());
                ofNullable(r.getWithId()).ifPresent((t) -> wrappedWriteStringField(gen, "withId", t));
                ofNullable(r.getWithAlias()).ifPresent((t) -> wrappedWriteStringField(gen, "withAlias", t));
                gen.writeStringField("action", action.name());
                gen.writeStringField("description", r.getDescription());
                ofNullable(r.getTechnology()).ifPresent((t) -> wrappedWriteStringField(gen, "technology", t));
                gen.writeEndObject();

            } catch (IOException e) {
                log.error("Failed to write relationships.", e);
                throw new IllegalStateException("Failed to write relationships.", e);
            }
        });
        gen.writeEndArray();
    }

    public static void wrappedWriteStringField(JsonGenerator gen, String fieldName, String fieldValue) {
        try {
            if (fieldValue != null) {
                gen.writeStringField(fieldName, fieldValue);
            }
        } catch (IOException e) {
            log.error("Failed to write field - " + fieldName, e);
            throw new IllegalStateException("Failed to write field - " + fieldName);
        }
    }

    private void writeTechnology(T value, JsonGenerator gen) {
        if (value instanceof HasTechnology) {
            ofNullable(((HasTechnology) value).getTechnology()).ifPresent((x) -> wrappedWriteStringField(gen, "technology", x));
        }
    }

    private void writeUrl(T value, JsonGenerator gen) {
        if (value instanceof HasUrl) {
            ofNullable(((HasUrl) value).getUrl()).ifPresent((x) -> wrappedWriteStringField(gen, "url", x));
        }
    }

    private void writeLocation(T value, JsonGenerator gen) throws IOException {
        if (value instanceof HasLocation) {
            Optional<String> location = ofNullable(((HasLocation) value).getLocation()).map(Enum::name);
            if (location.isPresent()) {
                gen.writeStringField("location", location.get());
            }
        }
    }
}
