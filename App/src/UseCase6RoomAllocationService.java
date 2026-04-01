import java.util.*;

// Booking Request Model
class BookingRequest {
    String customerName;
    String roomType;

    public BookingRequest(String customerName, String roomType) {
        this.customerName = customerName;
        this.roomType = roomType;
    }
}

// Inventory Service
class InventoryService {
    private Map<String, Integer> roomInventory;

    public InventoryService() {
        roomInventory = new HashMap<>();
        roomInventory.put("DELUXE", 2);
        roomInventory.put("STANDARD", 3);
        roomInventory.put("SUITE", 1);
    }

    public boolean isAvailable(String roomType) {
        return roomInventory.getOrDefault(roomType, 0) > 0;
    }

    public void decrementInventory(String roomType) {
        roomInventory.put(roomType, roomInventory.get(roomType) - 1);
    }

    public void displayInventory() {
        System.out.println("Current Inventory: " + roomInventory);
    }
}

// Booking Service
class BookingService {

    private Queue<BookingRequest> requestQueue;
    private Set<String> allocatedRoomIds;
    private Map<String, Set<String>> roomTypeToIds;
    private InventoryService inventoryService;

    public BookingService(InventoryService inventoryService) {
        this.requestQueue = new LinkedList<>();
        this.allocatedRoomIds = new HashSet<>();
        this.roomTypeToIds = new HashMap<>();
        this.inventoryService = inventoryService;
    }

    // Add booking request
    public void addRequest(BookingRequest request) {
        requestQueue.offer(request);
    }

    // Generate Unique Room ID
    private String generateRoomId(String roomType) {
        String roomId;
        do {
            roomId = roomType.substring(0, 3).toUpperCase() + "-" + UUID.randomUUID().toString().substring(0, 5);
        } while (allocatedRoomIds.contains(roomId));
        return roomId;
    }

    // Process Booking Requests
    public void processBookings() {
        while (!requestQueue.isEmpty()) {
            BookingRequest request = requestQueue.poll();
            System.out.println("\nProcessing request for: " + request.customerName);

            if (!inventoryService.isAvailable(request.roomType)) {
                System.out.println("No rooms available for type: " + request.roomType);
                continue;
            }

            // Generate Unique Room ID
            String roomId = generateRoomId(request.roomType);

            // Atomic Allocation
            allocatedRoomIds.add(roomId);
            roomTypeToIds
                    .computeIfAbsent(request.roomType, k -> new HashSet<>())
                    .add(roomId);

            // Update Inventory
            inventoryService.decrementInventory(request.roomType);

            // Confirmation
            System.out.println("Booking Confirmed!");
            System.out.println("Customer: " + request.customerName);
            System.out.println("Room Type: " + request.roomType);
            System.out.println("Allocated Room ID: " + roomId);
        }
    }

    public void displayAllocations() {
        System.out.println("\nRoom Allocations:");
        for (String type : roomTypeToIds.keySet()) {
            System.out.println(type + " -> " + roomTypeToIds.get(type));
        }
    }
}

// Main Class
public class UseCase6RoomAllocationService {

    public static void main(String[] args) {

        InventoryService inventoryService = new InventoryService();
        BookingService bookingService = new BookingService(inventoryService);

        // Add booking requests (FIFO)
        bookingService.addRequest(new BookingRequest("Alice", "DELUXE"));
        bookingService.addRequest(new BookingRequest("Bob", "DELUXE"));
        bookingService.addRequest(new BookingRequest("Charlie", "DELUXE")); // should fail
        bookingService.addRequest(new BookingRequest("David", "STANDARD"));
        bookingService.addRequest(new BookingRequest("Eva", "SUITE"));

        // Process Bookings
        bookingService.processBookings();

        // Display Results
        bookingService.displayAllocations();
        inventoryService.displayInventory();
    }
}
