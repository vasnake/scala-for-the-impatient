package Chapter18

object TypeParameters {
// topics:
    // generic classes
    // generic functions
    // bounds for type variables
    // view bounds
    // context bounds
    // the ClassTag context bound
    // multiple bounds
    // type constraints
    // variance
    // co- and contravariant positions
    // objects can't be generic
    // wildcards

    // work with multiple types;
    // classes, traits, methods, functions -- can have type parameters;
    // type bounds in form T <: Upper, T >: Lower, T: Context;
    // type constraint T <:< Upper;
    // +T covariance (output), -T contravariance (input)

    // generic classes
    def genericClasses = {
        ???
    }

    // generic functions
    def genericFunctions = {
        ???
    }

    // bounds for type variables
    def boundsForTypeVariables = {
        ???
    }

    // view bounds
    def viewBounds = {
        ???
    }

    // context bounds
    def contextBounds = {
        ???
    }

    // the ClassTag context bound
    def the_ClassTag_contextBound = {
        ???
    }

    // multiple bounds
    def multipleBounds = {
        ???
    }

    // type constraints
    def typeConstraints = {
        ???
    }

    // variance
    def variance = {
        ???
    }

    // co- and contravariant positions
    def co_and_contraVariantPositions = {
        ???
    }

    // objects can't be generic
    def objectsCantBeGeneric = {
        ???
    }

    // wildcards
    def wildcards = {
        ???
    }

}

object TypeParameters_Exercises {

    // 1. Define an immutable class Pair[T, S] with a method swap that returns a new pair with the
    //components swapped.
    def ex1 = {
        ???
    }

    // 2. Define a mutable class Pair[T] with a method swap that swaps the components of the pair.
    def ex2 = {
        ???
    }

    // 3. Given a class Pair[T, S], write a generic method swap that takes a pair as its argument
    //and returns a new pair with the components swapped.
    def ex3 = {
        ???
    }

    // 4. Why don’t we need a lower bound for the replaceFirst method in Section 18.3, “Bounds
    //for Type Variables,” on page 266 if we want to replace the first component of a
    //Pair[Person] with a Student?
    def ex4 = {
        ???
    }

    // 5. Why does RichInt implement Comparable[Int] and not Comparable[RichInt]?
    def ex5 = {
        ???
    }

    // 6. Write a generic method middle that returns the middle element from any Iterable[T].
    //For example, middle("World") is 'r'.
    def ex6 = {
        ???
    }

    // 7. Look through the methods of the Iterable[+A] trait. Which methods use the type parameter
    //A? Why is it in a covariant position in these methods?
    def ex7 = {
        ???
    }

    // 8. In Section 18.10, “Co- and Contravariant Positions,” on page 272, the replaceFirst
    //method has a type bound. Why can’t you define an equivalent method on a mutable Pair[T]?
    //Click here to view code image
    //def replaceFirst[R >: T](newFirst: R) { first = newFirst } // Error
    def ex8 = {
        ???
    }

    // 9. It may seem strange to restrict method parameters in an immutable class Pair[+T].
    //However, suppose you could define
    //def replaceFirst(newFirst: T)
    //in a Pair[+T]. The problem is that this method can be overridden in an unsound way.
    //Construct an example of the problem. Define a subclass NastyDoublePair of
    //Pair[Double] that overrides replaceFirst so that it makes a pair with the square root
    //of newFirst. Then construct the call replaceFirst("Hello") on a Pair[Any] that
    //is actually a NastyDoublePair.
    def ex9 = {
        ???
    }

    // 10. Given a mutable Pair[S, T] class, use a type constraint to define a swap method that can
    //be called if the type parameters are the same.
    def ex10 = {
        ???
    }

}
