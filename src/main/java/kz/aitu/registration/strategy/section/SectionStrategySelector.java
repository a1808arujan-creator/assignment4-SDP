package kz.aitu.registration.strategy.section;

import kz.aitu.registration.strategy.section.data.DemandDataSource;

public class SectionStrategySelector {

    private final DemandDataSource demandDataSource;

    public SectionStrategySelector(
            DemandDataSource demandDataSource) {

        this.demandDataSource = demandDataSource;
    }

    public SectionSelectionStrategy selectStrategy(
            int availableSeats,
            String preferredTime) {

        int demand =
                demandDataSource.getCurrentDemand();

        if (demand >= 70) {
            return new DemandAwareSectionStrategy(
                    demandDataSource
            );
        }

        if (availableSeats >= 10) {
            return new MostAvailableSeatsStrategy();
        }

        return new FirstAvailableSectionStrategy();
    }
}