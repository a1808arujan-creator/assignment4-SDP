package kz.aitu.registration.strategy.section;

public interface SectionSelectionStrategy {
    String selectSection(
        int availableSeats,
        int currentDemand,
        String preferredTime
    );
}
