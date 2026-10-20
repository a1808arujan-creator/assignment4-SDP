package kz.aitu.registration;

import kz.aitu.registration.eligibility.EligibilityRecord;
import kz.aitu.registration.eligibility.EligibilityStatus;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EligibilityRecordTest {

    @Test
    void eligibilityCanBeRejectedFromNewState() {

        EligibilityRecord record =
                new EligibilityRecord(
                        "wf-reject",
                        "ST001",
                        "SDP2301"
                );

        record.reject();

        assertEquals(
                EligibilityStatus.REJECTED,
                record.getStatus()
        );
    }

    @Test
    void approvedEligibilityCannotBeApprovedAgain() {

        EligibilityRecord record =
                new EligibilityRecord(
                        "wf-double-approve",
                        "ST002",
                        "SDP2301"
                );

        record.approve();

        assertThrows(
                IllegalStateException.class,
                record::approve
        );
    }

    @Test
    void approvedEligibilityCannotBeRejected() {

        EligibilityRecord record =
                new EligibilityRecord(
                        "wf-invalid-reject",
                        "ST003",
                        "SDP2301"
                );

        record.approve();

        assertThrows(
                IllegalStateException.class,
                record::reject
        );
    }

    @Test
    void newEligibilityCannotBeCompensated() {

        EligibilityRecord record =
                new EligibilityRecord(
                        "wf-invalid-compensation",
                        "ST004",
                        "SDP2301"
                );

        assertThrows(
                IllegalStateException.class,
                record::compensate
        );
    }
    @Test
    void newEligibilityCannotBeApproved() {

        EligibilityRecord record =
                new EligibilityRecord(
                        "wf-invalid-approve",
                        "ST004",
                        "SDP2301"
                );

        assertThrows(
                IllegalStateException.class,
                record::approve
        );
    }
}

