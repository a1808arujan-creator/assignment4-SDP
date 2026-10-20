package kz.aitu.registration.strategy.priority;

public class FirstComePriorityStrategy
        implements RegistrationPriorityStrategy {

    @Override
    public int calculatePriority(
            int studyYear,
            double gpa,
            int waitingMinutes) {

        return waitingMinutes;
    }
}