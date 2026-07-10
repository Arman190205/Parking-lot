import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Main Parking Lot coordination class.
 * Follows the requirement: (X) Constructor, attributes (V) setters, 1 object.
 * So the configuration is set up dynamically through setters instead of a complex constructor.
 */
public class ParkingLot {
    private int numFloors;
    private final Map<String, Slot> slots = new HashMap<>();
    private final Map<String, Gate> entryGates = new HashMap<>();
    // gateId -> slotId -> distance
    private final Map<String, Map<String, Double>> distances = new HashMap<>();
    // Active tickets map: ticketId -> Ticket
    private final Map<String, Ticket> activeTickets = new HashMap<>();

    public ParkingLot() {
        // No-arg constructor as requested
    }

    // --- Configuration / Layout (Create) APIs ---

    public void setFloors(int numFloors) {
        this.numFloors = numFloors;
    }

    public int getNumFloors() {
        return numFloors;
    }

    public void addSlot(int floor, String slotId, SlotType type, double hourlyRate) {
        if (floor < 0 || floor >= numFloors) {
            throw new IllegalArgumentException("Invalid floor number. Must be between 0 and " + (numFloors - 1));
        }
        Slot slot = new Slot(slotId, floor, type, hourlyRate);
        slots.put(slotId, slot);
    }

    public Slot getSlot(String slotId) {
        return slots.get(slotId);
    }

    public void addEntryGate(int floor, String gateId) {
        if (floor < 0 || floor >= numFloors) {
            throw new IllegalArgumentException("Invalid floor number. Must be between 0 and " + (numFloors - 1));
        }
        Gate gate = new Gate(gateId, floor);
        entryGates.put(gateId, gate);
    }

    public Gate getEntryGate(String gateId) {
        return entryGates.get(gateId);
    }

    public void setDistance(String gateId, String slotId, double distance) {
        if (!entryGates.containsKey(gateId)) {
            throw new IllegalArgumentException("Gate " + gateId + " does not exist.");
        }
        if (!slots.containsKey(slotId)) {
            throw new IllegalArgumentException("Slot " + slotId + " does not exist.");
        }
        distances.computeIfAbsent(gateId, k -> new HashMap<>()).put(slotId, distance);
    }

    public Double getDistance(String gateId, String slotId) {
        Map<String, Double> gateDistances = distances.get(gateId);
        if (gateDistances != null) {
            return gateDistances.get(slotId);
        }
        return null;
    }

    // --- Transaction (Park & Exit) APIs ---

    /**
     * Allocates a slot for a vehicle based on priorities and distance.
     * 
     * Prioritization rules:
     * - Small vehicle (SMALL): Look for SMALL slots first, then MEDIUM, then LARGE.
     * - Medium vehicle (MEDIUM): Look for MEDIUM slots first, then LARGE.
     * - Large vehicle (LARGE): Look for LARGE slots only.
     * 
     * Within each size category, the slot closest to the entry gate is chosen.
     */
    public Ticket park(Vehicle vehicle, LocalDateTime entryTime, SlotType requestedType, String entryGateId) {
        Gate gate = entryGates.get(entryGateId);
        if (gate == null) {
            throw new IllegalArgumentException("Entry gate " + entryGateId + " does not exist.");
        }

        // Determine list of slot types to search, in priority order
        List<SlotType> searchPriority = new ArrayList<>();
        if (requestedType == SlotType.SMALL) {
            searchPriority.add(SlotType.SMALL);
            searchPriority.add(SlotType.MEDIUM);
            searchPriority.add(SlotType.LARGE);
        } else if (requestedType == SlotType.MEDIUM) {
            searchPriority.add(SlotType.MEDIUM);
            searchPriority.add(SlotType.LARGE);
        } else if (requestedType == SlotType.LARGE) {
            searchPriority.add(SlotType.LARGE);
        } else {
            throw new IllegalArgumentException("Unknown requested SlotType: " + requestedType);
        }

        Slot selectedSlot = null;

        // Iterate through slot types in priority order
        for (SlotType type : searchPriority) {
            Slot bestSlotForType = null;
            double minDistance = Double.MAX_VALUE;

            for (Slot slot : slots.values()) {
                if (slot.getType() == type && !slot.isOccupied()) {
                    Double dist = getDistance(entryGateId, slot.getId());
                    // If distance is not configured, treat it as infinite or unroutable
                    double distVal = (dist != null) ? dist : Double.MAX_VALUE;

                    if (distVal < minDistance) {
                        minDistance = distVal;
                        bestSlotForType = slot;
                    }
                }
            }

            // If we found a vacant slot of this type, we pick it (as it is the closest of this size)
            if (bestSlotForType != null) {
                selectedSlot = bestSlotForType;
                break; // Stop searching larger slot types
            }
        }

        if (selectedSlot == null) {
            // No slots available
            return null;
        }

        // Occupy the slot
        selectedSlot.setOccupied(true);
        selectedSlot.setOccupiedBy(vehicle);

        // Generate Ticket
        String ticketId = "TK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Ticket ticket = new Ticket(ticketId, vehicle, selectedSlot, entryTime, gate);
        activeTickets.put(ticketId, ticket);

        return ticket;
    }

    /**
     * Releases the slot allocated to the ticket and calculates the billing amount.
     */
    public double exit(Ticket ticket, LocalDateTime exitTime) {
        if (ticket == null) {
            throw new IllegalArgumentException("Ticket cannot be null.");
        }
        Ticket activeTicket = activeTickets.remove(ticket.getTicketId());
        if (activeTicket == null) {
            throw new IllegalArgumentException("Invalid or already processed ticket.");
        }

        Slot slot = activeTicket.getSlot();
        // Free the slot
        slot.setOccupied(false);
        slot.setOccupiedBy(null);

        // Calculate rate based on hourly charges (rounded up to nearest hour)
        long durationMillis = Duration.between(activeTicket.getEntryTime(), exitTime).toMillis();
        if (durationMillis <= 0) {
            return 0.0;
        }

        // Ceiling rounding for billing
        double hours = Math.ceil(durationMillis / 3600000.0);
        return hours * slot.getHourlyRate();
    }
}

// --- Supporting Models ---

enum SlotType {
    SMALL, MEDIUM, LARGE
}

class Vehicle {
    private final String licensePlate;
    private final SlotType type;

    public Vehicle(String licensePlate, SlotType type) {
        this.licensePlate = licensePlate;
        this.type = type;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public SlotType getType() {
        return type;
    }
}

class Slot {
    private final String id;
    private final int floor;
    private final SlotType type;
    private final double hourlyRate;
    private boolean isOccupied;
    private Vehicle occupiedBy;

    public Slot(String id, int floor, SlotType type, double hourlyRate) {
        this.id = id;
        this.floor = floor;
        this.type = type;
        this.hourlyRate = hourlyRate;
        this.isOccupied = false;
        this.occupiedBy = null;
    }

    public String getId() {
        return id;
    }

    public int getFloor() {
        return floor;
    }

    public SlotType getType() {
        return type;
    }

    public double getHourlyRate() {
        return hourlyRate;
    }

    public boolean isOccupied() {
        return isOccupied;
    }

    public void setOccupied(boolean occupied) {
        isOccupied = occupied;
    }

    public Vehicle getOccupiedBy() {
        return occupiedBy;
    }

    public void setOccupiedBy(Vehicle vehicle) {
        this.occupiedBy = vehicle;
    }
}

class Gate {
    private final String id;
    private final int floor;

    public Gate(String id, int floor) {
        this.id = id;
        this.floor = floor;
    }

    public String getId() {
        return id;
    }

    public int getFloor() {
        return floor;
    }
}

class Ticket {
    private final String ticketId;
    private final Vehicle vehicle;
    private final Slot slot;
    private final LocalDateTime entryTime;
    private final Gate entryGate;

    public Ticket(String ticketId, Vehicle vehicle, Slot slot, LocalDateTime entryTime, Gate entryGate) {
        this.ticketId = ticketId;
        this.vehicle = vehicle;
        this.slot = slot;
        this.entryTime = entryTime;
        this.entryGate = entryGate;
    }

    public String getTicketId() {
        return ticketId;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public Slot getSlot() {
        return slot;
    }

    public LocalDateTime getEntryTime() {
        return entryTime;
    }

    public Gate getEntryGate() {
        return entryGate;
    }
}
