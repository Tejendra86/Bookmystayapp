import java.util.*;

// Custom Exception
class InvalidBookingException extends Exception {
    public InvalidBookingException(String message) {
        super(message);
    }
}

// Booking Model (RENAMED)
class Booking1 {
    String customerName;
    String roomType;

    public Booking1(String customerName, String roomType) {
        this.customerName = customerName;
        this.roomType = roomType;
    }
}

// Inventory Service
class InventoryService3 {
    private Map<String, Integer> inventory;

    public InventoryService3() {
        inventory = new HashMap<>();
        inventory.put("DELUXE", 2);
        inventory.put("STANDARD", 3);
        inventory.put("SUITE", 1);
    }

    public void validateRoomType(String type) throws InvalidBookingException {
        if (!inventory.containsKey(type)) {
            throw new InvalidBookingException("Invalid room type: " + type);
        }
    }

    public void checkAvailability(String type) throws InvalidBookingException {
        if (inventory.get(type) <= 0) {
            throw new InvalidBookingException("No rooms available for: " + type);
        }
    }

    public void reduce(String type) throws InvalidBookingException {
        int count = inventory.get(type);
        if (count <= 0) {
            throw new InvalidBookingException("Cannot reduce inventory below zero for: " + type);
        }
        inventory.put(type, count - 1);
    }
}

// Booking Service
class BookingService3 {

    private InventoryService3 inventory;

    public BookingService3(InventoryService3 inventory) {
        this.inventory = inventory;
    }

    public void processBooking(Booking1 booking) {
        try {
            System.out.println("\nProcessing: " + booking.customerName);

            // Validation
            inventory.validateRoomType(booking.roomType);
            inventory.checkAvailability(booking.roomType);

            // Safe update
            inventory.reduce(booking.roomType);

            System.out.println("Booking Confirmed!");
            System.out.println("Customer: " + booking.customerName);
            System.out.println("Room Type: " + booking.roomType);

        } catch (InvalidBookingException e) {
            System.out.println("Booking Failed: " + e.getMessage());
        }
    }
}

// Main Class
public class UseCase9ErrorHandlingValidation {

    public static void main(String[] args) {

        InventoryService3 inventory = new InventoryService3();
        BookingService3 bookingService = new BookingService3(inventory);

        // Test cases
        bookingService.processBooking(new Booking1("Alice", "DELUXE"));
        bookingService.processBooking(new Booking1("Bob", "STANDARD"));
        bookingService.processBooking(new Booking1("Charlie", "LUXURY")); // invalid
        bookingService.processBooking(new Booking1("David", "SUITE"));
        bookingService.processBooking(new Booking1("Eva", "SUITE")); // no availability
    }
}