package net.trilogy.arch.domain.c4;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class C4Tag {
    @NonNull
    String tag;

    @JsonValue
    public String getTag() {
        return tag;
    }
}
