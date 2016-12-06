import java.io.{IOException, PrintStream}
import javax.swing.JFrame

import scala.collection.{BitSet, BitSetLike, GenSet, Iterable, Set, SetLike, SortedSet}
import scala.collection.generic.{BitSetFactory, GenericSetTemplate}
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

/*
Just like classes, traits can have constructors, made up of field initializations
and other statements in the trait’s body.

These statements are executed during construction of any object incorporating the trait

Constructors execute in the following order:
• The superclass constructor is called first.
• Trait constructors are executed after the superclass constructor but before the class constructor.
• Traits are constructed left-to-right.
• Within each trait, the parents get constructed first.
• If multiple traits share a common parent, and that parent has already been constructed, it is not constructed again.
• After all traits are constructed, the subclass is constructed.

The constructor ordering is the reverse of the linearization of the class.

For example, consider this class:
    class SavingsAccount extends Account with FileLogger with ShortLogger
The constructors execute in the following order:
1. Account (the superclass).
2. Logger (the parent of the first trait).
3. FileLogger (the first trait).
4. ShortLogger (the second trait). Note that its Logger parent has already been constructed.
5. SavingsAccount (the class).

The linearization is a technical specification of all supertypes of a type.
The linearization gives the order in which super is resolved in a trait

the rule:
If C extends C1 with C2 with . . . with Cn,
then lin(C) = C » lin(Cn) » . . . » lin(C2) » lin(C1)
Here, » means “concatenate and remove duplicates, with the right winning out.”

For example, lin(SavingsAccount)
= SavingsAccount » lin(ShortLogger) » lin(FileLogger) » lin(Account)
= SavingsAccount » (ShortLogger » Logger) » (FileLogger » Logger) » lin(Account)
= SavingsAccount » ShortLogger » FileLogger » Logger » Account.
 */

// 10.11 Initializing Trait Fields

/*
Traits cannot have constructor parameters. Every trait has a single parameterless constructor

Interestingly, the absence of constructor parameters is the only technical difference
between traits and classes

This limitation can be a problem for traits that need some customization to be useful
 */
{
    trait Logger {
        def log(msg: String) // This method is abstract
    }
    class SavingsAccount extends Account with Logger {
        override def log(msg: String): Unit = {}
    }

    //The FileLogger can have an abstract field for the filename.
    trait FileLogger extends Logger {
        val filename: String
        val out = new PrintStream(filename)
        def log(msg: String) { out.println(msg); out.flush() }
    }

    // A class using this trait can override the filename field.
    // Unfortunately, there is a pitfall.
    // The straightforward approach does not work:
    val acct = new SavingsAccount with FileLogger {
        val filename = "myapp.log" // Does not work
    }
    // The problem is the construction order.
    // The FileLogger constructor runs before the subclass constructor

    // One remedy is an obscure feature that we described in Chapter 8: early definition.
    // Here is the correct version:
    val acct2 = new { // Early definition block after new
        val filename = "myapp.log"
    } with SavingsAccount with FileLogger
    // It’s not pretty, but it solves our problem.

    // If you need to do the same in a class, the syntax looks like this:
    class SavingsAccount2 extends { // Early definition block after extends
        val filename = "savings.log"
    } with Account with FileLogger {
        {} // SavingsAccount implementation
    }

    // Another alternative is to use a lazy value in the FileLogger constructor, like this:
    trait FileLogger2 extends Logger {
        val filename: String
        lazy val out = new PrintStream(filename)
        def log(msg: String) { out.println(msg) }
    }
    // However, lazy values are somewhat inefficient since they are
    // checked for initialization before every use.
}

// 10.12 Traits Extending Classes

/*
As you have seen, a trait can extend another trait, and it is common to have a hierarchy of traits.
Less commonly, a trait can also extend a class.
That class becomes a superclass of any class mixing in the trait.
 */
{
    trait Logged { def log(msg: String) { } }

    // The LoggedException trait extends the Exception class:
    trait LoggedException extends Exception with Logged {
        def log() { log(getMessage()) }
    }

    // Now let’s form a class that mixes in this trait:
    class UnhappyException extends LoggedException { // This class extends a trait
        override def getMessage() = "arggh!"
    }
    // Exception is a superclass
    // The Superclass of a trait becomes the superclass of any class mixing in the trait

    // What if our class already extends another class?
    // That’s OK, as long as it’s a subclass of the trait’s superclass.
    // For example,
    class UnhappyException2 extends IOException with LoggedException
    // Here UnhappyException extends IOException, which already extends Exception.

    // However, if our class extends an unrelated class,
    // then it is not possible to mix in the trait.
    // For example, you cannot form the following class:
    class UnhappyFrame extends JFrame with LoggedException
    // Error: Unrelated superclasses
    // It would be impossible to add both JFrame and Exception as superclasses.
}

// 10.13 Self Types

/*
When a trait extends a class, there is a guarantee that the superclass is present
in any class mixing in the trait.
Scala has an alternate mechanism for guaranteeing this: self types.

When a trait starts out with
    this: Type =>
then it can only be mixed into a subclass of the given type.
 */
{
    trait Logged { def log(msg: String) { } }

    trait LoggedException extends Logged {
        this: Exception => // type of 'this' must be Exception

        def log() { log(getMessage()) }
    }
    // Note that the trait does not extend the Exception class.
    // Instead, it has a self type of Exception.
    // That means, it can only be mixed into subclasses of Exception.

    // A trait with a self type is similar to a trait with a supertype.
    // In both cases, it is ensured that a type is present in a class that mixes in the trait

    // There are a few situations where the self type notation is more flexible
    // than traits with supertypes
    // -- Self types can handle circular dependencies between traits.
    //  This can happen if you have two traits that need each other.
    // -- Self types can also handle structural types

    // structural types — types that merely specify the methods that a class must have,
    // without naming the class.
    // Here is the LoggedException using a structural type:
    trait LoggedException2 extends Logged {
        this: { def getMessage() : String } => // type of 'this' must have 'getMessage'

        def log() { log(getMessage()) }
    }
    // The trait can be mixed into any class that has a getMessage method.
}

// 10.14 What Happens under the Hood

/*
A trait that has only abstract methods is simply turned into a Java interface.

If a trait has concrete methods, a companion class is created
whose static methods hold the code of the trait’s methods.

Fields in traits yield abstract getters and setters in the interface.

If a trait extends a superclass, the companion class does not inherit that superclass.
Instead, any class implementing the trait extends the superclass.
 */

//A trait that has only abstract methods is simply turned into a Java interface. For example,
{
    trait Logger {
        def log(msg: String)
    }
//    turns into
//        public interface Logger {
//        void log (String msg);
//    }
}

//If a trait has concrete methods, a companion class is created
// whose static methods hold the code of the trait’s methods. For example,
{
    trait Logger {
        def log(msg: String)
    }
    trait ConsoleLogger extends Logger {
        def log(msg: String) { println(msg) }
    }
//turns into
//  public interface ConsoleLogger extends Logger { // Generated Java interface
//      void log(String msg);
//  }
//  public class ConsoleLogger$class {
//      public static void log (ConsoleLogger self, String msg) {
//          println(msg);
//  }
}

//These companion classes don’t have any fields.
// Fields in traits yield abstract getters and setters in the
// interface. When a class implements the trait, the fields are added to that class
//For example,
{
    trait Logger {
        def log(msg: String)
    }
    trait ShortLogger extends Logger {
        val maxLength = 15 // A concrete field
    }
//is translated to
//    public interface ShortLogger extends Logger{
//        public abstract int maxLength();
//        public abstract void weird_prefix$maxLength_$eq(int);
//    }

//The weird setter is needed to initialize the field.
//This happens in an initialization method of the companion class:
//    public class ShortLogger$class {
//        public void $init$(ShortLogger self) {
//            self.weird_prefix$maxLength_$eq(15)
//        }
//    }
// When the trait is mixed into a class, the class gets a maxLength field
// with a getter and setter. The constructors of that class will call the initialization method.
}

// Exercises

/*
1. The java.awt.Rectangle class has useful methods 'translate' and 'grow'
that are unfortunately absent from classes such as java.awt.geom.Ellipse2D.
In Scala, you can fix this problem.
Define a trait 'RectangleLike' with concrete methods 'translate' and 'grow'.
Provide any abstract methods that you need for the implementation, so that you can mix in
the trait like this:

val egg = new java.awt.geom.Ellipse2D.Double(5, 10, 20, 30) with RectangleLike
egg.translate(10, -10)
egg.grow(10, 20)
 */
{
    import java.awt.{geom, Rectangle}

    trait RectangleLike {
        // self type: structural type
        this: {
            def setFrame(r: geom.Rectangle2D): Unit
            def getFrame(): geom.Rectangle2D
        } =>

        def translate(dx: Int, dy: Int): Unit = {
            val rct = new Rectangle()
            rct.setFrame(getFrame)
            rct.translate(dx, dy)
            setFrame(rct.getFrame)
        }

        def grow(h: Int, v: Int): Unit = {
            val rct = new Rectangle()
            rct.setFrame(getFrame)
            rct.grow(h, v)
            setFrame(rct.getFrame)
        }
    }

    val egg = new geom.Ellipse2D.Double(5, 10, 20, 30) with RectangleLike
    egg.translate(10, -10)
    egg.grow(10, 20)
}

/*
2. Define a class 'OrderedPoint'
by mixing scala.math.Ordered[Point] into java.awt.Point.
Use lexicographic
ordering, i.e. (x, y) < (x’, y’) if x < x’ or x = x’ and y < y’.
 */
{
    import java.awt.{Point}

    class OrderedPoint extends Point with scala.math.Ordered[Point] {
        override def compare(that: Point): Int = {
            val cx = this.x.compare(that.x)
            if (cx == 0) this.y.compare(that.y) else cx
        }
    }
}

/*
3. Look at the 'BitSet' class,
and make a diagram of all its superclasses and traits.
Ignore the type parameters (everything inside the [...]).
Then give the linearization of the traits.

The linearization is a technical specification of all supertypes of a type.
The linearization gives the order in which super is resolved in a trait

the rule: If
    C extends C1 with C2 with . . . with Cn,
then
    lin(C) = C » lin(Cn) » . . . » lin(C2) » lin(C1)
Here, » means “concatenate and remove duplicates, with the right winning out.”

trait BitSet extends SortedSet[Int] with BitSetLike[BitSet]
lin(BitSet) = BitSet >> lin(BitSetLike) >> lin(SortedSet)

trait SortedSet[A] extends Set[A] with SortedSetLike[A, SortedSet[A]]
lin(SortedSet) = SortedSet >> lin(SortedSetLike) >> lin(Set)

trait Set[A] extends (A => Boolean)
    with Iterable[A]
    with GenSet[A]
    with GenericSetTemplate[A, Set]
    with SetLike[A, Set[A]]
lin(Set) = Set >> lin(SetLike) >> lin(GenericSetTemplate) >> lin(GenSet) >> lin(Iterable) >> lin(A => Boolean)

lin(A => Boolean) = A => Boolean

trait Iterable[+A] extends Traversable[A]
    with GenIterable[A]
    with GenericTraversableTemplate[A, Iterable]
    with IterableLike[A, Iterable[A]]
lin(Iterable) = Iterable >> lin(IterableLike) >> lin(GenericTraversableTemplate)
    >> lin(GenIterable) >> lin(Traversable)

trait Traversable[+A] extends TraversableLike[A, Traversable[A]]
    with GenTraversable[A]
    with TraversableOnce[A]
    with GenericTraversableTemplate[A, Traversable]
lin(Traversable) = Traversable >> lin(GenericTraversableTemplate) >> lin(TraversableOnce)
    >> lin(GenTraversable) >> lin(TraversableLike)

trait TraversableLike[+A, +Repr] extends Any
    with HasNewBuilder[A, Repr]
    with FilterMonadic[A, Repr]
    with TraversableOnce[A]
    with GenTraversableLike[A, Repr]
    with Parallelizable[A, ParIterable[A]]
lin(TraversableLike) = TraversableLike >> lin(Parallelizable) >> lin(GenTraversableLike)
    >> lin(TraversableOnce) >> lin(FilterMonadic) >> lin(HasNewBuilder) >> lin(Any)

 */
