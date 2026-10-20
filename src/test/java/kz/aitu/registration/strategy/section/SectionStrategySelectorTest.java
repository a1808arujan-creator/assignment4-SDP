package kz.aitu.registration.strategy.section;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class SectionStrategySelectorTest {

    @Test
    void highDemandShouldSelectDemandAwareStrategy() {

        FakeDemandDataSource source =
                new FakeDemandDataSource(80);

        SectionStrategySelector selector =
                new SectionStrategySelector(source);

        SectionSelectionStrategy strategy =
                selector.selectStrategy(
                        5,
                        "MORNING"
                );

        assertInstanceOf(
                DemandAwareSectionStrategy.class,
                strategy
        );
    }

    @Test
    void manySeatsShouldSelectMostAvailableStrategy() {

        FakeDemandDataSource source =
                new FakeDemandDataSource(30);

        SectionStrategySelector selector =
                new SectionStrategySelector(source);

        SectionSelectionStrategy strategy =
                selector.selectStrategy(
                        15,
                        "MORNING"
                );

        assertInstanceOf(
                MostAvailableSeatsStrategy.class,
                strategy
        );
    }

    @Test
    void defaultShouldSelectFirstAvailableStrategy() {

        FakeDemandDataSource source =
                new FakeDemandDataSource(30);

        SectionStrategySelector selector =
                new SectionStrategySelector(source);

        SectionSelectionStrategy strategy =
                selector.selectStrategy(
                        5,
                        "MORNING"
                );

        assertInstanceOf(
                FirstAvailableSectionStrategy.class,
                strategy
        );
    }
}