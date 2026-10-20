package kz.aitu.registration.strategy.section;

public class FirstAvailableSectionStrategy
        implements SectionSelectionStrategy {

    @Override
    public String selectSection(
            int availableSeats,
            int currentDemand,
            String preferredTime) {

        if (availableSeats > 0) {
            return "FIRST_AVAILABLE_SECTION";
        }

        return "NO_SECTION_AVAILABLE";
    }
}