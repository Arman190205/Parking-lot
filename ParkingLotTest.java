import java.time.LocalDateTime;

public class ParkingLotTest {

    public static void main(String[] args) {
        System.out.println("Starting Parking Lot System Tests...\n");

        testLayoutConfiguration();
        testSmallVehiclePriority();
        testMediumVehiclePriority();
        testLargeVehiclePriority();
        testDistancePrioritization();
        testBillingCalculation();

        System.out.println("\nAll tests passed successfully!");
    }

    private static void assertEquals(Object expected, Object actual, String message) {
        if (!ObjectsEquals(expected, actual)) {
            throw new RuntimeException("Assertion failed: " + message + " | Expected: " + expected + ", Actual: " + actual);
        }
    }

    private static void assertEquals(double expected, double actual, double delta, String message) {
        if (Math.abs(expected - actual) > delta) {
            throw new RuntimeException("Assertion failed: " + message + " | Expected: " + expected + ", Actual: " + actual + " (delta: " + delta + ")");
        }
    }

    private static boolean ObjectsEquals(Object a, Object b) {
        return (a == b) || (a != null && a.equals(b));
    }

    private static void testLayoutConfiguration() {
        System.out.println("Testing layout configuration (Create)...");
        ParkingLot lot = new ParkingLot();
        lot.setFloors(3);
        lot.addSlot(0, "S-101", SlotType.SMALL, 20.0);
        lot.addSlot(1, "M-201", SlotType.MEDIUM, 40.0);
        lot.addSlot(2, "L-301", SlotType.LARGE, 60.0);

        lot.addEntryGate(0, "EG-1");
        lot.addEntryGate(0, "EG-2");

        lot.setDistance("EG-1", "S-101", 10.0);
        lot.setDistance("EG-1", "M-201", 25.0);

        assertEquals(3, lot.getNumFloors(), "Number of floors");
        assertEquals(SlotType.SMALL, lot.getSlot("S-101").getType(), "Slot S-101 type");
        assertEquals(40.0, lot.getSlot("M-201").getHourlyRate(), "Slot M-201 hourly rate");
        assertEquals(0, lot.getEntryGate("EG-1").getFloor(), "EG-1 floor");
        assertEquals(10.0, lot.getDistance("EG-1", "S-101"), "Distance EG-1 to S-101");
        System.out.println("-> Layout configuration test passed.");
    }

    private static void testSmallVehiclePriority() {
        System.out.println("Testing priority matching for SMALL vehicles (Scooty)...");
        ParkingLot lot = new ParkingLot();
        lot.setFloors(1);
        lot.addSlot(0, "S-1", SlotType.SMALL, 10.0);
        lot.addSlot(0, "M-1", SlotType.MEDIUM, 20.0);
        lot.addSlot(0, "L-1", SlotType.LARGE, 30.0);
        lot.addEntryGate(0, "EG-1");

        // Distance config
        lot.setDistance("EG-1", "S-1", 5.0);
        lot.setDistance("EG-1", "M-1", 10.0);
        lot.setDistance("EG-1", "L-1", 15.0);

        LocalDateTime now = LocalDateTime.now();

        // 1st Scooty: should go to SMALL slot
        Vehicle scooty1 = new Vehicle("SCOOTY1", SlotType.SMALL);
        Ticket ticket1 = lot.park(scooty1, now, SlotType.SMALL, "EG-1");
        assertEquals("S-1", ticket1.getSlot().getId(), "1st Scooty slot selection");

        // 2nd Scooty: SMALL is full, should go to MEDIUM slot
        Vehicle scooty2 = new Vehicle("SCOOTY2", SlotType.SMALL);
        Ticket ticket2 = lot.park(scooty2, now, SlotType.SMALL, "EG-1");
        assertEquals("M-1", ticket2.getSlot().getId(), "2nd Scooty slot selection");

        // 3rd Scooty: SMALL & MEDIUM full, should go to LARGE slot
        Vehicle scooty3 = new Vehicle("SCOOTY3", SlotType.SMALL);
        Ticket ticket3 = lot.park(scooty3, now, SlotType.SMALL, "EG-1");
        assertEquals("L-1", ticket3.getSlot().getId(), "3rd Scooty slot selection");

        // 4th Scooty: all slots full, should get null ticket
        Vehicle scooty4 = new Vehicle("SCOOTY4", SlotType.SMALL);
        Ticket ticket4 = lot.park(scooty4, now, SlotType.SMALL, "EG-1");
        assertEquals(null, ticket4, "4th Scooty slot selection (expected null)");

        System.out.println("-> SMALL vehicle priority test passed.");
    }

    private static void testMediumVehiclePriority() {
        System.out.println("Testing priority matching for MEDIUM vehicles (Car)...");
        ParkingLot lot = new ParkingLot();
        lot.setFloors(1);
        lot.addSlot(0, "S-1", SlotType.SMALL, 10.0);
        lot.addSlot(0, "M-1", SlotType.MEDIUM, 20.0);
        lot.addSlot(0, "L-1", SlotType.LARGE, 30.0);
        lot.addEntryGate(0, "EG-1");

        // Distance config
        lot.setDistance("EG-1", "S-1", 5.0);
        lot.setDistance("EG-1", "M-1", 10.0);
        lot.setDistance("EG-1", "L-1", 15.0);

        LocalDateTime now = LocalDateTime.now();

        // 1st Car: should go to MEDIUM slot (cannot go to SMALL)
        Vehicle car1 = new Vehicle("CAR1", SlotType.MEDIUM);
        Ticket ticket1 = lot.park(car1, now, SlotType.MEDIUM, "EG-1");
        assertEquals("M-1", ticket1.getSlot().getId(), "1st Car slot selection");

        // 2nd Car: MEDIUM full, should go to LARGE slot
        Vehicle car2 = new Vehicle("CAR2", SlotType.MEDIUM);
        Ticket ticket2 = lot.park(car2, now, SlotType.MEDIUM, "EG-1");
        assertEquals("L-1", ticket2.getSlot().getId(), "2nd Car slot selection");

        // 3rd Car: all compatible slots full, should get null ticket
        Vehicle car3 = new Vehicle("CAR3", SlotType.MEDIUM);
        Ticket ticket3 = lot.park(car3, now, SlotType.MEDIUM, "EG-1");
        assertEquals(null, ticket3, "3rd Car slot selection (expected null)");

        System.out.println("-> MEDIUM vehicle priority test passed.");
    }

    private static void testLargeVehiclePriority() {
        System.out.println("Testing priority matching for LARGE vehicles (Bus)...");
        ParkingLot lot = new ParkingLot();
        lot.setFloors(1);
        lot.addSlot(0, "S-1", SlotType.SMALL, 10.0);
        lot.addSlot(0, "M-1", SlotType.MEDIUM, 20.0);
        lot.addSlot(0, "L-1", SlotType.LARGE, 30.0);
        lot.addEntryGate(0, "EG-1");

        // Distance config
        lot.setDistance("EG-1", "S-1", 5.0);
        lot.setDistance("EG-1", "M-1", 10.0);
        lot.setDistance("EG-1", "L-1", 15.0);

        LocalDateTime now = LocalDateTime.now();

        // 1st Bus: should go to LARGE slot (cannot go to SMALL or MEDIUM)
        Vehicle bus1 = new Vehicle("BUS1", SlotType.LARGE);
        Ticket ticket1 = lot.park(bus1, now, SlotType.LARGE, "EG-1");
        assertEquals("L-1", ticket1.getSlot().getId(), "1st Bus slot selection");

        // 2nd Bus: LARGE full, should get null
        Vehicle bus2 = new Vehicle("BUS2", SlotType.LARGE);
        Ticket ticket2 = lot.park(bus2, now, SlotType.LARGE, "EG-1");
        assertEquals(null, ticket2, "2nd Bus slot selection (expected null)");

        System.out.println("-> LARGE vehicle priority test passed.");
    }

    private static void testDistancePrioritization() {
        System.out.println("Testing distance-based selection...");
        ParkingLot lot = new ParkingLot();
        lot.setFloors(1);
        lot.addSlot(0, "S-A", SlotType.SMALL, 10.0);
        lot.addSlot(0, "S-B", SlotType.SMALL, 10.0);
        lot.addSlot(0, "S-C", SlotType.SMALL, 10.0);
        lot.addEntryGate(0, "EG-1");

        // Set distances from EG-1 to slots
        // S-B is closest (2.5), S-C is next (5.0), S-A is furthest (10.0)
        lot.setDistance("EG-1", "S-A", 10.0);
        lot.setDistance("EG-1", "S-B", 2.5);
        lot.setDistance("EG-1", "S-C", 5.0);

        LocalDateTime now = LocalDateTime.now();

        // Should pick S-B first as it is the closest SMALL slot
        Vehicle scooty1 = new Vehicle("SCOOTY1", SlotType.SMALL);
        Ticket ticket1 = lot.park(scooty1, now, SlotType.SMALL, "EG-1");
        assertEquals("S-B", ticket1.getSlot().getId(), "Closest slot S-B");

        // Should pick S-C next as it is the second closest SMALL slot
        Vehicle scooty2 = new Vehicle("SCOOTY2", SlotType.SMALL);
        Ticket ticket2 = lot.park(scooty2, now, SlotType.SMALL, "EG-1");
        assertEquals("S-C", ticket2.getSlot().getId(), "Next closest slot S-C");

        // Should pick S-A last
        Vehicle scooty3 = new Vehicle("SCOOTY3", SlotType.SMALL);
        Ticket ticket3 = lot.park(scooty3, now, SlotType.SMALL, "EG-1");
        assertEquals("S-A", ticket3.getSlot().getId(), "Furthest slot S-A");

        System.out.println("-> Distance-based selection test passed.");
    }

    private static void testBillingCalculation() {
        System.out.println("Testing billing calculation during exit...");
        ParkingLot lot = new ParkingLot();
        lot.setFloors(1);
        lot.addSlot(0, "M-100", SlotType.MEDIUM, 15.0); // 15.0 per hour
        lot.addEntryGate(0, "EG-1");
        lot.setDistance("EG-1", "M-100", 5.0);

        LocalDateTime entryTime = LocalDateTime.of(2026, 7, 10, 10, 0, 0);

        // Case 1: Exactly 1 hour
        Vehicle car1 = new Vehicle("CAR-BILL-1", SlotType.MEDIUM);
        Ticket ticket1 = lot.park(car1, entryTime, SlotType.MEDIUM, "EG-1");
        LocalDateTime exitTime1 = entryTime.plusHours(1);
        double amount1 = lot.exit(ticket1, exitTime1);
        assertEquals(15.0, amount1, 0.001, "Billing for exactly 1 hour");

        // Case 2: 1 hour and 1 minute (should round up to 2 hours)
        Ticket ticket2 = lot.park(car1, entryTime, SlotType.MEDIUM, "EG-1");
        LocalDateTime exitTime2 = entryTime.plusHours(1).plusMinutes(1);
        double amount2 = lot.exit(ticket2, exitTime2);
        assertEquals(30.0, amount2, 0.001, "Billing for 1 hour 1 minute (rounded to 2 hours)");

        // Case 3: 2 hours and 59 minutes (should round up to 3 hours)
        Ticket ticket3 = lot.park(car1, entryTime, SlotType.MEDIUM, "EG-1");
        LocalDateTime exitTime3 = entryTime.plusHours(2).plusMinutes(59);
        double amount3 = lot.exit(ticket3, exitTime3);
        assertEquals(45.0, amount3, 0.001, "Billing for 2 hours 59 minutes (rounded to 3 hours)");

        // Case 4: 0 minutes or negative duration
        Ticket ticket4 = lot.park(car1, entryTime, SlotType.MEDIUM, "EG-1");
        LocalDateTime exitTime4 = entryTime.minusMinutes(10);
        double amount4 = lot.exit(ticket4, exitTime4);
        assertEquals(0.0, amount4, 0.001, "Billing for negative/zero duration");

        System.out.println("-> Billing calculation test passed.");
    }
}
