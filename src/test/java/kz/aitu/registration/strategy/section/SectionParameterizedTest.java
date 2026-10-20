package kz.aitu.registration.strategy.section;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class SectionParameterizedTest {

    static Stream<SectionSelectionStrategy> strategies() {

        return Stream.of(
                new FirstAvailableSectionStrategy(),
                new MostAvailableSeatsStrategy(),
                new DemandAwareSectionStrategy(
                        new FakeDemandDataSource(80)
                )
        );
    }

    @ParameterizedTest
    @MethodSource("strategies")
    void everyStrategyShouldReturnAResult(
            SectionSelectionStrategy strategy) {

        String result =
                strategy.selectSection(
                        15,
                        50,
                        "MORNING"
                );

        assertNotNull(result);
    }
}