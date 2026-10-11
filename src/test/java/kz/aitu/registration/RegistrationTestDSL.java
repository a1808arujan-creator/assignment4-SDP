package kz.aitu.registration;

import kz.aitu.registration.eligibility.EligibilityRecord;
import kz.aitu.registration.eligibility.EligibilityRepository;
import kz.aitu.registration.eligibility.EligibilityStatus;
import kz.aitu.registration.facade.RegistrationWorkflowFacade;
import kz.aitu.registration.finalization.RegistrationRecord;
import kz.aitu.registration.finalization.RegistrationRepository;
import kz.aitu.registration.finalization.RegistrationStatus;
import kz.aitu.registration.seat.SeatRepository;
import kz.aitu.registration.seat.SeatReservation;
import kz.aitu.registration.seat.SeatStatus;
import kz.aitu.registration.workflow.WorkflowRecord;
import kz.aitu.registration.workflow.WorkflowRepository;
import kz.aitu.registration.workflow.WorkflowStatus;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class RegistrationTestDSL {

    protected RegistrationWorkflowFacade facade;

    protected EligibilityRepository eligibilityRepository;
    protected SeatRepository seatRepository;
    protected RegistrationRepository registrationRepository;
    protected WorkflowRepository workflowRepository;

    protected void givenCleanStorage() {
        deleteFile("data/eligibility.json");
        deleteFile("data/seats.json");
        deleteFile("data/registrations.json");
        deleteFile("data/workflows.json");

        facade = new RegistrationWorkflowFacade();

        eligibilityRepository = new EligibilityRepository();
        seatRepository = new SeatRepository();
        registrationRepository = new RegistrationRepository();
        workflowRepository = new WorkflowRepository();
    }

    protected void registerSuccessfully(String workflowId) {

        facade.registerStudent(
                workflowId,
                "student-001",
                "SDP2301",
                true,
                false,
                5,
                true,
                true
        );
    }

    protected void registerWithNoAvailableSeats(
            String workflowId) {

        facade.registerStudent(
                workflowId,
                "student-002",
                "SDP2301",
                true,
                false,

                // B failure
                0,

                true,
                true
        );
    }

    protected void registerWithBillingFailure(
            String workflowId) {

        facade.registerStudent(
                workflowId,
                "student-003",
                "SDP2301",
                true,
                false,
                5,

                // C failure
                false,

                true
        );
    }

    protected void assertWorkflowCompleted(
            String workflowId) {

        WorkflowRecord workflow =
                workflowRepository
                        .findByWorkflowId(workflowId);

        assertNotNull(workflow);

        assertEquals(
                WorkflowStatus.COMPLETED,
                workflow.getStatus()
        );
    }

    protected void assertWorkflowCompensated(
            String workflowId) {

        WorkflowRecord workflow =
                workflowRepository
                        .findByWorkflowId(workflowId);

        assertNotNull(workflow);

        assertEquals(
                WorkflowStatus.COMPENSATED,
                workflow.getStatus()
        );
    }

    protected void assertEligibilityApproved(
            String workflowId) {

        EligibilityRecord eligibility =
                eligibilityRepository
                        .findByWorkflowId(workflowId);

        assertNotNull(eligibility);

        assertEquals(
                EligibilityStatus.APPROVED,
                eligibility.getStatus()
        );
    }

    protected void assertEligibilityCompensated(
            String workflowId) {

        EligibilityRecord eligibility =
                eligibilityRepository
                        .findByWorkflowId(workflowId);

        assertNotNull(eligibility);

        assertEquals(
                EligibilityStatus.COMPENSATED,
                eligibility.getStatus()
        );
    }

    protected void assertSeatReserved(
            String workflowId) {

        SeatReservation seat =
                seatRepository
                        .findByWorkflowId(workflowId);

        assertNotNull(seat);

        assertEquals(
                SeatStatus.RESERVED,
                seat.getStatus()
        );
    }

    protected void assertSeatReleased(
            String workflowId) {

        SeatReservation seat =
                seatRepository
                        .findByWorkflowId(workflowId);

        assertNotNull(seat);

        assertEquals(
                SeatStatus.RELEASED,
                seat.getStatus()
        );
    }

    protected void assertNoSeatWasPersisted(
            String workflowId) {

        SeatReservation seat =
                seatRepository
                        .findByWorkflowId(workflowId);

        assertNull(seat);
    }

    protected void assertRegistrationRecorded(
            String workflowId) {

        RegistrationRecord registration =
                registrationRepository
                        .findByWorkflowId(workflowId);

        assertNotNull(registration);

        assertEquals(
                RegistrationStatus.RECORDED,
                registration.getStatus()
        );
    }

    private void deleteFile(String fileName) {

        try {
            Files.deleteIfExists(
                    Path.of(fileName)
            );

        } catch (IOException e) {
            throw new RuntimeException(
                    "Could not clean test storage",
                    e
            );
        }
    }
}