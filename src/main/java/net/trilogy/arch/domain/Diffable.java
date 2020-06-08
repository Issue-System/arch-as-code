package net.trilogy.arch.domain;

public interface Diffable<T> {
    public T shallowCopy();
    public String getId();
    public boolean equals(Object o);
}
