package net.trilogy.arch.validation.architectureUpdate;

import lombok.Getter;

public enum ValidationErrorType {
    INVALID_TDD_REFERENCE("Invalid TDD Reference", ValidationStage.TDD),
    MISSING_CAPABILITY("Missing Capability", ValidationStage.CAPABILITY),
    MISSING_TDD("Missing TDD", ValidationStage.TDD);

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
