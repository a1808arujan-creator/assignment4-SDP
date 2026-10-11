package kz.aitu.registration.seat;

public class SeatReservation {

    private String workflowId;
    private String studentId;
    private String courseCode;
    private SeatStatus status;

    public SeatReservation() {
    }

    public SeatReservation(String workflowId,
                           String studentId,
                           String courseCode) {
        this.workflowId = workflowId;
        this.studentId = studentId;
        this.courseCode = courseCode;
        this.status = SeatStatus.NEW;
    }

    public void reserve() {
        if (status != SeatStatus.NEW) {
            throw new IllegalStateException(
                    "Seat can only be reserved from NEW state"
            );
        }

        status = SeatStatus.RESERVED;
    }

    public void waitlist() {
        if (status != SeatStatus.NEW) {
            throw new IllegalStateException(
                    "Seat can only be waitlisted from NEW state"
            );
        }

        status = SeatStatus.WAITLISTED;
    }

    public void release() {
        if (status != SeatStatus.RESERVED) {
            throw new IllegalStateException(
                    "Only reserved seat can be released"
            );
        }

        status = SeatStatus.RELEASED;
    }

    public String getWorkflowId() {
        return workflowId;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public SeatStatus getStatus() {
        return status;
    }
}