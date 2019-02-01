package Chapter01

import org.scalatest.{FlatSpec, Matchers}

/**
  * RichInt, StringOps, BigInt, BigDecimal, etc.
  */
class TheBasicsTest extends FlatSpec with Matchers {

    it should "show: there is no distinction between primitive types and class types" in {
        // you can invoke methods on numbers, e.g.
        val n: Int = 42
        assert(n.toString === "42" && 42.toString === "42")
        assert((1 to 3) === Range.inclusive(1, 3))
    }

    it should "java.lang.String implicitly converted to a StringOps" in {
        assert("Hello".intersect("World") === "lo")
    }

    it should "use methods, not casts, to convert types" in {
        assert(42.42.toInt === 42)
        assert(99.toChar === 'c')
        assert("99.44".toDouble === 99.44)
    }

    it should "show: operators are actually methods" in {
        assert(2 + 3 === 2.+(3))
        assert(BigInt(123)./%(4) === (30, 3) && BigInt(123) /% 4 === (30, 3))
    }

    it should "show: Scala does not have ++ or -- operators" in {
        var n = 1
        assert({n += 1 ; n} === 2)
    }

    it should "show: you can overload operators" in {
        val x: BigInt = 1234567890
        assert(x * x * x === BigInt("1881676371789154860897069000"))
    }

    it should "show: you don't have to use parentheses" in {
        assert("Bonjour".sorted === "Bjnooru")
    }

    it should "show usage of package object" in {
        // import methods from singleton object
        // scala/math/package.scala: package object math { ... }
        // use method w/o prefix
        import scala.math._
        assert(sqrt(2) === (1.4142 +- 0.0001))
    }

    it should "show: companion object methods as static methods" in {
        assert(BigInt.probablePrime(128, scala.util.Random) > Long.MaxValue)
    }

    it should "show: apply method" in {
        // sequence is a function idx => char
        assert("Hello"(4) === "Hello".apply(4) && "Hello"(4) === 'o')

        // conflict with implicit param
        // assert("Bonjour".sorted(3) === 'j')
        assert("Bonjour".sorted.apply(3) === 'o')

        // using the apply method of a companion object is a common Scala idiom for constructing objects
        assert(BigInt("123") === BigInt.apply("123"))
        assert(Array(1,2,3) === Array.apply(1,2,3) && Array(1,2,3) === List(1,2,3).toArray)
    }
}
