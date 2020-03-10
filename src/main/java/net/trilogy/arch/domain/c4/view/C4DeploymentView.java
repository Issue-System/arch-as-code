package net.trilogy.arch.domain.c4.view;

import lombok.*;
import net.trilogy.arch.domain.c4.C4Reference;
import net.trilogy.arch.domain.c4.C4Tag;

import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class C4DeploymentView extends C4View {
    private C4Reference system;
    private String environment;

    @Builder
    public C4DeploymentView(String key, @NonNull String name, @NonNull String description, Set<C4Tag> tags, Set<C4Reference> references, C4Reference system, String environment) {
        super(key, name, description, tags, references);
        this.system = system;
        this.environment = environment;
    }
}
