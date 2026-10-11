package kz.aitu.registration.facade;

import kz.aitu.registration.eligibility.*;
import kz.aitu.registration.finalization.*;
import kz.aitu.registration.seat.*;
import kz.aitu.registration.workflow.*;

public class RegistrationAdminFacade {

    private final WorkflowRepository workflowRepository;
    private final EligibilityRepository eligibilityRepository;
    private final SeatRepository seatRepository;
    private final RegistrationRepository registrationRepository;

    public RegistrationAdminFacade() {
        workflowRepository = new WorkflowRepository();
        eligibilityRepository = new EligibilityRepository();
        seatRepository = new SeatRepository();
        registrationRepository = new RegistrationRepository();
    }

    public void inspectWorkflow(String workflowId) {

        System.out.println();
        System.out.println("===== WORKFLOW INSPECTION =====");

        WorkflowRecord workflow =
                workflowRepository.findByWorkflowId(workflowId);

        if (workflow == null) {
            System.out.println("Workflow not found.");
            return;
        }

        System.out.println(
                "Workflow ID: " + workflow.getWorkflowId()
        );

        System.out.println(
                "Student: " + workflow.getStudentId()
        );

        System.out.println(
                "Course: " + workflow.getCourseCode()
        );

        System.out.println(
                "Workflow status: " + workflow.getStatus()
        );

        EligibilityRecord eligibility =
                eligibilityRepository
                        .findByWorkflowId(workflowId);

        if (eligibility != null) {
            System.out.println(
                    "A / Eligibility: "
                            + eligibility.getStatus()
            );
        }

        SeatReservation seat =
                seatRepository
                        .findByWorkflowId(workflowId);

        if (seat != null) {
            System.out.println(
                    "B / Seat: "
                            + seat.getStatus()
            );
        }

        RegistrationRecord registration =
                registrationRepository
                        .findByWorkflowId(workflowId);

        if (registration != null) {
            System.out.println(
                    "C / Registration: "
                            + registration.getStatus()
            );
        }

        System.out.println("===============================");
    }

    public void manualCompensate(String workflowId) {

        WorkflowRecord workflow =
                workflowRepository.findByWorkflowId(workflowId);

        if (workflow == null) {
            System.out.println("Workflow not found.");
            return;
        }

        if (workflow.getStatus() != WorkflowStatus.PENDING_B
                && workflow.getStatus() != WorkflowStatus.PENDING_C) {

            System.out.println(
                    "Manual compensation is not allowed from status: "
                            + workflow.getStatus()
            );

            return;
        }

        workflow.startCompensation();
        workflowRepository.save(workflow);

        /*
         * If B committed, undo B first.
         */
        if (workflow.getStatus() == WorkflowStatus.COMPENSATING) {

            SeatReservation seat =
                    seatRepository
                            .findByWorkflowId(workflowId);

            if (seat != null
                    && seat.getStatus() == SeatStatus.RESERVED) {

                seat.release();
                seatRepository.save(seat);

                System.out.println(
                        "[Admin] Seat RELEASED"
                );
            }
        }

        /*
         * Then undo A.
         */
        EligibilityRecord eligibility =
                eligibilityRepository
                        .findByWorkflowId(workflowId);

        if (eligibility != null
                && eligibility.getStatus()
                == EligibilityStatus.APPROVED) {

            eligibility.compensate();
            eligibilityRepository.save(eligibility);

            System.out.println(
                    "[Admin] Eligibility COMPENSATED"
            );
        }

        workflow.compensated();
        workflowRepository.save(workflow);

        System.out.println(
                "[Admin] Workflow COMPENSATED"
        );
    }
}