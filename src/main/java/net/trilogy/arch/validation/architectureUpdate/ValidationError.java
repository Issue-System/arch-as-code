package net.trilogy.arch.validation.architectureUpdate;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import net.trilogy.arch.domain.architectureUpdate.Decision;
import net.trilogy.arch.domain.architectureUpdate.EntityReference;
import net.trilogy.arch.domain.architectureUpdate.Tdd;

@ToString
@Getter
@EqualsAndHashCode
public class ValidationError {
    private final ValidationErrorType validationErrorType;
    private final EntityReference element;
    private final String description;

    public static ValidationError forMissingTddReference(Decision.Id entityId) {
        return new ValidationError(
                ValidationErrorType.MISSING_TDD,
                entityId,
                String.format("Decision \"%s\" must have at least one TDD reference.", entityId.getId())
        );
    }

    public static ValidationError forInvalidTddReferenceInDecisionOrRequirement(EntityReference entityId, Tdd.Id tddId) {
        return new ValidationError(
                ValidationErrorType.INVALID_TDD_REFERENCE_IN_DECISION_OR_REQUIREMENT,
                entityId,
                String.format("Entity \"%s\" contains TDD reference \"%s\" that does not exist.", entityId.getId(), tddId.getId())
        );
    }

    public static ValidationError forTddWithoutStory(Tdd.Id entityId) {
        return new ValidationError(
                ValidationErrorType.MISSING_CAPABILITY,
                entityId,
                String.format("TDD \"%s\" is not referred to by a story.", entityId.getId())
        );
    }

    public static ValidationError forTddWithoutCause(Tdd.Id tddId) {
        return new ValidationError(
                ValidationErrorType.TDD_WITHOUT_CAUSE,
                tddId,
                String.format("TDD \"%s\" is not referred to by a decision or functional requirement.", tddId.getId())
        );
    }

    public static ValidationError forInvalidTddReferenceInStory(Tdd.Id id, String storyTitle) {
        return new ValidationError(
                ValidationErrorType.INVALID_TDD_REFERENCE_IN_STORY,
                id,
                String.format("Story \"%s\" contains TDD reference \"%s\" that does not exist.", storyTitle, id.getId())
        );
    }

    public static ValidationError forInvalidComponentReference(Tdd.ComponentReference componentReference) {
        return new ValidationError(
                ValidationErrorType.INVALID_COMPONENT_REFERENCE,
                componentReference,
                String.format("Component id \"%s\" does not exist.", componentReference.getId())
        );
    }

    private ValidationError(ValidationErrorType validationErrorType, EntityReference element, String description) {
        this.validationErrorType = validationErrorType;
        this.element = element;
        this.description = description;
    }
}
