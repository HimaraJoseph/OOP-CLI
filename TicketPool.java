import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;

public class TicketPool {
    private final BlockingQueue<Integer> ticketPool;
    private final Lock lock = new ReentrantLock();
    private final Condition ticketsAvailable = lock.newCondition();

    public TicketPool(int maxCapacity) {
        // Validate max capacity
        if (maxCapacity <= 0) {
            throw new IllegalArgumentException("Maximum ticket pool capacity must be a positive integer.");
        }
        this.ticketPool = new ArrayBlockingQueue<>(maxCapacity);
    }

    public void addTicket() throws InterruptedException, IllegalStateException {
        lock.lock();
        try {
            // Check if pool is full before adding
            if (ticketPool.remainingCapacity() == 0) {
                throw new IllegalStateException("Ticket pool has reached maximum capacity.");
            }
            ticketPool.put(1);
            ticketsAvailable.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public void purchaseTicket() throws InterruptedException, IllegalStateException {
        lock.lock();
        try {
            // Check if tickets are available
            if (ticketPool.isEmpty()) {
                throw new IllegalStateException("No tickets available in the pool.");
            }
            while (ticketPool.isEmpty()) {
                ticketsAvailable.await();
            }
            ticketPool.take();
        } finally {
            lock.unlock();
        }
    }

    public boolean purchaseTicketIfAvailable() {
        lock.lock();
        try {
            if (!ticketPool.isEmpty()) {
                ticketPool.take();
                return true;
            }
            return false;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        } finally {
            lock.unlock();
        }
    }

    public int getPoolSize() {
        return ticketPool.size();
    }
}