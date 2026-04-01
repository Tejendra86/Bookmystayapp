import java.util.*;

// Booking Request Model (renamed)
class BookingRequest3 {
    String customerName;
    String roomType;

    public BookingRequest3(String customerName, String roomType) {
        this.customerName = customerName;
        this.roomType = roomType;
    }
}

// Thread-Safe Inventory Service
class InventoryService5 {
    private Map<String, Integer> inventory;

    public InventoryService5() {
        inventory = new HashMap<>();
        inventory.put("DELUXE", 2);
        inventory.put("STANDARD", 2);
        inventory.put("SUITE", 1);
    }

    // synchronized = critical section
    public synchronized boolean bookRoom(String type) {
        if (inventory.getOrDefault(type, 0) > 0) {
            inventory.put(type, inventory.get(type) - 1);
            return true;
        }
        return false;
    }

    public synchronized void display() {
        System.out.println("Final Inventory: " + inventory);
    }
}

// Shared Booking Queue
class BookingQueue {
    private Queue<BookingRequest3> queue = new LinkedList<>();

    public synchronized void addRequest(BookingRequest3 req) {
        queue.offer(req);
    }

    public synchronized BookingRequest3 getRequest() {
        return queue.poll();
    }
}

// Booking Processor (Thread)
class BookingProcessor extends Thread {

    private BookingQueue queue;
    private InventoryService5 inventory;

    public BookingProcessor(BookingQueue queue, InventoryService5 inventory) {
        this.queue = queue;
        this.inventory = inventory;
    }

    @Override
    public void run() {
        while (true) {
            BookingRequest3 req;

            // synchronized access to queue
            synchronized (queue) {
                req = queue.getRequest();
            }

            if (req == null) break;

            System.out.println(Thread.currentThread().getName() +
                    " processing: " + req.customerName);

            boolean success = inventory.bookRoom(req.roomType);

            if (success) {
                System.out.println("✅ Booking Confirmed for " +
                        req.customerName + " (" + req.roomType + ")");
            } else {
                System.out.println("❌ Booking Failed for " +
                        req.customerName + " (" + req.roomType + ")");
            }
        }
    }
}

// Main Class
public class UseCase11ConcurrentBookingSimulation {

    public static void main(String[] args) {

        InventoryService5 inventory = new InventoryService5();
        BookingQueue queue = new BookingQueue();

        // Add multiple booking requests
        queue.addRequest(new BookingRequest3("Alice", "DELUXE"));
        queue.addRequest(new BookingRequest3("Bob", "DELUXE"));
        queue.addRequest(new BookingRequest3("Charlie", "DELUXE")); // should fail
        queue.addRequest(new BookingRequest3("David", "STANDARD"));
        queue.addRequest(new BookingRequest3("Eva", "STANDARD"));
        queue.addRequest(new BookingRequest3("Frank", "STANDARD")); // may fail
        queue.addRequest(new BookingRequest3("Grace", "SUITE"));
        queue.addRequest(new BookingRequest3("Helen", "SUITE")); // fail

        // Create multiple threads
        Thread t1 = new BookingProcessor(queue, inventory);
        Thread t2 = new BookingProcessor(queue, inventory);
        Thread t3 = new BookingProcessor(queue, inventory);

        t1.setName("Thread-1");
        t2.setName("Thread-2");
        t3.setName("Thread-3");

        // Start threads
        t1.start();
        t2.start();
        t3.start();

        // Wait for completion
        try {
            t1.join();
            t2.join();
            t3.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Final state
        inventory.display();
    }
}