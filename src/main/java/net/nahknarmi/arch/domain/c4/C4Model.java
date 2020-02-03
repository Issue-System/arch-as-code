package net.nahknarmi.arch.domain.c4;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class C4Model {
    public static final C4Model NONE = new C4Model();

    @NonNull
    private List<C4Person> people = emptyList();
    @NonNull
    private List<C4SoftwareSystem> systems = emptyList();
    @NonNull
    private List<C4Container> containers = emptyList();
    @NonNull
    private List<C4Component> components = emptyList();

    public List<Entity> allEntities() {
        return Stream.of(getSystems(), getPeople(), getComponents(), getContainers())
                .flatMap(Collection::stream).collect(toList());
    }

    public List<C4Relationship> allRelationships() {
        return allEntities().stream().flatMap(x -> x.getRelationships().stream()).collect(toList());
    }

    public Entity getByPath(String path) {
        C4Path c4Path = new C4Path(path);
        return getByPath(c4Path);
    }

    public Entity getByPath(C4Path path) {
        if (path.getType().equals(C4Type.person)) {
            return people.stream()
                    .filter(p -> p.getName().equals(path.getPersonName()))
                    .findFirst()
                    .get();
        }
        if (path.getType().equals(C4Type.system)) {
            return systems.stream()
                    .filter(s -> s.getName().equals(path.getSystemName()))
                    .findFirst()
                    .get();
        }
        if (path.getType().equals(C4Type.container)) {
            return containers.stream()
                    .filter(cont -> cont.getPath().getSystemName().equals(path.getSystemName()) &&
                            cont.getPath().getContainerName().equals(path.getContainerName()))
                    .findFirst()
                    .get();
        }
        if (path.getType().equals(C4Type.component)) {
            return components.stream()
                    .filter(cont -> cont.getPath().getSystemName().equals(path.getSystemName()) &&
                            cont.getPath().getContainerName().equals(path.getContainerName()) &&
                            cont.getPath().getComponentName().equals(path.getComponentName()))
                    .findFirst()
                    .get();
        }

        return null;
    }

}
