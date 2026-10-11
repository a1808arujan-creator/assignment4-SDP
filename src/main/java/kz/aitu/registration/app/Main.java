package kz.aitu.registration.app;

import kz.aitu.registration.facade.RegistrationAdminFacade;
import kz.aitu.registration.facade.RegistrationWorkflowFacade;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        RegistrationWorkflowFacade workflowFacade =
                new RegistrationWorkflowFacade();

        RegistrationAdminFacade adminFacade =
                new RegistrationAdminFacade();

        while (true) {

            System.out.println();
            System.out.println(
                    "=================================="
            );
            System.out.println(
                    " UNIVERSITY REGISTRATION SYSTEM"
            );
            System.out.println(
                    "=================================="
            );

            System.out.println("1. Successful registration");
            System.out.println("2. Trigger B failure");
            System.out.println("3. Trigger C failure");
            System.out.println("4. Inspect persisted workflow");
            System.out.println("5. Resume persisted workflow");
            System.out.println("6. Manual compensation");
            System.out.println("0. Exit");

            System.out.print("Choose: ");

            String choice = scanner.nextLine();

            try {

                switch (choice) {

                    case "1" -> successfulRegistration(
                            workflowFacade
                    );

                    case "2" -> bFailure(
                            workflowFacade
                    );

                    case "3" -> cFailure(
                            workflowFacade
                    );

                    case "4" -> {

                        System.out.print(
                                "Workflow ID: "
                        );

                        String workflowId =
                                scanner.nextLine();

                        adminFacade.inspectWorkflow(
                                workflowId
                        );
                    }

                    case "5" -> {

                        System.out.print(
                                "Workflow ID: "
                        );

                        String workflowId =
                                scanner.nextLine();

                        workflowFacade.resumeWorkflow(
                                workflowId,
                                5,
                                true,
                                true
                        );
                    }

                    case "6" -> {

                        System.out.print(
                                "Workflow ID: "
                        );

                        String workflowId =
                                scanner.nextLine();

                        adminFacade.manualCompensate(
                                workflowId
                        );
                    }

                    case "0" -> {

                        System.out.println("Goodbye!");
                        scanner.close();
                        return;
                    }

                    default ->
                            System.out.println(
                                    "Invalid option."
                            );
                }

            } catch (RuntimeException e) {

                System.out.println();
                System.out.println(
                        "ERROR: " + e.getMessage()
                );
            }
        }
    }

    private static void successfulRegistration(
            RegistrationWorkflowFacade facade) {

        String workflowId =
                "success-" + System.currentTimeMillis();

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

        System.out.println(
                "Workflow ID: " + workflowId
        );
    }

    private static void bFailure(
            RegistrationWorkflowFacade facade) {

        String workflowId =
                "b-failure-" + System.currentTimeMillis();

        System.out.println(
                "Workflow ID: " + workflowId
        );

        facade.registerStudent(
                workflowId,
                "student-002",
                "SDP2301",
                true,
                false,

                // No available seats
                0,

                true,
                true
        );
    }

    private static void cFailure(
            RegistrationWorkflowFacade facade) {

        String workflowId =
                "c-failure-" + System.currentTimeMillis();

        System.out.println(
                "Workflow ID: " + workflowId
        );

        facade.registerStudent(
                workflowId,
                "student-003",
                "SDP2301",
                true,
                false,
                5,

                // Billing failure
                false,

                true
        );
    }
}