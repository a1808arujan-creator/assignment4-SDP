package kz.aitu.registration.eligibility;

public class EligibilityRecord {

    private String workflowId;
    private String studentId;
    private String courseCode;
    private EligibilityStatus status;

    public EligibilityRecord() {
    }

    public EligibilityRecord(String workflowId,
                             String studentId,
                             String courseCode) {
        this.workflowId = workflowId;
        this.studentId = studentId;
        this.courseCode = courseCode;
        this.status = EligibilityStatus.NEW;
    }

    public void approve() {
        if (status != EligibilityStatus.NEW) {
            throw new IllegalStateException(
                    "Eligibility can only be approved from NEW state"
            );
        }

        status = EligibilityStatus.APPROVED;
    }

    public void reject() {
        if (status != EligibilityStatus.NEW) {
            throw new IllegalStateException(
                    "Eligibility can only be rejected from NEW state"
            );
        }

        status = EligibilityStatus.REJECTED;
    }

    public void compensate() {
        if (status != EligibilityStatus.APPROVED) {
            throw new IllegalStateException(
                    "Only approved eligibility can be compensated"
            );
        }

        status = EligibilityStatus.COMPENSATED;
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

    public EligibilityStatus getStatus() {
        return status;
    }
}