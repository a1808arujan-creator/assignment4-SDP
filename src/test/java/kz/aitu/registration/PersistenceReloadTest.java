package kz.aitu.registration;

import kz.aitu.registration.eligibility.EligibilityRecord;
import kz.aitu.registration.eligibility.EligibilityRepository;
import kz.aitu.registration.facade.RegistrationWorkflowFacade;
import kz.aitu.registration.seat.SeatReservation;
import kz.aitu.registration.seat.SeatRepository;
import kz.aitu.registration.workflow.WorkflowRecord;
import kz.aitu.registration.workflow.WorkflowRepository;
import kz.aitu.registration.workflow.WorkflowStatus;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PersistenceReloadTest
        extends RegistrationTestDSL {

    @BeforeEach
    void setUp() {
        givenCleanStorage();
    }

    @Test
    void facadeResumesFromPersistedPendingCStateAfterRestart() {

        String workflowId =
                "restart-pending-c";

        /*
         * ==========================
         * BEFORE RESTART
         * ==========================
         *
         * Simulate A already committed.
         */

        EligibilityRecord eligibility =
                new EligibilityRecord(
                        workflowId,
                        "student-restart",
                        "SDP2301"
                );

        eligibility.approve();

        EligibilityRepository eligibilityRepositoryBeforeRestart =
                new EligibilityRepository();

        eligibilityRepositoryBeforeRestart.save(
                eligibility
        );

        /*
         * B already committed.
         */

        SeatReservation seat =
                new SeatReservation(
                        workflowId,
                        "student-restart",
                        "SDP2301"
                );

        seat.reserve();

        SeatRepository seatRepositoryBeforeRestart =
                new SeatRepository();

        seatRepositoryBeforeRestart.save(seat);

        /*
         * Persist chain progress:
         *
         * NEW
         * -> PENDING_B
         * -> PENDING_C
         */

        WorkflowRecord workflow =
                new WorkflowRecord(
                        workflowId,
                        "student-restart",
                        "SDP2301"
                );

        workflow.pendingB();
        workflow.pendingC();

        WorkflowRepository workflowRepositoryBeforeRestart =
                new WorkflowRepository();

        workflowRepositoryBeforeRestart.save(
                workflow
        );

        assertEquals(
                WorkflowStatus.PENDING_C,
                workflowRepositoryBeforeRestart
                        .findByWorkflowId(workflowId)
                        .getStatus()
        );

        /*
         * ==========================
         * SIMULATED RESTART
         * ==========================
         *
         * We throw away the old objects
         * and construct a completely new facade.
         *
         * The new facade must discover PENDING_C
         * from JSON on disk.
         */

        RegistrationWorkflowFacade facadeAfterRestart =
                new RegistrationWorkflowFacade();

        facadeAfterRestart.resumeWorkflow(
                workflowId,
                5,
                true,
                true
        );

        /*
         * ==========================
         * AFTER RESTART
         * ==========================
         */

        WorkflowRepository workflowRepositoryAfterRestart =
                new WorkflowRepository();

        WorkflowRecord reloaded =
                workflowRepositoryAfterRestart
                        .findByWorkflowId(workflowId);

        assertNotNull(reloaded);

        assertEquals(
                WorkflowStatus.COMPLETED,
                reloaded.getStatus()
        );

        assertRegistrationRecorded(
                workflowId
        );
    }

    @Test
    void persistedCompletedWorkflowCanBeReadByNewRepository() {

        String workflowId =
                "restart-completed";

        registerSuccessfully(workflowId);

        /*
         * New repository object simulates
         * reading the state after restart.
         */

        WorkflowRepository repositoryAfterRestart =
                new WorkflowRepository();

        WorkflowRecord loaded =
                repositoryAfterRestart
                        .findByWorkflowId(workflowId);

        assertNotNull(loaded);

        assertEquals(
                WorkflowStatus.COMPLETED,
                loaded.getStatus()
        );
    }
}