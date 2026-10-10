/**
 * Example 1: the eight primitive types, their ranges, literals, and default values.
 *
 * Run: java examples/Example1_Basics.java
 */
public class Example1_Basics {

    // Fields get default values; we read them below without assigning anything.
    static int defaultInt;
    static double defaultDouble;
    static boolean defaultBoolean;
    static char defaultChar;
    static String defaultString;

    public static void main(String[] args) {
        System.out.println("Ranges:");
        System.out.println("  byte  " + Byte.MIN_VALUE + " .. " + Byte.MAX_VALUE);
        System.out.println("  short " + Short.MIN_VALUE + " .. " + Short.MAX_VALUE);
        System.out.println("  int   " + Integer.MIN_VALUE + " .. " + Integer.MAX_VALUE);
        System.out.println("  long  " + Long.MIN_VALUE + " .. " + Long.MAX_VALUE);

        System.out.println("The same number, three ways:");
        int decimal = 26;
        int hex = 0x1A;
        int binary = 0b1_1010;      // underscores are allowed between digits
        System.out.println("  " + decimal + " " + hex + " " + binary);

        // Without L this literal wouldn't compile: it's too big for an int.
        long worldPopulation = 8_100_000_000L;
        System.out.println("World population: " + worldPopulation);

        float discount = 0.15f;     // f makes it a float literal
        double lightSpeed = 2.998e8;
        System.out.println("discount=" + discount + " lightSpeed=" + lightSpeed);

        char grade = 'A';
        char omega = (char) 0x03A9;   // the Greek letter omega, by its Unicode number
        System.out.println("grade=" + grade + " omega code=" + (int) omega + " quote:\"hi\"");

        boolean isWeekend = false;
        System.out.println("isWeekend=" + isWeekend);

        System.out.println("Field defaults:");
        System.out.println("  int=" + defaultInt + " double=" + defaultDouble
                + " boolean=" + defaultBoolean + " char code=" + (int) defaultChar
                + " String=" + defaultString);
    }
}

/* Expected output:
Ranges:
  byte  -128 .. 127
  short -32768 .. 32767
  int   -2147483648 .. 2147483647
  long  -9223372036854775808 .. 9223372036854775807
The same number, three ways:
  26 26 26
World population: 8100000000
discount=0.15 lightSpeed=2.998E8
grade=A omega code=937 quote:"hi"
isWeekend=false
Field defaults:
  int=0 double=0.0 boolean=false char code=0 String=null
*/
