package kz.aitu.registration.strategy.priority;

public class PriorityContext {

    private RegistrationPriorityStrategy strategy;

    public PriorityContext(
            RegistrationPriorityStrategy strategy) {

        this.strategy = strategy;
    }

    public void setStrategy(
            RegistrationPriorityStrategy strategy) {

        this.strategy = strategy;
    }

    public int calculatePriority(
            int studyYear,
            double gpa,
            int waitingMinutes) {

        return strategy.calculatePriority(
                studyYear,
                gpa,
                waitingMinutes
        );
    }
}