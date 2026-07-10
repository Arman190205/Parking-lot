# Multi-floor Parking Lot System

A clean, object-oriented, and highly flexible Multi-Floor Parking Lot System implemented in Java. The architecture handles multiple levels (floors), dynamic slot types (`SMALL`, `MEDIUM`, `LARGE`), custom entry gates, custom gate-to-slot distances, prioritization rules, and hourly ceiling-rounded billing.

---

## Features

1. **Flexible Layout Setup (Create)**:
   - Configured dynamically via setter methods (adhering to the design guideline of no complex/massive constructors).
   - Supports multiple floors and slots mapped dynamically to floors.
   - Supports entry gates mapped to specific floors.
   - Dynamic gate-to-slot distance configurations.

2. **Predefined Slot Types**:
   - **SMALL (S)**: Designed for 2-wheelers (e.g., Scooties). Can be placed in `SMALL`, `MEDIUM`, or `LARGE` slots, prioritizing `SMALL` first, then `MEDIUM`, then `LARGE`.
   - **MEDIUM (M)**: Designed for cars. Can be placed in `MEDIUM` or `LARGE` slots, prioritizing `MEDIUM` first, then `LARGE` (cannot park in `SMALL`).
   - **LARGE (L)**: Designed for buses. Can only be placed in `LARGE` slots.

3. **Closest-Distance Selection (Park)**:
   - When parking a vehicle from a specific entry gate, the system prioritizes matching the preferred slot type first.
   - Within the matching slot type, the slot **closest** to that entry gate (minimum configured distance) is allocated.

4. **Hourly Rate Billing (Exit)**:
   - Each slot is configured with its own hourly rate.
   - Billing calculations are automatically rounded up to the nearest hour (ceiling rounding). For example, a parking duration of 1 hour and 1 minute is billed as 2 hours.

---

## Design Diagrams
See [ParkingLot_UML.md](ParkingLot_UML.md) for full Mermaid and PlantUML diagrams mapping the class structure and relations.

---

## How to Build & Run

Ensure you have the Java Development Kit (JDK) installed (version 8 or newer).

### 1. Compile the Source Code
```powershell
javac ParkingLot.java ParkingLotTest.java
```

### 2. Run the Verification Tests
```powershell
java ParkingLotTest
```

### Expected Output
When running the tests, you should see the following outputs confirming that all priority rules, distances, layout creation, and billing calculations function correctly:
```text
Starting Parking Lot System Tests...

Testing layout configuration (Create)...
-> Layout configuration test passed.
Testing priority matching for SMALL vehicles (Scooty)...
-> SMALL vehicle priority test passed.
Testing priority matching for MEDIUM vehicles (Car)...
-> MEDIUM vehicle priority test passed.
Testing priority matching for LARGE vehicles (Bus)...
-> LARGE vehicle priority test passed.
Testing distance-based selection...
-> Distance-based selection test passed.
Testing billing calculation during exit...
-> Billing calculation test passed.

All tests passed successfully!
```

---

## Project Structure
- [ParkingLot.java](ParkingLot.java): Holds all data models (`Vehicle`, `SlotType`, `Slot`, `Gate`, `Ticket`) and the core `ParkingLot` class APIs (`create`, `park`, `exit`).
- [ParkingLotTest.java](ParkingLotTest.java): Comprehensive test suite validating layout setup, sizing constraints, gate-distance minimization, and billing.
- [ParkingLot_UML.md](ParkingLot_UML.md): Class diagram and PlantUML definition file.
