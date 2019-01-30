// Scala for the impatient
// 1. The Basics (A1)

1.toString
// RichInt
1.to(10)
// StringOps
"Hello".intersect("World")

val a: math.BigInt = 123
a /% 4 // (30,3)

// package import
import math._
sqrt(2)

// 'static': companion object
BigInt.probablePrime(100, util.Random)

// parameterless, no ()
"Hello".distinct

// apply method
"Hello"(4)
// apply in companion
BigInt("123")

// Scaladoc
// http://www.scala-lang.org/api/2.11.8/#package
// http://www.scala-lang.org/api/2.11.8/#scala.collection.immutable.StringOps

"Harry".patch(1, "ung", 2)

// 1.8. Exercises
3 - pow(sqrt(3), 2)

"crazy" * 3
// http://www.scala-lang.org/api/2.11.8/index.html#scala.collection.immutable.StringOps@*(n:Int):String

10 max 2

BigInt(2) pow 1024

{
  import math.BigInt._
  import util._
  probablePrime(100, Random)
}

// random BigInt
// convert to base 36
// yield a string
BigInt(128, util.Random) toString 36
// http://www.scala-lang.org/api/2.11.8/index.html#scala.math.BigInt$@apply(numbits:Int,rnd:scala.util.Random):scala.math.BigInt
// http://www.scala-lang.org/api/2.11.8/index.html#scala.math.BigInt@toString(radix:Int):String
BigInt.probablePrime(100, util.Random).toString(36)

"crazy".head
"crazy".last

// What  do  the
// take, drop, takeRight, and dropRight
// string  functions  do?
"crazy".take(3) // first 3 elems
"crazy".drop(3) // except first 3
"crazy".takeRight(3) // 3 from the end
"crazy".dropRight(3) // except 3 from the end
