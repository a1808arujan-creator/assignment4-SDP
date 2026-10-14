package kz.aitu.registration;

import kz.aitu.registration.eligibility.*;
import kz.aitu.registration.facade.RegistrationAdminFacade;
import kz.aitu.registration.finalization.*;
import kz.aitu.registration.seat.*;
import kz.aitu.registration.workflow.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class RegistrationAdminFacadeTest {

    private RegistrationAdminFacade adminFacade;
    private WorkflowRepository workflowRepository;
    private EligibilityRepository eligibilityRepository;
    private SeatRepository seatRepository;
    private RegistrationRepository registrationRepository;

    @BeforeEach
    void setUp() throws Exception {

        Files.deleteIfExists(Path.of("data/workflows.json"));
        Files.deleteIfExists(Path.of("data/eligibility.json"));
        Files.deleteIfExists(Path.of("data/seats.json"));
        Files.deleteIfExists(Path.of("data/registrations.json"));

        adminFacade = new RegistrationAdminFacade();

        workflowRepository = new WorkflowRepository();
        eligibilityRepository = new EligibilityRepository();
        seatRepository = new SeatRepository();
        registrationRepository = new RegistrationRepository();
    }

    @Test
    void inspectExistingWorkflowShouldRunSuccessfully() {

        String workflowId = "admin-inspect";

        WorkflowRecord workflow =
                new WorkflowRecord(
                        workflowId,
                        "ST001",
                        "SDP2301"
                );

        workflow.pendingB();
        workflowRepository.save(workflow);

        EligibilityRecord eligibility =
                new EligibilityRecord(
                        workflowId,
                        "ST001",
                        "SDP2301"
                );

        eligibility.approve();
        eligibilityRepository.save(eligibility);

        assertDoesNotThrow(
                () -> adminFacade.inspectWorkflow(workflowId)
        );
    }

    @Test
    void inspectMissingWorkflowShouldNotThrow() {

        assertDoesNotThrow(
                () -> adminFacade.inspectWorkflow("missing-workflow")
        );
    }

    @Test
    void manualCompensationFromPendingBShouldCompensateA() {

        String workflowId = "admin-pending-b";

        WorkflowRecord workflow =
                new WorkflowRecord(
                        workflowId,
                        "ST002",
                        "SDP2301"
                );

        workflow.pendingB();
        workflowRepository.save(workflow);

        EligibilityRecord eligibility =
                new EligibilityRecord(
                        workflowId,
                        "ST002",
                        "SDP2301"
                );

        eligibility.approve();
        eligibilityRepository.save(eligibility);

        adminFacade.manualCompensate(workflowId);

        WorkflowRecord savedWorkflow =
                workflowRepository.findByWorkflowId(workflowId);

        EligibilityRecord savedEligibility =
                eligibilityRepository.findByWorkflowId(workflowId);

        assertEquals(
                WorkflowStatus.COMPENSATED,
                savedWorkflow.getStatus()
        );

        assertEquals(
                EligibilityStatus.COMPENSATED,
                savedEligibility.getStatus()
        );
    }

    @Test
    void manualCompensationFromPendingCShouldCompensateBAndA() {

        String workflowId = "admin-pending-c";

        WorkflowRecord workflow =
                new WorkflowRecord(
                        workflowId,
                        "ST003",
                        "SDP2301"
                );

        workflow.pendingB();
        workflow.pendingC();
        workflowRepository.save(workflow);

        EligibilityRecord eligibility =
                new EligibilityRecord(
                        workflowId,
                        "ST003",
                        "SDP2301"
                );

        eligibility.approve();
        eligibilityRepository.save(eligibility);

        SeatReservation seat =
                new SeatReservation(
                        workflowId,
                        "ST003",
                        "SDP2301"
                );

        seat.reserve();
        seatRepository.save(seat);

        adminFacade.manualCompensate(workflowId);

        WorkflowRecord savedWorkflow =
                workflowRepository.findByWorkflowId(workflowId);

        EligibilityRecord savedEligibility =
                eligibilityRepository.findByWorkflowId(workflowId);

        SeatReservation savedSeat =
                seatRepository.findByWorkflowId(workflowId);

        assertEquals(
                WorkflowStatus.COMPENSATED,
                savedWorkflow.getStatus()
        );

        assertEquals(
                EligibilityStatus.COMPENSATED,
                savedEligibility.getStatus()
        );

        assertEquals(
                SeatStatus.RELEASED,
                savedSeat.getStatus()
        );
    }

    @Test
    void manualCompensationShouldBeRejectedForCompletedWorkflow() {

        String workflowId = "admin-completed";

        WorkflowRecord workflow =
                new WorkflowRecord(
                        workflowId,
                        "ST004",
                        "SDP2301"
                );

        workflow.pendingB();
        workflow.pendingC();
        workflow.complete();

        workflowRepository.save(workflow);

        adminFacade.manualCompensate(workflowId);

        WorkflowRecord savedWorkflow =
                workflowRepository.findByWorkflowId(workflowId);

        assertEquals(
                WorkflowStatus.COMPLETED,
                savedWorkflow.getStatus()
        );
    }

    @Test
    void manualCompensationForMissingWorkflowShouldNotThrow() {

        assertDoesNotThrow(
                () -> adminFacade.manualCompensate("does-not-exist")
        );
    }
}