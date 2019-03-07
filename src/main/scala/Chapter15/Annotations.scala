package Chapter15

import java.io.IOException
import javax.persistence._
import javax.inject._
import scala.beans._
import scala.annotation.meta._
import scala.annotation._
import scala.annotation.unchecked._
import org.checkerframework.checker.i18n.qual._
import org.junit._
import scala.collection.mutable


object Annotations {
// topics:
    // what are annotations
    // what can be annotated
    // annotations arguments
    // annotation implementations
    // annotations for java features
    // annotations for optimizations
    // annotations for errors and warnings

    // information for compiler or external tools;
    // in form: @annotation(...)
    // java modifiers: @volatile, @transient, @strictfp, @native, @throws
    // @tailrec
    // assert with @elidable
    // @deprecated

    // what are annotations
    def whatAreAnnotations = {
        // syntax is just like in java

        // e.g. java frameworks annotations
        @Test(timeout = 100) def testSomeFeature() = { ??? }
        @Entity class Credentials {
            @Id @BeanProperty var username: String = _
            @BeanProperty var password: String = _
        }

        // scala annotations: processed by scala compiler (plugin)

        // annotations can affect compiled code (in scala)
    }

    // what can be annotated
    def whatCanBeAnnotated = {
        // classes
        // methods
        // fields
        // local variables
        // parameters
        // primary constructor
        // expressions
        // type parameters
        // types

        @Entity class Credentials
        @Test def testFunc() = {}
        @BeanProperty var username = ""
        // def doStuff(@NotNull msg: String) = {}

        // multiple annotations, order doesn't matter
        @BeanProperty @Id var usernamE = ""

        // annotating primary constructor, place annotation before and add parenthesis
        class CredentialS @Inject() (var username: String)

        // with expressions, after a colon
        (Map(1->2).get(3): @unchecked) match { case a => println(a) }

        // type parameters
        // class Container[@specialized T] { ??? }
        // commented because of: compiler [error]   EmptyScope.enter

        // type (after type: method returns a localized string)
        def country: String @Localized = ???
    }

    // annotations arguments
    def annotationsArguments = {
        // annotation can have named arguments
        @Test(timeout=100, expected=classOf[IOException]) def func() = { ??? }

        // if the arg.name is 'value', name can be omitted
        @Named("creds") var credentials = "" // value="creds"

        // if no arguments, the parentheses can be omitted
        @Entity class Credentials

        // most annotation arguments have defaults, see @Test

        // arguments of scala annotations can be of arbitrary types
        // see @deprecatedName('oldname)

        // java anno arg. types:
        // numeric literals; strings; class literals; java enum; other anno; arrays of above
    }

    // annotation implementations
    def annotationImplementations = {
        // anno extends the Annotation trait
        class unchecked extends annotation.Annotation

        // a type anno must extend the TypeAnnotation
        class Localized extends StaticAnnotation with TypeConstraint

        // for a new java anno you have to write a java class

        // field definitions in scala can give rise to multiple features in java
        // e.g.
        // class Credentials(@NotNull @BeanProperty var username: String)
        // there are six items that can be annotation targets:
        // constructor parameter,
        // private field,
        // accessor method,
        // mutator method,
        // bean accessor,
        // bean mutator

        // by default, constructor parameter anno are only applied to the parameter itself,
        // and field anno applyed to the field

        // meta-annotations:
        // @param, @field, @getter, @setter, @beanGetter, @beanSetter
        // cause an anno to be attached elsewhere
        // e.g. @deprecated is defined as
        @getter @setter @beanGetter @beanSetter
        class deprecated(message: String="", since: String="") extends annotation.StaticAnnotation

        // or, you can apply these in an ad-hoc fashion
        @Entity class CredentialS {
            @(Id @beanGetter) @BeanProperty var id = 0
        }
        // @Id is applied to the java getId method
    }

    // annotations for java features
    def annotationsForJavaFeatures = {
        // for interoperating with java

        // java modifiers

        @volatile var done = false // volatile field
        @transient var recent = new mutable.HashMap[String, String] // not serialized
        @strictfp def calc(x: Double) = ??? // IEEE floating-point calculations, slower and less precise but more portable
        @native def win32RegKeys(root: Int, path: String): Array[String] = ??? // implemented in C/C++

        // marker interfaces

        // @cloneable class Employee    // Cloneable
        // @remote ...                  // java.rmi.Remote

        @SerialVersionUID(42L) class Employee extends Serializable

        // checked exceptions

        // if you call scala method from java you have to track exceptions
        // java compiler needs to know
        class Book {
            @throws(classOf[IOException]) def read(filename: String) = { ??? }
        }

        // variable arguments

        // again, if you call scala method from java
        @varargs def process(args: String*) = ??? // w/o anno it will be translated to args: Seq[String]
        // will generate 'void process(String... args)' bridge

        // java beans

        // @BeanProperty var foo = "" // getFoo, setFoo
        // @BoleanBeanProperty ...      // isFoo
        // @BeanDescription, @BeanDisplayName, @BeanInfo, @BeanInfoSkip
    }

    // annotations for optimizations
    def annotationsForOptimizations = {

        // tail recursion

        def sum_notailrec(xs: Seq[Int]): BigInt = if (xs.isEmpty) 0 else xs.head + sum_notailrec(xs.tail)
        def sum_tailrec(xs: Seq[Int], acc: BigInt): BigInt = if (xs.isEmpty) acc else sum_tailrec(xs.tail, acc + xs.head)
        // to force the compiler add @tailrec anno
        //e.g.
        class Util {
            // @tailrec // generate an error in compile time: method can be overridden
            def sum(xs: Seq[Int], acc: BigInt): BigInt =
                if (xs.isEmpty) acc else sum(xs.tail, acc + xs.head)
        }

        // more general mechanism: trampolining

        // run a loop calling functions, each function returns the next function to be called
        // e.g. mutually recursive functions
        import scala.util.control.TailCalls._
        def evenLength(xs: Seq[Int]): TailRec[Boolean] =
            if (xs.isEmpty) done(true) else tailcall(oddLength(xs.tail))
        def oddLength(xs: Seq[Int]): TailRec[Boolean] =
            if (xs.isEmpty) done(false) else tailcall(evenLength(xs.tail))
        val res = evenLength(1 to 1000000).result

        // jump table generation and inlining

        // jump table for switch / match
        (res: @switch) match {
            case true => "true"
            case false => "false"
            case _ => "?"
        }

        // inlining: for scala compiler, not jvm

        // @inline, @noinline

        // eliding methods
        // @elidable methods can be removed in production using compiler flags
        @elidable(500) def dump(props: String): Unit = { ??? }
        // will be replaced with Unit aka () if compiled with
        // scalac -Xelide-below 501 ...

        // constants from
        // import scala.annotation.elidable._
        //  final val ALL     = Int.MinValue  // Level.ALL.intValue()   //  final val MINIMUM = ALL
        //  final val FINEST  = 300           // Level.FINEST.intValue()
        //  final val FINER   = 400           // Level.FINER.intValue()
        //  final val FINE    = 500           // Level.FINE.intValue()
        //  final val CONFIG  = 700           // Level.CONFIG.intValue()
        //  final val INFO    = 800           // Level.INFO.intValue()
        //  final val WARNING = 900           // Level.WARNING.intValue()
        // by default methods below 1000 are elided
        //  final val SEVERE  = 1000          // Level.SEVERE.intValue()
        //  final val ASSERTION = 2000    // we should make this more granular
        //  final val OFF     = Int.MaxValue  // Level.OFF.intValue()   //  final val MAXIMUM = OFF

        // ALL and OFF are confusing
        // @elide(ALL) means that it always elided
        // @elided(OFF) means it never elided
        // but don't use these names in -Xelide-below
        // use MINIMUM, MAXIMUM

        // elidable assert
        assert(res) // to disable assertions compile with -Xelide-below MAXIMUM

        // don't elide non-Unit methods, you'll get ClassCastException

        // specialization for primitive types

        // avoiding wrap/unwrap primitive type values
        // e.g.
        def allDifferent_wrap[T](x: T, y: T, z: T) = x != y && x != z && y != z
        allDifferent_wrap(3, 4, 5) // integers are wrapped into java.lang.Integer
        // in order to generate overloaded versions like
        def allDifferent(x: Int, y: Int, z: Int) = ???
        // use @specialized anno
        def allDiffereNt[@specialized T](x: T, y: T, z: T) = ???

        // with restriction to subset of types
        def allDifferenT[@specialized(Long, Double) T](x: T, y: T, z: T) = ???
        // any subset of Unit, Boolean, Byte, Short, Char, Int, Long, Float, Double
    }

    // annotations for errors and warnings
    def annotationsForErrorsAndWarnings = {
        // @deprecated
        @deprecated(message="use factorial(n: BigInt) instead", since="1.0.1")
        def factorial(n: Int): Int = ???

        // @deprecatedName (argument is a symbol: name of some item in a program)
        def draw(@deprecatedName('sz) size: Int) = ???

        // @deprecatedInheritance
        // @deprecatedOverriding

        // @implicitNotFound, @implicitAmbiguous
        // see chapter 21

        // @unchecked suppresses a warning that a match is not exhaustive
        (List(): @unchecked) match { case h :: t => ??? }

        // @uncheckedVariance suppresses a variance error message
        // e.g. it make sense for java.util.Comparator to be contravariant
        // Comparator[Student] can be used when Comparator[Person] is required
        // but java generics have no variance
        // can fix with @uncheckedVariance
        trait Comparator[-T] extends java.util.Comparator[T @uncheckedVariance]
    }

}

object Annotations_Exercises {

    // 1. Write four JUnit test cases that use the @Test annotation
    // with and without each of its arguments.
    // Run the tests with JUnit.
    def ex1 = {

        @Test(timeout=1042, expected=classOf[IllegalArgumentException])
        def test1() = {
            ???
        }

        @Test(expected=classOf[IllegalArgumentException])
        def test2() = {
            ???
        }

        @Test(timeout=1042)
        def test3() = {
            ???
        }

        @Test
        def test4() = {
            ???
        }

    }

    // 2. Make an example class that shows every possible position of an annotation.
    // Use @deprecated as your sample annotation.
    def ex2 = {
        // classes
        // methods
        // fields
        // local variables
        // parameters
        // primary constructor
        // expressions
        // type parameters
        // types

        @deprecated // class
        class Deprecated @deprecated() // primary constructors
        (@deprecated task: String) { // parameters

            @deprecated // fields
            val id: Int = (task.split(" "): @deprecated // expressions
                ) match {
                case Array(a, b) => 1
                case _  => 0
            }

            @deprecated // methods
            def check: Boolean = {
                @deprecated // local variables
                val localid = id + 1
                localid == 2
            }

            def typeparameters[@deprecated D](d: D): Unit = println(d)

            def types: Deprecated @deprecated = this
        }
    }

    // 3. Which annotations from the Scala library use one of the meta-annotations
    // @param, @field, @getter, @setter, @beanGetter, or @beanSetter?
    def ex3 = {
        import scala.annotation.meta

        @compileTimeOnly(message="under meta") def cto = ???
        @implicitAmbiguous(msg="under meta") def ia = ???
        @deprecated def dep = ???
        @deprecatedInheritance def dih = ???
        @deprecatedName def dn = ???
        @deprecatedOverriding def dor = ???

        // may be other, see https://www.scala-lang.org/api/current/scala/annotation/Annotation.html
        // in 'known subclasses' section
    }

    // 4. Write a Scala method 'sum' with variable integer arguments that returns the sum of its
    // arguments. Call it from Java.
    def ex4 = {
        ???
    }

    // 5. Write a Scala method that returns a string containing all lines of a file.
    // Call it from Java.
    def ex5 = {
        ???
    }

    // 6. Write a Scala object with a volatile Boolean field.
    // Have one thread sleep for some time, then set the field to 'true', print a message, and exit.
    // Another thread will keep checking whether the field is true.
    // If so, it prints a message and exits. If not, it sleeps for a short time and tries again.
    // What happens if the variable is not volatile?
    def ex6 = {
        ???
    }

    // 7. Give an example to show that the tail recursion optimization is not valid when a method can be
    // overridden.
    def ex7 = {
        ???
    }

    // 8. Add the 'allDifferent' method to an object, compile and look at the bytecode.
    // What methods did the @specialized annotation generate?
    def ex8 = {
        ???
    }

    // 9. The Range.foreach method is annotated as @specialized(Unit).
    // Why? Look at the bytecode by running
    //  javap -classpath /path/to/scala/lib/scala-library.jar scala.collection.immutable.Range
    // and consider the @specialized annotations on Function1.
    // Click on the Function1.scala link in Scaladoc to see them.
    def ex9 = {
        ???
    }

    // 10. Add assert(n >= 0) to a factorial method.
    // Compile with assertions enabled and verify that factorial(-1) throws an exception.
    // Compile without assertions. What happens?
    // Use javap to check what happened to the assertion call.
    def ex10 = {
        ???
    }

}
