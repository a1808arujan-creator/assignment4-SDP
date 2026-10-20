package kz.aitu.registration.strategy.priority;

public class SeniorStudentPriorityStrategy 
        implements RegistrationPriorityStartegy {

    @Override
    public int calculatePriority(
        int studyYear,
        double gpa,
        int waitingMinutes) {

    return studyYear * 100 + waitingMinutes;
    }
}
