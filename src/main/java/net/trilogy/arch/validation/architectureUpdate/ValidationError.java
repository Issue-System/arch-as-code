package net.trilogy.arch.validation.architectureUpdate;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import net.trilogy.arch.domain.architectureUpdate.Decision;
import net.trilogy.arch.domain.architectureUpdate.EntityReference;
import net.trilogy.arch.domain.architectureUpdate.FunctionalRequirement;
import net.trilogy.arch.domain.architectureUpdate.Tdd;
import net.trilogy.arch.domain.architectureUpdate.Tdd.ComponentReference;

import static net.trilogy.arch.validation.architectureUpdate.ValidationErrorType.LINK_NOT_AVAILABLE;

@ToString
@Getter
@EqualsAndHashCode
public class ValidationError {
    private final ValidationErrorType validationErrorType;
    private final String description;

    private ValidationError(ValidationErrorType validationErrorType, String description) {
        this.validationErrorType = validationErrorType;
        this.description = description;
    }

    public static ValidationError forDecisionsMustHaveTdds(Decision.Id entityId) {
        return new ValidationError(
                ValidationErrorType.DECISION_MISSING_TDD,
                String.format("Decision \"%s\" must have at least one TDD reference.", entityId.toString())
        );
    }

    public static ValidationError forTddsMustBeValidReferences(EntityReference entityId, Tdd.Id tddId) {
        return new ValidationError(
                ValidationErrorType.INVALID_TDD_REFERENCE_IN_DECISION_OR_REQUIREMENT,
                String.format("%s \"%s\" contains TDD reference \"%s\" that does not exist.", getEntityTypeString(entityId), entityId.toString(), tddId.toString())
        );
    }

    public static ValidationError forMustHaveStories(EntityReference entityId) {
        return new ValidationError(
                ValidationErrorType.MISSING_CAPABILITY,
                String.format("%s \"%s\" needs to be referenced in a story.", getEntityTypeString(entityId), entityId.toString())
        );
    }

    public static ValidationError forTddsMustHaveDecisionsOrRequirements(Tdd.Id tddId) {
        return new ValidationError(
                ValidationErrorType.TDD_WITHOUT_CAUSE,
                String.format("TDD \"%s\" needs to be referenced by a decision or functional requirement.", tddId.toString())
        );
    }

    public static ValidationError forStoriesTddsMustBeValidReferences(Tdd.Id id, String storyTitle) {
        return new ValidationError(
                ValidationErrorType.INVALID_TDD_REFERENCE_IN_STORY,
                String.format("Story \"%s\" contains TDD reference \"%s\" that does not exist.", storyTitle, id.toString())
        );
    }

    public static ValidationError forTddsComponentsMustBeValidReferences(Tdd.ComponentReference componentReference) {
        return new ValidationError(
                ValidationErrorType.INVALID_COMPONENT_REFERENCE,
                String.format("Component id \"%s\" does not exist.", componentReference)
        );
    }

    public static ValidationError forDeletedTddsComponentsMustBeValidReferences(ComponentReference componentReference) {
        return new ValidationError(
                ValidationErrorType.INVALID_DELETED_COMPONENT_REFERENCE,
                String.format("Deleted component id \"%s\" is invalid.", componentReference.toString())
        );
    }

    public static ValidationError forFunctionalRequirementsMustBeValidReferences(String storyTitle, FunctionalRequirement.Id id) {
        return new ValidationError(
                ValidationErrorType.INVALID_FUNCTIONAL_REQUIREMENT_REFERENCE_IN_STORY,
                String.format("Story \"%s\" contains functional requirement reference \"%s\" that does not exist.", storyTitle, id.toString())
        );
    }

    public static ValidationError forStoriesMustHaveTdds(String storyTitle) {
        return new ValidationError(
                ValidationErrorType.STORY_MISSING_TDD,
                String.format("Story \"%s\" must have at least one TDD reference.", storyTitle)
        );
    }

    public static ValidationError forStoriesMustHaveFunctionalRequirements(String storyTitle) {
        return new ValidationError(
                ValidationErrorType.MISSING_FUNCTIONAL_REQUIREMENTS,
                String.format("Story \"%s\" must have at least one functional requirement reference.", storyTitle)
        );
    }

    public static ValidationError forDuplicatedTdd(Tdd.Id id) {
        return new ValidationError(
                ValidationErrorType.DUPLICATE_TDD_ID,
                String.format("TDD \"%s\" is duplicated.", id.toString())
        );
    }

    public static ValidationError forDuplicatedComponent(ComponentReference componentReference) {
        return new ValidationError(
                ValidationErrorType.DUPLICATE_COMPONENT_ID,
                String.format("Component id \"%s\" is duplicated.", componentReference.toString())
        );
    }

    public static ValidationError forNotAvailableLink(String linkPath) {
        return new ValidationError(LINK_NOT_AVAILABLE, String.format("Link %s must be a valid link and not N/A", linkPath));
    }

    private static String getEntityTypeString(EntityReference entityId) {
        if (entityId instanceof Tdd.Id) {
            return "TDD";
        } else if (entityId instanceof FunctionalRequirement.Id) {
            return "Functional Requirement";
        }
        return "Entity";
    }

}
