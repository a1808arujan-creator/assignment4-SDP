package kz.aitu.registration.seat;

public class WaitlistManager {

    public boolean shouldWaitlist(int availableSeats) {
        return availableSeats <= 0;
    }
}