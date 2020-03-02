package net.nahknarmi.arch.domain.c4.view;

import lombok.*;
import net.nahknarmi.arch.domain.c4.C4SoftwareSystem;
import net.nahknarmi.arch.domain.c4.C4Tag;
import net.nahknarmi.arch.domain.c4.HasIdentity;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class C4DeploymentView extends C4View implements HasSystemReference, HasIdentity<C4SoftwareSystem> {
    private String systemId;
    private String systemAlias;
    private String environment;
    private C4DeploymentNode deploymentNode;

    @Builder
    public C4DeploymentView(String key, @NonNull String name, @NonNull String description, @Singular List<C4Tag> tags, @Singular List<C4ViewReference> references, String systemId, String systemAlias) {
        super(key, name, description, tags, references);
        this.systemId = systemId;
        this.systemAlias = systemAlias;
    }

    @Override
    public String getId() {
        return systemId;
    }

    @Override
    public String getAlias() {
        return systemAlias;
    }
}
