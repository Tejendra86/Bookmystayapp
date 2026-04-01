import java.util.*;

// Booking Model
class Booking {
    String reservationId;
    String customerName;
    String roomType;

    public Booking(String reservationId, String customerName, String roomType) {
        this.reservationId = reservationId;
        this.customerName = customerName;
        this.roomType = roomType;
    }

    public String toString() {
        return reservationId + " | " + customerName + " | " + roomType;
    }
}

// Inventory Service
class InventoryService2 {
    private Map<String, Integer> inventory;

    public InventoryService2() {
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
}

// Booking History (List maintains order)
class BookingHistory {
    private List<Booking> history = new ArrayList<>();

    public void addBooking(Booking b) {
        history.add(b);
    }

    public List<Booking> getAllBookings() {
        return history;
    }
}

// Report Service
class BookingReportService {

    public void generateReport(List<Booking> bookings) {
        System.out.println("\n===== BOOKING REPORT =====");

        Map<String, Integer> countByType = new HashMap<>();

        for (Booking b : bookings) {
            countByType.put(b.roomType,
                    countByType.getOrDefault(b.roomType, 0) + 1);
        }

        for (String type : countByType.keySet()) {
            System.out.println(type + " Rooms Booked: " + countByType.get(type));
        }

        System.out.println("Total Bookings: " + bookings.size());
    }
}

// Booking Service
class BookingService2 {

    private Queue<Booking> queue = new LinkedList<>();
    private InventoryService2 inventory;
    private BookingHistory history;

    public BookingService2(InventoryService2 inventory, BookingHistory history) {
        this.inventory = inventory;
        this.history = history;
    }

    public void addRequest(String name, String type) {
        String id = "RES-" + UUID.randomUUID().toString().substring(0, 5);
        queue.offer(new Booking(id, name, type));
    }

    public void processBookings() {
        while (!queue.isEmpty()) {
            Booking b = queue.poll();
            System.out.println("\nProcessing: " + b.customerName);

            if (!inventory.isAvailable(b.roomType)) {
                System.out.println("No rooms available for " + b.roomType);
                continue;
            }

            inventory.reduce(b.roomType);
            history.addBooking(b); // ✅ store in history

            System.out.println("Confirmed: " + b);
        }
    }
}

// Main Class
public class UseCase8BookingHistoryReport {

    public static void main(String[] args) {

        InventoryService2 inventory = new InventoryService2();
        BookingHistory history = new BookingHistory();
        BookingService2 bookingService = new BookingService2(inventory, history);
        BookingReportService reportService = new BookingReportService();

        // Add booking requests
        bookingService.addRequest("Alice", "DELUXE");
        bookingService.addRequest("Bob", "STANDARD");
        bookingService.addRequest("Charlie", "DELUXE");
        bookingService.addRequest("David", "SUITE");

        // Process bookings
        bookingService.processBookings();

        // Show history
        System.out.println("\n===== BOOKING HISTORY =====");
        for (Booking b : history.getAllBookings()) {
            System.out.println(b);
        }

        // Generate report
        reportService.generateReport(history.getAllBookings());
    }
}