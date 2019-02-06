package Chapter05

object Classes {
// topics:
    // simple classes and parameterless methods
    // properties with getters and setters
    // properties with only getters
    // object-private fields
    // bean properties
    // auxiliary constructors
    // the primary constructor
    // nested classes

    // simple classes and parameterless methods
    def simpleClassesAndParameterlessMethods = {

        // simplest form
        class Counter {
            private var value = 0
            // public by default
            def increment() = { value += 1 }
            def current() = value
            // or w/o ()
            def current2 = value
        }

        val myCounter = new Counter
        // good style: parameterless method with () for mutator, w/o () for accessor
        myCounter.increment()
        println(s"counter value: ${myCounter.current}")
    }

    // properties with getters and setters
    def propertiesWithGettersAndSetters = {
        // getter + setter is better than public field
        // getter + setter = property

        // scala creates getter and setter automagically for each field, except private[this]
        class Person {
            var age = 0
        }
        val fred = new Person
        fred.age_=(21) // setter
        println(fred.age) // getter
/*
scala> :javap -private Person
Compiled from "Classes.scala"
public class Chapter05.Person {
  private int age;
  public int age();
  public void age_$eq(int);
  public Chapter05.Person();
}
 */

        // you can redefine getter/setter
        class Person2 {
            private var _age = 0 // generate private getter and setter

            // getter
            def age = _age

            // setter, can't get younger
            def age_=(newAge: Int) = { if (newAge > _age) _age = newAge }
        }
        val fred2 = new Person2
        fred2.age = 30
        fred2.age = 21
        println(fred2.age) // 30
        /*
    scala> :javap -private Person2
    Compiled from "Classes.scala"
    public class Chapter05.Person2 {
      private int _age;
      private int _age();
      private void _age_$eq(int);
      public int age();
      public void age_$eq(int);
      public Chapter05.Person2();
    }
         */

        // Bertrand Meyer: Uniform Access Principle = all services ... should be available
        // through a uniform notation, ... whether ... implemented through storage or computation

        // generator rules:
        // public var field: public getter/setter
        // private var field: private getter/setter
        // val field: getter
        // private[this]: no getter/setter
    }

    // properties with only getters
    def propertiesWithOnlyGetters = {

        // read-only property
        class Message {
            // private final field with getter
            val timeStamp = java.time.Instant.now
        }
        /*
scala> :javap -private Message
Compiled from "<pastie>"
public class $line4.$read$$iw$$iw$Message {
  private final java.time.Instant timeStamp;
  public java.time.Instant timeStamp();
  public $line4.$read$$iw$$iw$Message();
}
         */

        // if you want mutable state w/o setter
        class Counter {
            private var value = 0
            // mutator/settert
            def increment() = { value += 1 }
            def current = value
        }

        // choices to implement properties:
        // var foo: getter + setter
        // val foo: getter
        // define methods 'foo', 'foo_='
        // define method 'foo'

        // you can't have a write-only property
        class WO {
            private var foo = 0
            def value_=(newval: Int) = { foo = newval }
        }
        val wo = new WO
        // wo.value = 42 // error
        /*
scala> :javap -private WO
Compiled from "<pastie>"
public class $line5.$read$$iw$$iw$WO {
  private int foo;
  private int foo();
  private void foo_$eq(int);
  public void value_$eq(int);
  public $line5.$read$$iw$$iw$WO();
}
         */
    }

    // object-private fields
    def objectPrivateFields = {
        // object-private vs class-private
        // accessible only in own instance

        // class-private
        class Counter {
            private var value = 0
            // access private field of other object (instance)
            def isless(other: Counter) = { value < other.value }
        }

        // object-private
        class Counter2 {
            // access granted only for this instance
            private[this] var value = 0
            // can't
            // def isless(other: Counter) = { value < other.value }

            class Inner {
                // access granted for enclosing class
                private[Counter2] var value = 0
                // n.b. generate java public aux getter/setter
            }
        }
    }

    // bean properties
    def beanProperties = {
        // JavaBean spec https://www.oracle.com/technetwork/articles/javaee/spec-136004.html
        // getFoo/setFoo

        // just add annotation @BeanProperty and you have two methods added
        import scala.beans.BeanProperty
        class Person {
            @BeanProperty var name: String = _
        }
        /*
scala> :javap -private Person
Compiled from "<pastie>"
public class $line9.$read$$iw$$iw$Person {
  private java.lang.String name;
  public java.lang.String name();
  public void name_$eq(java.lang.String);
  public java.lang.String getName();
  public void setName(java.lang.String);
  public $line9.$read$$iw$$iw$Person();
}
         */

        // as class parameter / primary constructor parameter
        class Person2(@BeanProperty var name: String)
        /*
scala> :javap -private Person2
Compiled from "<console>"
public class $line10.$read$$iw$$iw$Person2 {
  private java.lang.String name;
  public java.lang.String name();
  public void name_$eq(java.lang.String);
  public java.lang.String getName();
  public void setName(java.lang.String);
  public $line10.$read$$iw$$iw$Person2(java.lang.String);
}
         */
    }

    // auxiliary constructors
    def auxiliaryConstructors = {
        // you have one primary constructor and any number of aux constructors

        class Person {
            // primary constructor
            private var name = ""
            private var age = 0

            // aux constructor
            def this(name: String) = {
                // must call previously defined constructor
                this()
                this.name = name
            }

            // another aux constructor
            def this(name: String, age: Int) = {
                // must call previously defined constructor
                this(name)
                this.age = age
            }
        }
        /*
scala> :javap -private Person
Compiled from "<pastie>"
public class $line11.$read$$iw$$iw$Person {
  private java.lang.String name;
  private int age;
  private java.lang.String name();
  private void name_$eq(java.lang.String);
  private int age();
  private void age_$eq(int);
  public $line11.$read$$iw$$iw$Person();
  public $line11.$read$$iw$$iw$Person(java.lang.String);
  public $line11.$read$$iw$$iw$Person(java.lang.String, int);
}
         */
        val p1 = new Person // primary constructor
        val p2 = new Person("Fred") // first aux constructor
        val p3 = new Person("Fred", 42) // second aux constructor
    }

    // the primary constructor
    def primaryConstructor = {
        // every class has a primary constructor: body of a class;
        // classes take parameters, just like methods do;
        // every keystroke is precious;

        // name, age: parameters of the primary constructor;
        // val/var parameters turns into class fields
        class Person(val name: String, val age: Int) {
            // primary constructor body ...
        }
        /*
scala> :javap -private Person
Compiled from "<pastie>"
public class $line16.$read$$iw$$iw$Person {
  private final java.lang.String name;
  private final int age;
  public java.lang.String name();
  public int age();
  public $line16.$read$$iw$$iw$Person(java.lang.String, int);
}
         */

        // you can eliminate aux constructors by using default arguments in the primary constructor
        class Person2(val name: String = "", private var age: Int = 0)
        /*
scala> :javap -private Person2
Compiled from "<console>"
public class $line17.$read$$iw$$iw$Person2 {
  private final java.lang.String name;
  private int age;
  public java.lang.String name();
  private int age();
  private void age_$eq(int);
  public $line17.$read$$iw$$iw$Person2(java.lang.String, int);
}
         */

        // primary constructor parameters can also be regular method parameters
        // they may or may not be converted to class fields, it depends on their usage
        class Person3(name: String, age: Int) // no usage => no fields
        class Person4(name: String, age: Int) { // used in method => add field, no getters/setters
            // object-private fields added
            def description = s"${name} is $age years old"
        }
        /*
scala> :javap -private Person3
Compiled from "<console>"
public class $line14.$read$$iw$$iw$Person3 {
  public $line14.$read$$iw$$iw$Person3(java.lang.String, int);
}
scala> :javap -private Person4
Compiled from "<pastie>"
public class $line15.$read$$iw$$iw$Person4 {
  private final java.lang.String name;
  private final int age;
  public java.lang.String description();
  public $line15.$read$$iw$$iw$Person4(java.lang.String, int);
}
         */

        // private primary constructor: user must use aux
        class Person5 private(val id: Int) {
            def this() = this(scala.util.Random.nextInt)
        }
    }

    // nested classes
    def nestedClasses = {
        // you can nest just about anything inside anything

        import scala.collection.mutable
        class Network {
            class Member(val name: String) {
                val contacts = mutable.ArrayBuffer.empty[Member] // this network members
            }
            private val members = mutable.ArrayBuffer.empty[Member]
            def join(name: String) = {
                val m = new Member(name) // create a new member of this network
                members += m // mutator!
                m // return a new member
            }
        }

        // two networks, each instance has its own class Member
        // chatter.Member and myface.Member are different classes
        val chatter = new Network
        val myface = new Network
        val fred = chatter.join("Fred") // chatter.Member
        val wilma = chatter.join("Wilma")
        fred.contacts += wilma // ok
        // fred.contacts += myface.join("Barney") // can't do

        // to allow fred.contacts += myface.join("Barney") you can create companion object:
        // object Network and define class Member there
        // or, you can use a 'type projection' Network#Member = a member of any network
        class Network2 {
            class Member(val name: String) {
                val contacts = mutable.ArrayBuffer.empty[Network2#Member] // any network members
            }
        }
        // do you need 'inner class per object' in your program?

        // self type syntax: access to enclosing class 'this' via alias
        class Network3(val name: String) { outer => // eq Network3.this
            class Member(val name: String) {
                def description = s"member ${name} inside ${outer.name} network"
            }
        }
    }

}

object Classes_Exercises {

    // improve the Counter class: it doesn't turn negative at Int.MaxValue
    def ex1 = {
        ???
    }

    // write class BankAccount: methods deposit, withdraw, read-only property balance
    def ex2 = {
        ???
    }

    // write class Time: read-only properties hours, minutes, method before(other: Time): Boolean
    def ex3 = {
        ???
    }

    // rewrite class Time from ex3: internal repr. is the number of minutes since midnight
    def ex4 = {
        ???
    }

    // write class Student: read-write beans properties 'name', 'id'
    // use javap to check the methods generated
    def ex5 = {
        ???
    }

    // class Person: provide a primary constructor that turns negative age to 0
    def ex6 = {
        ???
    }

    // write class Person: primary constructor("name lastname"); read-only properties firstName, lastName
    def ex7 = {
        ???
    }

    // write class Car: read-only properties for make, model, model year; read-write license plate
    def ex8 = {
        ???
    }

    // reimplement ex8 in java
    def ex9 = {
        ???
    }

    // class Employee: rewrite it to use explicit fields and default primary constructor
    def ex10 = {
        ???
    }

}

class Person {
    var age = 0
    // sbt
    // console
    // import Chapter05.Person
/*
scala> :javap -private Person
Compiled from "Classes.scala"
public class Chapter05.Person {
  private int age;
  public int age();
  public void age_$eq(int);
  public Chapter05.Person();
}
 */
}

class Person2 {
    private var _age = 0
    def age = _age
    def age_=(newAge: Int) = { if (newAge > _age) _age = newAge }
    /*
scala> :javap -private Person2
Compiled from "Classes.scala"
public class Chapter05.Person2 {
  private int _age;
  private int _age();
  private void _age_$eq(int);
  public int age();
  public void age_$eq(int);
  public Chapter05.Person2();
}
     */
}
