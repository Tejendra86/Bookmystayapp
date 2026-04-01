import java.util.*;

// Booking Request (renamed)
class BookingRequest1 {
    String customerName;
    String roomType;
    String reservationId;

    public BookingRequest1(String customerName, String roomType) {
        this.customerName = customerName;
        this.roomType = roomType;
    }
}

// Inventory Service (renamed)
class InventoryService1 {
    private Map<String, Integer> roomInventory;

    public InventoryService1() {
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
}

// Booking Service (renamed)
class BookingService1 {

    private Queue<BookingRequest1> requestQueue = new LinkedList<>();
    private InventoryService1 inventoryService;

    public BookingService1(InventoryService1 inventoryService) {
        this.inventoryService = inventoryService;
    }

    public void addRequest(BookingRequest1 request) {
        requestQueue.offer(request);
    }

    public List<String> processBookings() {
        List<String> ids = new ArrayList<>();

        while (!requestQueue.isEmpty()) {
            BookingRequest1 request = requestQueue.poll();
            System.out.println("\nProcessing: " + request.customerName);

            if (!inventoryService.isAvailable(request.roomType)) {
                System.out.println("No rooms available for " + request.roomType);
                continue;
            }

            String id = "RES-" + UUID.randomUUID().toString().substring(0, 5);
            request.reservationId = id;

            ids.add(id);
            inventoryService.decrementInventory(request.roomType);

            System.out.println("Booking Confirmed!");
            System.out.println("Reservation ID: " + id);
            System.out.println("Customer: " + request.customerName);
            System.out.println("Room Type: " + request.roomType);
        }

        return ids;
    }
}

// Add-On Service
class AddOnService {
    String name;
    double cost;

    public AddOnService(String name, double cost) {
        this.name = name;
        this.cost = cost;
    }

    public String toString() {
        return name + " ($" + cost + ")";
    }
}

// Add-On Manager
class AddOnServiceManager {

    private Map<String, List<AddOnService>> map = new HashMap<>();

    public void addService(String id, AddOnService service) {
        map.computeIfAbsent(id, k -> new ArrayList<>()).add(service);
    }

    public double getTotal(String id) {
        double sum = 0;
        if (map.containsKey(id)) {
            for (AddOnService s : map.get(id)) {
                sum += s.cost;
            }
        }
        return sum;
    }

    public void display() {
        System.out.println("\nAdd-On Services:");
        for (String id : map.keySet()) {
            System.out.println(id + " -> " + map.get(id)
                    + " | Total Cost: $" + getTotal(id));
        }
    }
}

// Main Class
public class UseCase7AddOnServiceSelection {

    public static void main(String[] args) {

        InventoryService1 inv = new InventoryService1();
        BookingService1 booking = new BookingService1(inv);
        AddOnServiceManager manager = new AddOnServiceManager();

        booking.addRequest(new BookingRequest1("Alice", "DELUXE"));
        booking.addRequest(new BookingRequest1("Bob", "STANDARD"));
        booking.addRequest(new BookingRequest1("Charlie", "DELUXE"));

        List<String> ids = booking.processBookings();

        if (!ids.isEmpty()) {
            manager.addService(ids.get(0), new AddOnService("Breakfast", 20));
            manager.addService(ids.get(0), new AddOnService("WiFi", 10));
        }

        if (ids.size() > 1) {
            manager.addService(ids.get(1), new AddOnService("Airport Pickup", 30));
        }

        manager.display();
    }
}