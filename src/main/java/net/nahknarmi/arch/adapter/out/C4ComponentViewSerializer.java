package net.nahknarmi.arch.adapter.out;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import net.nahknarmi.arch.domain.c4.view.C4ComponentView;

import java.io.IOException;

public class C4ComponentViewSerializer extends C4BaseViewSerializer<C4ComponentView> {
    public C4ComponentViewSerializer(Class<C4ComponentView> t) {
        super(t);
    }

    @Override
    public void serialize(C4ComponentView value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();

        gen.writeStringField("containerPath", value.getContainerPath().getPath());

        baseViewSerialize(value, gen);

        gen.writeEndObject();
    }
}
