package kz.aitu.registration.strategy.section.data;

public class SimulatedDemandDataSource
        implements DemandDataSource {

    @Override
    public int getCurrentDemand() {
        return 75;
    }
}