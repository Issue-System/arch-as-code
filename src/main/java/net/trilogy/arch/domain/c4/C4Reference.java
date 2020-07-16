package net.trilogy.arch.domain.c4;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode
public class C4Reference {
    private String id;
    private String alias;

    public C4Reference(String id, String alias) {
        if (id == null && alias == null) {
            throw new IllegalStateException("C4Reference has null for both id and alias attributes.");
        }

        this.id = id;
        this.alias = alias;
    }
}
