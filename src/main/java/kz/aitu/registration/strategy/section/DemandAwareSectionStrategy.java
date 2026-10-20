package kz.aitu.registration.strategy.section;

import kz.aitu.registration.strategy.section.data.DemandDataSource;

public class DemandAwareSectionStrategy
        implements SectionSelectionStrategy {

    private final DemandDataSource demandDataSource;

    public DemandAwareSectionStrategy(
            DemandDataSource demandDataSource) {

        this.demandDataSource = demandDataSource;
    }

    @Override
    public String selectSection(
            int availableSeats,
            int currentDemand,
            String preferredTime) {

        int demand =
                demandDataSource.getCurrentDemand();

        if (demand >= 70 && availableSeats >= 10) {
            return "HIGH_DEMAND_LARGE_SECTION";
        }

        if (availableSeats > 0) {
            return "REGULAR_SECTION";
        }

        return "NO_SECTION_AVAILABLE";
    }
}