import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;
import java.util.logging.ConsoleHandler;
import java.util.logging.SimpleFormatter;

public class Main {
    private static final Logger logger = Logger.getLogger(Main.class.getName());

    static {
        Logger rootLogger = Logger.getLogger("");
        java.util.logging.Handler[] handlers = rootLogger.getHandlers();
        for (java.util.logging.Handler handler : handlers) {
            rootLogger.removeHandler(handler);
        }
        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setFormatter(new SimpleFormatter());
        logger.addHandler(consoleHandler);
    }

    public static void main(String[] args) {
        // Load configuration from CLI input or file
        Configuration config = Configuration.configureFromCLI();

        // Check if max ticket capacity is sufficient
        if (config.getMaxTicketCapacity() < config.getTotalTickets()) {
            logger.severe("Error: Maximum Ticket Capacity must be greater than or equal to Total Tickets.");
            return;
        }

        // Create the TicketPool with the max ticket capacity
        TicketPool ticketPool = new TicketPool(config.getMaxTicketCapacity());

        CountDownLatch vendorStartLatch = new CountDownLatch(1);

        // Create Vendor first so it can be passed to Customer
        Vendor vendor = new Vendor(ticketPool, config.getTicketReleaseRate(), config.getTotalTickets(), vendorStartLatch);

        // Create and start Vendor and Customer threads
        Thread vendorThread = new Thread(vendor);
        Thread customerThread = new Thread(new Customer(ticketPool, config.getCustomerRetrievalRate(), vendorStartLatch, vendor));

        logger.info("Starting VendorThread...");
        vendorThread.start();

        customerThread.start();

        try {
            vendorThread.join();
            customerThread.join();
        } catch (InterruptedException e) {
            logger.severe("Monitor thread interrupted.");
        }
        logger.info("Ticket booking process completed.");
    }
}