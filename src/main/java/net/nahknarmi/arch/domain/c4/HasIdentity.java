package net.nahknarmi.arch.domain.c4;

public interface HasIdentity<T> {
    String getId();

    String getAlias();

    T getReferenced(C4Model dataStructureModel);
}
