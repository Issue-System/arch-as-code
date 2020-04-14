package net.trilogy.arch.domain.architectureUpdate;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
@EqualsAndHashCode
public class CapabilitiesContainer {
    private Epic epic;

    @JsonProperty(value = "feature-stories")
    private final List<FeatureStory> featureStories;

    public CapabilitiesContainer(Epic epic, List<FeatureStory> featureStories) {
        this.epic = epic;
        this.featureStories = featureStories;
    }

    public static CapabilitiesContainer blank() {
        return new CapabilitiesContainer(Epic.blank(), List.of(FeatureStory.blank()));
    }
}
