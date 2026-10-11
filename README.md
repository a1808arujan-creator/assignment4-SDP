# University Registration Facade

## Overview

This project implements the Facade Design Pattern for a University Registration system.

The system consists of three independent subsystems:

- Subsystem A — Eligibility
- Subsystem B — Seat Management
- Subsystem C — Registration Finalization

The main facade coordinates the complete registration workflow:

A → B → C

From the client's point of view, registration is one logical operation.

---

## Subsystem A — Eligibility

Subsystem A checks whether the student is allowed to register.

Main classes:

- EligibilityRecord
- EligibilityStatus
- PrerequisiteChecker
- FinancialHoldChecker
- EligibilityRepository

The subsystem checks:

1. Required prerequisites
2. Financial holds

Example statuses:

- NEW
- APPROVED
- REJECTED
- COMPENSATED

---

## Subsystem B — Seat Management

Subsystem B manages course seats.

Main classes:

- SeatReservation
- SeatStatus
- SeatAllocator
- WaitlistManager
- SeatRepository

Example statuses:

- NEW
- RESERVED
- WAITLISTED
- RELEASED

---

## Subsystem C — Registration Finalization

Subsystem C finalizes registration.

Main classes:

- RegistrationRecord
- RegistrationStatus
- BillingSystem
- TranscriptService
- RegistrationRepository

The subsystem creates the billing record and records the course registration.

---

## Facades

### RegistrationWorkflowFacade

This facade provides the common student registration workflow.

It coordinates:

1. Eligibility check
2. Seat allocation
3. Registration finalization

The client does not need to communicate with each subsystem directly.

### RegistrationAdminFacade

This facade provides administrative functionality such as:

- workflow inspection
- manual compensation
- persisted state inspection

---

## Successful Workflow

A normal registration follows this sequence:

A — Eligibility APPROVED

↓

B — Seat RESERVED

↓

C — Registration RECORDED

↓

Workflow COMPLETED

---

## Compensation Scenario 1 — B Failure

If Subsystem A succeeds but Subsystem B fails:

A succeeds

↓

B fails

↓

Facade compensates A

Example:

Eligibility APPROVED → COMPENSATED

This may happen when no course seat is available.

---

## Compensation Scenario 2 — C Failure

If A and B succeed but C fails:

A succeeds

↓

B succeeds

↓

C fails

↓

Compensate B

↓

Compensate A

Example:

Seat RESERVED → RELEASED

Eligibility APPROVED → COMPENSATED

This may happen when the billing system or transcript service fails.

---

## Durable Persistence

The application uses JSON files as durable storage.

Files:

- data/eligibility.json
- data/seats.json
- data/registrations.json
- data/workflows.json

The data survives an application restart.

Workflow progress is also persisted using statuses such as:

- NEW
- PENDING_B
- PENDING_C
- COMPLETED
- COMPENSATED

Therefore, after restarting the application, the facade can determine which step must continue.

---

## Principle of Least Knowledge

### Before Facade

Without a facade, the client would need to know many subsystem classes:

```java
PrerequisiteChecker prerequisiteChecker =
        new PrerequisiteChecker();

FinancialHoldChecker financialHoldChecker =
        new FinancialHoldChecker();

SeatAllocator seatAllocator =
        new SeatAllocator();

BillingSystem billingSystem =
        new BillingSystem();

TranscriptService transcriptService =
        new TranscriptService();

prerequisiteChecker.hasPrerequisites(true);
financialHoldChecker.hasFinancialHold(false);
seatAllocator.hasAvailableSeat(5);
billingSystem.canCreateBilling(true);
transcriptService.canRecordRegistration(true);