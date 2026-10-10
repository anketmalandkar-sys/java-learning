/**
 * Example 1: the four kinds of variables in one small class.
 *
 * Run: java examples/Example1_Basics.java
 */
public class Example1_Basics {

    static class Lamp {
        // Static field: ONE copy shared by every Lamp.
        static int lampsCreated = 0;

        // Constant: static + final, named in UPPER_SNAKE_CASE.
        static final int MAX_BRIGHTNESS = 100;

        // Instance fields: EACH Lamp has its own copy.
        String room;
        int brightness;     // not set in the constructor, so it starts at the default 0
        boolean on;         // default false

        // 'room' here is a parameter. It shadows the field, so we need 'this.room'.
        Lamp(String room) {
            this.room = room;
            lampsCreated++;
        }

        // 'amount' is a parameter; 'newLevel' is a local variable.
        void brighten(int amount) {
            int newLevel = brightness + amount;
            // Cap at the constant so no lamp goes above 100.
            brightness = Math.min(newLevel, MAX_BRIGHTNESS);
            on = brightness > 0;
        }

        @Override
        public String toString() {
            return room + " lamp: on=" + on + ", brightness=" + brightness;
        }
    }

    public static void main(String[] args) {   // 'args' is a parameter too
        Lamp kitchen = new Lamp("Kitchen");
        Lamp bedroom = new Lamp("Bedroom");

        System.out.println("Fresh lamps (fields have default values):");
        System.out.println("  " + kitchen);
        System.out.println("  " + bedroom);

        kitchen.brighten(60);
        kitchen.brighten(70);   // would be 130, but capped at MAX_BRIGHTNESS

        System.out.println("After brightening only the kitchen lamp:");
        System.out.println("  " + kitchen);
        System.out.println("  " + bedroom + "   <- unaffected: instance fields are per object");

        // Static fields belong to the class, so read them through the class name.
        System.out.println("Lamps created: " + Lamp.lampsCreated);

        // A local variable must be assigned before it's read.
        int totalBrightness = 0;
        totalBrightness += kitchen.brightness;
        totalBrightness += bedroom.brightness;
        System.out.println("Total brightness: " + totalBrightness);
    }
}

/* Expected output:
Fresh lamps (fields have default values):
  Kitchen lamp: on=false, brightness=0
  Bedroom lamp: on=false, brightness=0
After brightening only the kitchen lamp:
  Kitchen lamp: on=true, brightness=100
  Bedroom lamp: on=false, brightness=0   <- unaffected: instance fields are per object
Lamps created: 2
Total brightness: 100
*/
