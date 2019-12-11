package net.nahknarmi.arch.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RelationshipPair {
    @NonNull
    private String name;
    @NonNull
    private String with;
}
