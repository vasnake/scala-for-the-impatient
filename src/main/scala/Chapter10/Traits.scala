package Chapter10

object Traits {
// topics:
    // why no multiple inheritance?
    // traits as interfaces
    // traits with concrete implementations
    // objects with traits
    // layered traits
    // overriding abstract methods in traits
    // traits for rich interfaces
    // concrete fields in traits
    // abstract fields in traits
    // trait construction order
    // initializing trait fields
    // traits extending classes
    // self types
    // what happens under the hood

    // class extends one or more traits;
    // traits can supply state and behaviour;
    // traits can require (implementing) classes to have certain features;
    // the order matters -- first execute the trait on the back;

    // prepare stage, some base classes
    class Account { var balance = 0.0 }
    trait Logger { def log(msg: String): Unit } // abstract
    trait ConsoleLogger extends Logger { def log(msg: String): Unit = println(msg) } // not abstract, implementation provided

    // why no multiple inheritance?
    def whyNoMultipleInheritance = {
        // if you combine classes with common features, you have a lot of mess;
        // what feature from what class override other?

        // diamond inheritance problem:
        //  person: name; student extends person; employee extends person; assistant extends student, employee

        // jvm: only one superclass; any number of interfaces;
        // interfaces: abstract, static, default methods; no fields;

        // traits can have methods and fields, like classes, but no constructor parameters;
        // class can mix-in multiple traits;
        // trait fields will be placed in a class;
    }

    // traits as interfaces
    def traitsAsInterfaces = {

        //interface
        trait Logger {
            def log(msg: String): Unit // abstract
        }

        //implementation
        class ConsoleLogger extends Logger {
            def log(msg: String): Unit = println(msg)
        }
        // n.b. no override for abstract members

        // add other traits using 'with'
        class ConsoleLogger2 extends
            Logger with Cloneable with Serializable {
            def log(msg: String): Unit = println(msg)
        }
        // n.b. all java interfaces can be used as traits

        // n.b. class extends construct 'Logger with Cloneable with Serializable'
    }

    // traits with concrete implementations
    def traitsWithConcreteImplementations = {
        // not abstract members of a trait

        trait ConsoleLogger {
            def log(msg: String): Unit = println(msg) // not abstract, implementation provided
        }

        // usage example
        class SavingsAccount extends
            Account with ConsoleLogger {

            def withdraw(amount: Double) = {
                if (amount > balance) log("Insufficient funds") // from trait, mixed in
                else balance -= amount
            }
        }
        class Account { var balance = 0.0 }
    }

    // objects with traits
    def objectsWithTraits = {
        // you can add trait to an any class instance

        // abstract too, because of logger
        abstract class SavingsAccount extends
            Account with Logger {

            def withdraw(amount: Double) = {
                if (amount > balance) log("Insufficient funds") // abstract 'log'
                else balance -= amount
            }
        }

        // but, you can mix in implementation when constructing an object
        val acct = new SavingsAccount with ConsoleLogger
        // dependency injection?
    }

    // layered traits
    def layeredTraits = {
        // traits that invoke each other via 'super'

        // prep stage
        trait Logger { def log(msg: String): Unit } // abstract
        trait ConsoleLogger { def log(msg: String): Unit = println(msg) } // not abstract, implementation provided
        abstract class SavingsAccount extends Account with Logger {
            def withdraw(amount: Double) = { if (amount > balance) log("Insufficient funds") else balance -= amount }
        }

        trait TimestampLogger extends ConsoleLogger {
            // call super
            override def log(msg: String): Unit = super.log(s"${java.time.Instant.now} $msg")
        }
        trait ShortLogger extends ConsoleLogger {
            // call super
            override def log(msg: String): Unit = super.log(if (msg.length <= 15) msg else s"${msg.take(15)}")
        }

        // you can't be sure that super will be ConsoleLogger,
        // only object construction order can give an answer
        // back to front rule
        val acct1 = new SavingsAccount with TimestampLogger with ShortLogger
        // acc.log calls shortlogger.log calls timestamplogger.log calls consolelogger.log
        val acct2 = new SavingsAccount with ShortLogger with TimestampLogger
        // acc.log calls timestamplogger.log calls shortlogger.log calls consolelogger.log

        // you can control which trait's method is invoked
        trait TimestampConsoleLogger extends ConsoleLogger {
            // call super only from ConsoleLogger
            override def log(msg: String): Unit = super[ConsoleLogger].log(s"${java.time.Instant.now} $msg")
            // ConsoleLogger must be immediate supertype
        }
    }

    // overriding abstract methods in traits
    def overridingAbstractMethodsInTraits = {
        // one trait override another trait abstract member

        trait Logger {
            def log(msg: String): Unit // abstract
        }

//        trait TimestampLogger extends Logger {
//            // call abstract super, compile error, should be marked as abstract
//            override def log(msg: String): Unit = super.log(s"${java.time.Instant.now} $msg")
//        }

        trait TimestampLogger extends Logger {
            // call abstract super => abstract
            abstract override def log(msg: String): Unit = super.log(s"${java.time.Instant.now} $msg")
        }

    }

    // traits for rich interfaces
    def traitsForRichInterfaces = {
        // many utility methods that depend on a few abstract, e.g. Iterator trait;
        // very common in scala

        trait Logger {
            def log(msg: String): Unit // abstract

            def info(msg: String) = log(s"INFO: ${msg}")
            def warn(msg: String) = log(s"WARN: ${msg}")
            def severe(msg: String) = log(s"SEVERE: ${msg}")
        }

        // usage
        abstract class SavingsAccount extends Account with Logger {
            def withdraw(amount: Double) = {
                if (amount > balance) severe("Insufficient funds")
                else balance -= amount
            }
        }
        class Account { var balance = 0.0 }

    }

    // concrete fields in traits
    def concreteFieldsInTraits = {
        // field with initial value: concrete

        trait ShortLogger extends Logger {
            val maxLength = 15 // a concrete field

            // call abstract super
            abstract override def log(msg: String): Unit = super.log(
                if (msg.length <= maxLength) msg
                else s"${msg.take(maxLength)}")
        }

        // a class that mixes in this trait gets a field, not inherited but added to subclass
        // consequence: if trait changes, all classes that mix in that trait must be recompiled!

        // superclass: balance
        // subclass: interest, maxLength
        class SavingsAccount extends Account with ConsoleLogger with ShortLogger {
            var interest = 0.0
            def withdraw(amount: Double) = {
                if (amount > balance) log("Insufficient funds")
                else balance -= amount
            }
        }

    }

    // abstract fields in traits
    def abstractFieldsInTraits = {
        // uninitialized, must be overridden

        trait Logger {
            def log(msg: String): Unit // abstract
        }
        trait ShortLogger extends Logger {
            val maxLength: Int // abstract
            abstract override def log(msg: String): Unit = super.log(
                if (msg.length <= maxLength) msg
                else s"${msg.take(maxLength)}")
        }

        // must supply maxLength field
        class SavingsAccount extends Account with ConsoleLogger with ShortLogger {
            val maxLength: Int = 20 // no override necessary
        }
        class Account { var balance = 0.0 }
        trait ConsoleLogger extends Logger { def log(msg: String): Unit = println(msg) }

        // handy when constructing objects on the fly
        abstract class SavingsAccount2 extends Account with Logger { }
        val acct = new SavingsAccount2 with ConsoleLogger with ShortLogger { override val maxLength: Int = 20 }
    }

    // trait construction order
    def traitConstructionOrder = {

        //example
        trait Logger { def log(msg: String): Unit }
        trait FileLogger extends Logger {
            // constructor
            val out = new java.io.PrintWriter("/tmp/app.log")
            out.println(s"# ${java.time.Instant.now}")
            def log(msg: String): Unit = { out.println(msg); out.flush() }
        }
        // executed during construction of any object incorporating the trait

        // constructors execution order:
        //  superclass
        //  traits left-to-right
        //  within each trait, parents constructed first (each parent only once)
        //  subclass

        // e.g.
        // class SavingsAccount extends Account with FileLogger with ShortLogger
        //  Account as superclass
        //  Logger as parent of FileLogger
        //  FileLogger
        //  ShortLogger w/o Logger
        //  SavingsAccount

        // constructor ordering is the _reverse_ of the linearization

        // linearization of the class: tech spec of all superclasses, defined by rule:
        // if C extends C1 with C2 with ... Cn,
        // then lin(C) = C >> lin(Cn) >> ... >> lin(C2) >> lin(C1)
        // where '>>' means "concatenate and remove duplicates, with the right winning out"
        // e.g. lin(SavingsAccount)
        //  = SavingsAccount >> lin(ShortLogger) >> lin(FileLogger) >> lin(Account)
        //  = SavingsAcount >> (ShortLogger >> Logger) >> (FileLogger >> Logger) >> Account
        //  = SavingsAcount >> ShortLogger >> FileLogger >> Logger >> Account

        // linearization gives the order (left-to-right) in which 'super' is resolved in a _trait_

    }

    // initializing trait fields
    def initializingTraitFields = {
        // traits cannot have constructor parameters

        class SavingsAccount { }
        trait Logger { def log(msg: String): Unit }

        trait FileLogger extends Logger {
            // but we would like to specify the log file:
            val out = new java.io.PrintWriter("/tmp/app.log")
            out.println(s"# ${java.time.Instant.now}")
            def log(msg: String): Unit = { out.println(msg); out.flush() }
        }
        // what should we do?

        // abstract field with later overriding?
        trait FileLogger2 extends Logger {
            val filename: String
            val out = new java.io.PrintWriter(filename)
            def log(msg: String): Unit = { out.println(msg); out.flush() }
        }
        val acct = new SavingsAccount with FileLogger {
            val filename = "/tmp/app.log" // no, does not work
            // problem: construction order, FileLogger before this structural type
        }

        // use early definition (structural type):
        val acct2 = new { val filename = "/tmp/app.log" } with SavingsAccount with FileLogger

        // or, use early definition (class)
        class SavingsAccount2 extends { val filename = "/tmp/oops.log" } with SavingsAccount with FileLogger { }

        // or, use lazy value for file in a trait along with abstract filename
        trait FileLogger3 extends Logger {
            val filename: String
            lazy val out = new java.io.PrintWriter(filename) // on first call
            def log(msg: String): Unit = { out.println(msg); out.flush() }
        } // lazy add some overhead, may be ineffective
        val acct3 = new SavingsAccount with FileLogger3 {
            val filename = "/tmp/app.log" // works just fine with lazy file handler initialization
        }

    }

    // traits extending classes
    def traitsExtendingClasses = {
        // hierarchy of traits: it's common and normal;
        // not so common: trait extending a class

        // that class becomes a superclass of any class mixing it the trait

        trait LoggedException extends
            Exception with ConsoleLogger {
            // Exception will be a superclass for any user of this trait
            def log(): Unit = log(getMessage)
        }

        // class with implicit superclass
        class UnhappyException extends LoggedException { override def getMessage: String = "arggh!" }

        // it's possible for user to extend another class
        // as long as it's a subclass of Exception
        class UnhappyException2 extends java.io.IOException with LoggedException {
            override def getMessage: String = "arggh!" }

        // other extensions is not possible (only one superclass in jvm)
        // class UnhappyException3 extends javax.swing.JFrame with LoggedException // no way


        trait Logger { def log(msg: String): Unit }
        trait ConsoleLogger { def log(msg: String) = println(msg) }
    }

    // self types
    def selfTypes = {
        // if trait extends a class, there is a
        // guarantee that superclass members are available, services is present

        // alternate mechanism for guaranteeing this: self type

        trait LoggedException extends ConsoleLogger {
            this: Exception => // self type of Exception, trait can only be mixed into Exception subtype

            def log(): Unit = { log(getMessage) }
        }

        // self type notation is more flexible
        // can handle circular dependencies but, more important:
        // can handle structural types
        trait LoggedException2 extends ConsoleLogger {
            this: { def getMessage: String } => // mix with any class that have getMessage

            def log(): Unit = { log(getMessage) }
        }


        trait Logger { def log(msg: String): Unit }
        trait ConsoleLogger { def log(msg: String) = println(msg) }
    }

    // what happens under the hood
    def whatHappensUnderTheHood = {
        // after all, classes and traits become classes and interfaces in jvm.

        // trait turned into java interface;
        // trait methods become interface default methods;
        // trait fields: interface has abstract getter and setter (and 'init');
        //  class gets a field and getter/setter implementation; constructor invokes init of the trait;
        // if trait extends a superclass, class gets this superclass;
    }

}

object Traits_Exercises {

    // 1. The java.awt.Rectangle class has useful methods translate and grow that are
    // unfortunately absent from classes such as java.awt.geom.Ellipse2D.
    // In Scala, you can fix this problem.
    // Define a trait RectangleLike with concrete methods translate and grow.
    // Provide any abstract methods that you need for the implementation, so
    // that you can mix in the trait like this:
    //  val egg = new java.awt.geom.Ellipse2D.Double(5, 10, 20, 30) with RectangleLike
    //  egg.translate(10, -10)
    //  egg.grow(10, 20)
    def ex1 = {
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

    // 2. Define a class OrderedPoint by mixing scala.math.Ordered[Point] into
    // java.awt.Point. Use lexicographic ordering, i.e.
    // (x, y) < (x’, y’) if x < x’ or x = x’ and y < y’.
    def ex2 = {
        import java.awt.Point

        class OrderedPoint extends
            Point with scala.math.Ordered[Point] {

            override def compare(that: Point): Int = {
                val cx = this.x.compare(that.x)
                if (cx == 0) this.y.compare(that.y) else cx
            }
        }
    }

    // 3. Look at the BitSet class, and make a diagram of all its superclasses and traits.
    // Ignore the type parameters (everything inside the [...]).
    // Then give the linearization of the traits.
    def ex3 = {
        /*
trait BitSet extends
    SortedSet[Int] with BitSetLike[BitSet]

lin(BitSet) = BitSet
    >> lin(BitSetLike)
    >> lin(SortedSet)

It was a hell of a job, very tedious.
Don't do it if you don't interested in stdlib internals

lin(BitSet) = BitSet
    >> BitSetLike
    >> SortedSet
    >> SortedSetLike
    >> Sorted
    >> Set
    >> SetLike
    >> Subtractable
    >> GenSet
    >> GenericSetTemplate
    >> GenSetLike
    >> Iterable
    >> IterableLike
    >> Equals
    >> GenIterable
    >> GenIterableLike
    >> Traversable
    >> GenTraversable
    >> GenericTraversableTemplate
    >> TraversableLike
    >> GenTraversableLike
    >> Parallelizable
    >> TraversableOnce
    >> GenTraversableOnce
    >> FilterMonadic
    >> HasNewBuilder >> Any
    >> A => Boolean
         */
    }

    // 4. Provide a CryptoLogger trait that encrypts the log messages with the Caesar cipher.
    // The key should be 3 by default, but it should be overridable by the user.
    // Provide usage examples with the default key and a key of –3.
    def ex4 = {
        // http://www.rosettacode.org/wiki/Caesar_cipher#Scala

        // abstract logger
        trait Logger { def log(msg: String): Unit }
        // simple logger
        trait ConsoleLogger extends Logger { def log(msg: String) = println(msg) }

        trait CryptoLogger extends Logger {
            abstract override def log(msg: String): Unit = {
                super.log(encode(msg, key))
            }

            protected def key = 3 // default key
            // alphabet
            private val lower = 'a' to 'z'
            private val upper = 'A' to 'Z'

            private def encode(msg: String, key: Int) = msg.map {
                case a if lower.contains(a) => rotate(lower, a, key)
                case b if upper.contains(b) => rotate(upper, b, key)
                case char => char
            }

            private def rotate(abc: Seq[Char], char: Char, key: Int) = {
                val idx = (char - abc.head + key + abc.size) % abc.size
                abc(idx)
            }
        }

        // test
        class Test extends
            ConsoleLogger with CryptoLogger {

            def test(msg: String) = { log(msg) }
        }

        val one = new Test
        one.test("The five boxing wizards jump quickly")

        val two = new Test { override val key = -3 }
        two.test("Wkh ilyh eralqj zlcdugv mxps txlfnob")
    }

    // 5. The JavaBeans specification has the notion of a 'property change listener',
    // a standardized way for beans to communicate changes in their properties.
    // The PropertyChangeSupport class is provided as a convenience superclass for any bean
    // that wishes to support property change listeners.
    // Unfortunately, a class that already has another superclass — such as JComponent —
    // must reimplement the methods.
    // Reimplement PropertyChangeSupport as a trait, and mix it into the java.awt.Point class.
    def ex5 = {
        import java.beans.{PropertyChangeSupport, PropertyChangeListener, PropertyChangeEvent}
        import java.awt.Point

        trait PropertyChangeSupportTrait {
            val pcs = new PropertyChangeSupport(this)

            def addPropertyChangeListener(propertyName: String, listener: PropertyChangeListener): Unit =
                pcs.addPropertyChangeListener(propertyName, listener)
        }

        class PointWithMonitoring extends
            Point with PropertyChangeSupportTrait {

            override def setLocation(x: Double, y: Double): Unit = {
                pcs.firePropertyChange("setLocation", (getX, getY), (x, y))
                super.setLocation(x, y)
            }
        }

        // test

        class SimpleListener extends PropertyChangeListener {
            override def propertyChange(evt: PropertyChangeEvent): Unit =
                println(s"propertyChange: ${evt.getSource.getClass}, ${evt.getPropertyName} " +
                    s"${evt.getOldValue} => ${evt.getNewValue}")
        }

        val point = new PointWithMonitoring
        point.addPropertyChangeListener("setLocation", new SimpleListener)
        point.setLocation(1d, 2d)
    }

    // 6. In the Java AWT library, we have a class Container, a subclass of Component that
    // collects multiple components.
    // For example, a Button is a Component, but a Panel is a Container.
    // That’s the composite pattern at work.
    // Swing has JComponent and JButton, but if you look closely, you will notice something strange.
    // JComponent extends Container, even though it makes no sense to add other components to, say, a JButton.
    // Ideally, the Swing designers would have preferred the design in Figure 10–4.
    // But that’s not possible in Java.
    // Explain why not. How could the design be executed in Scala with traits?
    def ex6 = {
        // JContainer on a diagram extends two classes: JComponent and Container;
        // JComponent and Container both derived from Component.
        // Evidently, Component and Container should be implemented as traits.
        // Or, JComponent could be made as a trait.
    }

    // 7. Construct an example where a class needs to be recompiled when one of the mixins changes.
    // Start with class SavingsAccount extends Account with ConsoleLogger.
    // Put each class and trait in a separate source file.
    // Add a field to Account. In Main (also in a separate source file),
    // construct a SavingsAccount and access the new field.
    // Recompile all files except for SavingsAccount and verify that the program works.
    // Now add a field to ConsoleLogger and access it in Main.
    // Again, recompile all files except for SavingsAccount. What happens? Why?
    def ex7 = {

        def before = {
            // logger src file
            trait Logger { def log(msg: String): Unit }
            trait ConsoleLogger { def log(msg: String): Unit = println(msg) }
            // account src file
            class Account { var balance = 0.0 }
            class SavingsAccount extends
                Account with ConsoleLogger {
                def withdraw(amount: Double) = {
                    if (amount > balance) log("Insufficient funds")
                    else balance -= amount
                }
            }
            // main src file
            val acct = new SavingsAccount
            println(s"${acct.balance}")
        }

        def after = {
            // logger src file
            trait Logger { def log(msg: String): Unit }
            trait ConsoleLogger {
                def log(msg: String): Unit = println(msg)
                var count = 0
            }
            // account src file
            class Account { var balance = 0.0 }
            class SavingsAccount extends
                Account with ConsoleLogger {
                def withdraw(amount: Double) = {
                    if (amount > balance) log("Insufficient funds")
                    else balance -= amount
                }
            }
            // main src file
            val acct = new SavingsAccount
            println(s"${acct.balance}")
            println(s"${acct.count}") // field added to SavingsAccount only after recompile
            // fields from traits go to classes that mix in that traits
        }
    }

    // 8. There are dozens of Scala trait tutorials with silly examples of barking dogs or philosophizing frogs.
    // Reading through contrived hierarchies can be tedious and not very helpful, but designing
    // your own is very illuminating.
    // Make your own silly trait hierarchy example that demonstrates
    // layered traits,
    // concrete and abstract methods,
    // and concrete and abstract fields.
    def ex8 = {
        trait FooBase {
            val name: String
            val id = "FooBase"
            def out(msg: String): Unit
            def debugPrint(msg: String = ""): Unit = out(s"${java.time.Instant.now.toString}, " +
                s"id: ${id}, name: ${name}, ext: '${msg}'")
        }
        trait Foo extends FooBase {
            val name = "foo"
            val description = "fffffooooooo"
            override val id: String = "Foo"
            override def debugPrint(msg: String = ""): Unit = super.debugPrint(
                s"description: ${description}, msg: ${msg}")
        }

        val foo = new { override val name = "bar" } with Foo {
            override def out(msg: String): Unit = print(msg)
        }
        foo.debugPrint("baz")
        //2019-02-14T11:07:12.949Z, id: Foo, name: bar, ext: 'description: fffffooooooo, msg: baz'
    }

    // 9. In the java.io library, you add buffering to an input stream with a BufferedInputStream decorator.
    // Reimplement buffering as a trait.
    // For simplicity, override the read method.
    def ex9 = {
        import java.io.{FileInputStream, BufferedInputStream, InputStream}
        trait BufferedIS { this: InputStream =>
            val buff = new BufferedInputStream(this)
            override def read(b: Array[Byte]): Int = buff.read(b)
            override def mark(readlimit: Int): Unit = buff.mark(readlimit)
            override def markSupported(): Boolean = buff.markSupported()
            override def reset(): Unit = buff.reset()
        }

        val file = new FileInputStream("/tmp/test.txt") with BufferedIS
        val buff = Array.ofDim[Byte](1024)
        file.mark(3)
        file.read(buff); println(buff.map(_.toChar).mkString)
        file.reset()
        file.read(buff); println(buff.map(_.toChar).mkString)
        file.close()
    }

    // 10. Using the logger traits from this chapter, add logging to the solution of the preceding problem
    // that demonstrates buffering.
    def ex10 = {

        import java.io.{FileInputStream, BufferedInputStream, InputStream}
        trait Logger { def log(msg: String): Unit }
        trait ConsoleLogger extends Logger { def log(msg: String): Unit = println(msg) }

        trait BufferedIS extends Logger { this: InputStream =>
            val buff = {log(s"new buffer for ${this}"); new BufferedInputStream(this)}
            override def read(): Int = {
                log(s"(buf bytes - stream bytes): ${buff.available - this.available}")
                buff.read()
            }
        }

        val file = new FileInputStream("/tmp/test.txt") with BufferedIS with ConsoleLogger
        val chars = Iterator.continually(file.read).takeWhile(b => b >= 0).map(_.toChar)
        println(chars.mkString)
        file.close()
    }

    // 11. Implement a class IterableInputStream that extends java.io.InputStream with the trait Iterable[Byte].
    def ex11 = {

        import java.io.{InputStream, FileInputStream}

        class IterableInputStream(is: InputStream) extends
            InputStream with Iterable[Byte] {

            override def read(): Int = is.read()
            override def iterator: Iterator[Byte] = Iterator.continually(read().toByte).takeWhile(_ >= 0)

//            override def iterator: Iterator[Byte] = new Iterator[Byte] {
//                override def hasNext: Boolean = is.available > 0
//                override def next(): Byte = read().toByte
//            }

        }

        // in REPL you have to use lazy iterator, or it will be evaluated immediately
        lazy val file = new IterableInputStream(new FileInputStream("/tmp/test.txt"))
        val chars = file.iterator.map(_.toChar)
        println(chars.mkString)
    }

    // 12. Using javap -c -private, analyze how the call super.log(msg) is translated to
    //Java. How does the same call invoke two different methods, depending on the mixin order?
    def ex12 = {
        ???
    }

}
