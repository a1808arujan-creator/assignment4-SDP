package kz.aitu.registration.finalization;

public class RegistrationRecord {

    private String workflowId;
    private String studentId;
    private String courseCode;
    private RegistrationStatus status;

    public RegistrationRecord() {
    }

    public RegistrationRecord(String workflowId,
                              String studentId,
                              String courseCode) {
        this.workflowId = workflowId;
        this.studentId = studentId;
        this.courseCode = courseCode;
        this.status = RegistrationStatus.NEW;
    }

    public void markBilled() {
        if (status != RegistrationStatus.NEW) {
            throw new IllegalStateException(
                    "Registration can only be billed from NEW state"
            );
        }

        status = RegistrationStatus.BILLED;
    }

    public void markRecorded() {
        if (status != RegistrationStatus.BILLED) {
            throw new IllegalStateException(
                    "Registration can only be recorded after billing"
            );
        }

        status = RegistrationStatus.RECORDED;
    }

    public void markFailed() {
        if (status == RegistrationStatus.RECORDED) {
            throw new IllegalStateException(
                    "Completed registration cannot be marked as failed"
            );
        }

        status = RegistrationStatus.FAILED;
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

    public RegistrationStatus getStatus() {
        return status;
    }
}