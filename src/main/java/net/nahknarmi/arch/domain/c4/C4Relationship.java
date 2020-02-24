package net.nahknarmi.arch.domain.c4;


import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
public class C4Relationship implements HasIdentity<C4Relationship> {
    private String id;
    private String alias;
    @NonNull
    private C4Action action;
    private String withAlias;
    private String withId;
    @NonNull
    private String description;
    private String technology;

    @Builder
    public C4Relationship(String id, String alias, @NonNull C4Action action, String withAlias, String withId, @NonNull String description, String technology) {
        this.id = id;
        this.alias = alias;
        this.action = action;
        this.withAlias = withAlias;
        this.withId = withId;
        this.description = description;
        this.technology = technology;
    }

    @Override
    public C4Relationship getReferenced(C4Model dataStructureModel) {
        C4Relationship result;
        if (id != null) {
            result = dataStructureModel.findRelationshipById(id);
        } else if (alias != null) {
            result = dataStructureModel.findRelationshipByAlias(alias);
        } else {
            throw new IllegalStateException("Relationship is missing id and alias: " + this);
        }

        return result;
    }
}
