package net.nahknarmi.arch.generator;

import com.structurizr.model.ContainerInstance;
import com.structurizr.model.Element;
import com.structurizr.model.IdGenerator;
import com.structurizr.model.Relationship;
import io.vavr.Tuple2;
import lombok.NonNull;
import net.nahknarmi.arch.domain.c4.*;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.stream.Collectors.toList;

public class NumericIdGenerator implements IdGenerator {
    private final C4Model dataStructureModel;

    public NumericIdGenerator(@NonNull C4Model dataStructureModel) {
        this.dataStructureModel = dataStructureModel;
    }

    @Override
    public String generateId(Element element) {
        C4Type c4Type = C4Type.from(element);

        if (c4Type.equals(C4Type.containerInstance)) {
            return makeContainerInstanceId(element);
        }

        List<@NonNull Entity> possibleEntities = dataStructureModel
                .allEntities()
                .stream()
                .filter(e -> e.getType().equals(c4Type))
                .filter(e -> e.getName().equals(element.getName()))
                .filter(entity -> {
                    switch (c4Type) {
                        case container: {
                            String systemName = element.getParent().getName();
                            C4Container cont = (C4Container) entity;

                            if (cont.getSystemId() != null) {
                                C4SoftwareSystem sys = (C4SoftwareSystem) dataStructureModel.findEntityById(cont.getSystemId());
                                return sys.getName().equals(systemName);
                            } else if (cont.getSystemAlias() != null) {
                                C4SoftwareSystem sys = (C4SoftwareSystem) dataStructureModel.findEntityByAlias(cont.getSystemAlias());
                                return sys.getName().equals(systemName);
                            } else {
                                throw new IllegalStateException("Container systemId and systemAlias are missing: " + cont);
                            }
                        }
                        case component: {
                            String containerName = element.getParent().getName();
                            String systemName = element.getParent().getParent().getName();
                            C4Component comp = (C4Component) entity;


                            C4Container cont;
                            if (comp.getContainerId() != null) {
                                cont = (C4Container) dataStructureModel.findEntityById(comp.getContainerId());
                            } else if (comp.getContainerAlias() != null) {
                                cont = (C4Container) dataStructureModel.findEntityByAlias(comp.getContainerAlias());
                            } else {
                                throw new IllegalStateException("Component containerId and containerAlias are missing: " + comp);
                            }

                            C4SoftwareSystem sys;
                            if (cont.getSystemId() != null) {
                                sys = (C4SoftwareSystem) dataStructureModel.findEntityById(cont.getSystemId());
                            } else if (cont.getSystemAlias() != null) {
                                sys = (C4SoftwareSystem) dataStructureModel.findEntityByAlias(cont.getSystemAlias());
                            } else {
                                throw new IllegalStateException("Container systemId and systemAlias are missing: " + cont);
                            }

                            return sys.getName().equals(systemName)
                                    && cont.getName().equals(containerName);
                        }
                        case system:
                        case person:
                        case deploymentNode:
                            return true;
                        default:
                            throw new IllegalStateException("Unsupported type " + c4Type);
                    }
                })
                .collect(toList());

        if (possibleEntities.isEmpty()) {
            throw new IllegalStateException("Entity could not be found for element " + element);
        }

        if (possibleEntities.size() > 1) {
            throw new IllegalStateException("More than 1 matching entity found for element " + element);
        }

        return possibleEntities.get(0).getId();
    }

    private String makeContainerInstanceId(Element element) {
        String id = ((ContainerInstance) element).getContainer().getId() + "-" + ((ContainerInstance) element).getInstanceId();
        return id;
    }

    @Override
    public String generateId(Relationship relationship) {
        String sourceId = relationship.getSourceId();
        checkNotNull(sourceId, relationship);
        String destinationId = relationship.getDestinationId();
        checkNotNull(destinationId, relationship);

        if (sourceId.contains("-") && destinationId.contains("-")) {
            // containerInstance -> ContainerInstance relationship
            return sourceId + "->" + destinationId;
        }

        List<Tuple2<Entity, C4Relationship>> possibleRelationships = dataStructureModel
                .allRelationships()
                .stream()
                .filter(t -> t._1.getId().equals(relationship.getSource().getId()))
                .filter(t -> {
                    String entityId = dataStructureModel.findEntityByRelationshipWith(t._2).getId();
                    return entityId.equals(relationship.getDestination().getId());
                })
                .filter(t -> {
                    if (relationship.getTechnology() == null && t._2.getTechnology() == null) {
                        return true;
                    }
                    if (relationship.getTechnology() != null && t._2.getTechnology() != null) {
                        return t._2.getTechnology().equals(relationship.getTechnology());
                    } else {
                        return false;
                    }
                }).filter(t -> {
                    if (relationship.getDescription() == null && t._2.getDescription() == null) {
                        return true;
                    }
                    if (relationship.getDescription() != null && t._2.getDescription() != null) {
                        return t._2.getDescription().equals(relationship.getDescription());
                    } else {
                        return false;
                    }
                })
                .collect(toList());

        if (possibleRelationships.isEmpty()) {
            throw new IllegalStateException("C4Relationship could not be found for relationship" + relationship);
        } else {
            return possibleRelationships.get(0)._2.getId();
        }
    }

    @Override
    public void found(String id) {
    }

}
