import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class Configuration {
    private final int totalTickets;
    private final int ticketReleaseRate;
    private final int customerRetrievalRate;
    private final int maxTicketCapacity;

    public Configuration(int totalTickets, int ticketReleaseRate, int customerRetrievalRate, int maxTicketCapacity) {
        // Validate input parameters
        validatePositiveInteger(totalTickets, "Total Tickets");
        validatePositiveInteger(ticketReleaseRate, "Ticket Release Rate");
        validatePositiveInteger(customerRetrievalRate, "Customer Retrieval Rate");
        validatePositiveInteger(maxTicketCapacity, "Maximum Ticket Capacity");

        // Additional validation
        if (maxTicketCapacity < totalTickets) {
            throw new IllegalArgumentException("Maximum Ticket Capacity must be greater than or equal to Total Tickets.");
        }

        this.totalTickets = totalTickets;
        this.ticketReleaseRate = ticketReleaseRate;
        this.customerRetrievalRate = customerRetrievalRate;
        this.maxTicketCapacity = maxTicketCapacity;
    }

    // Validate positive integer input
    private void validatePositiveInteger(int value, String paramName) {
        if (value <= 0) {
            throw new IllegalArgumentException(paramName + " must be a positive integer.");
        }
    }

    // Existing getters remain the same
    public int getTotalTickets() {
        return totalTickets;
    }

    public int getTicketReleaseRate() {
        return ticketReleaseRate;
    }

    public int getCustomerRetrievalRate() {
        return customerRetrievalRate;
    }

    public int getMaxTicketCapacity() {
        return maxTicketCapacity;
    }

    // Existing methods with additional error handling
    public static Configuration configureFromCLI() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Welcome to the Real-Time Event Ticketing System Configuration!");

        System.out.print("Do you want to load the previous configuration file/data? (yes/no): ");
        String loadConfig = validateStringInput(scanner, new String[]{"yes", "no"},
                "Invalid input. Please enter 'yes' or 'no'.");

        Configuration config = null;

        if (loadConfig.equals("yes")) {
            try (FileReader reader = new FileReader("config.json")) {
                Gson gson = new Gson();
                config = gson.fromJson(reader, Configuration.class);
                if (config != null) {
                    System.out.println("\n--- Loaded Configuration ---");
                    printConfig(config);
                } else {
                    System.out.println("No valid configuration found in file. Please enter new configuration.");
                }
            } catch (IOException | JsonSyntaxException e) {
                System.out.println("Error loading configuration. Please enter new configuration.");
            }
        }

        if (config == null) {
            System.out.println("\nPlease enter the following parameters:\n");

            int totalTickets = promptForPositiveInt(scanner, "1. Total Number of Tickets: ");
            int ticketReleaseRate = promptForPositiveInt(scanner, "2. Ticket Release Rate (milliseconds per ticket): ");
            int customerRetrievalRate = promptForPositiveInt(scanner, "3. Customer Retrieval Rate (milliseconds per ticket): ");
            int maxTicketCapacity = promptForPositiveInt(scanner, "4. Maximum Ticket Capacity in the system at any time: ");

            try {
                config = new Configuration(totalTickets, ticketReleaseRate, customerRetrievalRate, maxTicketCapacity);
            } catch (IllegalArgumentException e) {
                System.out.println("Configuration Error: " + e.getMessage());
                return configureFromCLI(); // Recursive call to restart configuration
            }

            System.out.println("\n--- Configuration Summary ---");
            printConfig(config);

            System.out.print("\nDo you want to save this configuration? (yes/no): ");
            if (validateStringInput(scanner, new String[]{"yes", "no"},
                    "Invalid input. Please enter 'yes' or 'no'.").equalsIgnoreCase("yes")) {
                saveConfig(config);
            }
        }

        System.out.println("\n--- System Ready ---");
        return config;
    }

    // Add a method to validate string input against allowed values
    private static String validateStringInput(Scanner scanner, String[] allowedValues, String errorMessage) {
        while (true) {
            String input = scanner.nextLine().trim().toLowerCase();
            for (String allowed : allowedValues) {
                if (input.equals(allowed)) {
                    return input;
                }
            }
            System.out.println(errorMessage);
        }
    }

    // Existing methods remain the same
    private static void printConfig(Configuration config) {
        System.out.println("Total Tickets: " + config.getTotalTickets());
        System.out.println("Ticket Release Rate: " + config.getTicketReleaseRate() + " milliseconds/ticket");
        System.out.println("Customer Retrieval Rate: " + config.getCustomerRetrievalRate() + " milliseconds/ticket");
        System.out.println("Maximum Ticket Capacity: " + config.getMaxTicketCapacity());
    }

    private static int promptForPositiveInt(Scanner scanner, String message) {
        while (true) {
            System.out.print(message);
            try {
                int input = Integer.parseInt(scanner.nextLine());
                if (input > 0) return input;
            } catch (NumberFormatException ignored) {}
            System.out.println("Invalid input. Please enter a positive number.");
        }
    }

    private static void saveConfig(Configuration config) {
        try (FileWriter writer = new FileWriter("config.json")) {
            Gson gson = new Gson();
            gson.toJson(config, writer);
            System.out.println("\nConfiguration saved successfully to \"config.json\"!");
        } catch (IOException e) {
            System.out.println("Error saving configuration: " + e.getMessage());
        }
    }
}