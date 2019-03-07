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
import scala.concurrent.Future


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
        new Ex4
    }
    class Ex4 {
        @varargs def sum(xs: Int*): Int = xs.sum
    }

    // 5. Write a Scala method that returns a string containing all lines of a file.
    // Call it from Java.
    def ex5 = {
        new Ex5
    }
    class Ex5 {
        @throws(classOf[java.io.IOException])
        def textFromFile(path: String): String = {
            scala.io.Source.fromFile(path).mkString
        }
    }

    // 6. Write a Scala object with a volatile Boolean field.
    // Have one thread sleep for some time, then set the field to 'true', print a message, and exit.
    // Another thread will keep checking whether the field is true.
    // If so, it prints a message and exits. If not, it sleeps for a short time and tries again.
    // What happens if the variable is not volatile?
    def ex6 = {
        // volatile keyword is used to mark a Java variable as "being stored in main memory".
        // More precisely that means, that every read of a volatile variable will be read from the
        // computer's main memory, and not from the CPU cache, and that every write to a
        // volatile variable will be written to main memory, and not just to the CPU cache

        @volatile var flag = false
        // Here, the volatile serves as a hint to the compiler to be a bit more careful with optimizations
        // no cached values:
        // scala> Chapter15.Annotations_Exercises.ex6
        // after 1 second:
        //thread1: flag is set, bye
        //thread2: flag is set, positive; bye

        // var flag = false
        // exactly the same on my workstation,
        // because in this scenario no race condition or deadlocks or other shit happens.
        // a biggest effect here could be only if compiler/jvm decided to cache our 'flag'.
        // another case if read happens in the same time as write,
        // or a tick later. In this case we have to wait another cycle before next read.

        def javaThread() = {
            new Thread {
                override def run(): Unit = {
                    println("thread1: going to sleep ...")
                    Thread.sleep(999)
                    flag = true
                    println("thread1: flag is set, bye")
                }
            }.start()
        }

        def scalaFuture() = {
            import scala.concurrent.ExecutionContext.Implicits.global
            Future {
                println("future1: going to sleep ...")
                Thread.sleep(998)
                flag = true
                println("future1: flag is set, bye")
            }
        }

        javaThread()
        scalaFuture()

        for (ms <- 50 to 1 by -1; if !flag) { // total_ms = triangle_number = n * (n + 1) / 2
            println(s"thread2: will sleep $ms millis")
            Thread.sleep(ms)
            if (flag) println("thread2: flag is set, positive; bye")
        }

        // http://tutorials.jenkov.com/java-concurrency/volatile.html
        // https://alvinalexander.com/scala/how-to-create-java-thread-runnable-in-scala
        // https://alvinalexander.com/scala/differences-java-thread-vs-scala-future
    }

    // 7. Give an example to show that the tail recursion optimization is not valid when a method can be
    // overridden.
    def ex7 = {

        class Demo1 {
            // @tailrec // compiler error
            def func(xs: List[Int], acc: Int = 0): Int = xs match {
                case Nil => { println(s"tailrec func done: $acc"); acc }
                case h :: t => func(t, h + acc)
            }

            @tailrec // can't override, will be a loop
            final def loopfunc(xs: List[Int], acc: Int = 0): Int = xs match {
                case Nil => { println(s"loopfunc func done: $acc"); acc }
                case h :: t => loopfunc(t, h + acc)
            }
        }

        class Demo2 extends Demo1 {
            override def func(xs: List[Int], acc: Int = 0): Int = xs match {
                case Nil => { println(s"recursive func done"); 0 }
                case h :: t => h + func(t)
            }
        }

        def getObj(version: Int): Demo1 = version match {
            case 1 => new Demo1
            case 2 => new Demo2
            case _ => throw new IllegalArgumentException(s"unknown version: ${version}")
        }

        val d: Demo1 = getObj(1 + scala.util.Random.nextInt(2))
        d.func(List(1, 2, 3)) // tailrec or not tailrec? who knows?
    }

    // 8. Add the 'allDifferent' method to an object, compile and look at the bytecode.
    // What methods did the @specialized annotation generate?
    def ex8 = {

        class NotSpecialized {
            def allDifferent[T](x: T, y: T, z: T) = x != y && x != z && y != z
        }
        // scala> :javap -cp NotSpecialized
        //public class $line2.$read$$iw$$iw$NotSpecialized {
        //  public <T> boolean allDifferent(T, T, T);
        //  public $line2.$read$$iw$$iw$NotSpecialized();

        class Specialized {
            def allDifferent[@specialized T](x: T, y: T, z: T) = x != y && x != z && y != z
        }
        // scala> :javap -cp Specialized
        //public class $line3.$read$$iw$$iw$Specialized {
        //  public <T> boolean allDifferent(T, T, T);
        //  public boolean allDifferent$mZc$sp(boolean, boolean, boolean);
        //  public boolean allDifferent$mBc$sp(byte, byte, byte);
        //  public boolean allDifferent$mCc$sp(char, char, char);
        //  public boolean allDifferent$mDc$sp(double, double, double);
        //  public boolean allDifferent$mFc$sp(float, float, float);
        //  public boolean allDifferent$mIc$sp(int, int, int);
        //  public boolean allDifferent$mJc$sp(long, long, long);
        //  public boolean allDifferent$mSc$sp(short, short, short);
        //  public boolean allDifferent$mVc$sp(scala.runtime.BoxedUnit, scala.runtime.BoxedUnit, scala.runtime.BoxedUnit);
        //  public $line3.$read$$iw$$iw$Specialized();
    }

    // 9. The Range.foreach method is annotated as @specialized(Unit).
    // Why? Look at the bytecode by running
    //  javap -classpath /path/to/scala/lib/scala-library.jar scala.collection.immutable.Range
    // and consider the @specialized annotations on Function1.
    // Click on the Function1.scala link in Scaladoc to see them.
    def ex9 = {
        // because 'foreach' apply Function1[T, Unit]
        // and Function1 specialized itself, so
        // for primitive types 'foreach' will call specialized function: primitive => Unit

        // scala> :javap -cp scala.collection.immutable.Range
        //Compiled from "Range.scala"
        //public class scala.collection.immutable.Range extends scala.collection.AbstractSeq<java.lang.Object> implements scala.collection.immutable.IndexedSeq<java.lang.Object>, scala.collection.CustomParallelizable<java.lang.Object, scala.collection.parallel.immutable.ParRange>, scala.Serializable {
        //  public static final long serialVersionUID;
        //
        //  public final <U> void foreach(scala.Function1<java.lang.Object, U>);
        //    Code:
        //       0: aload_0
        //       1: invokevirtual #172                // Method isEmpty:()Z
        //       4: ifne          42
        //       7: aload_0
        //       8: invokevirtual #148                // Method start:()I
        //      11: istore_2
        //      12: aload_1
        //      13: iload_2
        //      14: invokestatic  #253                // Method scala/runtime/BoxesRunTime.boxToInteger:(I)Ljava/lang/Integer;
        //      17: invokeinterface #306,  2          // InterfaceMethod scala/Function1.apply:(Ljava/lang/Object;)Ljava/lang/Object;
        //      22: pop
        //      23: iload_2
        //      24: aload_0
        //      25: invokevirtual #189                // Method scala$collection$immutable$Range$$lastElement:()I
        //      28: if_icmpne     32
        //      31: return
        //      32: iload_2
        //      33: aload_0
        //      34: invokevirtual #154                // Method step:()I
        //      37: iadd
        //      38: istore_2
        //      39: goto          12
        //      42: return
        //
        //  public final void foreach$mVc$sp(scala.Function1<java.lang.Object, scala.runtime.BoxedUnit>);
        //    Code:
        //       0: aload_0
        //       1: invokevirtual #172                // Method isEmpty:()Z
        //       4: ifne          38
        //       7: aload_0
        //       8: invokevirtual #148                // Method start:()I
        //      11: istore_2
        //      12: aload_1
        //      13: iload_2
        //      14: invokeinterface #436,  2          // InterfaceMethod scala/Function1.apply$mcVI$sp:(I)V
        //      19: iload_2
        //      20: aload_0
        //      21: invokevirtual #189                // Method scala$collection$immutable$Range$$lastElement:()I
        //      24: if_icmpne     28
        //      27: return
        //      28: iload_2
        //      29: aload_0
        //      30: invokevirtual #154                // Method step:()I
        //      33: iadd
        //      34: istore_2
        //      35: goto          12
        //      38: return
    }

    // 10. Add assert(n >= 0) to a factorial method.
    // Compile with assertions enabled and verify that factorial(-1) throws an exception.
    // Compile without assertions. What happens?
    // Use javap to check what happened to the assertion call.
    def ex10 = {
        ???
    }

}
