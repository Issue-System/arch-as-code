package net.trilogy.arch.validation.architectureUpdate;

import net.trilogy.arch.domain.architectureUpdate.ArchitectureUpdate;
import net.trilogy.arch.domain.architectureUpdate.Decision;

import java.util.Map;

public class ArchitectureUpdateValidator {
    public static boolean isValid(ArchitectureUpdate au) {
        for (Map.Entry<Decision.Id, Decision> entry : au.getDecisions().entrySet()) {
            if (entry.getValue().getTddReferences().isEmpty()) return false;
        }
        return true;
    }
}
