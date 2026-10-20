package kz.aitu.registration.strategy.priority;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PriorityStrategyTest {

    @Test
    void firstComeStrategyShouldUseWaitingTime() {

        RegistrationPriorityStrategy strategy =
                new FirstComePriorityStrategy();

        int result =
                strategy.calculatePriority(
                        2,
                        3.2,
                        40
                );

        assertEquals(40, result);
    }

    @Test
    void seniorStrategyShouldUseStudyYearAndWaitingTime() {

        RegistrationPriorityStrategy strategy =
                new SeniorStudentPriorityStrategy();

        int result =
                strategy.calculatePriority(
                        4,
                        3.0,
                        20
                );

        assertEquals(420, result);
    }

    @Test
    void meritStrategyShouldUseGpa() {

        RegistrationPriorityStrategy strategy =
                new AcademicMeritPriorityStrategy();

        int result =
                strategy.calculatePriority(
                        2,
                        3.8,
                        10
                );

        assertEquals(380, result);
    }
}