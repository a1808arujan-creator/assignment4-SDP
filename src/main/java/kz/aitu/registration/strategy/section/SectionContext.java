package kz.aitu.registration.strategy.section;

public class SectionContext {

    private SectionSelectionStrategy strategy;

    public SectionContext(
            SectionSelectionStrategy strategy) {

        this.strategy = strategy;
    }

    public void setStrategy(
            SectionSelectionStrategy strategy) {

        this.strategy = strategy;
    }

    public String selectSection(
            int availableSeats,
            int currentDemand,
            String preferredTime) {

        return strategy.selectSection(
                availableSeats,
                currentDemand,
                preferredTime
        );
    }
}