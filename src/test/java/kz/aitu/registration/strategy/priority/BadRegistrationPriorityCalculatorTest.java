package kz.aitu.registration.strategy.priority;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BadRegistrationPriorityCalculatorTest {

    private final BadRegistrationPriorityCalculator calculator =
            new BadRegistrationPriorityCalculator();

    @Test
    void firstComePriorityShouldUseWaitingTime() {

        int result =
                calculator.calculatePriority(
                        "FIRST_COME",
                        2,
                        3.5,
                        40
                );

        assertEquals(40, result);
    }

    @Test
    void seniorPriorityShouldUseStudyYearAndWaitingTime() {

        int result =
                calculator.calculatePriority(
                        "SENIOR",
                        4,
                        3.0,
                        20
                );

        assertEquals(420, result);
    }

    @Test
    void meritPriorityShouldUseGpa() {

        int result =
                calculator.calculatePriority(
                        "MERIT",
                        1,
                        3.8,
                        10
                );

        assertEquals(380, result);
    }

    @Test
    void unknownTypeShouldThrowException() {

        assertThrows(
                IllegalArgumentException.class,
                () -> calculator.calculatePriority(
                        "UNKNOWN",
                        1,
                        3.0,
                        10
                )
        );
    }
}