package net.nahknarmi.arch.domain.c4;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class C4SoftwareSystem extends BaseEntity implements Entity, Locatable {
    private C4Location location;

    @Builder
    public C4SoftwareSystem(@NonNull C4Path path, @NonNull String technology, @NonNull String description, @NonNull List<C4Tag> tags, @NonNull List<C4Relationship> relationships, C4Location location) {
        super(path, technology, description, tags, relationships);
        this.location = location;
    }

    @JsonIgnore
    public String getName() {
        return path.getSystemName();
    }
}
