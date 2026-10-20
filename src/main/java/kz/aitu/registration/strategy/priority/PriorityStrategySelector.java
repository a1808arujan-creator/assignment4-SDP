package kz.aitu.registration.strategy.priority;

public class PriorityStrategySelector {

    public RegistrationPriorityStrategy selectStrategy(
            int studyYear,
            double gpa,
            int waitingMinutes) {

        if (gpa >= 3.7) {
            return new AcademicMeritPriorityStrategy();
        }

        if (studyYear >= 4) {
            return new SeniorStudentPriorityStrategy();
        }

        return new FirstComePriorityStrategy();
    }
}