package kz.aitu.registration.strategy.priority;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class PriorityStrategySelectorTest {

    private final PriorityStrategySelector selector =
            new PriorityStrategySelector();

    @Test
    void highGpaShouldSelectMeritStrategy() {

        RegistrationPriorityStrategy strategy =
                selector.selectStrategy(
                        2,
                        3.8,
                        20
                );

        assertInstanceOf(
                AcademicMeritPriorityStrategy.class,
                strategy
        );
    }

    @Test
    void seniorStudentShouldSelectSeniorStrategy() {

        RegistrationPriorityStrategy strategy =
                selector.selectStrategy(
                        4,
                        3.3,
                        20
                );

        assertInstanceOf(
                SeniorStudentPriorityStrategy.class,
                strategy
        );
    }

    @Test
    void defaultShouldSelectFirstComeStrategy() {

        RegistrationPriorityStrategy strategy =
                selector.selectStrategy(
                        2,
                        3.2,
                        20
                );

        assertInstanceOf(
                FirstComePriorityStrategy.class,
                strategy
        );
    }
}