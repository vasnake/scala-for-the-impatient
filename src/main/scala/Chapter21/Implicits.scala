package Chapter21

import java.io.File

import scala.io.Source

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
    // implicit parameters list: obtained from objects in scope or companion objects;
    // implicit param that is a single-argument func: implicit conversion;
    // type context bound: require the existence of an implicit object;
    // https://stackoverflow.com/a/8535107 it's worth noting that type classes / type traits are infinitely more flexible

    // implicit conversions
    def implicitConversions = {
        import scala.language.implicitConversions
        // is a function declared with the 'implicit' keyword, with a single argument;

        // consider type
        object Fraction { def apply(n: Int, d: Int): Fraction = new Fraction(n, d) }
        class Fraction(n: Int, d: Int) {
            private val num: Int = ???
            private val den: Int = ???
            def *(other: Fraction) = new Fraction(num * other.num, den * other.den)
        }
        // and conversion
        implicit def int2Fraction(n: Int): Fraction = new Fraction(n, 1)
        // and now we can evaluate
        3 * Fraction(4,5)

        // sourceToTarget naming convention;
    }

    // using implicits for enriching existing classes
    def usingImplicitsForEnrichingExistingClasses = {
        // do you ever wish that a class had a method its creator failed to provide?
        // java.io.File.read for example?

        object firstattempt {
            // you can define an enriched class and conversion to it
            class RichFile(val from: File) { def read: String = Source.fromFile(from.getPath).mkString }
            implicit def fileToRichFile(from: File): RichFile = new RichFile(from)
            // now you got it:
            new File("/tmp/test.txt").read
        }

        object secondattempt {
            // you can do better: define an implicit class (primary constructor)
            implicit class RichFile(val from: File) { def read: String = ??? }
            // must have a primary constructor with exactly one argument
            new File("/tmp/test.txt").read
        }

        object evenbetter {
            // it is a good idea to declare the enriched class as a value class
            implicit class RichFile(val from: File) extends AnyVal { def read: String = ??? }
            // file.read is compiled into a static method call
            new File("/tmp/test.txt").read
        }

        // n.b. an implicit class can't be a top-level class

    }

    // importing implicits
    def importingImplicits = {
        // scala search implicit conversions in:
        // - companion objects of source/target types (or type parameters);
        // - defined/imported in scope -- w/o a prefix!;

        object placeone {
            // companion object of a target type
            object Fraction {
                implicit def int2Fraction(n: Int): Fraction = ???
                def apply(n: Int, d: Int): Fraction = ???
            }
            class Fraction(n: Int, d: Int) { def *(other: Fraction): Fraction = ??? }
            // int converted to fraction
            val res: Fraction = 3 * Fraction(4,5)
        }

        object placetwo {
            object Fraction { def apply(n: Int, d: Int): Fraction = ??? }
            class Fraction(n: Int, d: Int) { def *(other: Fraction): Fraction = ??? }
            // conversions in separate namespace
            object FractionConversions { implicit def int2Fraction(n: Int): Fraction = ??? }

            // int converted to fraction imported into scope
            import FractionConversions._
            val res: Fraction = 3 * Fraction(4,5)
        }

        // in REPL you can type ':implicits -v' to see all implicits;

        // select the specific conversions
        object select {
            import scala.language.implicitConversions
            object Fraction { def apply(n: Int, d: Int): Fraction = ??? }
            class Fraction(n: Int, d: Int) { def *(other: Fraction): Fraction = ??? }
            object FractionConversions {
                implicit def int2Fraction(n: Int): Fraction = ???
                implicit def fraction2Double(f: Fraction): Double = ???
            }
            // exclude f2d conversion
            import FractionConversions.{fraction2Double => _, _}
            val res: Fraction = 3 * Fraction(4,5)

            // you can try to use conversion explicitly, to clarify 'strange' compiler behavior
            val x: Double = 3 * FractionConversions.fraction2Double(Fraction(4, 5))
        }

    }

    // rules for implicit conversions
    def rulesForImplicitConversions = {
        // when implicit conversions are attempted:
        // - if the type of an expression differs from expected;
        // - if an object accesses a nonexistent member;
        // - if a method called with parameters of unmatched type;

        // 3 * Fraction // f2d, Int has a method '*(Double)
        // 3.denominator // i2f
        // Fraction * 3 // i2f, Fraction.* method wanted a Fraction

        // implicit conversion is not attempted:
        // - code compiles w/o it;
        // - never multiple conversions;
        // - ambiguous conversions;

        // 3 * Fraction // 3 * f2d wins, it does not require modification of the first object

        // compile 'scalac -Xprint:typer fname.scala' to see src after implicit conversions
        // In SBT just add the following setting: set scalacOptions in (Compile, console) := "-Xprint:typer"
        // in REPL you can use :settings -Xprint:typer

    }

    // implicit parameters
    def implicitParameters = {
        // function/method parameters list marked as 'implicit'
        // compiler will look for default values in scope (a nice way to define different environments)

        // n.b. function is curried
        def quote(what: String)(implicit delims: Delimiters) = delims.left + what + delims.right

        // call with explicit delims
        quote("Bonjour le monde")(Delimiters("«", "»"))

        object delimitersScope {
            // or, call with implicit, searched in scope or companion objects for type/type parameters
            object FrenchPunctuation { implicit val quoteDelimiters = Delimiters("«", "»") }
            import FrenchPunctuation._

            // implicit: search in scope
            quote("Bonjour le monde")
        }

        // companion
        object Delimiters { implicit val quoteDelimiters = Delimiters("«", "»") }
        // implicit: search in companion object
        quote("Bonjour le monde")

        // n.b. only one implicit value for a given type allowed: not a good idea to use common types like:
        // ...(implicit left: String, right: String)

        case class Delimiters(left: String, right: String)
    }

    // implicit conversions with implicit parameters
    def implicitConversionsWithImplicitParameters = {
        // implicit parameter could be a Function1 aka implicit conversion

        // consider
        // def smaller[T](a: T, b: T) = if (a < b) a else b // type T have not '<' method

        object ordered1 {
            // we can add conversion from T to Ordered
            def smaller[T](a: T, b: T)(implicit order: T => Ordered[T]) =
                if (order(a) < b) a else b

            // Predef object defines a lot of implicit values T => Ordered[T]
            smaller(1, 2)
            smaller("Hello", "World")

            // but not for Fraction, you have to define it (in companion object in this case)
            class Fraction(n: Int, d: Int) { def *(other: Fraction): Fraction = ??? }
            object Fraction { def apply(n: Int, d: Int): Fraction = ??? }
            // smaller(Fraction(1,2), Fraction(3,4))
        }

        // the point is: function 'order' is an implicit conversion for 'smaller' scope
        def smaller[T](a: T, b: T)(implicit order: T => Ordered[T]) = if (a < b) a else b
    }

    // context bounds
    def contextBounds = {
        // as you can see, implicit conversion T => M[T] is quite useful;
        // remember 'context bound'? def foo[T : M](...)
        // it tells as that we have the implicit value of type M[T] in scope.

        // consider
        // class Pair[T: Ordering]
        // that require an implicit value of Ordering[T]
        // that value can be used in class methods:
        class Pair[T: Ordering](val first: T, val second: T) {

            // that 'ord' becomes a field of the class?
            def smaller1(implicit ord: Ordering[T]): T =
                if (ord.compare(first, second) < 0) first else second

            // alternatively, you can summon implicit value from nether world using identity function
            def smaller2 =
                if (implicitly[Ordering[T]].compare(first, second) < 0) first else second
            // Predef: def implicitly[T](implicit e: T) = e

            // alternatively, you can take advantage of Ordering => Ordered
            // implicit conversion in Ordered trait
            def smaller3 = { import Ordered._; if (first < second) first else second }
        }
        // you can instantiate Pair[T] whenever there is an implicit value of type Ordering[T]

        // e.g.
        class Fraction(n: Int, d: Int)
        object Fraction { def apply(n: Int, d: Int): Fraction = ??? }
        implicit object FractionOrdering extends Ordering[Fraction] {
            override def compare(x: Fraction, y: Fraction): Int = ???
        }
        new Pair(Fraction(1,2), Fraction(3,4))

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
