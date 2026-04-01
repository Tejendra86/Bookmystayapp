import java.io.*;
import java.util.*;

// Booking Model (Serializable)
class Booking3 implements Serializable {
    String reservationId;
    String customerName;
    String roomType;

    public Booking3(String id, String name, String type) {
        this.reservationId = id;
        this.customerName = name;
        this.roomType = type;
    }

    public String toString() {
        return reservationId + " | " + customerName + " | " + roomType;
    }
}

// System State (Inventory + Bookings)
class SystemState implements Serializable {
    Map<String, Integer> inventory;
    List<Booking3> bookings;

    public SystemState(Map<String, Integer> inventory, List<Booking3> bookings) {
        this.inventory = inventory;
        this.bookings = bookings;
    }
}

// Persistence Service
class PersistenceService {

    private static final String FILE_NAME = "system_state.ser";

    // SAVE
    public void save(SystemState state) {
        try (ObjectOutputStream out =
                     new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {

            out.writeObject(state);
            System.out.println("\n✅ System state saved successfully.");

        } catch (IOException e) {
            System.out.println("❌ Error saving state: " + e.getMessage());
        }
    }

    // LOAD
    public SystemState load() {
        try (ObjectInputStream in =
                     new ObjectInputStream(new FileInputStream(FILE_NAME))) {

            SystemState state = (SystemState) in.readObject();
            System.out.println("✅ System state loaded successfully.");
            return state;

        } catch (FileNotFoundException e) {
            System.out.println("⚠ No previous state found. Starting fresh.");
        } catch (Exception e) {
            System.out.println("❌ Error loading state. Starting fresh.");
        }
        return null;
    }
}

// Main Class
public class UseCase12DataPersistenceRecovery {

    public static void main(String[] args) {

        PersistenceService persistence = new PersistenceService();

        // Try loading previous state
        SystemState state = persistence.load();

        Map<String, Integer> inventory;
        List<Booking3> bookings;

        if (state == null) {
            // Fresh start
            inventory = new HashMap<>();
            inventory.put("DELUXE", 2);
            inventory.put("STANDARD", 3);
            inventory.put("SUITE", 1);

            bookings = new ArrayList<>();

            System.out.println("\nStarting new system...");
        } else {
            // Restore state
            inventory = state.inventory;
            bookings = state.bookings;

            System.out.println("\nRecovered System State:");
            System.out.println("Inventory: " + inventory);
            System.out.println("Bookings: " + bookings);
        }

        // Simulate booking
        String id = "RES-" + UUID.randomUUID().toString().substring(0, 5);

        if (inventory.get("DELUXE") > 0) {
            inventory.put("DELUXE", inventory.get("DELUXE") - 1);

            Booking3 b = new Booking3(id, "Alice", "DELUXE");
            bookings.add(b);

            System.out.println("\nBooking Confirmed: " + b);
        } else {
            System.out.println("\nNo DELUXE rooms available");
        }

        // Save current state
        persistence.save(new SystemState(inventory, bookings));
    }
}