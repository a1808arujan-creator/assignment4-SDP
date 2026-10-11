package kz.aitu.registration;

import kz.aitu.registration.exception.FinalizationException;
import kz.aitu.registration.exception.InvalidRegistrationException;
import kz.aitu.registration.exception.SeatAllocationException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RegistrationWorkflowFacadeTest
        extends RegistrationTestDSL {

    @BeforeEach
    void setUp() {
        givenCleanStorage();
    }

    /*
     * BUILD:
     * clean storage + valid input
     *
     * OPERATE:
     * register student
     *
     * CHECK:
     * A, B, C and workflow are persisted correctly
     */
    @Test
    void successfulRegistrationCompletesEntireWorkflow() {

        String workflowId = "test-success";

        registerSuccessfully(workflowId);

        assertEligibilityApproved(workflowId);
        assertSeatReserved(workflowId);
        assertRegistrationRecorded(workflowId);
        assertWorkflowCompleted(workflowId);
    }

    /*
     * Required compensation depth #1:
     *
     * A succeeds
     * B fails
     * A must be compensated
     */
    @Test
    void whenBFailsFacadeCompensatesAOnly() {

        String workflowId = "test-b-failure";

        assertThrows(
                SeatAllocationException.class,
                () -> registerWithNoAvailableSeats(
                        workflowId
                )
        );

        assertEligibilityCompensated(workflowId);

        /*
         * B did not commit,
         * therefore there should be no persisted seat.
         */
        assertNoSeatWasPersisted(workflowId);

        assertWorkflowCompensated(workflowId);
    }

    /*
     * Required compensation depth #2:
     *
     * A succeeds
     * B succeeds
     * C fails
     *
     * compensation order:
     * B then A
     */
    @Test
    void whenCFailsFacadeCompensatesBThenA() {

        String workflowId = "test-c-failure";

        assertThrows(
                FinalizationException.class,
                () -> registerWithBillingFailure(
                        workflowId
                )
        );

        assertSeatReleased(workflowId);
        assertEligibilityCompensated(workflowId);
        assertWorkflowCompensated(workflowId);
    }

    /*
     * Extra realistic failure required
     * by the assignment.
     *
     * Invalid input fails before A commits.
     */
    @Test
    void invalidStudentIdFailsBeforeAnythingCommits() {

        String workflowId = "invalid-input";

        assertThrows(
                InvalidRegistrationException.class,
                () -> facade.registerStudent(
                        workflowId,

                        // invalid student ID
                        "",

                        "SDP2301",
                        true,
                        false,
                        5,
                        true,
                        true
                )
        );

        assertNull(
                workflowRepository
                        .findByWorkflowId(workflowId)
        );

        assertNull(
                eligibilityRepository
                        .findByWorkflowId(workflowId)
        );

        assertNull(
                seatRepository
                        .findByWorkflowId(workflowId)
        );
    }

    @Test
    void missingPrerequisiteProducesFailedWorkflow() {

        String workflowId =
                "missing-prerequisite";

        assertThrows(
                InvalidRegistrationException.class,
                () -> facade.registerStudent(
                        workflowId,
                        "student-004",
                        "SDP2301",

                        // prerequisite missing
                        false,

                        false,
                        5,
                        true,
                        true
                )
        );

        assertEquals(
                kz.aitu.registration.workflow.WorkflowStatus.FAILED,
                workflowRepository
                        .findByWorkflowId(workflowId)
                        .getStatus()
        );
    }

    @Test
    void financialHoldProducesFailedWorkflow() {

        String workflowId =
                "financial-hold";

        assertThrows(
                InvalidRegistrationException.class,
                () -> facade.registerStudent(
                        workflowId,
                        "student-005",
                        "SDP2301",
                        true,

                        // financial hold exists
                        true,

                        5,
                        true,
                        true
                )
        );

        assertEquals(
                kz.aitu.registration.workflow.WorkflowStatus.FAILED,
                workflowRepository
                        .findByWorkflowId(workflowId)
                        .getStatus()
        );
    }

    @Test
    void transcriptFailureAlsoCompensatesBAndA() {

        String workflowId =
                "transcript-failure";

        assertThrows(
                FinalizationException.class,
                () -> facade.registerStudent(
                        workflowId,
                        "student-006",
                        "SDP2301",
                        true,
                        false,
                        5,
                        true,

                        // transcript fails
                        false
                )
        );

        assertSeatReleased(workflowId);
        assertEligibilityCompensated(workflowId);
        assertWorkflowCompensated(workflowId);
    }
}