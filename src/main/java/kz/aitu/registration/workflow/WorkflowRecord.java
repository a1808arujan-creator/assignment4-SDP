package kz.aitu.registration.workflow;

public class WorkflowRecord {

    private String workflowId;
    private String studentId;
    private String courseCode;
    private WorkflowStatus status;

    public WorkflowRecord() {
    }

    public WorkflowRecord(String workflowId,
                          String studentId,
                          String courseCode) {
        this.workflowId = workflowId;
        this.studentId = studentId;
        this.courseCode = courseCode;
        this.status = WorkflowStatus.NEW;
    }

    public void pendingB() {
        if (status != WorkflowStatus.NEW) {
            throw new IllegalStateException("Expected NEW status");
        }

        status = WorkflowStatus.PENDING_B;
    }

    public void pendingC() {
        if (status != WorkflowStatus.PENDING_B) {
            throw new IllegalStateException("Expected PENDING_B status");
        }

        status = WorkflowStatus.PENDING_C;
    }

    public void complete() {
        if (status != WorkflowStatus.PENDING_C) {
            throw new IllegalStateException("Expected PENDING_C status");
        }

        status = WorkflowStatus.COMPLETED;
    }

    public void startCompensation() {
        if (status != WorkflowStatus.PENDING_B
                && status != WorkflowStatus.PENDING_C) {
            throw new IllegalStateException(
                    "Workflow cannot be compensated from " + status
            );
        }

        status = WorkflowStatus.COMPENSATING;
    }

    public void compensated() {
        if (status != WorkflowStatus.COMPENSATING) {
            throw new IllegalStateException(
                    "Expected COMPENSATING status"
            );
        }

        status = WorkflowStatus.COMPENSATED;
    }

    public void fail() {
        status = WorkflowStatus.FAILED;
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

    public WorkflowStatus getStatus() {
        return status;
    }
}