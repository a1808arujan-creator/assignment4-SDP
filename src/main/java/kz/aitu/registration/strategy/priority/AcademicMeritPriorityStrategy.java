package kz.aitu.registration.strategy.priority;

public class AcademicMeritPriorityStrategy
        implements RegistrationPriorityStrategy {

    @Override
    public int calculatePriority(
            int studyYear,
            double gpa,
            int waitingMinutes) {

        return (int) (gpa * 100);
    }
}