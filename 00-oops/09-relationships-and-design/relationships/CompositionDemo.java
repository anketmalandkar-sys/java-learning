package oops.relationships;

import java.util.ArrayList;
import java.util.List;

/*
 * ============================================================================
 *  COMPOSITION — a STRONG "whole/part" association  ("owns-a", "part-of")
 * ============================================================================
 *
 *         House <#>------- Room                 (UML: FILLED diamond on the whole)
 *
 *  - It IS an aggregation (whole/part) and therefore also an association ...
 *  - ... PLUS exclusive OWNERSHIP and a SHARED LIFECYCLE:
 *       * the whole CREATES its parts (the `new` happens inside the whole)
 *       * a part belongs to exactly ONE whole, never shared
 *       * no outside code holds a reference to the part
 *       * when the whole dies, its parts die with it
 *
 *  Java signal: `new Part(...)` inside the whole's constructor/methods,
 *  part stored in a private (often final) field, never handed out as-is.
 *  (Java has garbage collection, so "dies with" means: once the whole is
 *   unreachable, its parts are unreachable too and get collected together.)
 *
 *  Other examples: Human <#>- Heart, Order <#>- OrderLine,
 *  Book <#>- Chapter, Car <#>- Engine.
 */

// --------------------------------------------------------------------------
// House owns Rooms
// --------------------------------------------------------------------------
class House {

    /*
     * A nested (inner) class is a nice way to express "this part has no
     * meaning outside its whole". A Room can only be created by a House.
     */
    final class Room {
        private final String type;
        private final double area;

        private Room(String type, double area) {       // private: only House can call it
            this.type = type;
            this.area = area;
        }

        @Override
        public String toString() { return type + " " + area + "m2 of " + address; }
    }

    private final String address;
    private final List<Room> rooms = new ArrayList<>();

    House(String address) {
        this.address = address;
        // The whole creates its parts — nobody outside does `new Room`.
        rooms.add(new Room("Kitchen", 12));
        rooms.add(new Room("Bedroom", 16));
        rooms.add(new Room("Hall", 20));
    }

    void addRoom(String type, double area) {
        rooms.add(new Room(type, area));                 // still created inside
    }

    double totalArea() {
        return rooms.stream().mapToDouble(r -> r.area).sum();
    }

    // Expose INFORMATION about parts, not the parts themselves.
    List<String> roomSummary() {
        return rooms.stream().map(Room::toString).toList();
    }

    void demolish() {
        System.out.println("  Demolishing " + address + " - its " + rooms.size() + " rooms go with it.");
        rooms.clear();
    }
}

// --------------------------------------------------------------------------
// Car owns an Engine
// --------------------------------------------------------------------------
class Engine {
    private final int horsePower;
    private boolean running;

    Engine(int horsePower) { this.horsePower = horsePower; }

    void start() { running = true; }
    void stop()  { running = false; }
    boolean isRunning() { return running; }
    int getHorsePower() { return horsePower; }
}

class Car {
    private final String model;
    private final Engine engine;          // private final: owned for life

    Car(String model, int hp) {
        this.model = model;
        this.engine = new Engine(hp);     // created HERE -> composition
        // Compare with aggregation: Car(String model, Engine engine) {...}
        // where the engine is built elsewhere and could be swapped/shared.
    }

    // The Car DELEGATES to its part. Outside code talks to the Car only.
    void start() {
        engine.start();
        System.out.println("  " + model + " started (" + engine.getHorsePower() + " hp).");
    }

    void stop() {
        engine.stop();
        System.out.println("  " + model + " stopped.");
    }

    boolean isRunning() { return engine.isRunning(); }
}

public class CompositionDemo {

    public static void main(String[] args) {
        System.out.println("=== House <#>- Room ===");
        House house = new House("221B Baker Street");
        house.addRoom("Study", 10);
        house.roomSummary().forEach(r -> System.out.println("  " + r));
        System.out.println("  Total area: " + house.totalArea() + " m2");

        // House.Room r = house.new Room("Garage", 30);  // COMPILE ERROR: Room() is private

        house.demolish();
        house = null;
        // There is NO variable anywhere else pointing to a Room, so every
        // Room is now unreachable together with the House. Parts died with
        // the whole. (Contrast with AggregationDemo where professors survived.)

        System.out.println("\n=== Car <#>- Engine ===");
        Car car = new Car("Nexon", 120);
        car.start();
        System.out.println("  Running? " + car.isRunning());
        car.stop();
        // There is no getEngine(): the engine is an internal part, you
        // operate it only THROUGH the car.
        car = null;   // car and its engine become garbage together
        System.out.println("  Car gone -> engine gone.");
    }
}
