// Scala for the Impatient
// 5 Classes

import scala.collection.mutable.ArrayBuffer

/*
 Fields in classes automatically come with getters and setters.
• You can replace a field with a custom getter/setter without changing the client of a class—that is the “uniform access principle.”
• Use the @BeanProperty annotation to generate the JavaBeans getXxx/setXxx methods.
• Every class has a primary constructor that is “interwoven” with the class definition. Its parameters turn into the fields of the class. The primary constructor executes all statements in the body of the class.
• Auxiliary constructors are optional. They are called this.
 */

/*
It is considered good style to use () for a mutator method
(a method that changes the object state), and to drop the ()
for an accessor method (a method that does not change the object state)
 */

{
//    Scala provides getter and setter methods for every field. Here, we define a public field:
    class Person {
        var age = 0
    }
//    Scala generates a class for the JVM with a private age field and getter and setter methods
//    In Scala, the getter and setter methods are called age and age_=
    val fred = new Person
    println(fred.age) // Calls the method fred.age()
    fred.age = 21 // Calls fred.age_=(21)
//    At any time, you can redefine the getter and setter methods yourself.
}
/*
To see these methods with your own eyes, compile the Person class and then look at the bytecode with javap:
$ scalac Person.scala
$ javap -private Person
Compiled from "Person.scala"
public class Person extends java.lang.Object implements scala.ScalaObject{
  private int age;
  public int age();
  public void age_$eq(int);
  public Person();
}
 */
/*
Bertrand Meyer, the inventor of the influential Eiffel language,
formulated the Uniform Access Principle that states:
“All services offered by a module should be available through a uniform notation,
which does not betray whether they are implemented through storage or through computation.”
 */

{
    // use val ... for
    class Message {
        val timeStamp = new java.util.Date
     }
//    Scala makes a private final field and a getter method, but no setter.
}
/*
To summarize, you have four choices for implementing properties:
1. var foo: Scala synthesizes a getter and a setter.
2. val foo: Scala synthesizes a getter.
3. You define methods foo and foo_=.
4. You define a method foo.

In Scala, you cannot have a write-only property (that is, a property with a setter and no getter).
 */

{
//    In Scala (as well as in Java or C++), a method can access the private fields of all objects of its class. For example,
    class Counter {
        private var value = 0
        def increment() { value += 1 }

        def isLess(other : Counter) = value < other.value
        // Can access private field of other object
    }
//    Scala allows an even more severe access restriction, with the private[this] qualifier:
//    private[this] var value = 0 // Accessing someObject.value is not allowed
//    This access is sometimes called object-private
//    no getters and setters are generated at all
}

//The JavaBeans specification defines a Java property as a pair of getFoo/setFoo
// methods (or just a getFoo method for a read-only property)
{
//    When you annotate a Scala field with @BeanProperty,
    // then such methods are automatically generated. For example,
    import scala.beans.BeanProperty
    class Person {
        @BeanProperty var name: String = _
    }
}

{
//    1. The auxiliary constructors are called this. (In Java or C++, constructors have the same name as the class—which is not so convenient if you rename the class.)
//    2. Each auxiliary constructor must start with a call to a previously defined auxiliary constructor or the primary constructor.
//    Here is a class with two auxiliary constructors.
    class Person {
        private var name = ""
        private var age = 0

        def this(name: String) { // An auxiliary constructor
            this() // Calls primary constructor
            this.name = name
        }

        def this(name: String, age: Int) { // Another auxiliary constructor
            this(name) // Calls previous auxiliary constructor
            this.age = age
        }
    }
}
//Parameters of the primary constructor turn into fields
//The primary constructor executes all statements in the class definition
//You can often eliminate auxiliary constructors by using default arguments in the primary constructor
//Construction parameters can also be regular method parameters, without val or var

{
//    If a parameter without val or var is used inside at least one method, it becomes a field. For example,
    class Person(name: String, age: Int) {
        def description = name + " is " + age + " years old"
    }
//    declares and initializes immutable fields name and age that are object-private.
}

// Nested classes
{
    import scala.collection.mutable.ArrayBuffer
    class Network {
        class Member(val name: String) {
            val contacts = new ArrayBuffer[Member]
        }

        private val members = new ArrayBuffer[Member]

        def join(name: String) = {
            val m = new Member(name)
            members += m
            m
        }
    }
//    Consider two networks:
    val chatter = new Network
    val myFace = new Network
//    In Scala, each instance has its own class Member, just like each instance has its own field members.
    // That is, chatter.Member and myFace.Member are different classes
}

{
//    you can use a type projection Network#Member,
// which means “a Member of any Network.” For example,
    class Network {
        class Member(val name: String) {
            val contacts = new ArrayBuffer[Network#Member]
        }
    }
}

{
//    In a nested class, you can access the this reference of the enclosing class as
    // EnclosingClass.this, like in Java.
    // If you like, you can establish an alias for that reference with the following syntax:
    class Network(val name: String) { outer =>
        class Member(val name: String) {
             def description = name + " inside " + outer.name
        }
    }
}

// Exercises

// 1. Improve the Counter class in Section 5.1,
// “Simple Classes and Parameterless Methods,”
// on page 49 so that it doesn’t turn negative at Int.MaxValue
{
    class Counter {
        private var value = 0 // You must initialize the field
        def current() = value
        def increment() {
            if (value == Int.MaxValue) sys.error("counter out of bounds")
            else value += 1
        }
    }
}

// 2. Write a class BankAccount with methods deposit and withdraw, and a read-only property balance
{
    class BankAccount {
        private var _balance = 0
        def balance = _balance
        def deposit(n: Int) = _balance += n
        def withdraw(n: Int) = _balance -= n
    }
}

/*
3. Write a class Time with read-only properties hours and minutes
and a method before(other: Time): Boolean
that checks whether this time comes before the other.
A Time object should be constructed as new Time(hrs, min),
where hrs is in military time format (between 0 and 23)
 */
{
    class Time(val hours: Int, val minutes: Int) {
        def before(other: Time) =
            hours < other.hours || (hours == other.hours && minutes < other.minutes)
    }
}

/*
4. Reimplement the Time class from the preceding exercise so that
the internal representation is the number of minutes since midnight (between 0 and 24 × 60 – 1).
Do not change the public interface. That is, client code should be unaffected by your change
 */
{
    class Time(val hours: Int, val minutes: Int) {
        private val totminutes = (hours * 60) + minutes
        def before(other: Time) = totminutes < other.totminutes
    }
}

/*
5. Make a class Student with read-write JavaBeans properties name (of type String) and id (of type Long).
What methods are generated? (Use javap to check.)
Can you call the JavaBeans getters and setters in Scala? Should you?
 */
{
    import scala.beans.BeanProperty
    class Student {
        @BeanProperty var name: String = "" // def name; def name_=; def getName; def setName
        @BeanProperty var id: Long = 0
    }
    // Yes, I can. No, I shouldn't.
}

/*
6. In the Person class of Section 5.1, “Simple Classes and Parameterless Methods,” on page 49,
provide a primary constructor that turns negative ages to 0
 */
{
    class Person (var age: Int = 0) {
        if (age < 0) age = 0
    }
}

/*
7. Write a class Person with a primary constructor that accepts a string containing a
first name, a space, and a last name, such as new Person("Fred Smith").
Supply read-only properties firstName and lastName.
Should the primary constructor parameter be a var, a val, or a plain parameter? Why?
 */
{
    class Person (names: String = "John Doe") {
        val (firstName, lastName) = names.split(' ') match {
            case Array(a: String, b: String, _*) => (a, b)
            case _ => ("", "")
        }
    }
    // plain parameter, no need for extra methods and fields
    val p = new Person("Fred Smith")
    println(s"${p.firstName} ${p.lastName}")
}

/*
8. Make a class Car with read-only properties for
manufacturer, model name, and model year,
and a read-write property for the license plate.
Supply four constructors.
All require the manufacturer and model name.
Optionally, model year and license plate can also be specified in the constructor.
If not, the model year is set to -1 and the license plate to the empty string.
Which constructor are you choosing as the primary constructor? Why?
 */
{
    class Car(
                 val manufacturer: String,
                 val model: String,
                 val year: Int = -1,
                 var plate: String = "") {

        def this(mnfct: String, mdl: String) = {
            this(mnfct, mdl, -1, "")
        }
        def this(mnfct: String, mdl: String, yr: Int) = {
            this(mnfct, mdl, yr, "")
        }
        def this(mnfct: String, mdl: String, plt: String) = {
            this(mnfct, mdl, -1, plt)
        }
    }
    // primary constructor with all properties, optional params allow to skip unknown data
    val cr = new Car("ZAZ", "369", 1970)
    cr
}

/*
9. Reimplement the class of the preceding exercise in Java, C#, or C++ (your choice).
How much shorter is the Scala class?
 */
// about 2,3 times shorter

/*
10. Consider the class
class Employee(val name: String, var salary: Double) {
  def this() { this("John Q. Public", 0.0) }
}
Rewrite it to use explicit fields and a default primary constructor.
Which form do you prefer? Why?
 */
{
    class Employee(val name: String = "John Q. Public", var salary: Double = 0.0) {
        // nothing to do here, in primary constructor
        // that's why I like this better. And explicit better than implicit
    }
}
