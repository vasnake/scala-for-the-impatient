package Chapter06

object Objects {
// topics:
    // singletons
    // companion objects
    // objects extending a class or trait
    // the apply method
    // application objects
    // enumerations

    // use object when you need a singleton, or home for values and functions
    // or companion object

    // you cannot provide constructor parameters;
    // constructor executes on a first usage of an object (lazy value?);
    // define class and companion object together (one source file);
    // they can access each other private members;

    // singletons
    def singletons = {
        ???
    }

    // companion objects
    def companionObjects = {
        ???
    }

    // objects extending a class or trait
    def objectsExtendingAClassOrTrait = {
        ???
    }

    // the apply method
    def theApplyMethod = {
        ???
    }

    // application objects
    def applicationObjects = {
        ???
    }

    // enumerations
    def enumerations = {
        ???
    }

}

object Objects_Exercises {

    // 1. Write an object Conversions with methods inchesToCentimeters,
    // gallonsToLiters, and milesToKilometers.
    def ex1 = {
        ???
    }

    // 2. The preceding problem wasn’t very object-oriented. Provide a general superclass
    //UnitConversion and define objects InchesToCentimeters, GallonsToLiters,
    //and MilesToKilometers that extend it.
    def ex2 = {
        ???
    }

    // 3. Define an Origin object that extends java.awt.Point. Why is this not actually a good
    //idea? (Have a close look at the methods of the Point class.)
    def ex3 = {
        ???
    }

    // 4. Define a Point class with a companion object so that you can construct Point instances as
    //Point(3, 4), without using new.
    def ex4 = {
        ???
    }

    // 5. Write a Scala application, using the App trait, that prints its command-line arguments in
    //reverse order, separated by spaces. For example, scala Reverse Hello World should
    //print World Hello.
    def ex5 = {
        ???
    }

    // 6. Write an enumeration describing the four playing card suits so that the toString method
    //returns ♣, ♦, ♥, or ♠.
    def ex6 = {
        ???
    }

    // 7. Implement a function that checks whether a card suit value from the preceding exercise is red.
    def ex7 = {
        ???
    }

    // 8. Write an enumeration describing the eight corners of the RGB color cube. As IDs, use the color
    //values (for example, 0xff0000 for Red).
    def ex8 = {
        ???
    }
}
