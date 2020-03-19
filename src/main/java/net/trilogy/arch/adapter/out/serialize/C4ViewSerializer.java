package net.trilogy.arch.adapter.out.serialize;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import net.trilogy.arch.domain.c4.view.C4View;
import net.trilogy.arch.domain.c4.view.HasContainerReference;
import net.trilogy.arch.domain.c4.view.HasSystemReference;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;

public class C4ViewSerializer<T extends C4View> extends StdSerializer<T> {
    private static final Log log = LogFactory.getLog(C4ViewSerializer.class);

    public C4ViewSerializer(Class<T> t) {
        super(t);
    }

    @Override
    public void serialize(T value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();

        baseViewSerialize(value, gen);

        gen.writeEndObject();
    }

    protected void baseViewSerialize(T value, JsonGenerator gen) throws IOException {
        gen.writeStringField("name", value.getName());
        gen.writeStringField("key", value.getKey());

        writeSystemIdentity(value, gen);
        writeContainerIdentity(value, gen);
        gen.writeStringField("description", value.getDescription());

        writeTags(value, gen);
        writeReferences(value, gen);
    }

    private void writeReferences(T value, JsonGenerator gen) throws IOException {
        gen.writeFieldName("references");
        gen.writeStartArray();
        value.getReferences().forEach(ref -> {
            try {
                if (ref.getId() != null) {
                    gen.writeStartObject();
                    gen.writeStringField("id", ref.getId());
                    gen.writeEndObject();
                } else if (ref.getAlias() != null) {
                    gen.writeStartObject();
                    gen.writeStringField("alias", ref.getAlias());
                    gen.writeEndObject();
                } else {
                    throw new IllegalStateException("Reference missing both id and alias: " + ref);
                }
            } catch (IOException e) {
                log.error("Failed to write references.", e);
                throw new IllegalStateException("Failed to references.", e);
            }
        });
        gen.writeEndArray();
    }

    private void writeTags(T value, JsonGenerator gen) throws IOException {
        gen.writeFieldName("tags");
        gen.writeStartArray();
        value.getTags().forEach(t -> {
            try {
                gen.writeString(t.getTag());
            } catch (IOException e) {
                log.error("Failed to write tags.", e);
                throw new IllegalStateException("Failed to tags.", e);
            }
        });
        gen.writeEndArray();
    }

    private void writeContainerIdentity(T value, JsonGenerator gen) {
        if (value instanceof HasContainerReference) {
            boolean hasContainerAlias = ((HasContainerReference) value).getContainerAlias() != null;
            boolean hasContainerId = ((HasContainerReference) value).getContainerId() != null;
            if (!hasContainerAlias && !hasContainerId) {
                throw new IllegalStateException("HasContainerReference missing both id and alias: " + value);
            }
            if (hasContainerAlias) {
                C4EntitySerializer.wrappedWriteStringField(gen, "containerAlias", ((HasContainerReference) value).getContainerAlias());
            }
            if (hasContainerId) {
                C4EntitySerializer.wrappedWriteStringField(gen, "containerId", ((HasContainerReference) value).getContainerId());
            }
        }

    }

    private void writeSystemIdentity(T value, JsonGenerator gen) {
        if (value instanceof HasSystemReference) {
            boolean hasSystemAlias = ((HasSystemReference) value).getSystemAlias() != null;
            boolean hasSystemId = ((HasSystemReference) value).getSystemId() != null;
            if (!hasSystemAlias && !hasSystemId) {
                throw new IllegalStateException("HasContainerReference missing both id and alias: " + value);
            }
            if (hasSystemAlias) {
                C4EntitySerializer.wrappedWriteStringField(gen, "systemAlias", ((HasSystemReference) value).getSystemAlias());
            }
            if (hasSystemId) {
                C4EntitySerializer.wrappedWriteStringField(gen, "systemId", ((HasSystemReference) value).getSystemId());
            }
        }
    }
}
