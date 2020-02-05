package net.nahknarmi.arch.domain.c4;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class C4Person extends BaseEntity implements Entity, Locatable {
    private C4Location location;

    C4Person() {
        super();
    }

    @Builder
    C4Person(@NonNull C4Path path, @NonNull String description, @NonNull List<C4Tag> tags, @NonNull List<C4Relationship> relationships, C4Location location) {
        super(path, description, tags, relationships);
        this.location = location;
    }

    @JsonIgnore
    public String getName() {
        return path.getPersonName();
    }
}
