package net.trilogy.arch.domain.c4.view;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.trilogy.arch.domain.c4.C4Reference;
import net.trilogy.arch.domain.c4.C4Tag;

import java.util.Optional;
import java.util.Set;

import static java.util.Collections.emptySet;

@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class C4View {
    private String key;
    private String name;
    private String description;
    private Set<C4Tag> tags = emptySet();
    private Set<C4Reference> references = emptySet();

    // description is never null, write test
    public String getKey() {
        return Optional.ofNullable(key).orElse(getName() + "-" + getDescription());
    }
}
