package net.trilogy.arch.validation.architectureUpdate;

import net.trilogy.arch.domain.architectureUpdate.ArchitectureUpdate;
import net.trilogy.arch.domain.architectureUpdate.Decision;
import net.trilogy.arch.domain.architectureUpdate.Tdd;

import java.util.*;
import java.util.stream.Collectors;

public class ArchitectureUpdateValidator {

    public static Results validate(ArchitectureUpdate au) {
        final Results results = new Results();

        for (Map.Entry<Decision.Id, Decision> entry : au.getDecisions().entrySet()) {
            final Decision decisionBeingChecked = entry.getValue();
            final Decision.Id decisionIdBeingChecked = entry.getKey();
            if (decisionBeingChecked.getTddReferences().isEmpty()) {
                results.add(ErrorType.decisions_must_have_at_least_one_tdd, decisionIdBeingChecked);
            } else {
                final Set<Tdd.Id> allTdds = getAllTddIds(au);
                decisionBeingChecked.getTddReferences().forEach(tdd_ref -> {
                    if (!allTdds.contains(tdd_ref)) {
                        results.add(ErrorType.invalid_tdd_reference, decisionIdBeingChecked);
                    }
                });
            }
        }

        return results;
    }

    private static Set<Tdd.Id> getAllTddIds(ArchitectureUpdate au) {
        return au.getTDDs().values().stream().flatMap(Collection::stream).map(Tdd::getId).collect(Collectors.toSet());
    }

    public static class Results {
        private final Map<ErrorType, Set<Decision.Id>> map;

        private Results() {
            map = new LinkedHashMap<>();
        }

        public boolean isValid() {
            return map.isEmpty();
        }

        public Set<Decision.Id> getIds() {
            return map.values()
                    .stream()
                    .flatMap(Collection::stream)
                    .collect(Collectors.toSet());
        }

        public Set<Decision.Id> getIds(ErrorType errorType) {
            return map.get(errorType);
        }

        public Set<ErrorType> getErrors() {
            return map.keySet();
        }

        private void add(ErrorType errorType, Decision.Id id) {
            map.putIfAbsent(errorType, new LinkedHashSet<>());
            map.get(errorType).add(id);
        }
    }

    public enum ErrorType {
        decisions_must_have_at_least_one_tdd,
        invalid_tdd_reference
    }
}
