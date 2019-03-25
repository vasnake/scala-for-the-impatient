package Chapter21

object Implicits {
// topics
    // implicit conversions
    // using implicits for enriching existing classes
    // importing implicits
    // rules for implicit conversions
    // implicit parameters
    // implicit conversions with implicit parameters
    // context bounds
    // type classes
    // evidence
    // the @implicitNotFound annotation
    // CanBuildFrom demystified

    // useful for building elegant libraries;
    // implicit conversions must be in scope: import them;
    // implicit parameters list: obtained from objects in scope or companion object of the desired type;
    // implicit param that is a single-argument func: implicit conversion;
    // type context bound: require the existence of an implicit object;

    // implicit conversions
    def implicitConversions = {
        ???
    }

    // using implicits for enriching existing classes
    def usingImplicitsForEnrichingExistingClasses = {
        ???
    }

    // importing implicits
    def importingImplicits = {
        ???
    }

    // rules for implicit conversions
    def rulesForImplicitConversions = {
        ???
    }

    // implicit parameters
    def implicitParameters = {
        ???
    }

    // implicit conversions with implicit parameters
    def implicitConversionsWithImplicitParameters = {
        ???
    }

    // context bounds
    def contextBounds = {
        ???
    }

    // type classes
    def typeClasses = {
        ???
    }

    // evidence
    def evidence = {
        ???
    }

    // the @implicitNotFound annotation
    def theImplicitNotFoundAnnotation = {
        ???
    }

    // CanBuildFrom demystified
    def CanBuildFromDemystified = {
        ???
    }

}

object Implicits_Exercises {

    // 1. How does -> work? That is, how can "Hello" -> 42 and 42 -> "Hello" be pairs
    //("Hello", 42) and (42, "Hello")? Hint: Predef.ArrowAssoc.
    def ex1 = {
        ???
    }

    // 2. Define an operator +% that adds a given percentage to a value. For example, 120 +% 10
    //should be 132. Use an implicit class.
    def ex2 = {
        ???
    }

    // 3. Define a ! operator that computes the factorial of an integer. For example, 5.! is 120. Use an
    //implicit class.
    def ex3 = {
        ???
    }

    // 4. Some people are fond of “fluent APIs” that read vaguely like English sentences. Create such an
    //API for reading integers, floating-point numbers, and strings from the console. For example:
    //Read in aString askingFor "Your name" and anInt askingFor "Your
    //age" and aDouble askingFor "Your weight".
    def ex4 = {
        ???
    }

    // 5. Provide the machinery that is needed to compute
    //smaller(Fraction(1, 7), Fraction(2, 9))
    //with the Fraction class of Chapter 11. Supply an implicit class RichFraction that
    //extends Ordered[Fraction].
    def ex5 = {
        ???
    }

    // 6. Compare objects of the class java.awt.Point by lexicographic comparison.
    def ex6 = {
        ???
    }

    // 7. Continue the previous exercise, comparing two points according to their distance to the origin.
    //How can you switch between the two orderings?
    def ex7 = {
        ???
    }

    // 8. Use the implicitly command in the REPL to summon the implicit objects described in
    //Section 21.5, “Implicit Parameters,” on page 328 and Section 21.6, “Implicit Conversions with
    //Implicit Parameters,” on page 329. What objects do you get?
    def ex8 = {
        ???
    }

    // 9. Explain why Ordering is a type class and why Ordered is not.
    def ex9 = {
        ???
    }

    // 10. Generalize the average method in Section 21.8, “Type Classes,” on page 331 to a Seq[T].
    def ex10 = {
        ???
    }

    // 11. Make String a member of the NumberLike type class in Section 21.8, “Type Classes,” on
    //page 331. The divBy method should retain every nth letter, so that average("Hello",
    //"World") becomes "Hlool".
    def ex11 = {
        ???
    }

    // 12. Look up the =:= object in Predef.scala. Explain how it works.
    def ex12 = {
        ???
    }

    // 13. The result of "abc".map(_.toUpper) is a String, but the result of
    //"abc".map(_.toInt) is a Vector. Find out why.
    def ex13 = {
        ???
    }

}
