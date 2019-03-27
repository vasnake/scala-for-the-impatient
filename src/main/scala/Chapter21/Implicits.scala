package Chapter21

import java.io.File

import scala.annotation.{implicitNotFound, tailrec}
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
            // implicit class RichFile(val from: File) extends AnyVal { def read: String = ??? }
            // but: value class may not be a member of another class

            // file.read is compiled into a static method call
            // new File("/tmp/test.txt").read
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
        val p = new Pair(Fraction(1,2), Fraction(3,4))

    }

    // type classes
    def typeClasses = {
        // in OOP design, to use a function 'smaller' aka 'algorithm', we have to extend some trait;
        // but using 'implicit conversions' or, better 'context bounds aka implicit values'
        // we can use algorithm w/o changing the class. as-hoc polymorphism, very useful.

        // a trait such as Ordering (abstract generic interface, de-facto) is called a
        // type class.

        // a type class defines some behaviour and a type can join the class by providing that behaviour

        // example: compute averages, to do so, we need sum and divide;

        // define a type class:
        trait NumberLike[T] {
            def plus(x: T, y: T): T
            def divideBy(x: T, n: Int): T
        }

        // add some members to type class
        object NumberLike {
            implicit object NumberLikeDouble extends NumberLike[Double] {
                override def plus(x: Double, y: Double): Double = x + y
                override def divideBy(x: Double, n: Int): Double = x / n
            }
        }

        // calc averages using the NumberLike type class;

        // let's use implicit parameter: evidence
        def average[T](x: T, y: T)(implicit ev: NumberLike[T]): T =
            ev.divideBy(ev.plus(x, y), 2)

        // or, using context bound
        def average2[T: NumberLike](x: T, y: T): T = {
            val ev = implicitly[NumberLike[T]]
            ev.divideBy(ev.plus(x, y), 2)
        }

        // how Point can join a type class?
        class Point(val x: Double, val y: Double) { ??? }
        object Point {
            def apply(x: Double,y: Double): Point = new Point(x, y)
            // by defining an implicit object of type NumberLike[Point]
            implicit object NumberLikePoint extends NumberLike[Point] {
                override def plus(x: Point, y: Point): Point = ???
                override def divideBy(x: Point, n: Int): Point = ???
            }
        }

        // scala stdlib useful type classes:
        // Equiv, Ordering, Numeric, Fractional, Hashing, IsTraversableOnce, IsTraversableLike, etc.

    }

    // evidence
    def evidence = {
        // evidence object: its existence is evidence of the fact that type T related to type U by
        // type constraints:
        // T =:= U
        // T <:< U
        // T => U

        // for using such a type constraints, you supply an implicit parameter, evidence
        // e.g.
        def firstLast[A, C](it: C)(implicit ev: C <:< Iterable[A]) = {
            // here ev is an identity function: implicit conversion from C to Iterable[A]
            (it.head, it.last)
        }

        // =:= and <:< are classes with implicit values, defined in the Predef
        object explained {
            // trick in variance
            abstract class <:<[-From, +To] extends Function1[From, To]
            object <:< { implicit def conforms[A] = new (A <:< A) { def apply(x: A) = x } }
            // suppose the compiler processes a constraint 'implicit ev: String <:< AnyRef';
            // then object
            implicit val stringidentity = <:<.conforms[String]
            // is usable as a String <:< AnyRef instance, because of variance;
            // evidence of the fact that String is a subtype of AnyRef
        }
    }

    // the @implicitNotFound annotation
    def theImplicitNotFoundAnnotation = {
        // you can give a useful error message when implicit not found

        // e.g.
        // @implicitNotFound(msg = "Cannot prove that ${From} <:< ${To}.")
        // abstract class <:<[-From, +To] extends (From => To)
        def firstLast[A, C](it: C)(implicit ev: C <:< Iterable[A]) = ???

        // and if you call
        // firstLast[String, List[Int]](List(1,2,3))
        // then error message is
        // Cannot prove that List[Int] <:< Iterable[String].
    }

    // CanBuildFrom demystified
    def CanBuildFromDemystified = {
        // about implicit parameter: collection buffer builder in collections lib.
        // collection builder factories.

        // implicit parameter CanBuildFrom[Repr, B, That] locates a factory object,
        // that can produce a builder for target collection;
        // the builder factory is defined as implicit in the companion of Repr.

        // consider the 'map' method of Iterable[A, Repr]
        // def map[B, That](f: A => B)(implicit bf: CanBuildFrom[Repr, B, That]): That = {
        //      val builder = bf() ... builder += f(next) ... builder.result }

        // Repr is used when collection is not primitive, e.g. Range or String, for them
        // ArrayBuffer will be used.

        // trait CanBuildFrom[From, E, To] provides evidence that it is possible to create a collection
        // of type To, holding values of type E, implemented as From

        // CanBuildFrom trait has an apply method: Builder[E, To]
        // Builder has += method for adding elements into an internal buffer
        // and 'result' for producing the desired collection

        // each collection provides an implicit CanBuildFrom object in its companion

        // a builder for the Range does not return a Range
        // (1 to 10).map(x => x*x) should be a Vector as implemented in IndexedSeq, super for Range
    }

}

object Implicits_Exercises {

    // 1. How does '->' work? That is, how can
    // "Hello" -> 42
    // and
    // 42 -> "Hello"
    // be pairs ("Hello", 42) and (42, "Hello")?
    // Hint: Predef.ArrowAssoc
    def ex1 = {
        val p1: (String, Int) = "hello" -> 42
        val p2: (Int, String) = 42 -> "hello"

        object explain {
            // implicit class (conversion from A to ArrowAssoc[A])
            // has method '->(b: B)' that produces a tuple (A, B);
            // so, calling a method x.->(y) we have an implicit conversion x => ArrowAssoc(x) first,
            // then trivial
            implicit final class ArrowAssoc[A](private val self: A) {
                @inline def -> [B](y: B): Tuple2[A, B] = Tuple2(self, y)
                def →[B](y: B): Tuple2[A, B] = ->(y)
            }
        }
    }

    // 2. Define an operator '+%' that adds a given percentage to a value.
    // For example, 120 +% 10 should be 132.
    // Use an implicit class.
    def ex2 = {
        implicit class RichInt(x: Int) {
            def +%(y: Int): Int = (x + x / 100d * y).toInt
        }

        // test
        assert(120 +% 10 == 132)
    }

    // 3. Define a '!' operator that computes the factorial of an integer.
    // For example, 5.! is 120.
    // Use an implicit class.
    def ex3 = {
        implicit class RichInt(x: Int) {
            def ! : Int = (1 to x).product
        }

        // test
        assert(5.! == 120)
    }

    // 4. Some people are fond of “fluent APIs” that read vaguely like English sentences.
    // Create such an API for reading integers, floating-point numbers, and strings from the console.
    // For example:
    // Read in aString askingFor "Your name" and anInt askingFor "Yourage" and aDouble askingFor "Your weight"
    def ex4 = {
        object Read {
            // API
            object aString extends Target
            object anInt extends Target
            object aDouble extends Target

            def in(target: Target): this.type = { nextTarget = target; this }
            def and(target: Target): this.type = in(target)
            def askingFor(prompt: String): this.type = {
                val inp = Option(scala.io.StdIn.readLine(prompt))
                storage = storage.updated(prompt, Item(nextTarget, inp.getOrElse("")))
                this
            }

            // internals
            sealed trait Target
            case class Item(klass: Target, value: String)
            private var storage: Map[String, Item] = Map.empty
            private var nextTarget: Target = _
            override def toString: String = storage.toString
        }

        // employ implicits, e.g. def in(target: String)(implicit reader: Reader[String]) ...
        object Read2 {
            // API
            object aString extends Target
            object anInt extends Target
            object aDouble extends Target

            def and(target: aString.type)(implicit reader: Reader[String]): this.type = { nextReader = reader; this }
            def and(target: anInt.type)(implicit reader: Reader[Int]): this.type = { nextReader = reader; this }
            def and(target: aDouble.type)(implicit reader: Reader[Double]): this.type = { nextReader = reader; this }

            def in(target: Target): this.type = {
                target match {
                    case a: aString.type => and(a)(Readers.StringReader)
                    case b: anInt.type => and(b)(Readers.IntReader)
                    case c: aDouble.type => and(c)(Readers.DoubleReader)
            } }

            def askingFor(prompt: String): this.type = {
                val res = nextReader.read(prompt)
                println(s"read value: '$res' of type ${res.getClass.getName}")
                this
            }

            // internals
            sealed trait Target

            trait Reader[T] {
                def read(prompt: String): T
                def readLine(prompt: String): String = Option(scala.io.StdIn.readLine(prompt+": ")).getOrElse("")
            }

            object Readers {
                implicit object StringReader extends Reader[String] { override def read(prompt: String): String = readLine(prompt) }
                implicit object IntReader extends Reader[Int] { override def read(prompt: String): Int = readLine(prompt).toInt }
                implicit object DoubleReader extends Reader[Double] { override def read(prompt: String): Double = readLine(prompt).toDouble }
            }

            private var nextReader: Reader[_] = _
        }

        // test
        def test1 = {
            import Read._
            val res = Read in aString askingFor "Your name" and anInt askingFor "Your age" and aDouble askingFor "Your weight"
            res.toString
        }

        def test2 = {
            import Read2.Readers._
            import Read2._
            val res = Read2 in aString askingFor "Your name" and anInt askingFor "Your age" and aDouble askingFor "Your weight"
            res.toString
        }

        // test1
        test2
    }

    // 5. Provide the machinery that is needed to compute
    //      smaller(Fraction(1, 7), Fraction(2, 9))
    // with the Fraction class of Chapter 11.
    // Supply an implicit class RichFraction that extends Ordered[Fraction].
    def ex5 = {
        def smaller[T](a: T, b: T)(implicit order: T => Ordered[T]) = if (a < b) a else b

        class Fraction(n: Int, d: Int) {
            import Fraction._
            private[this] val _gcd = gcd(n.abs, d.abs)

            val num: Int = if (d == 0) 1 else n * d.signum / _gcd
            val den: Int = if (d == 0) 0 else d * d.signum / _gcd
            override def toString = s"$num/$den"
            def double: Double = num.toDouble / den.toDouble
        }

        object Fraction {
            def apply(n: Int, d: Int): Fraction = new Fraction(n, d)
            @tailrec def gcd(a: Int, b: Int): Int = if (b == 0) scala.math.abs(a) else gcd(b, a % b)
        }

        implicit class RichFraction(self: Fraction) extends Ordered[Fraction] {
            override def compare(that: Fraction): Int = Ordering[Double].compare(self.double, that.double)
        }

        // test
        assert(smaller(Fraction(1,2), Fraction(3,4)).toString == Fraction(1,2).toString)
    }

    // 6. Compare objects of the class
    // java.awt.Point
    // by lexicographic comparison
    def ex6 = {
        import java.awt.Point

        implicit class RichPoint(self: Point) extends Ordered[Point] {
            // not effective but simple
            override def compare(that: Point): Int = s"${self.x}${self.y}".compare(s"${that.x}${that.y}")
        }

        // test
        val p1 = new Point(1,2)
        val p2 = new Point(3,4)
        assert(p1 < p2)

    }

    // 7. Continue the previous exercise,
    // comparing two points according to their distance to the origin.
    // How can you switch between the two orderings?
    def ex7 = {
        import java.awt.Point

        object PointOrderings {
            implicit class LexicographicalPoint(self: Point) extends Ordered[Point] {
                override def compare(that: Point): Int = s"${self.x}${self.y}".compare(s"${that.x}${that.y}")
            }
            implicit class DistancePoint(self: Point) extends Ordered[Point] {
                override def compare(that: Point): Int = self.distanceSq(0, 0).compare(that.distanceSq(0, 0))
            }
        }

        // test
        // switch using diff. imports:
        // import PointOrderings.LexicographicalPoint
        import PointOrderings.DistancePoint

        val p1 = new Point(1,2)
        val p2 = new Point(3,4)
        assert(p1 < p2)

    }

    // 8. Use the 'implicitly' command in the REPL to summon the implicit objects described in
    // Section 21.5, “Implicit Parameters,” on page 328
    // and
    // Section 21.6, “Implicit Conversions with Implicit Parameters,” on page 329.
    // What objects do you get?
    def ex8 = {
        //Implicit Parameters
        case class Delimiters(left: String, right: String)
        def quote(what: String)(implicit delims: Delimiters) = delims.left + what + delims.right

        // or, call with implicit, searched in scope or companion objects for type/type parameters
        object FrenchPunctuation { implicit val quoteDelimiters = Delimiters("«", "»") }
        import FrenchPunctuation._
        quote("Bonjour le monde")

        // companion
        object Delimiters { implicit val quoteDelimiters = Delimiters("«", "»") }
        // implicit: search in companion object
        quote("Bonjour le monde")

        // implicit conversions with implicit parameters
        def smaller[T](a: T, b: T)(implicit order: T => Ordered[T]) = if (order(a) < b) a else b
        // Predef object defines a lot of implicit values T => Ordered[T]
        smaller(1, 2)
        smaller("Hello", "World")

        // scala> implicitly[Delimiters]
        //res4: Delimiters = Delimiters(«,»)

        // scala> implicitly[Int => Ordered[Int]]
        //res5: Int => Ordered[Int] = $$Lambda$6227/606712809@94f1e64

        //scala> implicitly[String => Ordered[String]]
        //res6: String => Ordered[String] = $$Lambda$6228/1172546004@17e5614b

        // scala> :implicits
        ///* 1 implicit members imported from FrenchPunctuation */
        //  /* 1 defined in FrenchPunctuation */
        //  implicit val quoteDelimiters: Delimiters

        // scala> :implicits -v
        ///* 69 implicit members imported from scala.Predef */
        //  /* 7 inherited from scala */
        //  final implicit class ArrayCharSequence extends CharSequence
        //  final implicit class ArrowAssoc[A <: <?>] extends AnyVal
        //  final implicit class Ensuring[A <: <?>] extends AnyVal
        //  final implicit class RichException extends AnyVal
        //  final implicit class SeqCharSequence extends CharSequence
        //  final implicit class StringFormat[A <: <?>] extends AnyVal
        //  final implicit class any2stringadd[A <: <?>] extends AnyVal
        //
        //  /* 40 inherited from scala.Predef */
        //  implicit def ArrowAssoc[A](self: A): ArrowAssoc[A]
        //  implicit def Ensuring[A](self: A): Ensuring[A]
        //  implicit def StringFormat[A](self: A): StringFormat[A]
        //  implicit def any2stringadd[A](self: A): any2stringadd[A]
        //
        //  implicit def $conforms[A]: A <:< A
        // ...
        // ...
        // ...

    }

    // 9. Explain why 'Ordering' is a type class and why 'Ordered' is not.
    def ex9 = {
        // trait Ordering is an abstract interface with abstract 'compare' method;
        // any type can join that type class by defining that method;
        // the important feature of this trait that makes it a good type class is definition of
        // the 'compare' method. It takes two objects of type T and compares them.
        // it allows to separate algorithm from object implementation
        val orderingInt = Ordering[Int]
        // trait Ordering[T] extends Comparator[T] with PartialOrdering[T] with Serializable {
        //  outer =>
        // ...
        //  def compare(x: T, y: T): Int
        // ...

        // well, technically, it is a type class, but intentions are different;
        // moreover, method 'compare' defined with one argument: other object.
        // you can't redefine comparison algorithm without changes in type implementation
        val orderedInt = Ordered[Int]
        //  *  Ordered should be used for data with a single, natural ordering (like
        // *  integers) while Ordering allows for multiple ordering implementations.
        // *  An Ordering instance will be implicitly created if necessary.
        // *
        // *  [[scala.math.Ordering]] is an alternative to this trait that allows multiple orderings to be
        // *  defined for the same type.
        // ...
        //trait Ordered[A] extends Any with java.lang.Comparable[A] {
        // ...
        //  def compare(that: A): Int
        // ...
    }

    // 10. Generalize the average method in
    // Section 21.8, “Type Classes,” on page 331
    // to a Seq[T].
    def ex10 = {
        ???
    }

    // 11. Make String a member of the NumberLike type class in
    // Section 21.8, “Type Classes,” on page 331.
    // The divBy method should retain every n-th letter, so that
    // average("Hello", "World") becomes "Hlool".
    def ex11 = {
        ???
    }

    // 12. Look up the '=:=' object in Predef.scala.
    // Explain how it works.
    def ex12 = {
        ???
    }

    // 13. The result of
    // "abc".map(_.toUpper)
    // is a String, but the result of
    // "abc".map(_.toInt)
    // is a Vector.
    // Find out why.
    def ex13 = {
        ???
    }

}
