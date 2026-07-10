# Parking Lot System UML Diagram

This file documents the class diagram of the Parking Lot system in both **Mermaid** format (visualized automatically in GitHub, VS Code, and other Markdown viewers) and **PlantUML** format.

---

## 1. Mermaid Class Diagram

```mermaid
classDiagram
    direction TB
    
    class SlotType {
        <<enumeration>>
        SMALL
        MEDIUM
        LARGE
    }

    class Vehicle {
        -String licensePlate
        -SlotType type
        +Vehicle(String licensePlate, SlotType type)
        +getLicensePlate() String
        +getType() SlotType
    }

    class Slot {
        -String id
        -int floor
        -SlotType type
        -double hourlyRate
        -boolean isOccupied
        -Vehicle occupiedBy
        +Slot(String id, int floor, SlotType type, double hourlyRate)
        +getId() String
        +getFloor() int
        +getType() SlotType
        +getHourlyRate() double
        +isOccupied() boolean
        +setOccupied(boolean occupied) void
        +getOccupiedBy() Vehicle
        +setOccupiedBy(Vehicle vehicle) void
    }

    class Gate {
        -String id
        -int floor
        +Gate(String id, int floor)
        +getId() String
        +getFloor() int
    }

    class Ticket {
        -String ticketId
        -Vehicle vehicle
        -Slot slot
        -LocalDateTime entryTime
        -Gate entryGate
        +Ticket(String ticketId, Vehicle vehicle, Slot slot, LocalDateTime entryTime, Gate entryGate)
        +getTicketId() String
        +getVehicle() Vehicle
        +getSlot() Slot
        +getEntryTime() LocalDateTime
        +getEntryGate() Gate
    }

    class ParkingLot {
        -int numFloors
        -Map~String, Slot~ slots
        -Map~String, Gate~ entryGates
        -Map~String, Map~String, Double~~ distances
        -Map~String, Ticket~ activeTickets
        +ParkingLot()
        +setFloors(int numFloors) void
        +getNumFloors() int
        +addSlot(int floor, String slotId, SlotType type, double hourlyRate) void
        +getSlot(String slotId) Slot
        +addEntryGate(int floor, String gateId) void
        +getEntryGate(String gateId) Gate
        +setDistance(String gateId, String slotId, double distance) void
        +getDistance(String gateId, String slotId) Double
        +park(Vehicle vehicle, LocalDateTime entryTime, SlotType requestedType, String entryGateId) Ticket
        +exit(Ticket ticket, LocalDateTime exitTime) double
    }

    ParkingLot "1" *-- "*" Slot : manages
    ParkingLot "1" *-- "*" Gate : manages
    ParkingLot "1" *-- "*" Ticket : tracks active
    Ticket "1" --> "1" Vehicle : references
    Ticket "1" --> "1" Slot : references
    Ticket "1" --> "1" Gate : references
    Slot "0..1" --> "1" Vehicle : occupied by
    Slot "*" --> "1" SlotType : has type
    Vehicle "*" --> "1" SlotType : has type
```

---

## 2. PlantUML Source Code

If you prefer to render using PlantUML tools, copy the code block below:

```plantuml
@startuml
enum SlotType {
  SMALL
  MEDIUM
  LARGE
}

class Vehicle {
  - String licensePlate
  - SlotType type
  + Vehicle(String, SlotType)
  + getLicensePlate() : String
  + getType() : SlotType
}

class Slot {
  - String id
  - int floor
  - SlotType type
  - double hourlyRate
  - boolean isOccupied
  - Vehicle occupiedBy
  + Slot(String, int, SlotType, double)
  + getId() : String
  + getFloor() : int
  + getType() : SlotType
  + getHourlyRate() : double
  + isOccupied() : boolean
  + setOccupied(boolean) : void
  + getOccupiedBy() : Vehicle
  + setOccupiedBy(Vehicle) : void
}

class Gate {
  - String id
  - int floor
  + Gate(String, int)
  + getId() : String
  + getFloor() : int
}

class Ticket {
  - String ticketId
  - Vehicle vehicle
  - Slot slot
  - LocalDateTime entryTime
  - Gate entryGate
  + Ticket(String, Vehicle, Slot, LocalDateTime, Gate)
  + getTicketId() : String
  + getVehicle() : Vehicle
  + getSlot() : Slot
  + getEntryTime() : LocalDateTime
  + getEntryGate() : Gate
}

class ParkingLot {
  - int numFloors
  - Map<String, Slot> slots
  - Map<String, Gate> entryGates
  - Map<String, Map<String, Double>> distances
  - Map<String, Ticket> activeTickets
  + ParkingLot()
  + setFloors(int) : void
  + getNumFloors() : int
  + addSlot(int, String, SlotType, double) : void
  + getSlot(String) : Slot
  + addEntryGate(int, String) : void
  + getEntryGate(String) : Gate
  + setDistance(String, String, double) : void
  + getDistance(String, String) : Double
  + park(Vehicle, LocalDateTime, SlotType, String) : Ticket
  + exit(Ticket, LocalDateTime) : double
}

ParkingLot "1" *-- "*" Slot : manages
ParkingLot "1" *-- "*" Gate : manages
ParkingLot "1" *-- "*" Ticket : tracks active
Ticket "1" --> "1" Vehicle : references
Ticket "1" --> "1" Slot : references
Ticket "1" --> "1" Gate : references
Slot "0..1" --> "1" Vehicle : occupied by
Slot "*" --> "1" SlotType : has type
Vehicle "*" --> "1" SlotType : has type
@enduml
```
