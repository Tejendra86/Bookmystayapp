import java.util.*;

// Reservation (Represents booking intent)
class Reservation {
    private String guestName;
    private String roomType;
    private int nights;

    public Reservation(String guestName, String roomType, int nights) {
        this.guestName = guestName;
        this.roomType = roomType;
        this.nights = nights;
    }

    public String getGuestName() {
        return guestName;
    }

    public String getRoomType() {
        return roomType;
    }

    public int getNights() {
        return nights;
    }

    public void display() {
        System.out.println("Guest: " + guestName +
                " | Room Type: " + roomType +
                " | Nights: " + nights);
    }
}

// Booking Request Queue (FIFO)
class BookingRequestQueue {
    private Queue<Reservation> queue;

    public BookingRequestQueue() {
        queue = new LinkedList<>();
    }

    // Add request (enqueue)
    public void addRequest(Reservation reservation) {
        queue.offer(reservation);
        System.out.println("Booking request added for " + reservation.getGuestName());
    }

    // View all requests (read-only)
    public void displayQueue() {
        if (queue.isEmpty()) {
            System.out.println("\nNo booking requests in queue.");
            return;
        }

        System.out.println("\nBooking Requests in Queue (FIFO Order):\n");

        for (Reservation r : queue) {
            r.display();
        }
    }

    // Peek next request (without removing)
    public Reservation peekNext() {
        return queue.peek();
    }
}

// Main Class
public class UseCase5BookingRequestQueue {
    public static void main(String[] args) {

        // Step 1: Create Booking Queue
        BookingRequestQueue bookingQueue = new BookingRequestQueue();

        // Step 2: Simulate Guest Requests
        Reservation r1 = new Reservation("Tej", "Single", 2);
        Reservation r2 = new Reservation("Arun", "Double", 3);
        Reservation r3 = new Reservation("Priya", "Suite", 1);

        // Step 3: Add Requests to Queue (FIFO)
        bookingQueue.addRequest(r1);
        bookingQueue.addRequest(r2);
        bookingQueue.addRequest(r3);

        // Step 4: Display Queue
        bookingQueue.displayQueue();

        // Step 5: Show next request (without removing)
        Reservation next = bookingQueue.peekNext();
        if (next != null) {
            System.out.println("\nNext Request to be Processed:");
            next.display();
        }
    }
}