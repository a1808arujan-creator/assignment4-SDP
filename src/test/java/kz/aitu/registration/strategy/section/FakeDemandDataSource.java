package kz.aitu.registration.strategy.section;

import kz.aitu.registration.strategy.section.data.DemandDataSource;

public class FakeDemandDataSource
        implements DemandDataSource {

    private final int demand;

    public FakeDemandDataSource(int demand) {
        this.demand = demand;
    }

    @Override
    public int getCurrentDemand() {
        return demand;
    }
}