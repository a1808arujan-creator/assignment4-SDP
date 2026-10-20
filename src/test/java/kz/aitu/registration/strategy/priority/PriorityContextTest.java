package kz.aitu.registration.strategy.priority;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PriorityContextTest {

    @Test
    void contextShouldUseCurrentStrategy() {

        PriorityContext context =
                new PriorityContext(
                        new FirstComePriorityStrategy()
                );

        int result =
                context.calculatePriority(
                        2,
                        3.2,
                        50
                );

        assertEquals(50, result);
    }

    @Test
    void contextShouldSwitchStrategyAtRuntime() {

        PriorityContext context =
                new PriorityContext(
                        new FirstComePriorityStrategy()
                );

        int firstResult =
                context.calculatePriority(
                        2,
                        3.8,
                        20
                );

        context.setStrategy(
                new AcademicMeritPriorityStrategy()
        );

        int secondResult =
                context.calculatePriority(
                        2,
                        3.8,
                        20
                );

        assertEquals(20, firstResult);
        assertEquals(380, secondResult);
    }
}