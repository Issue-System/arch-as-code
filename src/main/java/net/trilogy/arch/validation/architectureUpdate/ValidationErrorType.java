package net.trilogy.arch.validation.architectureUpdate;

import lombok.Getter;

public enum ValidationErrorType {
    INVALID_TDD_REFERENCE_IN_DECISION_OR_REQUIREMENT("Invalid TDD Reference in Decision or Requirement", ValidationStage.TDD),
    INVALID_FUNCTIONAL_REQUIREMENT_REFERENCE_IN_STORY("Invalid Functional Requirement Reference in Story", ValidationStage.STORY),
    MISSING_CAPABILITY("Missing Capability", ValidationStage.STORY),
    DECISION_MISSING_TDD("Decision Missing TDD", ValidationStage.TDD),
    STORY_MISSING_TDD("Story Missing TDD", ValidationStage.STORY),
    MISSING_FUNCTIONAL_REQUIREMENTS("Story Missing Functional Requirement", ValidationStage.STORY),
    TDD_WITHOUT_CAUSE("TDD without cause", ValidationStage.TDD),
    INVALID_TDD_REFERENCE_IN_STORY("Invalid TDD Reference in Story", ValidationStage.STORY),
    INVALID_COMPONENT_REFERENCE("Invalid Component Reference", ValidationStage.TDD),
    DUPLICATE_ID("Duplicate ID", ValidationStage.TDD);

    @Getter private final String label;
    @Getter private final ValidationStage stage;

    ValidationErrorType(String label, ValidationStage stage) {
        this.label = label;
        this.stage = stage;
    }

    @Override
    public String toString() {
        return label;
    }
}
