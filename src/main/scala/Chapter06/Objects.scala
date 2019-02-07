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
        // scala has no static methods or fields
        // use 'object' instead

        // single instance, created on first use
        // can't provide constructor/class parameters
        object Accounts {
            private var lastNumber = 0
            def newUniqueNumber(): Int = { lastNumber += 1; lastNumber } // mutator
        }
        // constructor executes now:
        println(s"next number: ${Accounts.newUniqueNumber()}")
    }

    // companion objects
    def companionObjects = {
        // class and objects share the same name;
        // must be defined together, in one source file;
        // can access each other private members

        class Account {
            // n.b. object's members are not in the scope
            val id = Account.newUniqueNumber()
            private var balance = 0.0
            def deposit(amount: Double): Unit = { balance += amount } // mutator
        }
        object Account {
            // only Account class can use this singleton
            private var lastNumber = 0
            private def newUniqueNumber(): Int = { lastNumber += 1; lastNumber } // mutator
        }
    }

    // objects extending a class or trait
    def objectsExtendingAClassOrTrait = {
        // the result is an object of a class that extends ... + own features.
        // e.g. useful application
        abstract class UndoableAction(val description: String) {
            def undo(): Unit
            def redo(): Unit
        }
        // useful default action, need only one of them
        object DoNothingAction extends UndoableAction("Do nothing") {
            override def redo(): Unit = {}
            override def undo(): Unit = {}
        }
        // usage
        val actions = Map("open" -> DoNothingAction, "save" -> DoNothingAction)
    }

    // the apply method
    def theApplyMethod = {
        // 'apply' method as a companion class fabric method
        val a = Array("Mary", "had") // object Array 'apply' method here

        // confusion
        val b = Array(1000) // one element of thousand
        val c = new Array(1000) // 1000 elements of null

        // e.g. you can use only fabric: private constructor
        class Account private (val id: Int, initialBalance: Double) {
            //val id = Account.newUniqueNumber()
            private var balance = initialBalance
            def deposit(amount: Double): Unit = { balance += amount }
        }
        object Account {
            def apply(initialBalance: Double) = new Account(newUniqueNumber(), initialBalance)
            private var lastNumber = 0
            private def newUniqueNumber(): Int = { lastNumber += 1; lastNumber }
        }
        val acct = Account(1000.0)
    }

    // application objects
    def applicationObjects = {
        // 'main' method or App trait?

        object HelloMain {
            def main(args: Array[String]): Unit = {
                args foreach println
            }
        }

        object HelloApp extends App {
            args foreach println
        }
        // scala -Dscala.time HelloApp // check it out
        // App trait extends DelayedInit with compiler cpecial treatment

        // n.b. compiler will be unhappy here:
        // [warn]   Reason: companion contains its own main method, which means no static forwarder can be generated
        // https://www.scala-sbt.org/release/docs/Howto-Logging.html
    }

    // enumerations
    def enumerations = {
        // scala does not have enumerated types, but

        object TrafficLightColor extends Enumeration {
            val Red, Yellow, Green = Value // three Value objects created with default (id, name)
            // Value is an inner class
            // id 0..maxint; name = field name
            val Red2 = Value(0, "Stop")
            val Yellow2 = Value(10) // name Yellow
            val Green2 = Value("Go") // id 11
        }

        // n.b. the type of enumeration:
        def doWhat(color: TrafficLightColor.Value) = {
            color match {
                case TrafficLightColor.Red => "stop"
                case _ => "stop anyway"
            }
        }

        // you can use alias
        type Light = TrafficLightColor.Value
        def doWhat2(color: Light) = {
            import TrafficLightColor.{Red, Yellow, Green}
            color match {
                case Red => "stop"
                case _ => "stop anyway"
            }
        }

        // useful methods
        TrafficLightColor.values.foreach(v => println(s"id: ${v.id}, name: ${v.toString}"))
        // find by id or name
        println(s"""by id: ${TrafficLightColor(0)}, by name: ${TrafficLightColor.withName("Red")}""")
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
