package net.nahknarmi.arch.adapter.out;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import net.nahknarmi.arch.domain.c4.view.C4SystemView;

import java.io.IOException;

public class C4SystemViewSerializer extends C4BaseViewSerializer<C4SystemView> {
    public C4SystemViewSerializer(Class<C4SystemView> t) {
        super(t);
    }

    @Override
    public void serialize(C4SystemView value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();

        gen.writeStringField("systemPath", value.getSystemPath().getPath());

        baseViewSerialize(value, gen);

        gen.writeEndObject();
    }
}
