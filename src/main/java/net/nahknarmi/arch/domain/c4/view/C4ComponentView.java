package net.nahknarmi.arch.domain.c4.view;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import net.nahknarmi.arch.domain.c4.C4Container;
import net.nahknarmi.arch.domain.c4.C4Model;
import net.nahknarmi.arch.domain.c4.Entity;
import net.nahknarmi.arch.domain.c4.HasIdentity;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class C4ComponentView extends C4View implements HasContainerReference, HasIdentity<C4Container> {
    private String containerId;
    private String containerAlias;

    @Override
    public String getId() {
        return containerId;
    }

    @Override
    public String getAlias() {
        return containerAlias;
    }

    public C4Container getReferenced(C4Model dataStructureModel) {
        Entity result;
        if (containerId != null) {
            result = dataStructureModel.findEntityById(containerId);
        } else if (containerAlias != null) {
            result = dataStructureModel.findEntityByAlias(containerAlias);
        } else {
            throw new IllegalStateException("SystemView is missing id and alias: " + this);
        }

        if (result instanceof C4Container) {
            return (C4Container) result;
        } else {
            throw new IllegalStateException("ComponentView is not referencing a C4Container: " + result);
        }
    }
}
