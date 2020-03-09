package net.nahknarmi.arch.domain.c4;

import lombok.*;

import java.util.List;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;
import static java.lang.String.format;
import static java.util.Optional.ofNullable;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class C4Component extends BaseEntity implements Entity, HasTechnology, HasUrl {
    protected String containerId;
    protected String containerAlias;
    @NonNull
    protected String technology;
    protected String url;

    @Builder(toBuilder = true)
    public C4Component(@NonNull String id, String alias, C4Path path, @NonNull String name, String description, @Singular Set<C4Tag> tags, @Singular List<C4Relationship> relationships, String containerId, String containerAlias, String technology, String url) {
        super(id, alias, path, name, description, tags, relationships);
        this.containerId = containerId;
        this.containerAlias = containerAlias;
        this.technology = technology;
        this.url = url;
    }

    public String name() {
        return ofNullable(this.name).orElse(path.componentName().orElseThrow(() -> new IllegalStateException("Component name could not be derived.")));
    }

    public C4Type getType() {
        return C4Type.component;
    }

    public static class C4ComponentBuilder {
        public C4ComponentBuilder path(C4Path path) {
            checkArgument(C4Type.component.equals(path.type()), format("Path %s is not valid for Component.", path));
            this.path = path;
            return this;
        }
    }
}
