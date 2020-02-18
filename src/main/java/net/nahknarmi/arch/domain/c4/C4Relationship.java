package net.nahknarmi.arch.domain.c4;


import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
public class C4Relationship {
    private String id;
    @NonNull
    private C4Action action;
    @NonNull
    private C4Path with;
    @NonNull
    private String description;
    private String technology;

    @Builder
    public C4Relationship(String id, @NonNull C4Action action, @NonNull C4Path with, @NonNull String description, String technology) {
        this.id = id;
        this.action = action;
        this.with = with;
        this.description = description;
        this.technology = technology;
    }
}
