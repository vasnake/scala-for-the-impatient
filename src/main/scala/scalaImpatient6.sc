// Scala for the Impatient
// 6. Objects
/*
• Use objects for singletons and utility methods.
• A class can have a companion object with the same name.
• Objects can extend classes or traits.
• The apply method of an object is usually used for constructing new instances of the companion class.
• To avoid the main method, use an object that extends the App trait.
• You can implement enumerations by extending the Enumeration object.
 */

// Scala has no static methods or fields. Instead, you use the object construct

// An object can have essentially all the features of a class
// it can even extend other classes or traits
// (see Section 6.3, “Objects Extending a Class or Trait,” on page 67).
// There is just one exception: You cannot provide constructor parameters.

// You use an object in Scala whenever you would have used a singleton object in Java or C++:
//• As a home for utility functions or constants
//• When a single immutable instance can be shared efficiently
//• When a single instance is required to coordinate some service (the singleton design pattern)

// In Java or C++, you often have a class with both instance methods and static methods.
// In Scala, you achieve this by having a class and a “companion” object of the same name.
{
    object Account { // The companion object
        private var lastNumber = 0
        private def newUniqueNumber() = { lastNumber += 1; lastNumber }
    }
    class Account {
        val id = Account.newUniqueNumber()
        private var balance = 0.0
        def deposit(amount: Double) { balance += amount }
    }
    // The class and its companion object can access each other’s private features.
    // They must be located in the same source file.
}

// An object can extend a class and/or one or more traits.
// One useful application is to specify default objects that can be shared

// It is common to have objects with an apply method.
// The apply method is called for expressions of the form
// Object(arg1, ..., argN)
// Typically, such an apply method returns an object of the companion class
{
    object Account { // The companion object
        private var lastNumber = 0
        private def newUniqueNumber() = { lastNumber += 1; lastNumber }
        def apply(initialBalance: Double) = new Account(newUniqueNumber(), initialBalance)
    }
    class Account private (val id: Int, initialBalance: Double) {
        private var balance = initialBalance
    }
    val acct = Account(1000.0)
}

// Instead of providing a main method for your application,
// you can extend the App trait and place the program code into the constructor body:
object Hello extends App {
    println("Hello, World!")
}
// If you need the command-line arguments, you can get them from the args property:
{
    object Hello extends App {
        if (args.length > 0)
            println("Hello, " + args(0))
        else
            println("Hello, World!")
    }
}
// If you invoke the application with the scala.time option set,
// then the elapsed time is displayed when the program exits.
//$ scalac Hello.scala
//$ scala -Dscala.time Hello Fred
//Hello, Fred
//    [total 4ms]

// Unlike Java or C++, Scala does not have enumerated types. However,
// the standard library provides an Enumeration helper class that you can use to produce enumerations
{
    object TrafficLightColor extends Enumeration {
        val Red, Yellow, Green = Value
    }
    // Remember that the type of the enumeration is TrafficLightColor.Value
    TrafficLightColor(0) // Calls Enumeration.apply
    TrafficLightColor.withName("Red")

}
// The call TrafficLightColor.values yields a set of all values
// you can look up an enumeration value by its ID or name. Both of the following yield the object TrafficLightColor.Red:

// Exercises

/*
1. Write an object Conversions with methods inchesToCentimeters, gallonsToLiters, and milesToKilometers
 */
{
    object Conversions {
        def inchesToCentimeters(inch: Double) = inch * 2.54
        def gallonsToLiters(gallons: Double) = gallons * 3.78541
        def milesToKilometers(miles: Double) = miles * 1.60934
    }
}

/*
2. The preceding problem wasn’t very object-oriented.
Provide a general superclass UnitConversion and define objects
InchesToCentimeters, GallonsToLiters, and MilesToKilometers that extend it.
 */
{
    trait UnitConversion {
        def apply(x: Double): Double
    }
    object InchesToCentimeters extends UnitConversion {
        def apply(inches: Double) = inches * 2.54
    }
    object GallonsToLiters extends UnitConversion {
        def apply(gallons: Double) = gallons * 3.78541
    }
    object MilesToKilometers extends UnitConversion {
        def apply(miles: Double) = miles * 1.60934
    }
    MilesToKilometers(3)
}

/*
3. Define an Origin object that extends java.awt.Point.
Why is this not actually a good idea?
(Have a close look at the methods of the Point class.)
 */
{
    object Origin extends  java.awt.Point
    // Point is a mutable data object
    // Origin is a singleton, should be immutable
}

/*
4. Define a Point class with a companion object so that you can construct
Point instances as Point(3, 4), without using new.
 */
{
    class Point(val x: Int, val y: Int)
    object Point {
        def apply(x: Int, y: Int) = new Point(x, y)
    }
    Point(3, 7)
}

/*
5. Write a Scala application, using the App trait,
that prints the command-line arguments in reverse order, separated by spaces.
For example, scala Reverse Hello World should print World Hello.
 */
{
    object Reverse extends App {
        println(args.reverse.mkString(" "))
    }
}

/*
6. Write an enumeration describing the four playing card suits
so that the toString method returns ♣, ♦, ♥, or ♠.
 */
{
    object SuitsEnum extends Enumeration {
        val club = Value("♣")
        val diamond = Value("♦")
        val heart = Value("♥")
        val spade = Value("♠")
    }
    for (s <- SuitsEnum.values) println(s"suit: $s")
}

/*
7. Implement a function that checks whether a card suit value from the preceding exercise is red.
 */
{
    object SuitsEnum extends Enumeration {
        val club = Value("♣")
        val diamond = Value("♦")
        val heart = Value("♥")
        val spade = Value("♠")
    }

    def isRed(suit: SuitsEnum.Value): Boolean = {
        suit == SuitsEnum.heart || suit == SuitsEnum.diamond
    }
    isRed(SuitsEnum.heart)
}

/*
8. Write an enumeration describing the eight corners of the RGB color cube.
As IDs, use the color values (for example, 0xff0000 for Red).
 */
{
    object ColorsEnum extends Enumeration {
        val RED =       Value(0xff0000)
        val MAGENTA =   Value(0xff00ff)
        val YELLOW =    Value(0xffff00)
        val GREEN =     Value(0x00ff00)
        val BLUE =      Value(0x0000ff)
        val CYAN =      Value(0x00ffff)
        val WHITE =     Value(0xffffff)
        val BLACK =     Value(0x000000)
    }
    for (c <- ColorsEnum.values) println(s"color $c ${c.id}")
}
