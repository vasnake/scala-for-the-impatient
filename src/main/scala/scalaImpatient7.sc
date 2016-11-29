// Scala for the Impatient
// Chapter 7. Packages and Imports
/*
• Packages nest just like inner classes.
• Package paths are not absolute.
• A chain x.y.z in a package clause leaves the intermediate packages x and x.y invisible.
• Package statements without braces at the top of the file extend to the entire file.
• A package object can hold functions and variables.
• Import statements can import packages, classes, and objects.
• Import statements can be anywhere.
• Import statements can rename and hide members.
• java.lang, scala, and Predef are always imported.
 */


/*
//To add items to a package, you can include them in package statements, such as:

package com {
    package horstmann {
        package impatient {
            class Employee
        }
    }
}
//Then the class name Employee can be accessed anywhere as com.horstmann.impatient.Employee.
 */

/*
Unlike the definition of an object or a class, a package can be defined in multiple files.
The preceding code might be in a file Employee.scala, and a file Manager.scala might contain

package com {
  package horstmann {
    package impatient {
      class Manager
    }
  }
}
 */

// You don’t have to put Employee.scala and Manager.scala into a com/horstmann/impatient directory.

/*
Conversely, you can contribute to more than one package in a single file.
The file Employee.scala can contain

package com {
  package horstmann {
    package impatient {
      class Employee
    }
  }
}

package org {
  package bigjava {
    class Counter
  }
}
 */

/*
Scala packages nest just like all other scopes.
You can access names from the enclosing scope. For example,

package com {
  package horstmann {
    object Utils {
      def percentOf(value: Double, rate: Double) = value * rate / 100
    }

    package impatient {
      class Employee {
        def giveRaise(rate: scala.Double) {
          salary += Utils.percentOf(salary, rate)
        }
      }
    }
  }
}
Note the Utils.percentOf qualifier.
The Utils class was defined in the parent package.
Everything in the parent package is in scope, and it is not necessary to use
com.horstmann.Utils.percentOf.
 */

/*
There is a fly in the ointment, however. Consider

package com {
  package horstmann {
    package impatient {
      class Manager {
        val subordinates = new collection.mutable.ArrayBuffer[Employee]
      }
    }
  }
}
This code takes advantage of the fact that the scala package is always imported.
Therefore, the collection package is actually scala.collection.
And now suppose someone introduces the following package, perhaps in a different file:

package com {
  package horstmann {
    package collection {

    }
  }
}
Now the Manager class no longer compiles.
It looks for a mutable member inside the com.horstmann.collection package and doesn’t find it.
 */
// in Scala, package names are relative, just like inner class names

/*
One solution is to use absolute package names, starting with _root_, for example:

val subordinates = new _root_.scala.collection.mutable.ArrayBuffer[Employee]

Another approach is to use “chained” package clauses
 */

/*
A package clause can contain a “chain,” or path segment, for example:

package com.horstmann.impatient {
  // Members of com and com.horstmann are not visible here
  package people {
    class Person
  }
}
Such a clause limits the visible members.
Now a com.horstmann.collection package would no longer be accessible as collection.
 */

/*
Instead of the nested notation that we have used up to now,
you can have package clauses at the top of the file, without braces. For example:

package com.horstmann.impatient
package people

class Person

This is equivalent to
package com.horstmann.impatient {
  package people {
    class Person
    // Until the end of the file
  }
}
This is the preferred notation if all the code in the file belongs to the
same package (which is the usual case).
Note that in the example above, everything in the file belongs to the package
com.horstmann.impatient.people, but the package
com.horstmann.impatient has also been opened up so you can refer to its contents.
 */

// A package can contain classes, objects, and traits,
// but not the definitions of functions or variables
// That’s an unfortunate limitation of the Java virtual machine

/*
Package objects address this limitation.
Every package can have one package object.
You define it in the parent package, and it has the same name as the child package

package com.horstmann.impatient

package object people {
  val defaultName = "John Q. Public"
}

package people {
  class Person {
    var name = defaultName // A constant from the package
  }
}
 */
// Behind the scenes, the package object gets compiled into a
// JVM class with static methods and fields, called package.class
// In our example, that would be a class
// com.horstmann.impatient.people.package with a static field defaultName

/*
In Java, a class member that isn’t declared as public, private, or protected is visible
in the package containing the class.
In Scala, you can achieve the same effect with qualifiers.
The following method is visible in its own package:

package com.horstmann.impatient.people

class Person {
  private[people] def description = "A person with name " + name
}

You can extend the visibility to an enclosing package:

private[impatient] def description = "A person with name " + name
 */

/*
Imports let you use short names instead of long ones. With the clause
import java.awt.Color
you can write Color in your code instead of java.awt.Color
 */
{
    import java.awt.Color
}

/*
You can import all members of a package as
import java.awt._
This is the same as the * wildcard in Java
 */
{
    import java.awt._
}

/*
You can also import all members of a class or object.

import java.awt.Color._
val c1 = RED // Color.RED
val c2 = decode("#ff0000") // Color.decode

This is like import static in Java.
Java programmers seem to live in fear of this variant,
but in Scala it is commonly used.
 */
{
    import java.awt.Color._
    val c1 = RED // Color.RED
    val c2 = decode("#ff0000") // Color.decode

}

// In Scala, an import statement can be anywhere, not just at the top of a file.
// The scope of the import statement extends until the end of the enclosing block.

/*
If you want to import a few members from a package, use a selector like this:
import java.awt.{Color, Font}
The selector syntax lets you rename members:
import java.util.{HashMap => JavaHashMap}
import scala.collection.mutable._
 */
{
    import java.awt.{Color, Font}
    import java.util.{HashMap => JavaHashMap}
    import scala.collection.mutable._
}
/*
The selector HashMap => _ hides a member instead of renaming it. This is only useful if you import others:
import java.util.{HashMap => _, _}
import scala.collection.mutable._
Now HashMap unambiguously refers to scala.collection.mutable.HashMap since java.util.HashMap is hidden
 */
{
    import java.util.{HashMap => _, _}
    import scala.collection.mutable._
}

/*
Implicit Imports
Every Scala program implicitly starts with
import java.lang._
import scala._
import Predef._
 */
{
    import java.lang._
    import scala._
    import Predef._
}

// Exercises

/*
1. Write an example program to demonstrate that
package com.horstmann.impatient
is not the same as
package com
package horstmann
package impatient

package com.horstmann.impatient
val coll = collection.mutable.Map.empty[Int]

package com
package horstmann
package impatient
val coll = collection.mutable.Map.empty[Int] // collection from com.horstman

package com.horstmann.collection
// oops
 */

/*
2. Write a puzzler that baffles your Scala friends,
using a package com that isn’t at the top level.

package horstmann.com
object Nuts

package horstmann
import com.Nuts._
 */

/*
3. Write a package random with functions
nextInt(): Int,
nextDouble(): Double, and
setSeed(seed: Int): Unit.
To generate random numbers, use the linear congruential generator
next = (previous × a + b) mod 2^n,
where a = 1664525, b = 1013904223, n = 32, and the inital value of previous is seed.
 */

/*
// package vasnake

package object random {
    private val a = 1664525
    private val b = 1013904223
    private val n = 32
    private var prev = compat.Platform.currentTime.toDouble
    private def nextRnd: Double = {
        prev = (prev * a + b) % math.pow(2, n)
        prev
    }
    def nextInt(): Int = nextRnd.toInt
    def nextDouble(): Double = nextRnd
    def setSeed(seed: Int): Unit = prev = seed
}
// package random

 */

/*
4. Why do you think the Scala language designers provided the package object syntax
instead of simply letting you add functions and variables to a package?
 */
// JVM limitations

/*
5. What is the meaning of private[com] def giveRaise(rate: Double)? Is it useful?
 */
// method accessible only for 'com' package members. Yes, for fine-grained access control.

/*
6. Write a program that copies all elements from a Java hash map into a Scala hash map.
Use imports to rename both classes.
 */
{
    import java.util.{HashMap => juHashMap}
    import collection.mutable.{HashMap => cmHashMap, Map}
    import collection.JavaConversions.mapAsScalaMap
    def copy[T, S](a: Map[T, S], b: cmHashMap[T, S]): Unit = {
        for ((k,v) <- a) b.update(k, v)
    }
    val jhm: Map[String, Int] = new juHashMap[String, Int]()
    jhm.put("one", 1)
    jhm.put("two", 2)
    val shm = cmHashMap.empty[String, Int]
    copy(jhm, shm)
    for ((k,v) <- shm) println(s"$k: $v")
}

/*
7. In the preceding exercise, move all imports into the innermost scope possible.
 */
{
    object Maps {
        import java.util.{HashMap => juHashMap}
        import collection.mutable.{HashMap => cmHashMap, Map}
        def copy[T, S](a: Map[T, S], b: cmHashMap[T, S]): Unit = {
            for ((k,v) <- a) b.update(k, v)
        }
    }

    def test = {
        import java.util.{HashMap => juHashMap}
        import collection.mutable.{HashMap => cmHashMap, Map}
        import collection.JavaConversions.mapAsScalaMap

        val jhm: Map[String, Int] = new juHashMap[String, Int]()
        jhm.put("one", 1)
        jhm.put("two", 2)
        val shm = cmHashMap.empty[String, Int]
        Maps.copy(jhm, shm)
        for ((k, v) <- shm) println(s"$k: $v")
    }

    test
}

/*
8. What is the effect of import java._ import javax._ Is this a good idea?
 */
object q8 {
    import java._
    import javax._
    // same names in both packages, javax._ will redefine java._ names
    // in these packages contains a lot of names, do you need them all?
    // bad idea
}

/*
9. Write a program that imports the java.lang.System class,
reads the user name from the user.name system property,
reads a password from the Console object,
and prints a message to the standard error stream if the password is not "secret".
Otherwise, print a greeting to the standard output stream.
Do not use any other imports, and do not use any qualified names (with dots).
 */
object ch7ex9 extends App {
    import java.lang.{System => jls}
    val uname = jls.getProperty("user.name", "John/Jane Doe")
    println(s"your name is $uname and your password is (it's a question):")
    val pwd = jls.console().readPassword().mkString("")
    if (pwd == "secret") jls.out.print(s"Hello $uname, welcome back!")
    else jls.err.print(s"$uname, you should run away now.")
}

/*
10. Apart from StringBuilder, what other members of java.lang does the scala package override?

Boolean, Byte, Long, ...
http://www.scala-lang.org/api/2.11.8/#scala.package
http://docs.oracle.com/javase/8/docs/api/
 */
