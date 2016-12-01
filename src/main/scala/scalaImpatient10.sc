// Scala for the Impatient
// chapter 10: Traits

/*
A class can implement any number of traits.
• Traits can require that implementing classes have certain fields, methods, or superclasses.
• Unlike Java interfaces, a Scala trait can provide implementations of methods and fields.
• When you layer multiple traits, the order matters—the trait whose methods execute first goes to the back.
 */

// some preparations
class Account {
    var balance: Double = 0.0
}

// 10.1 Why No Multiple Inheritance?

/*
Scala, like Java, does not allow a class to inherit from multiple superclasses.
Multiple inheritance works fine when you combine classes that have nothing in common.
Java: A class can extend only one superclass; it can implement any number of interfaces,
but interfaces can have only abstract methods and no fields.
Scala: has traits instead of interfaces.
A trait can have both abstract and concrete methods, and a class can implement multiple traits.
 */

// 10.2 Traits as Interfaces

{
    trait Logger {
        def log(msg: String) // An abstract method
    }
//Note that you need not declare the method as abstract —
// an unimplemented method in a trait is automatically abstract.

//    A subclass can provide an implementation:
    class ConsoleLogger extends Logger { // Use extends, not implements
        def log(msg: String) { println(msg) } // No override needed
    }

//    If you need more than one trait, add the others using the with keyword:
//    In Scala, 'Logger with Cloneable with Serializable' is the entity that the class extends
    class ConsoleLogger2 extends Logger with Cloneable with Serializable {
        def log(msg: String) { println(msg) }
    }
//  All Java interfaces can be used as Scala traits.
}

// 10.3 Traits with Concrete Implementations

/*
In Scala, the methods of a trait need not be abstract.
There is one disadvantage of having traits with concrete behavior. When a trait changes, all
classes that mix in that trait must be recompiled.
 */
{
    trait ConsoleLogger {
        def log(msg: String) { println(msg) }
    }

    // Here is an example of using this trait
    class SavingsAccount extends Account with ConsoleLogger {
        def withdraw(amount: Double) {
            if (amount > balance) log("Insufficient funds")
            else balance -= amount
        }
        // ...
    }
// In Scala (and other programming languages that allow this),
// we say that the ConsoleLogger functionality
// is “mixed in” with the SavingsAccount class.
}

// 10.4 Objects with Traits

/*
You can add a trait to an individual object when you construct it.
 */
{
    trait Logged { // do nothing
        def log(msg: String) { }
    }
//    Let’s use that trait in a class definition:
    class SavingsAccount extends Account with Logged {
        def withdraw(amount: Double) {
            if (amount > balance) log("Insufficient funds")

        }
        // ...
    }
// Now, nothing gets logged, which might seem pointless.
// But you can “mix in” a better logger when constructing an object.
    trait ConsoleLogger extends Logged {
        override def log(msg: String) { println(msg) }
    }
//    You can add this trait when constructing an object:
    val acct = new SavingsAccount with ConsoleLogger

    //    another object can add in a different trait:
    trait FileLogger extends Logged {
        override def log(msg: String): Unit = { }
    }
    val acct2 = new SavingsAccount with FileLogger
}

// 10.5 Layered Traits

/*
You can add multiple traits that invoke each other starting with the last one.
This is useful when you need to transform a value in stages.
Here is a simple example. We may want to add a timestamp to all logging messages.
 */
{
    trait Logged {
        def log(msg: String) { } // do nothing
    }

    trait TimestampLogger extends Logged {
        override def log(msg: String) {
            super.log(new java.util.Date() + " " + msg)
        }
    }

//    Also, suppose we want to truncate the overly chatty log messages
    trait ShortLogger extends Logged {
        val maxLength = 15 // See Section 10.8 on fields in traits
        override def log(msg: String) {
            super.log(
                if (msg.length <= maxLength) msg
                else msg.substring(0, maxLength - 3) + "..."
            )
        }
    }
    /*
With traits, 'super.log' does not have the same meaning as it does with classes.
 ...
super.log calls the next trait in the trait hierarchy, which depends on the order in which the
traits are added
Generally, traits are processed starting with the last one
     */
    // example
    class SavingsAccount extends Account with Logged {
        def withdraw(amount: Double) { if (amount > balance) log("Insufficient funds") } }
    trait ConsoleLogger extends Logged {
        override def log(msg: String) { println(msg) }}

    //    To see why the order matters, compare the following two examples:
    val acct1 = new SavingsAccount with ConsoleLogger with
        TimestampLogger with ShortLogger
    val acct2 = new SavingsAccount with ConsoleLogger with
        ShortLogger with TimestampLogger
//    If we overdraw acct1, we get a message
// Sun Feb 06 17:45:45 ICT 2011 Insufficient...
//    As you can see, the ShortLogger’s log method was called first
//    However, overdrawing acct2 yields
// Sun Feb 06 1...
//    Here, the TimestampLogger appeared last in the list of traits
}
// With traits, you cannot tell from the source code which method is invoked by
// 'super.someMethod'. The exact method depends on the ordering of the traits in the object or class
// that uses them. This makes 'super' far more flexible than in plain old inheritance
//If you need to control which trait’s method is invoked, you can specify it in brackets:
//    super[ConsoleLogger].log(...). The specified type must be an immediate supertype

// 10.6 Overriding Abstract Methods in Traits

{
    trait Logger {
        def log(msg: String) // This method is abstract
    }
    // add layer
    trait TimestampLogger extends Logger {
        override def log(msg: String) { // Overrides an abstract method
            super.log(new java.util.Date() + " " + msg) // Is super.log defined?
        }
    }
    // The compiler flags the call to super.log as an error
    /*
Scala takes the position that TimestampLogger.log is still abstract —
it requires a concrete log method to be mixed in.
You therefore need to tag the method with the 'abstract' keyword and the 'override' keyword,
like this:
abstract override def log(msg: String) { super.log(new java.util.Date() + " " + msg) }
     */
}

// 10.7 Traits for Rich Interfaces

/*
A trait can have many utility methods that depend on a few abstract ones.
One example is the Scala Iterator trait that defines dozens of methods in terms of
the abstract next and hasNext methods.
 */

{
    trait Logger {
        def log(msg: String) // abstract
        def info(msg: String) { log("INFO: " + msg) }
        def warn(msg: String) { log("WARN: " + msg) }
        def severe(msg: String) { log("SEVERE: " + msg) }
    }
    // Note the combination of abstract and concrete methods.

    // A class that uses the Logger trait can now call any of these logging messages, for example:
    class SavingsAccount extends Account with Logger {
        def withdraw(amount: Double) {
            if (amount > balance) severe("Insufficient funds")
        }
        override def log(msg:String) { println(msg); }
    }
}

// 10.8 Concrete Fields in Traits

/*
A field in a trait can be concrete or abstract. If you supply an initial value, the field is concrete.

In general, a class gets a field for each concrete field in one of its traits.
These fields are not inherited; they are simply added to the subclass

In the JVM, a class can only extend one superclass, so the trait fields can’t be inherited in the same
way. Instead, the maxLength field is added to the SavingsAccount class, next to the interest field.
You can think of concrete trait fields as “assembly instructions” for the classes that use the trait. Any
such fields become fields of the class.
 */
{
    trait Logged {
        def log(msg: String) // abstract
    }
    trait ShortLogger extends Logged {
        val maxLength = 15 // A concrete field
    }
    trait ConsoleLogger extends Logged {
        override def log(msg: String) = println(msg)
    }

    class SavingsAccount extends Account with ConsoleLogger with ShortLogger {
        var interest = 0.0
        // Note that our subclass has a field interest. That’s a plain old field in the subclass.
        // Where go 'maxLength'? It's copied to SavingsAccount
        def withdraw(amount: Double) {
            if (amount > balance) log("Insufficient funds")
        }

        // balance
        class Account {
            var balance = 0.0
        }
        // The SavingsAccount class inherits that field in the usual way.
    }
}

// 10.9 Abstract Fields in Traits

/*
An uninitialized field in a trait is abstract and must be overridden in a concrete subclass.
For example, the following maxLength field is abstract:
 */
{
    trait Logged {
        def log(msg: String)
    }
    trait ConsoleLogger extends Logged {
        override def log(msg: String) = println(msg)
    }

    trait ShortLogger extends Logged {
        val maxLength: Int // An abstract field

        override def log(msg: String) {
            super.log(
                if (msg.length <= maxLength) msg
                else msg.substring(0, maxLength - 3) + "...")
            // The maxLength field is used in the implementation
        }
    }

    // When you use this trait in a concrete class, you must supply the maxLength field
    class SavingsAccount extends Account with ConsoleLogger with ShortLogger {
        val maxLength = 20 // No 'override' necessary
    }

    // This way of supplying values for trait parameters is particularly handy
    // when you construct objects on the fly
    val acct = new SavingsAccount with ConsoleLogger with ShortLogger {
        override val maxLength = 20
    }
}

// 10.10 Trait Construction Order
