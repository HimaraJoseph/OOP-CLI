import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;

public class Vendor implements Runnable {
    private static final Logger logger = Logger.getLogger(Vendor.class.getName());
    private final TicketPool ticketPool;
    private final int releaseRate;
    private final int totalTickets;
    private int ticketsRemaining;
    private final CountDownLatch vendorStartLatch;

    public Vendor(TicketPool ticketPool, int releaseRate, int totalTickets, CountDownLatch vendorStartLatch) {
        this.ticketPool = ticketPool;
        this.releaseRate = releaseRate;
        this.totalTickets = totalTickets;
        this.ticketsRemaining = totalTickets;
        this.vendorStartLatch = vendorStartLatch;
    }

    @Override
    public void run() {
        try {
            while (ticketsRemaining > 0) {
                ticketPool.addTicket();
                ticketsRemaining--;
                log("Added a ticket. Tickets in pool: " + ticketPool.getPoolSize() + ". Remaining tickets: " + ticketsRemaining);
                Thread.sleep(releaseRate);

                if (ticketsRemaining == totalTickets - 1) {
                    vendorStartLatch.countDown();
                }
            }
            Logger.getLogger(Main.class.getName()).info("Vendor has stopped releasing tickets.");
        } catch (InterruptedException e) {
            log("Vendor interrupted.");
        }
    }

    // Method to get remaining tickets
    public int getTicketsRemaining() {
        return ticketsRemaining;
    }

    private void log(String message) {
        System.out.println("Vendor: " + message + " [" +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "]");
    }
}