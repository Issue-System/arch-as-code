package net.nahknarmi.arch.adapter.out;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import net.nahknarmi.arch.domain.c4.C4Person;

import java.io.IOException;

public class C4PersonSerializer extends StdSerializer<C4Person> {


    public C4PersonSerializer(Class<C4Person> t) {
        super(t);
    }

    @Override
    public void serialize(C4Person value, JsonGenerator gen, SerializerProvider provider) throws IOException {

        gen.writeStartObject();

        gen.writeStringField("path", value.getPath().getPath());
        gen.writeStringField("description", value.getDescription());
        gen.writeStringField("location", value.getLocation().name());

        gen.writeFieldName("tags");
        gen.writeStartArray();
        value.getTags().forEach(t -> {
            try {
                gen.writeString(t.getTag());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        gen.writeEndArray();

        gen.writeFieldName("relationships");
        gen.writeStartArray();
        value.getRelationships().forEach(x -> {
            try {
                gen.writeObject(x);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        gen.writeEndArray();



        gen.writeEndObject();
    }
}
