package Chapter08

object Inheritance {
// topics:
    // extending a class
    // overriding methods
    // type checks and casts
    // protected fields and methods
    // superclass construction
    // overriding fields
    // anonymous subclasses
    // abstract classes
    // abstract fields
    // construction order and early definitions
    // the scala inheritance hierarchy
    // object equality
    // value classes

    // only the PRIMARY constructor can call the PRIMARY superclass constructor;
    // you can override fields;
    // 'final' means can't be overridden or extended (in java final means immutable);

    // extending a class
    def extendingAClass = {
        // keyword: extends

        // final
        class Person {}
        // if final => can't extend or override

        class Employee extends Person {
            var salary = 0.0
        }
    }

    // overriding methods
    def overridingMethods = {
        // modifier: override for non-abstract members

        class Person(val name: String) {
            override def toString: String = s"${getClass.getName} name: $name"
        }

        // override helps with diagnostics:
        // - misspell the name;
        // - provide a wrong param. type;
        // - add a new method in super that clashes with a subclass (fragile base class problem)

        // in java some people declare all methods final to solve "fragile base class"...
        // but @Overrides annotation is better

        // call super:
        class Employee(name: String, val salary: Double) extends Person(name) {
            override def toString: String = super.toString + s"; salary: $salary"
        }
    }

    // type checks and casts
    def typeChecksAndCasts = {
        // isInstanceOf vs classOf vs pattern matching
        val p = AnyRef
        type Foo = List[String]

        // check class type: if p ai System or its subclass
        val s = if (p.isInstanceOf[Foo]) p.asInstanceOf[Foo] else sys.error("oops")

        // check object type (runtime class), NOT a subclass
        val o = if (p.getClass == classOf[Foo]) p.asInstanceOf[Foo] else sys.error("oops")

        // pattern matching better
        val pm = p match {
            case s: Foo => s.asInstanceOf[Foo]
            case _ => sys.error("oops")
        }
    }

    // protected fields and methods
    def protectedFieldsAndMethods = {
        // access granted from any subclass (not package)

        class Person {
            protected var name = ""         // class protected
            protected[this] var backup = "" // object protected
        }
    }

    // superclass construction
    def superclassConstruction = {
        // aux constructors must start with a call to a preceding constructor =>
        // aux constructor can never call a super constructor, only primary can do this

        class Person(val name: String, val age: Int) {}
        class Employee(name: String, age: Int, val salary: Double)
            extends Person(name, age) // call sup constructor // never like super(name, age)
        { ??? } // subclass constructor

        // scala class can extend a java class
        import java.nio.charset.Charset
        import java.nio.file.{Files, Path}
        class PathWriter(p: Path, cs: Charset)
            extends java.io.PrintWriter(Files.newBufferedWriter(p, cs)) // call one of the java class constructors
        { ??? }
    }

    // overriding fields
    def overridingFields = {
        // class field = private field + getter/setter

        // dumb but illustrative example
        class Person(val name: String) {
            override def toString: String = s"${getClass.getName} name: $name"
        }
        class SecretAgent(codename: String) extends Person(codename) {
            override val name: String = "secret" // val over val
            override val toString = "secret" // val over def
        }

        // common case: override abstract def with a val
        abstract class Person2 {
            def id: Int // contract, not defined => abstract
        }
        class Student(override val id: Int) extends Person2 {
            ???
        }
        // restrictions:
        // - def over def;
        // - val over def or val;
        // - var over abstract var only

        // so, if you implement getter/setter with a var, all subclasses are stuck with it!
    }

    // anonymous subclasses
    def anonymousSubclasses = {
        // new Foo { definitions } : object of a structural type
        // that can be used as any other type

        class Person(val name: String) {
            override def toString: String = s"${getClass.getName} name: $name"
        }

        val alien = new Person("Fred") { def greeting: String = "Greetings, Earthling!" }

        // n.b. parameter type declaration
        def meet(p: Person{def greeting: String}) = {
            println(s"$p says ${p.greeting}")
        }
    }

    // abstract classes
    def abstractClasses = {
        // omit member body => abstract class;
        abstract class Person(val name: String) {
            def id: Int // turn all class to abstract
        }
        class Employee(name: String) extends Person(name) {
            def id = name.hashCode // override not required
        }
    }

    // abstract fields
    def abstractFields = {
        // field w/o a value

        abstract class Person {
            val id: Int // abstract field with an abstract getter
            var name: String // + abstract setter
            // generated java class has no fields
            /*
scala> :javap -private Person
Compiled from "<console>"
public abstract class $line2.$read$$iw$$iw$Person {
  public abstract int id();
  public abstract java.lang.String name();
  public abstract void name_$eq(java.lang.String);
  public $line2.$read$$iw$$iw$Person();
}
             */
        }

        // no override required
        class Employee(val id: Int) extends Person {
            var name = ""
        }

        // customize by anonymous type (structural type)
        val fred = new Person { val id = 42; var name = "Fred" }
    }

    // construction order and early definitions
    def constructionOrderAndEarlyDefinitions = {
        // using methods (getters) in constructor is a bad idea:
        // jvm call overridden method from subclass while constructing superclass: by design

        // example: override a val that used in sup constructor
        class Creature {
            val range: Int = 10
            val env: Array[Int] = new Array[Int](range) // oops, call a range getter!
        }
        class Ant extends Creature {
            override val range = 2 // field value initialized AFTER sup constructor
            // and getter called by sup return 0
        }
        // remember: no method calls in constructor (no val getters)!

        // remedies:
        // - declare val as final;
        // - declare val as lazy;
        // - use the early definition

        // early definition: init val fields of subclass BEFORE sup's constructor
        class Ant2 extends { override val range = 2 } with Creature
        // n.b. extends block with sup; like traits mixin

        // use -Xcheckinit compiler flag to find access to uninitialized fields
    }

    // the scala inheritance hierarchy
    def theScalaInheritanceHierarchy = {
        // Any: root (isInstanceOf, equal, hashCode)
        // AnyVal: value classes, primitives
        // AnyRef: compound classes (java.lang.Object, add wait, notify, synchronized)
        // Null: subtype of all ref. types (singleton null)
        // Nothing: subtype of all types (no instances, useful for generics and exceptions)

        // Nothing != void or Unit

        // parameters of Any type placed in a tuple
        def show(x: Any) { println(s"${x.getClass}: $x") }
        show(1,2,3) // class scala.Tuple3: (1,2,3)
    }

    // object equality
    def objectEquality = {
        // when you implement a class, consider overriding methods
        // 'equal' and 'hashCode'
        // using class instances as map keys or set items require that

        // good example
        class Item(val description: String, val price: Double) {
            // final: you should not extend equals, because of a symmetry problem:
            // a equals b should be the same as b equals a, even if b is a subclass
            final override def equals(other: Any): Boolean = other match {
                case that: Item => { description == that.description && price == that.price }
                case _ => false
            }
            // define hashCode as well, from the fields used in equals
            final override def hashCode(): Int = (description, price).##
            // ## method is null-safe: yields 0 for null
        }

        // in app code use '==' operator
    }

    // value classes
    def valueClasses = {
        // for classes with a single field, such as the wrapper for primitive types,
        // it's inefficient to allocate a new object for every value
        // hence: value classes, a trick that inline class methods

        // value class properties:
        // - extends AnyVal;
        // - primary constructor has exactly one val param and no body;
        // - has no other fields or constructors;
        // - automatically provides equals and hashCode.

        // value class may not be a local class
        import Chapter08.{valueClasses => vc}
        val mt = vc.MilTime(2359)

//        class MilitaryTime(val time: Int) extends AnyVal {
//            def minutes = time % 100
//            def hours = time / 100
//            override def toString: String = f"$time%04d"
//        }
//
//        // or, better, provide a factory with proper initialization (no body for constructor!)
//        class MilTime private (val time: Int) extends AnyVal {
//            def minutes = time % 100
//            def hours = time / 100
//            override def toString: String = f"$time%04d"
//        }
//
//        object MilTime {
//            def apply(time: Int) = {
//                if (0 <= time && time < 2400 && time % 100 < 60) new MilTime(time)
//                else throw new IllegalArgumentException("time should be between 0000 and 2359 inclusive")
//            }
//        }
//
//        // if you need a value class with a trait,
//        // the trait must explicitly extend Any, and it may not have fields.
//        // such traits are called 'universal traits'
//
//        // example for overhead-free tiny types
//        class Author(val name: String) extends AnyVal
//        class Title(val value: String) extends AnyVal
//        class Book(val author: Author, val title: Title) // can't switch author and title
    }

}

object Inheritance_Exercises {

    // 1. Extend the following BankAccount class to a CheckingAccount class
    // that charges $1 for every deposit and withdrawal.
    //
    //    class BankAccount(initialBalance: Double) {
    //        private var balance = initialBalance
    //        def currentBalance = balance
    //        def deposit(amount: Double) = { balance += amount; balance }
    //        def withdraw(amount: Double) = { balance -= amount; balance }
    //    }
    def ex1 = {
        class BankAccount(initialBalance: Double) {
            private var balance = initialBalance
            def currentBalance = balance
            def deposit(amount: Double) = { balance += amount; balance }
            def withdraw(amount: Double) = { balance -= amount; balance }
        }
        class CheckingAccount(initialBalance: Double)
            extends BankAccount(initialBalance) {
            // to charge deposit & withdrawal
            override def deposit(amount: Double) = { super.deposit(amount - 1.0) }
            override def withdraw(amount: Double) = { super.withdraw(amount + 1.0) }
        }
    }

    // 2. Extend the BankAccount class of the preceding exercise into a class SavingsAccount
    // that earns interest every month (when a method earnMonthlyInterest is called) and has
    // three free deposits or withdrawals every month. Reset the transaction count in the
    // earnMonthlyInterest method.
    def ex2 = {
        class BankAccount(initialBalance: Double) {
            private var balance = initialBalance
            def currentBalance = balance
            def deposit(amount: Double) = { balance += amount; balance }
            def withdraw(amount: Double) = { balance -= amount; balance }
        }
        class SavingsAccount(initialBalance: Double)
            extends BankAccount(initialBalance) {

            override def deposit(amount: Double) = { super.deposit(amount - charge()) }
            override def withdraw(amount: Double) = { super.withdraw(amount + charge()) }

            def earnMonthlyInterest(): Unit = { // mutator
                freeCount = 4 // and decrement it to 3
                deposit(currentBalance * intRate)
            }

            private def charge() = { // mutator
                if (freeCount <= 0) 1.0 else { freeCount -= 1; 0.0 }
            }

            private[this] val intRate = 1.0/100.0
            private[this] var freeCount = 3
        }

    }

    // 3. Consult your favorite Java or C++ textbook which is sure to have an example of a toy
    // inheritance hierarchy, perhaps involving employees, pets, graphical shapes, or the like.
    // Implement the example in Scala.
    def ex3 = {
        // http://www.java2s.com/Tutorials/Java/Java_Object_Oriented_Design/0300__Java_Inheritance.htm
        class Employee (var name: String = "Unknown")
        class Manager extends Employee
        val mgr = new Manager
        mgr.name = "Tom"
        println(s"Manager's name: ${mgr.name}")
    }

    // 4. Define an abstract class Item with methods price and description. A SimpleItem
    // is an item whose price and description are specified in the constructor. Take advantage of the
    // fact that a val can override a def. A Bundle is an item that contains other items. Its price is
    // the sum of the prices in the bundle. Also provide a mechanism for adding items to the bundle
    // and a suitable description method.
    def ex4 = {
        abstract class Item {
            def price: Double
            def description: String
        }
        class SimpleItem(val price: Double, val description: String) extends Item
        class Bundle extends Item {
            private[this] var items = List.empty[Item]
            def add(item: Item): Unit = items :+= item
            def price: Double = items.map(_.price).sum
            def description: String = items.map(_.description).mkString(";")
        }
    }

    // 5. Design a class Point whose x and y coordinate values can be provided in a constructor.
    // Provide a subclass LabeledPoint whose constructor takes a label value and x and y
    // coordinates, such as
    //
    //    new LabeledPoint("Black Thursday", 1929, 230.07)
    def ex5 = {
        class Point(val x: Double = 0, val y: Double = 0)
        class LabeledPoint(val label: String, x: Double, y: Double) extends Point(x, y)
    }

    // 6. Define an abstract class Shape with an abstract method centerPoint and subclasses
    // Rectangle and Circle. Provide appropriate constructors for the subclasses and override
    // the centerPoint method in each subclass.
    def ex6 = {
        import scala.util.Random.nextInt
        class Point(val x: Int = 0, val y: Int = 0)
        abstract class Shape { def centerPoint: Point }
        // perhaps I should define constructors as two points for rectangle and point and radius for circle?
        class Rectangle extends Shape { def centerPoint = new Point(nextInt(), nextInt()) }
        class Circle extends Shape { def centerPoint = new Point(nextInt(), nextInt()) }
    }

    // 7. Provide a class Square that extends java.awt.Rectangle and has three constructors:
    // one that constructs a square with a given corner point and width,
    // one that constructs a square with corner (0, 0) and a given width,
    // and one that constructs a square with corner (0, 0) and width 0.
    def ex7 = {
        class Point(val x: Int = 0, val y: Int = 0)
        class Square(corner: Point, width: Int)
            extends java.awt.Rectangle(corner.x, corner.y, width, width) {
            def this(width: Int) = this(new Point(0,0), width)
            def this() = this(new Point(0,0), 0)
        }
    }

    // 8. Compile the Person and SecretAgent classes in Section 8.6, “Overriding Fields,” on
    // page 95 and analyze the class files with javap. How many name fields are there? How many
    // name getter methods are there? What do they get? (Hint: Use the -c and -private options.)
    def ex8 = {
        class Person(val name: String) {
            override def toString = getClass.getName + "[name=" + name + "]"
        }
        // How many 'name' fields are there? 1
        // How many 'name' getter methods are there? 1
        // What do they get? name field value
        /*
scala> :javap -c -private Person
Compiled from "<pastie>"
public class $line12.$read$$iw$$iw$Person {
  private final java.lang.String name;

  public java.lang.String name();
    Code:
       0: aload_0
       1: getfield      #18                 // Field name:Ljava/lang/String;
       4: areturn

  public java.lang.String toString();
...
  public $line12.$read$$iw$$iw$Person(java.lang.String);
...}
 */

        class SecretAgent(codename: String) extends Person(codename) {
            override val name = "secret" // Don't want to reveal name . . .
            override val toString = "secret" // . . . or class name
        }
        // How many 'name' fields are there? 1
        // How many 'name' getter methods are there? 1
        // What do they get? name field value = secret
        /*
scala> :javap -c -private SecretAgent
Compiled from "<pastie>"
public class $line13.$read$$iw$$iw$SecretAgent extends $line12.$read$$iw$$iw$Person {
  private final java.lang.String name;
  private final java.lang.String toString;

  public java.lang.String name();
    Code:
       0: aload_0
       1: getfield      #26                 // Field name:Ljava/lang/String;
       4: areturn

  public java.lang.String toString();
...
  public $line13.$read$$iw$$iw$SecretAgent(java.lang.String);
    Code:
       0: aload_0
       1: aload_1
       2: invokespecial #35                 // Method $line12/$read$$iw$$iw$Person."<init>":(Ljava/lang/String;)V
       5: aload_0
       6: ldc           #37                 // String secret
       8: putfield      #26                 // Field name:Ljava/lang/String;
      11: aload_0
      12: ldc           #37                 // String secret
      14: putfield      #30                 // Field toString:Ljava/lang/String;
      17: return
}
 */
    }

    // 9. In the Creature class of Section 8.10, “Construction Order and Early Definitions,” on page 98,
    // replace val range with a def
    // What happens when you also use a def in the Ant subclass?
    // What happens when you use a val in the subclass? Why?
    def ex9 = {
        class Creature {
            def range: Int = 10
            val env: Array[Int] = new Array[Int](range) // call to overridden method!
        }
        class AntDef extends Creature {
            override def range = 2
        }
        class Ant extends Creature {
            override val range = 2
        }
        // What happens when you also use a def in the Ant subclass?
        //      superclass constructor uses range from subclass, env.length = 2
        // What happens when you use a val in the subclass?
        //      superclass constructor uses range from subclass, env.length = 0
        // Why? Ant.val is undefined (= 0) in time when super constructor is executed

//        scala> new Ant
//        scala> res5.env.length
//        res6: Int = 0

//        scala> new AntDef
//        scala> res7.env.length
//        res8: Int = 2
    }

    // 10. The file scala/collection/immutable/Stack.scala contains the definition
    //
    //    class Stack[A] protected (protected val elems: List[A])
    //
    // Explain the meanings of the protected keywords. (Hint: Review the discussion of private
    // constructors in Chapter 5.)
    def ex10 = {
        // protected constructor for Stack
        // Stack have protected list 'elems'
        // these names accessible only from Stack subclasses in the same location

        // A protected constructor can only be accessed by an auxiliary constructor or companion object
        // from same or descendant class
    }

    // 11. Define a value class Point that packs integer x and y coordinates into a Long
    // (which you should make private).
    def ex11 = {
        // value class may not be a local class
        import Chapter08.valueClasses.exercise11
        val p = exercise11.Point(0, 0)

//        class Point private(private val xy: Long) extends AnyVal {
//            def x: Int = (xy >> 32).toInt
//            def y: Int = xy.toInt
//        }
//        object Point {
//            def apply(x: Int, y: Int): Point = {
//                val xy: Long = (x.toLong << 32) | (y & 0xffffffffL)
//                new Point(xy)
//            }
//        }
    }
}

package valueClasses {
    // value class may not be a local class

    class MilitaryTime(val time: Int) extends AnyVal {
        def minutes = time % 100
        def hours = time / 100
        override def toString: String = f"$time%04d"
    }

    // or, better, provide a factory with proper initialization (no body for constructor!)
    class MilTime private (val time: Int) extends AnyVal {
        def minutes = time % 100
        def hours = time / 100
        override def toString: String = f"$time%04d"
    }

    object MilTime {
        def apply(time: Int) = {
            if (0 <= time && time < 2400 && time % 100 < 60) new MilTime(time)
            else throw new IllegalArgumentException("time should be between 0000 and 2359 inclusive")
        }
    }

    // if you need a value class with a trait,
    // the trait must explicitly extend Any, and it may not have fields.
    // such traits are called 'universal traits'

    // example for overhead-free tiny types
    class Author(val name: String) extends AnyVal
    class Title(val value: String) extends AnyVal
    class Book(val author: Author, val title: Title) // can't switch author and title

    package exercise11 {
        // TODO: add tests for corner cases / negative coords
        class Point private(private val xy: Long) extends AnyVal {
            def x: Int = (xy >> 32).toInt
            def y: Int = xy.toInt
        }
        object Point {
            def apply(x: Int, y: Int): Point = {
                val xy: Long = (x.toLong << 32) | (y & 0xffffffffL)
                new Point(xy)
            }
        }
    }
}

