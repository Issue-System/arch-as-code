package net.trilogy.arch.domain.c4;

import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class C4Tag {
    @NonNull
    String tag;

    @JsonValue
    public String getTag() {
        return tag;
    }
}
