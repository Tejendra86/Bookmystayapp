import java.util.*;

// Booking Model (renamed to avoid conflicts)
class Booking2 {
    String reservationId;
    String customerName;
    String roomType;
    boolean isCancelled;

    public Booking2(String reservationId, String customerName, String roomType) {
        this.reservationId = reservationId;
        this.customerName = customerName;
        this.roomType = roomType;
        this.isCancelled = false;
    }

    public String toString() {
        return reservationId + " | " + customerName + " | " + roomType +
                (isCancelled ? " (CANCELLED)" : "");
    }
}

// Inventory Service
class InventoryService4 {
    private Map<String, Integer> inventory;

    public InventoryService4() {
        inventory = new HashMap<>();
        inventory.put("DELUXE", 2);
        inventory.put("STANDARD", 3);
        inventory.put("SUITE", 1);
    }

    public boolean isAvailable(String type) {
        return inventory.getOrDefault(type, 0) > 0;
    }

    public void reduce(String type) {
        inventory.put(type, inventory.get(type) - 1);
    }

    public void increase(String type) {
        inventory.put(type, inventory.get(type) + 1);
    }

    public void display() {
        System.out.println("\nInventory: " + inventory);
    }
}

// Booking Service
class BookingService4 {

    private Map<String, Booking2> bookings = new HashMap<>();
    private Stack<String> rollbackStack = new Stack<>();
    private InventoryService4 inventory;

    public BookingService4(InventoryService4 inventory) {
        this.inventory = inventory;
    }

    // Create booking
    public void book(String name, String type) {
        System.out.println("\nProcessing Booking for: " + name);

        if (!inventory.isAvailable(type)) {
            System.out.println("Booking Failed: No rooms available for " + type);
            return;
        }

        String id = "RES-" + UUID.randomUUID().toString().substring(0, 5);
        Booking2 b = new Booking2(id, name, type);

        bookings.put(id, b);
        rollbackStack.push(id); // track for rollback
        inventory.reduce(type);

        System.out.println("Booking Confirmed: " + b);
    }

    // Cancel booking
    public void cancel(String id) {
        System.out.println("\nCancelling Booking: " + id);

        if (!bookings.containsKey(id)) {
            System.out.println("Cancellation Failed: Invalid Reservation ID");
            return;
        }

        Booking2 b = bookings.get(id);

        if (b.isCancelled) {
            System.out.println("Cancellation Failed: Already Cancelled");
            return;
        }

        // LIFO rollback
        if (!rollbackStack.isEmpty() && rollbackStack.peek().equals(id)) {
            rollbackStack.pop();
        }

        // Restore inventory
        inventory.increase(b.roomType);
        b.isCancelled = true;

        System.out.println("Cancellation Successful: " + b);
    }

    // ✅ FIX: Getter method instead of direct access
    public List<String> getAllBookingIds() {
        return new ArrayList<>(bookings.keySet());
    }

    public void displayBookings() {
        System.out.println("\nBooking Records:");
        for (Booking2 b : bookings.values()) {
            System.out.println(b);
        }
    }
}

// Main Class
public class UseCase10BookingCancellation {

    public static void main(String[] args) {

        InventoryService4 inventory = new InventoryService4();
        BookingService4 service = new BookingService4(inventory);

        // Bookings
        service.book("Alice", "DELUXE");
        service.book("Bob", "STANDARD");
        service.book("Charlie", "SUITE");

        // ✅ FIXED: use getter
        List<String> ids = service.getAllBookingIds();

        // Cancel last booking (LIFO behavior)
        if (!ids.isEmpty()) {
            service.cancel(ids.get(ids.size() - 1));
        }

        // Invalid cancellation
        service.cancel("RES-XXXXX");

        // Display final state
        service.displayBookings();
        inventory.display();
    }
}