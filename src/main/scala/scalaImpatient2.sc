// Scala for the impatient
// 2. Control Structures and Functions (A1)

import java.io.{IOException, InputStream}
import java.net.{MalformedURLException, URL}
import java.text.MessageFormat

// An expression has a value; a statement carries out an action

val a = if ('a' > 65) 1 else -1
val b = if (a < 0) -1 // else Unit aka ()

// If you want to continue a long statement over two lines, you need to make sure
// that the first line ends in a symbol that cannot be the end of a statement
val c = a +
  3 -
  7

// block expression
val distance = {
  import math._
  val x, x0, y, y0 = 3
  val dx = x - x0; val dy = y - y0; sqrt(dx * dx + dy * dy)
}
// The value of the { } block is the last expression
// The variables dx and dy, which were only needed as intermediate values in the
// computation, are neatly hidden from the rest of the program

//assignments have Unit value, don’t chain them together.
{
  var y = 0
  val x = y = 1 // No
}

// loops, no break or continue
{
  val s = "crazy"
  var sum = 0
  for (i <- 0 until s.length) sum += s(i)

  var n = 0; sum = 0
  while (n < s.length) { sum += s(n); n += 1 }

  sum = 0
  for (ch <- s) sum += ch
}

// advanced loops
{
  for (i <- 1 to 3; j <- 1 to 3) print((10 * i + j) + " "); println

  // guard
  for (i <- 1 to 3; j <- 1 to 3 if i != j) print((10 * i + j) + " "); ; println

  // define a var inside
  for (i <- 1 to 3; from = 4 - i; j <- from to 3) print((10 * i + j) + " "); println

  // When the body of the for loop starts with yield, then the loop constructs a
  // collection of values, one for each iteration:
  println( for (i <- 1 to 10) yield i % 3 )
  // This type of loop is called a 'for comprehension'.

  // The generated collection is compatible with the first generator
  println( for (c <- "Hello"; i <- 0 to 1) yield (c + i).toChar )
  // or
  println( for (i <- 0 to 1; c <- "Hello") yield (c + i).toChar )

  // If you prefer, you can enclose the generators, guards, and definitions of a
  // for loop inside braces, and you can use newlines
  // instead of semicolons to separate them:
  for {
    i <- 1 to 3
    from = 4 - i
    j <- from to 3
  } yield(10 * i + j)
}

// functions

def fac(n : Int) = {
  var r = 1
  for (i <- 1 to n) r = r * i
  r
}
// or
def fact(n: Int): Int = if (n <= 0) 1 else n * fact(n - 1)

// don't use 'return'
// Pretty soon, you will be using lots of anonymous functions, and there,
// return doesn’t return a value to the caller.
// It breaks out to the enclosing named function

// default and named arguments
def decorate(str: String, left: String = "[", right: String = "]") =
  left + str + right
// If you supply fewer arguments than there are parameters,
// the defaults are applied from the end
decorate("Hello", ">>>[")
decorate(left = "<<<", str = "Hello", right = ">>>")
// You can mix unnamed and named arguments, provided the unnamed ones come first
decorate("Hello", right = "]<<<")

//variable number of arguments. The following example shows the syntax:
def sum(args: Int*) = { // Seq
  var result = 0
  for (arg <- args) result += arg
  result
}
sum(1, 4, 9, 16, 25)

// If you already have a sequence of values,
// you cannot pass it directly to such a function
val s = sum(1 to 5: _*) // Consider 1 to 5 as an argument sequence
//This call syntax is needed in a recursive definition:
def recursiveSum(args: Int*) : Int = {
  if (args.length == 0) 0
  else args.head + recursiveSum(args.tail : _*)
}

// When you call a Java method with variable arguments of type Object,
// such as PrintStream.printf or MessageFormat.format,
// you need to convert any primitive types by hand
MessageFormat.format("The answer to {0} is {1}",  "everything", 42.asInstanceOf[AnyRef])

// lazy

// When a val is declared as lazy, its initialization is deferred until it is
// accessed for the first time
lazy val words = scala.io.Source.fromFile("/usr/share/dict/words").mkString
// If the program never accesses words, the file is never opened
// You can think of lazy values as halfway between val and def

val wordsVal = scala.io.Source.fromFile("/usr/share/dict/words").mkString
// Evaluated as soon as words is defined
lazy val wordsLazyval = scala.io.Source.fromFile("/usr/share/dict/words").mkString
// Evaluated the first time words is used
def wordsDef = scala.io.Source.fromFile("/usr/share/dict/words").mkString
// Evaluated every time words is used

// Laziness is not cost-free. Every time a lazy value is accessed, a method is
// called that checks, in a threadsafe manner, whether the value has
// already been initialized

// exceptions

val e = if (a >= 0) math.sqrt(a) else
  throw new IllegalArgumentException("a should not be negative")
//The first branch has type Double, the second has type Nothing.
// Therefore, the if/else expression also has type Double.

// catching
try {
  def process(url: URL) = throw new MalformedURLException("oops")

  val url = new URL("http://horstmann.com/fred-tiny.gif")
  try {
    process(url)
  } catch {
    case _: MalformedURLException => println("Bad URL: " + url)
    case ex: IOException => ex.printStackTrace()
  }

  // The try/finally statement lets you dispose of a resource whether or not
  // an exception has occurred
  def processin(in: InputStream) = ???
  var in = new URL("http://ya.ru").openStream()
  try {
    processin(in)
  } finally {
    println("in.close")
    in.close()
  }
  // Why isn’t val in = new URL(...).openStream() inside the try block?
  // Then the scope of in would not extend to the finally clause

} catch {
  case ex: NotImplementedError => println("oopsy-daisy: "+ ex.getMessage)
}

// The try/catch statement handles exceptions, and the try/finally statement
// takes some action (usually cleanup) when an exception is not handled
// try { ... } catch { ... } finally { ... }
// This is the same as
// try { try { ... } catch { ... } } finally { ... }

// Exercises

// The signum of a number is 1 if the number is positive,
// –1 if it is negative, and 0 if it is zero.
// Write a function that computes this value
def signum(n: Int): Int = {
  if (n > 0) 1 else if (n < 0) -1 else 0
}
println(s"signum: ${signum(-3)}, ${signum(0)}, ${signum(3)}")

// What is the value of an empty block expression {}? What is its type?
// () aka Unit
{}

// Come up with one situation where the assignment x = y = 1 is valid in Scala
var y: Int = 0
val x: Unit = y = 1

// Write a Scala equivalent for the Java loop
// for (int i = 10; i >= 0; i--) System.out.println(i);
for (i <- 10 to 0 by -1) println(i)

// Write a procedure countdown(n: Int) that prints the numbers from n to 0
def countdown(n: Int): Unit = {
  print(s"$n ")
  if (n < 0) countdown(n+1)
  else if (n > 0) countdown(n-1)
}
countdown(10)
println("")
countdown(-10)

// Write a for loop for computing the product of the
// Unicode codes of all letters in a string.
// For example, the product of the characters in "Hello" is 9415087488L.
var prod = 1L
for (ch <- "Hello") prod *= ch
prod // 9415087488

// Solve the preceding exercise without writing a loop.
// (Hint: Look at the StringOps Scaladoc.)
prod = (1L /: "Hello")(_*_) // foldleft

// Write a function product(s : String)
// that computes the product, as described in the preceding exercises
def product(chs: String): Long = {
  (1L /: chs)(_*_)
}
product("Hello")

// Make the function of the preceding exercise a recursive function.
def productRecur(chs: String): Long =  {
  if (chs.isEmpty) 1L
  else chs.head * productRecur(chs.tail)
}
productRecur("Hello")

// Write a function that computes x^n, where n is an integer.
// Use the following recursive definition:
//• x^n = y^2 if n is even and positive, where y = x^(n / 2)
//• x^n = x·x^(n – 1) if n is odd and positive.
//• x^0 = 1.
//• x^n = 1 / x^–n if n is negative.
//  Don’t use a return statement
def pow(x: Double, n: Int): Double = {
  def even(n: Int): Boolean = n % 2 == 0

  if (n > 0) {
    if (even(n)) { val y = pow(x, n/2); y * y }
    else x * pow(x, n-1)
  }
  else if (n == 0) 1
  else 1.0 / pow(x, -n)
}
pow(2, 3)
pow(2, -3)
