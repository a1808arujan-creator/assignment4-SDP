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


# Assignment 5 — Strategy Pattern

## Strategy Family 1 — Registration Priority

The Registration Priority family provides interchangeable algorithms
for calculating a student's registration priority.

### Strategies

- FirstComePriorityStrategy — calculates priority using waiting time.
- SeniorStudentPriorityStrategy — calculates priority using study year and waiting time.
- AcademicMeritPriorityStrategy — calculates priority using GPA.

### Context

PriorityContext stores the current RegistrationPriorityStrategy.
The strategy can be replaced at runtime using setStrategy().

### Selector

PriorityStrategySelector automatically selects a strategy based on
student information such as GPA and study year.


## Strategy Family 2 — Section Selection

The Section Selection family provides different algorithms
for selecting a course section.

### Strategies

- FirstAvailableSectionStrategy — selects an available section.
- MostAvailableSeatsStrategy — prefers sections with more available seats.
- DemandAwareSectionStrategy — considers current demand from an external data source.

### External Data Source

DemandDataSource hides the source of demand information.

SimulatedDemandDataSource provides simulated demand during the demo.

FakeDemandDataSource provides fixed demand values during unit tests.


## Strategy vs State vs Template Method

This solution uses Strategy because different algorithms can be selected
and replaced at runtime.

Strategy changes how an operation is performed by using interchangeable
objects that implement the same interface.

State is different because behavior changes according to an object's
internal state. The state object represents the current state of the
object rather than an independently selected algorithm.

Template Method is also different because it defines the overall
algorithm structure in a base class and allows subclasses to override
specific steps. My solution uses composition instead of inheritance,
so Strategy is more appropriate.


## Runtime Strategy Switching

Both contexts support setStrategy(), allowing the algorithm to change
while the application is running.

Example:

PriorityContext initially uses FirstComePriorityStrategy and can later
switch to AcademicMeritPriorityStrategy.


## Automatic Strategy Selection

PriorityStrategySelector and SectionStrategySelector automatically
choose an appropriate strategy based on the current situation.

The Context itself contains no if/switch for choosing strategy types.

## Class Responsibilities

| Class | Responsibility |
|---|---|
| RegistrationPriorityStrategy | Defines priority calculation contract |
| FirstComePriorityStrategy | Calculates priority from waiting time |
| SeniorStudentPriorityStrategy | Calculates priority from study year |
| AcademicMeritPriorityStrategy | Calculates priority from academic performance |
| PriorityContext | Executes the currently selected priority strategy |
| PriorityStrategySelector | Selects a priority strategy automatically |
| SectionSelectionStrategy | Defines section selection contract |
| FirstAvailableSectionStrategy | Selects an available section |
| MostAvailableSeatsStrategy | Selects a section using seat availability |
| DemandAwareSectionStrategy | Selects a section using current demand |
| SectionContext | Executes the currently selected section strategy |
| SectionStrategySelector | Selects a section strategy automatically |
| DemandDataSource | Defines access to demand information |
| SimulatedDemandDataSource | Supplies simulated demand information |


## Refactoring History

Before Strategy:
https://github.com/a1808arujan-creator/assignment4-SDP/commit/4bb021cabf8a252bbaec4ea4eebfafc3d280b2e0

After Strategy:
https://github.com/a1808arujan-creator/assignment4-SDP/commit/a5a67207803cc743098eb3186d96e1dc4bdc432f