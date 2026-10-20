package kz.aitu.registration.facade;

import kz.aitu.registration.eligibility.*;
import kz.aitu.registration.exception.*;
import kz.aitu.registration.finalization.*;
import kz.aitu.registration.seat.*;
import kz.aitu.registration.workflow.*;

public class RegistrationWorkflowFacade {

    private final EligibilityRepository eligibilityRepository;
    private final SeatRepository seatRepository;
    private final RegistrationRepository registrationRepository;
    private final WorkflowRepository workflowRepository;

    private final PrerequisiteChecker prerequisiteChecker;
    private final FinancialHoldChecker financialHoldChecker;
    private final SeatAllocator seatAllocator;
    private final WaitlistManager waitlistManager;
    private final BillingSystem billingSystem;
    private final TranscriptService transcriptService;

    public RegistrationWorkflowFacade() {
        eligibilityRepository = new EligibilityRepository();
        seatRepository = new SeatRepository();
        registrationRepository = new RegistrationRepository();
        workflowRepository = new WorkflowRepository();

        prerequisiteChecker = new PrerequisiteChecker();
        financialHoldChecker = new FinancialHoldChecker();
        seatAllocator = new SeatAllocator();
        waitlistManager = new WaitlistManager();
        billingSystem = new BillingSystem();
        transcriptService = new TranscriptService();
    }

    public void registerStudent(
            String workflowId,
            String studentId,
            String courseCode,
            boolean prerequisiteCompleted,
            boolean financialHold,
            int availableSeats,
            boolean billingAvailable,
            boolean transcriptAvailable) {

        validateInput(workflowId, studentId, courseCode);

        WorkflowRecord workflow =
                new WorkflowRecord(workflowId, studentId, courseCode);

        workflowRepository.save(workflow);

        System.out.println("[A] Checking eligibility...");

        if (!prerequisiteChecker.hasPrerequisites(prerequisiteCompleted)) {
            workflow.fail();
            workflowRepository.save(workflow);

            throw new InvalidRegistrationException(
                    "Required prerequisite is missing"
            );
        }

        if (financialHoldChecker.hasFinancialHold(financialHold)) {
            workflow.fail();
            workflowRepository.save(workflow);

            throw new InvalidRegistrationException(
                    "Student has a financial hold"
            );
        }

        EligibilityRecord eligibility =
                new EligibilityRecord(
                        workflowId,
                        studentId,
                        courseCode
                );

        eligibility.approve();
        eligibilityRepository.save(eligibility);

        workflow.pendingB();
        workflowRepository.save(workflow);

        System.out.println(
                "[A] Eligibility APPROVED and persisted"
        );

        processBAndC(
                workflow,
                eligibility,
                availableSeats,
                billingAvailable,
                transcriptAvailable
        );
    }

    public void resumeWorkflow(
            String workflowId,
            int availableSeats,
            boolean billingAvailable,
            boolean transcriptAvailable) {

        WorkflowRecord workflow =
                workflowRepository.findByWorkflowId(workflowId);

        if (workflow == null) {
            throw new InvalidRegistrationException(
                    "Workflow not found: " + workflowId
            );
        }

        System.out.println(
                "[Facade] Persisted workflow status: "
                        + workflow.getStatus()
        );

        if (workflow.getStatus() == WorkflowStatus.PENDING_B) {

            EligibilityRecord eligibility =
                    eligibilityRepository
                            .findByWorkflowId(workflowId);

            if (eligibility == null) {
                throw new IllegalStateException(
                        "Persisted eligibility record is missing"
                );
            }

            System.out.println(
                    "[Facade] Resuming from subsystem B"
            );

            processBAndC(
                    workflow,
                    eligibility,
                    availableSeats,
                    billingAvailable,
                    transcriptAvailable
            );

            return;
        }

        if (workflow.getStatus() == WorkflowStatus.PENDING_C) {

            EligibilityRecord eligibility =
                    eligibilityRepository
                            .findByWorkflowId(workflowId);

            SeatReservation seat =
                    seatRepository
                            .findByWorkflowId(workflowId);

            if (eligibility == null || seat == null) {
                throw new IllegalStateException(
                        "Persisted chain state is incomplete"
                );
            }

            System.out.println(
                    "[Facade] Resuming from subsystem C"
            );

            processC(
                    workflow,
                    eligibility,
                    seat,
                    billingAvailable,
                    transcriptAvailable
            );

            return;
        }

        if (workflow.getStatus() == WorkflowStatus.COMPLETED) {
            System.out.println(
                    "[Facade] Workflow is already COMPLETED"
            );
            return;
        }

        if (workflow.getStatus() == WorkflowStatus.COMPENSATED) {
            System.out.println(
                    "[Facade] Workflow is already COMPENSATED"
            );
            return;
        }

        throw new IllegalStateException(
                "Workflow cannot be resumed from status "
                        + workflow.getStatus()
        );
    }

    private void processBAndC(
            WorkflowRecord workflow,
            EligibilityRecord eligibility,
            int availableSeats,
            boolean billingAvailable,
            boolean transcriptAvailable) {

        System.out.println("[B] Allocating seat...");

        if (!seatAllocator.hasAvailableSeat(availableSeats)) {

            if (waitlistManager.shouldWaitlist(availableSeats)) {
                System.out.println(
                        "[B] No seats available"
                );
            }

            compensateA(workflow, eligibility);

            throw new SeatAllocationException(
                    "Seat allocation failed"
            );
        }

        SeatReservation seat =
                new SeatReservation(
                        workflow.getWorkflowId(),
                        workflow.getStudentId(),
                        workflow.getCourseCode()
                );

        seat.reserve();
        seatRepository.save(seat);

        workflow.pendingC();
        workflowRepository.save(workflow);

        System.out.println(
                "[B] Seat RESERVED and persisted"
        );

        processC(
                workflow,
                eligibility,
                seat,
                billingAvailable,
                transcriptAvailable
        );
    }

    private void processC(
            WorkflowRecord workflow,
            EligibilityRecord eligibility,
            SeatReservation seat,
            boolean billingAvailable,
            boolean transcriptAvailable) {

        try {

            System.out.println(
                    "[C] Finalizing registration..."
            );

            RegistrationRecord registration =
                    new RegistrationRecord(
                            workflow.getWorkflowId(),
                            workflow.getStudentId(),
                            workflow.getCourseCode()
                    );

            if (!billingSystem.canCreateBilling(billingAvailable)) {

                registration.markFailed();
                registrationRepository.save(registration);

                throw new FinalizationException(
                        "Billing system failed"
                );
            }

            registration.markBilled();

            if (!transcriptService
                    .canRecordRegistration(transcriptAvailable)) {

                registration.markFailed();
                registrationRepository.save(registration);

                throw new FinalizationException(
                        "Transcript service failed"
                );
            }

            registration.markRecorded();
            registrationRepository.save(registration);

            workflow.complete();
            workflowRepository.save(workflow);

            System.out.println(
                    "[C] Registration RECORDED"
            );

            System.out.println(
                    "[Facade] Workflow COMPLETED"
            );

        } catch (FinalizationException e) {

            compensateBAndA(
                    workflow,
                    seat,
                    eligibility
            );

            throw e;
        }
    }

    private void compensateA(
            WorkflowRecord workflow,
            EligibilityRecord eligibility) {

        System.out.println(
                "[Facade] B failed. Compensating A..."
        );

        workflow.startCompensation();
        workflowRepository.save(workflow);

        if (eligibility.getStatus()
                == EligibilityStatus.APPROVED) {

            eligibility.compensate();
            eligibilityRepository.save(eligibility);
        }

        workflow.compensated();
        workflowRepository.save(workflow);

        System.out.println(
                "[A] Eligibility COMPENSATED"
        );
    }

    private void compensateBAndA(
            WorkflowRecord workflow,
            SeatReservation seat,
            EligibilityRecord eligibility) {

        System.out.println(
                "[Facade] C failed."
        );

        workflow.startCompensation();
        workflowRepository.save(workflow);

        System.out.println(
                "[Facade] Compensating B..."
        );

        if (seat.getStatus() == SeatStatus.RESERVED) {
            seat.release();
            seatRepository.save(seat);
        }

        System.out.println(
                "[B] Seat RELEASED"
        );

        System.out.println(
                "[Facade] Compensating A..."
        );

        if (eligibility.getStatus()
                == EligibilityStatus.APPROVED) {

            eligibility.compensate();
            eligibilityRepository.save(eligibility);
        }

        System.out.println(
                "[A] Eligibility COMPENSATED"
        );

        workflow.compensated();
        workflowRepository.save(workflow);

        System.out.println(
                "[Facade] Workflow COMPENSATED"
        );
    }

    private void validateInput(
            String workflowId,
            String studentId,
            String courseCode) {

        if (workflowId == null || workflowId.isBlank()) {
            throw new InvalidRegistrationException(
                    "Workflow ID cannot be empty"
            );
        }

        if (studentId == null || studentId.isBlank()) {
            throw new InvalidRegistrationException(
                    "Student ID cannot be empty"
            );
        }

        if (courseCode == null || courseCode.isBlank()) {
            throw new InvalidRegistrationException(
                    "Course code cannot be empty"
            );
        }
        
    }
}