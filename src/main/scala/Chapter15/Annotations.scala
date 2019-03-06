package Chapter15

object Annotations {
// topics:
    // what are annotations
    // what can be annotated
    // annotations arguments
    // annotation implementations
    // annotations for java features
    // annotations for optimizations
    // annotations for errors and warnings

    //

    // what are annotations
    def whatAreAnnotations = {
        ???
    }

    // what can be annotated
    def whatCanBeAnnotated = {
        ???
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

    // 1. Write four JUnit test cases that use the @Test annotation with and without each of its
    // arguments. Run the tests with JUnit.
    def ex1 = {
        ???
    }

    // 2. Make an example class that shows every possible position of an annotation. Use
    //@deprecated as your sample annotation.
    def ex2 = {
        ???
    }

    // 3. Which annotations from the Scala library use one of the meta-annotations @param, @field,
    //@getter, @setter, @beanGetter, or @beanSetter?
    def ex3 = {
        ???
    }

    // 4. Write a Scala method sum with variable integer arguments that returns the sum of its
    //arguments. Call it from Java.
    def ex4 = {
        ???
    }

    // 5. Write a Scala method that returns a string containing all lines of a file. Call it from Java.
    def ex5 = {
        ???
    }

    // 6. Write a Scala object with a volatile Boolean field. Have one thread sleep for some time, then
    //set the field to true, print a message, and exit. Another thread will keep checking whether the
    //field is true. If so, it prints a message and exits. If not, it sleeps for a short time and tries
    //again. What happens if the variable is not volatile?
    def ex6 = {
        ???
    }

    // 7. Give an example to show that the tail recursion optimization is not valid when a method can be
    //overridden.
    def ex7 = {
        ???
    }

    // 8. Add the allDifferent method to an object, compile and look at the bytecode. What
    //methods did the @specialized annotation generate?
    def ex8 = {
        ???
    }

    // 9. The Range.foreach method is annotated as @specialized(Unit). Why? Look at the
    //bytecode by running
    //Click here to view code image
    //javap -classpath /path/to/scala/lib/scala-library.jar
    //scala.collection.immutable.Range
    //and consider the @specialized annotations on Function1. Click on the
    //Function1.scala link in Scaladoc to see them.
    def ex9 = {
        ???
    }

    // 10. Add assert(n >= 0) to a factorial method. Compile with assertions enabled and
    //verify that factorial(-1) throws an exception. Compile without assertions. What
    //happens? Use javap to check what happened to the assertion call.
    def ex10 = {
        ???
    }

}
