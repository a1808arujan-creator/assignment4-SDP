package kz.aitu.registration.strategy.priority;

public interface RegistrationPriorityStrat { 
    int calculatePriority(
        int studyYear,
        double gpa,
        int waitingMinutes
    )
}