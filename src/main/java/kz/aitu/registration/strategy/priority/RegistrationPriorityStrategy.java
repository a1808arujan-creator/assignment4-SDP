package kz.aitu.registration.strategy.priority;

public interface RegistrationPriorityStrategy {

    int calculatePriority(
            int studyYear,
            double gpa,
            int waitingMinutes
    );
}