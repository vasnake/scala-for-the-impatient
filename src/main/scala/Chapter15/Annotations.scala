package Chapter15

import scala.beans._
import javax.persistence._
import javax.inject._
import org.checkerframework.checker.i18n.qual._
import org.junit._


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
        // expressions
        // type parameters
        // types

        @Entity class Credentials
        @Test def testFunc() = {}
        @BeanProperty var username = ""
        def doStuff(@NotNull msg: String) = {}

        // multiple annotations, order doesn't matter
        @BeanProperty @Id var usernamE = ""

        // annotating primary constructor, place annotation before and add parenthesis
        class CredentialS @Inject() (var username: String)

        // with expressions, after a colon
        (Map(1->2).get(3): @unchecked) match { case a => println(a) }

        // type parameters
        class Container[@specialized T] { ??? }

        // type (after type: method returns a localized string)
        def country: String @Localized = ???
    }

    // annotations arguments
    def annotationsArguments = {
        ???
    }

    // annotation implementations
    def annotationImplementations = {
        ???
    }

    // annotations for java features
    def annotationsForJavaFeatures = {
        ???
    }

    // annotations for optimizations
    def annotationsForOptimizations = {
        ???
    }

    // annotations for errors and warnings
    def annotationsForErrorsAndWarnings = {
        ???
    }

}

object Annotations_Exercises {

    // 1. Write four JUnit test cases that use the @Test annotation
    // with and without each of its arguments.
    // Run the tests with JUnit.
    def ex1 = {
        ???
    }

    // 2. Make an example class that shows every possible position of an annotation.
    // Use @deprecated as your sample annotation.
    def ex2 = {
        ???
    }

    // 3. Which annotations from the Scala library use one of the meta-annotations
    // @param, @field, @getter, @setter, @beanGetter, or @beanSetter?
    def ex3 = {
        ???
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
