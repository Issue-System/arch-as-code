package net.nahknarmi.arch.domain.c4;

import lombok.*;

import java.util.List;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;
import static java.lang.String.format;
import static java.util.Optional.ofNullable;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class C4Container extends BaseEntity implements Entity, HasTechnology, HasUrl {
    protected String systemId;
    protected String systemAlias;
    @NonNull
    protected String technology;
    protected String url;


    @Builder(toBuilder = true)
    public C4Container(@NonNull String id, String alias, C4Path path, @NonNull String name, @NonNull String description, Set<C4Tag> tags, List<C4Relationship> relationships, String systemId, String systemAlias, @NonNull String technology, String url) {
        super(id, alias, path, name, description, tags, relationships);
        this.systemId = systemId;
        this.systemAlias = systemAlias;
        this.technology = technology;
        this.url = url;
    }

    public String name() {
        return ofNullable(name)
                .orElse(path.containerName().orElseThrow(()
                        -> new IllegalStateException("Container name couldn't be extracted from " + path)));
    }

    public C4Type getType() {
        return C4Type.container;
    }

    public static class C4ContainerBuilder {
        public C4ContainerBuilder path(C4Path path) {
            checkArgument(C4Type.container.equals(path.type()), format("Path %s is not valid for Container.", path));
            this.path = path;
            return this;
        }
    }
}
