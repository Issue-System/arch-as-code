package net.trilogy.arch.validation.architectureUpdate;

import net.trilogy.arch.domain.architectureUpdate.ArchitectureUpdate;
import net.trilogy.arch.domain.architectureUpdate.Decision;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;


public class ArchitectureUpdateValidatorTest {

    @Test
    public void allDecisionsShouldHaveTdd() {
        var au = makeAuWith(new Decision("SOME DECISION", List.of()));

        assertThat(ArchitectureUpdateValidator.isValid(au), is(false));
    }

    private ArchitectureUpdate makeAuWith(Decision decision) {
        return ArchitectureUpdate.builderPreFilledWithBlanks().decisions(Map.of(Decision.Id.blank(), decision)).build();
    }

}
