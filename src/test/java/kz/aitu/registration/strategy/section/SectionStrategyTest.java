package kz.aitu.registration.strategy.section;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SectionStrategyTest {

    @Test
    void firstAvailableShouldReturnAvailableSection() {

        SectionSelectionStrategy strategy =
                new FirstAvailableSectionStrategy();

        String result =
                strategy.selectSection(
                        5,
                        20,
                        "MORNING"
                );

        assertEquals(
                "FIRST_AVAILABLE_SECTION",
                result
        );
    }

    @Test
    void mostAvailableSeatsShouldChooseLargeSection() {

        SectionSelectionStrategy strategy =
                new MostAvailableSeatsStrategy();

        String result =
                strategy.selectSection(
                        15,
                        20,
                        "MORNING"
                );

        assertEquals(
                "LARGE_SECTION",
                result
        );
    }

    @Test
    void demandAwareShouldUseExternalDemandSource() {

        FakeDemandDataSource source =
                new FakeDemandDataSource(80);

        SectionSelectionStrategy strategy =
                new DemandAwareSectionStrategy(source);

        String result =
                strategy.selectSection(
                        15,
                        0,
                        "MORNING"
                );

        assertEquals(
                "HIGH_DEMAND_LARGE_SECTION",
                result
        );
    }
}