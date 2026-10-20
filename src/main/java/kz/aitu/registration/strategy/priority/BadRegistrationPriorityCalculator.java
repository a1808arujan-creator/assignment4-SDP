package kz.aitu.registration.strategy.priority;

public class BadRegistrationPriorityCalculator {

    public int calculatePriority(
            String type,
            int studyYear,
            double gpa,
            int waitingMinutes) {

        if (type.equals("FIRST_COME")) {
            return waitingMinutes;
        }

        if (type.equals("SENIOR")) {
            return studyYear * 100 + waitingMinutes;
        }

        if (type.equals("MERIT")) {
            return (int) (gpa * 100);
        }

        throw new IllegalArgumentException(
                "Unknown priority type: " + type
        );
    }
}
