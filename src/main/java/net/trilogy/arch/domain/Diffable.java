package net.trilogy.arch.domain;

public interface Diffable extends Cloneable {
    public Diffable shallowCopy();
    public String getId();
    public boolean equals(Object o);
}
