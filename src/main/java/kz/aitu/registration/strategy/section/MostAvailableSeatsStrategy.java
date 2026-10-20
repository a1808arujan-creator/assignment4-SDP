package kz.aitu.registration.strategy.section;

public class MostAvailableSeatsStrategy
        implements SectionSelectionStrategy {

    @Override
    public String selectSection(
            int availableSeats,
            int currentDemand,
            String preferredTime) {

        if (availableSeats >= 10) {
            return "LARGE_SECTION";
        }

        if (availableSeats > 0) {
            return "SMALL_SECTION";
        }

        return "NO_SECTION_AVAILABLE";
    }
}