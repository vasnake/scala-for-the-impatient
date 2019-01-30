// Scala for the Impatient
// Chapter 8. Inheritance
/*
• The extends and final keywords are as in Java.
• You must use override when you override a method.
• Only the primary constructor can call the primary superclass constructor.
• You can override fields.
 */

// we only discuss the case in which a class inherits from another class

// extends, final

//You extend a class in Scala just like you would in Java—with the extends keyword:
{
    class Person
    class Employee extends Person {
        var salary = 0.0
    }
}
// As in Java, you can declare a class final so that it cannot be extended.
// You can also declare individual methods or fields final so that they cannot be overridden.
// Note that this is different from Java, where a final field is immutable, similar to val in Scala.

// override

// In Scala, you must use the override modifier when you override a method that isn’t abstract.
{
    class Person {
        val name = ""
        override def toString = getClass.getName + "[name=" + name + "]"
    }
}

// super

// Invoking a superclass method in Scala works exactly like in Java, with the keyword super:
{
    class Person {
        val name = ""
        override def toString = getClass.getName + "[name=" + name + "]"
    }
    class Employee extends Person {
        var salary = 0.0
         override def toString = super.toString + "[salary=" + salary + "]"
    }
}

// Type Checks and Casts (bad idea)

// To test whether an object belongs to a given class, use the isInstanceOf method.
// If the test succeeds, you can use the asInstanceOf method to convert a
// reference to a subclass reference:
{
    class Employee
    val p = new Employee
    if (p.isInstanceOf[Employee]) {
        val s = p.asInstanceOf[Employee] // s has type Employee
    }
    // If you want to test whether p refers to an Employee object, but not a subclass, use
    if (p.getClass == classOf[Employee]) 1
}
// The p.isInstanceOf[Employee] test succeeds if p refers to an object of class
// Employee or its subclass

// However, pattern matching is usually a better alternative to using type checks and casts

// protected

// As in Java or C++, you can declare a field or method as protected.
// Such a member is accessible from any subclass, but not from other locations
// Unlike in Java, a protected member is not visible throughout the package
// to which the class belongs. (If you want this visibility, you can use a package modifier—see Chapter 7.)
// There is also a protected[this] variant that restricts access to the current object,
// similar to the private[this] variant discussed in Chapter 5.

// super

// an auxiliary constructor can never invoke a superclass constructor directly
// Only the primary constructor can call a superclass constructor

// This defines a subclass
// and a primary constructor that calls the superclass constructor
{
    class Person(name: String, age: Int)
    class Employee(name: String, age: Int, val salary: Double)
        extends Person(name, age)
}
// In a Scala constructor, you can never call super(params),
// as you would in Java, to call the superclass constructor

// A Scala class can extend a Java class. Its primary constructor must invoke
// one of the constructors of the Java superclass. For example,
{
    class Square(x: Int, y: Int, width: Int)
        extends java.awt.Rectangle(x, y, width, width)
}

// override

// a field in Scala consists of a private field and accessor/mutator methods.
// You can override a val (or a parameterless def) with another val field of the same name.
// The subclass has a private field and a public getter, and the getter overrides
// the superclass getter (or method)
{
    class Person(val name: String) {
        override def toString = getClass.getName + "[name=" + name + "]"
    }
    class SecretAgent(codename: String) extends Person(codename) {
        override val name = "secret" // Don't want to reveal name . . .
        override val toString = "secret" // . . . or class name
    }

    // A more common case is to override an abstract def with a val, like this:
    abstract class Personn { // See Section 8.8 for abstract classes
        def id: Int // Each person has an ID that is computed in some way
    }
    class Student(override val id: Int) extends Personn
    // A student ID is simply provided in the constructor
}
/*
override restrictions
• A def can only override another def.
• A val can only override another val or a parameterless def.
• A var can only override an abstract var

In Chapter 5, I said that it’s OK to use a var because you can always change your mind
and reimplement it as a getter/setter pair.
However, the programmers extending your class do not have that choice.
They cannot override a var with a getter/setter pair.
In other words, if you provide a var, all subclasses are stuck with it
 */

// anonymous subclasses

/*
As in Java, you make an instance of an anonymous subclass if you include a block
with definitions or overrides.
Technically, this creates an object of a structural type
—see Chapter 18 for details.
The type is denoted as Person{def greeting: String}
 */
{
    class Person(name: String)
    val alien = new Person("Fred") {
        def greeting = "Greetings, Earthling! My name is Fred."
    }
}

// abstract

// As in Java, you can use the abstract keyword to denote a class that cannot be instantiated,
// usually because one or more of its methods are not defined. For example,
{
    // As in Java, a class with at least one abstract method must be declared abstract
    abstract class Person(val name: String) {
        def id: Int // No method body—this is an abstract method
        // In Scala, unlike Java, you do not use the abstract keyword for an abstract method.
        // You simply omit its body.
        // Here we say that every person has an ID, but we don’t know how to compute it.
        // Each concrete subclass of Person needs to specify an id method

        // In addition to abstract methods, a class can also have abstract fields.
        // An abstract field is simply a field without an initial value
        var sname: String
    }
    //In a subclass, you need not use the override keyword
    // when you define a method that was abstract in the superclass.
    class Employee(name: String) extends Person(name) {
        def id = name.hashCode // override keyword not required
        // Concrete subclasses must provide concrete fields
        var sname: String = ""
    }

    // You can always customize an abstract field by using an anonymous type:
    val fred = new Person("") {
        val id = 1729
        var sname = "Fred"
    }
}

//8.10 Construction Order and Early Definitions

// the problem: using a subclass method in a constructor
{
    class Creature {
        val range: Int = 10
        val env: Array[Int] = new Array[Int](range)
    }
    class Ant extends Creature {
        override val range = 2
    }
/*
    Unfortunately, we now have a problem. The range value is used in the superclass constructor,
    and the superclass constructor runs before the subclass constructor.
    Specifically, here is what happens:
    1. The Ant constructor calls the Creature constructor before doing its own construction.
    2. The Creature constructor sets its range field to 10.
    3. The Creature constructor, in order to initialize the env array, calls the range() getter.
    4. That method is overridden to yield the (as yet uninitialized) range field of the Ant class.
    5. The range method returns 0. (That is the initial value of all integer fields when an object is allocated.)
    6. env is set to an array of length 0.
    7. The Ant constructor continues, setting its range field to 2.
 */
}
// The moral is that you should not rely on the value of a val in the body of a constructor

// In Java, you have a similar issue when you call a method in a superclass constructor.
// The method might be overridden in a subclass, and it might not do what you want it to do

/*
There are several remedies.
• Declare the val as final. This is safe but not very flexible.
• Declare the val as lazy in the superclass (see Chapter 2). This is safe but a bit inefficient.
• Use the early definition syntax in the subclass—see below.
The “early definition” syntax lets you initialize val fields of a subclass before
the superclass is executed. The syntax is so ugly that only a mother could love it.
You place the val fields in a block after the extends keyword, like this:
class Bug extends {
  override val range = 2
} with Creature
 */
{
    class Creature {
        val range: Int = 10
        val env: Array[Int] = new Array[Int](range)
    }
    class Bug extends {
        override val range = 2
    } with Creature
}
// You can debug construction order problems with the -Xcheckinit compiler flag.
// This flag generates code that throws an exception (instead of yielding the default value)
// when an uninitialized field is accessed

// 8.12 Object Equality

/*
In Scala, the eq method of the AnyRef class checks whether two references refer to the same object.
The equals method in AnyRef calls eq.
When you implement a class, you should consider overriding the equals method
 */
{
    class Item(val description: String, val price: Double) {
        final override def equals(other: Any) = {
            val that = other.asInstanceOf[Item]
            if (that == null) false
            else description == that.description && price == that.price
        }
        final override def hashCode = 13 * description.hashCode + 17 * price.hashCode
    }
}
/*
We defined the method as final because it is generally very difficult to correctly
extend equality in a subclass.
The problem is symmetry. You want a.equals(b) to have the same result as b.equals(a),
even when b belongs to a subclass

Be sure to define the equals method with parameter type Any. The following would be wrong:
final def equals(other: Item) = { ... }
This is a different method that does not override the equals method of AnyRef

When you define equals, remember to define hashCode as well.
The hash code should be computed only from the fields that you use in the equality check
 */

// Exercises

/*
1. Extend the following BankAccount class to a CheckingAccount class that charges $1
for every deposit and withdrawal.
 */
{
    class BankAccount(initialBalance: Double) {
        private var balance = initialBalance
        def currentBalance = balance
        def deposit(amount: Double) = { balance += amount; balance }
        def withdraw(amount: Double) = { balance -= amount; balance }
    }
    class CheckingAccount(initialBalance: Double)
        extends BankAccount(initialBalance) {
        // can't override var balance
        override def deposit(amount: Double) = { super.deposit(amount - 1.0) }
        override def withdraw(amount: Double) = { super.withdraw(amount + 1.0) }
    }
}

/*
2. Extend the BankAccount class of the preceding exercise into a class SavingsAccount
that earns interest every month (when a method earnMonthlyInterest is called)
and has three free deposits or withdrawals every month.
Reset the transaction count in the earnMonthlyInterest method.
 */
{
    class BankAccount(initialBalance: Double) {
        private var balance = initialBalance
        def currentBalance = balance
        def deposit(amount: Double) = { balance += amount; balance }
        def withdraw(amount: Double) = { balance -= amount; balance }
    }
    class SavingsAccount(initialBalance: Double)
        extends BankAccount(initialBalance) {

        private val intRate = 1.0/100.0
        private var freeCount = 3
        private def charge = {
            if (freeCount <= 0) 1.0 else { freeCount -= 1; 0.0}
        }

        override def deposit(amount: Double) = { super.deposit(amount - charge) }
        override def withdraw(amount: Double) = { super.withdraw(amount + charge) }

        def earnMonthlyInterest(): Unit = {
            freeCount = 4
            deposit(currentBalance * intRate)
        }
    }
}

/*
3. Consult your favorite Java or C++ textbook that is sure to have an example of a
toy inheritance hierarchy, perhaps involving employees, pets, graphical shapes,
or the like. Implement the example in Scala.
 */
{
    // http://www.java2s.com/Tutorials/Java/Java_Object_Oriented_Design/0300__Java_Inheritance.htm
    class Employee (var name: String = "Unknown")
    class Manager extends Employee
    val mgr = new Manager
    mgr.name = "Tom"
    println(s"Manager name: ${mgr.name}")
}

/*
4. Define an abstract class Item with methods price and description.
A SimpleItem is an item whose price and description are specified in the constructor.
Take advantage of the fact that a val can override a def.
A Bundle is an item that contains other items. Its price is the sum of the prices in the bundle.
Also provide a mechanism for adding items to the bundle and a suitable description method.
 */
{
    abstract class Item {
        def price: Double
        def description: String
    }
    class SimpleItem(val price: Double, val description: String)
        extends Item

    class Bundle extends Item {
        private var items = List.empty[Item]
        def add(item: Item): Unit = items :+= item
        def price = items.foldLeft(0.0)(_ + _.price)
        def description: String = items.map(_.description).mkString(";")
    }
}

/*
5. Design a class Point whose x and y coordinate values can be provided in a constructor.
Provide a subclass LabeledPoint whose constructor takes a label value and
x and y coordinates, such as
new LabeledPoint("Black Thursday", 1929, 230.07)
 */
{
    class Point(val x: Int = 0, val y: Int = 0)
    class LabeledPoint(val label: String, x: Int, y: Int)
        extends Point(x, y)
}

/*
6. Define an abstract class Shape with an abstract method centerPoint and
subclasses Rectangle and Circle.
Provide appropriate constructors for the subclasses and override the centerPoint
method in each subclass.
 */
{
    class Point(val x: Int = 0, val y: Int = 0)
    abstract class Shape {
        def centerPoint: Point
    }
    class Rectangle extends Shape {
        def centerPoint = new Point(1, 2)
    }
    class Circle extends Shape {
        def centerPoint = new Point(3, 4)
    }
}

/*
7. Provide a class Square that extends java.awt.Rectangle and
has three constructors:
one that constructs a square with a given corner point and width,
one that constructs a square with corner (0, 0) and a given width, and
one that constructs a square with corner (0, 0) and width 0.
 */
{
    class Point(val x: Int = 0, val y: Int = 0)

    class Square(corner: Point, width: Int)
        extends java.awt.Rectangle(corner.x, corner.y, width, 0) {
        def this(width: Int) = this(new Point(0,0), width)
        def this() = this(new Point(0,0), 0)
    }
}

/*
8. Compile the Person and SecretAgent classes in Section 8.6, “Overriding Fields,”
on page 89 and analyze the class files with javap.
How many name fields are there?
How many name getter methods are there?
What do they get? (Hint: Use the -c and -private options.)
 */
{
    class Person(val name: String) {
        override def toString = getClass.getName + "[name=" + name + "]"
    }

    class SecretAgent(codename: String) extends Person(codename) {
        override val name = "secret" // Don't want to reveal name . . .
        override val toString = "secret" // . . . or class name
    }
    /*
How many name fields are there? 1
How many name getter methods are there? 1
What do they get? name

How many name fields are there? 1
How many name getter methods are there? 1
What do they get? name
     */
}

/*
9. In the Creature class of Section 8.10, “Construction Order and Early Definitions,” on page 92,
replace val range with a def.
What happens when you also use a def in the Ant subclass?
What happens when you use a val in the subclass?
Why?
 */
{ // original
    class Creature {
        val range: Int = 10
        val env: Array[Int] = new Array[Int](range)
    }
    class Ant extends Creature {
        override val range = 2
    }
}

{
    class Creature {
        def range: Int = 10
        val env: Array[Int] = new Array[Int](range)
    }
    class Ant extends Creature {
        override val range = 2
    }
// What happens when you also use a def in the Ant subclass?
//      superclass constructor uses range from subclass, env.length = 2
// What happens when you use a val in the subclass?
//      superclass constructor uses range from subclass, env.length = 0
// Why? Ant.val is undefined (= 0) in time when super constructor is executed
    val a = new Ant
    a.env.length
}

/*
10. The file scala/collection/immutable/Stack.scala contains the definition

class Stack[A] protected (protected val elems: List[A])
Explain the meanings of the protected keywords.
(Hint: Review the discussion of private constructors in Chapter 5.)

protected constructor for Stack
Stack have protected list 'elems'
these names accessible only from Stack subclasses in the same location
 */
// A protected constructor can only be accessed by an auxiliary constructor
// from same or descendant class
