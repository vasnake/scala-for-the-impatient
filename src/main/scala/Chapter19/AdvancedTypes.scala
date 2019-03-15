package Chapter19

object AdvancedTypes {
// topics:
    // singleton types
    // type projections
    // paths
    // type aliases
    // structural types
    // compound types
    // infix types
    // existential types
    // the scala type system
    // self types
    // dependency injection
    // abstract types
    // family polymorphism
    // higher-kinded types

    // singleton types
    def singletonTypes = {
        ???
    }

    // type projections
    def typeProjections = {
        ???
    }

    // paths
    def paths = {
        ???
    }

    // type aliases
    def typeAliases = {
        ???
    }

    // structural types
    def structuralTypes = {
        ???
    }

    // compound types
    def compoundTypes = {
        ???
    }

    // infix types
    def infixTypes = {
        ???
    }

    // existential types
    def existentialTypes = {
        ???
    }

    // the scala type system
    def theScalaTypeSystem = {
        ???
    }

    // self types
    def selfTypes = {
        ???
    }

    // dependency injection
    def dependencyInjection = {
        ???
    }

    // abstract types
    def abstractTypes = {
        ???
    }

    // family polymorphism
    def familyPolymorphism = {
        ???
    }

    // higher-kinded types
    def higherKindedTypes = {
        ???
    }

}

object AdvancedTypes_Exercises {

    // 1. Implement a Bug class modeling a bug that moves along a horizontal line. The move method
    //moves in the current direction, the turn method makes the bug turn around, and the show
    //method prints the current position. Make these methods chainable. For example,
    //Click here to view code image
    //bugsy.move(4).show().move(6).show().turn().move(5).show()
    //should display 4 10 5.
    def ex1 = {
        ???
    }

    // 2. Provide a fluent interface for the Bug class of the preceding exercise, so that one can write
    //Click here to view code image
    //bugsy move 4 and show and then move 6 and show turn around move 5 and show
    def ex2 = {
        ???
    }

    // 3. Complete the fluent interface in Section 19.1, “Singleton Types,” on page 280 so that one can
    //call
    //Click here to view code image
    //book set Title to "Scala for the Impatient" set Author to "Cay Horstmann"
    def ex3 = {
        ???
    }

    // 4. Implement the equals method for the Member class that is nested inside the Network class
    //in Section 19.2, “Type Projections,” on page 281. For two members to be equal, they need to be
    //in the same network.
    def ex4 = {
        ???
    }

    // 5. Consider the type alias
    //Click here to view code image
    //type NetworkMember = n.Member forSome { val n: Network }
    //and the function
    //Click here to view code image
    //def process(m1: NetworkMember, m2: NetworkMember) = (m1, m2)
    //How does this differ from the process function in Section 19.8, “Existential Types,” on page
    //286?
    def ex5 = {
        ???
    }

    // 6. The Either type in the Scala library can be used for algorithms that return either a result or
    //some failure information. Write a function that takes two parameters: a sorted array of integers
    //and an integer value. Return either the index of the value in the array or the index of the element
    //that is closest to the value. Use an infix type as the return type.
    def ex6 = {
        ???
    }

    // 7. Implement a method that receives an object of any class that has a method
    //def close(): Unit
    //together with a function that processes that object. Call the function and invoke the close
    //method upon completion, or when any exception occurs.
    def ex7 = {
        ???
    }

    // 8. Write a function printValues with three parameters f, from, to that prints all values of f
    //with inputs from the given range. Here, f should be any object with an apply method that
    //consumes and yields an Int. For example,
    //Click here to view code image
    //printValues((x: Int) => x * x, 3, 6) // Prints 9 16 25 36
    //printValues(Array(1, 1, 2, 3, 5, 8, 13, 21, 34, 55), 3, 6) // Prints 3 5 8 13
    def ex8 = {
        ???
    }

    // 9. Consider this class that models a physical dimension:
    //Click here to view code image
    //abstract class Dim[T](val value: Double, val name: String) {
    //protected def create(v: Double): T
    //def +(other: Dim[T]) = create(value + other.value)
    //override def toString() = s"$value $name"
    //}
    //Here is a concrete subclass:
    //Click here to view code image
    //class Seconds(v: Double) extends Dim[Seconds](v, "s") {
    //override def create(v: Double) = new Seconds(v)
    //}
    //But now a knucklehead could define
    //Click here to view code image
    //class Meters(v: Double) extends Dim[Seconds](v, "m") {
    //override def create(v: Double) = new Seconds(v)
    //}
    //allowing meters and seconds to be added. Use a self type to prevent that.
    def ex9 = {
        ???
    }

    // 10. Self types can usually be replaced with traits that extend classes, but there can be situations
    //where using self types changes the initialization and override orders. Construct such an
    //example.
    def ex10 = {
        ???
    }

}
