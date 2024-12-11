import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;
import java.util.concurrent.atomic.AtomicBoolean;

public class Customer implements Runnable {
    private static final Logger logger = Logger.getLogger(Customer.class.getName());
    private final TicketPool ticketPool;
    private final int retrievalRate;
    private final CountDownLatch vendorStartLatch;
    private int purchasedTickets = 0;
    private final Vendor vendor;

    public Customer(TicketPool ticketPool, int retrievalRate, CountDownLatch vendorStartLatch, Vendor vendor) {
        this.ticketPool = ticketPool;
        this.retrievalRate = retrievalRate;
        this.vendorStartLatch = vendorStartLatch;
        this.vendor = vendor;
    }

    @Override
    public void run() {
        try {
            vendorStartLatch.await();
            long nextRetrievalTime = System.currentTimeMillis();
            boolean customerStarted = false;

            while (!Thread.currentThread().isInterrupted()) {
                long currentTime = System.currentTimeMillis();

                if (currentTime >= nextRetrievalTime) {
                    if (!customerStarted) {
                        Logger.getLogger(Main.class.getName()).info("Starting CustomerThread...");
                        customerStarted = true;
                    }

                    // Check if vendor has finished releasing all tickets and pool is empty
                    if (vendor.getTicketsRemaining() == 0 && ticketPool.getPoolSize() == 0) {
                        log("All tickets have been processed. Stopping customer thread.");
                        break;
                    }

                    // Try to purchase a ticket if available
                    if (ticketPool.getPoolSize() > 0) {
                        try {
                            ticketPool.purchaseTicket();
                            purchasedTickets++;
                            log("Purchased a ticket. Tickets in pool: " + ticketPool.getPoolSize() +
                                    ". Total purchased: " + purchasedTickets);
                        } catch (InterruptedException e) {
                            log("Ticket purchase interrupted.");
                            break;
                        }
                    } else {
                        // No tickets in pool, but vendor still has tickets
                        log("Waiting for tickets. Vendor still has " + vendor.getTicketsRemaining() + " tickets to release.");
                    }

                    nextRetrievalTime += retrievalRate;
                } else {
                    Thread.sleep(50);
                }
            }
        } catch (InterruptedException e) {
            log("Customer interrupted.");
        } finally {
            log("Customer thread completed. Total tickets purchased: " + purchasedTickets);
        }
    }

    private void log(String message) {
        System.out.println("Customer: " + message + " [" +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "]");
    }
}