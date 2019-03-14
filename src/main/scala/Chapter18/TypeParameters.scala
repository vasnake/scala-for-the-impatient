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
        // use square brackets after name, for type parameters
        class Pair[T, S](val first: T, val second: S)

        // a class with one or more type parameters is generic

        // let scala infer types; or specify explicitly
        val p1 = new Pair(42, "foo")
        val p2 = new Pair[Any, Any](42, "foo")

    }

    // generic functions
    def genericFunctions = {
        // not only classes can have type parameters
        def getMiddle[T](a: Array[T]) = a(a.length / 2)

        // scala infers the type
        val ms = getMiddle(Array("Mary", "had", "a", "little", "lamb"))

        // of you can specify
        val mf = getMiddle[String] _ // Array[String] => String
    }

    // bounds for type variables
    def boundsForTypeVariables = {
        // sometimes you need some restrictions or insurance about type;
        // bounds tell compiler about some expected properties of types

        // e.g. we want to be able to compare values
        class Pair_wrong[T](val first: T, val second: T) {
            // def smaller = if (first.compareTo(second) < 0) first else second // error
            // no way to know if T has 'compareTo'
        }

        // let's add an upper bound

        // T must be a subtype of Comparable[T]
        class Pair[T <: Comparable[T]](val first: T, val second: T) {
            def smaller = if (first.compareTo(second) < 0) first else second
        }

        // good for strings
        val p1 = new Pair("Fred", "Brooks")
        p1.smaller // Brooks

        // bad for URL // inferred type arguments [java.net.URL] do not conform to class Pair's type parameter bounds [T <: Comparable[T]]
        // val p2 = new Pair(new java.net.URL(""), new java.net.URL(""))

        // n.b. Pair[Int] won't work either, only RichIng have compareTo

        // when do you need a lower bound?

        // T must be a supertype of X
        // e.g. we have some update method
        class Pair_m[T](val first: T, val second: T) {
            def replaceFirst(newFirst: T) = new Pair_m(newFirst, second)
        }
        // OK so far, now we want to use person-student hierarchy
        class Person; class Student extends Person
        // and we want to replace student with a person
        // it should be ok, if method return pair of persons.
        // val sp: Pair_m[Student] = ???
        // val rp = sp.replaceFirst(new Person) // error

        // n.b. replace person with a student is OK because student is a person
        val sp: Pair_m[Person] = ???
        val rp = sp.replaceFirst(new Student) // OK

        // enters lower bound [R >: T]
        class Pair_lb[T](val first: T, val second: T) {
            // it allows to cast 'second' from student to person
            def replaceFirst[R >: T](newFirst: R) = new Pair_lb[R](newFirst, second)
        }
        val x: Pair_lb[Student] = ???
        val y = x.replaceFirst(new Person) // OK, Pair_lb[Person]

        // n.b. if you omit the lower bound
        // def replaceFirst[R](newFirst: R) = new Pair(newFirst, second)
        // it will yield a Pair[Any], because Any is a common supertype
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

    // 1. Define an immutable class Pair[T, S] with a method 'swap' that returns a new pair with the
    // components swapped.
    def ex1 = {
        ???
    }

    // 2. Define a mutable class Pair[T] with a method 'swap' that swaps the components of the pair.
    def ex2 = {
        ???
    }

    // 3. Given a class Pair[T, S], write a generic method 'swap' that takes a pair as its argument
    // and returns a new pair with the components swapped.
    def ex3 = {
        ???
    }

    // 4. Why don’t we need a lower bound for the 'replaceFirst' method in
    // Section 18.3, “Bounds for Type Variables,” on page 266
    // if we want to replace the first component of a Pair[Person] with a Student?
    def ex4 = {
        ???
    }

    // 5. Why does RichInt implement Comparable[Int] and not Comparable[RichInt]?
    def ex5 = {
        ???
    }

    // 6. Write a generic method 'middle' that returns the middle element from any Iterable[T].
    // For example, middle("World") is 'r'.
    def ex6 = {
        ???
    }

    // 7. Look through the methods of the Iterable[+A] trait.
    // Which methods use the type parameter A?
    // Why is it in a covariant position in these methods?
    def ex7 = {
        ???
    }

    // 8. In Section 18.10, “Co- and Contravariant Positions,” on page 272,
    // the 'replaceFirst' method has a type bound.
    // Why can’t you define an equivalent method on a mutable Pair[T]?
    //  def replaceFirst[R >: T](newFirst: R) { first = newFirst } // Error
    def ex8 = {
        ???
    }

    // 9. It may seem strange to restrict method parameters in an immutable class Pair[+T].
    // However, suppose you could define
    //  def replaceFirst(newFirst: T)
    // in a Pair[+T].
    // The problem is that this method can be overridden in an unsound way.
    //
    // Construct an example of the problem.
    // Define a subclass NastyDoublePair of Pair[Double] that overrides
    // replaceFirst so that it makes a pair with the square root of newFirst.
    // Then construct the call replaceFirst("Hello") on a Pair[Any] that
    // is actually a NastyDoublePair.
    def ex9 = {
        ???
    }

    // 10. Given a mutable Pair[S, T] class, use a type constraint to define a 'swap' method
    // that can be called if the type parameters are the same.
    def ex10 = {
        ???
    }

}
