// Scala for the Impatient
// Chapter 3. Working with Arrays

import scala.collection.mutable.ArrayBuffer

// Scala and Java arrays are interoperable;
// with ArrayBuffer, use scala.collection.JavaConversions

// Arrays are mutable, indexed collections of values
// Inside the JVM, a Scala Array is implemented as a Java array

// An array of ten integers, all initialized with zero
val nums = new Array[Int](10)
for (n <- nums) print(s"$n ")
println()

// An Array[String] of length 2, the type is inferred
// Note: No new when you supply initial values
val s = Array("Hello", "World")
s(0) = "Goodbye"
for (n <- s) print(s"$n ") // Goodbye World
println

// Given an array buffer of integers,
// we want to remove all but the first negative number
val arr = Array(2, -3, 5, -7, 11)

{
    val a = arr.toBuffer
    var first = true
    var n = a.length
    var i = 0
    while (i < n) {
        if (a(i) >= 0) i += 1
        else {
            if (first) { first = false; i += 1 }
            else { a.remove(i); n -= 1 }
        }
    }
    a.mkString(",")
}
// It’s inefficient to remove elements in an array buffer.
// It is much better to copy the nonnegative values to the front
{
    val a = arr.toBuffer
    var first = true
    val indexes = for (i <- 0 until a.length if first || a(i) >= 0) yield {
        if (a(i) < 0) first = false; i
    }
    for (j <- 0 until indexes.length) a(j) = a(indexes(j))
    a.trimEnd(a.length - indexes.length)
    a.mkString(",")
    // it is better to have all index values together
    // instead of seeing them one by one
}

// a large percentage of business computations are nothing but computing sums and sorting
{
    val b = ArrayBuffer(1, 7, 2, 9)
    val bSorted = b.sorted
    // b is unchanged; bSorted is ArrayBuffer(1, 2, 7, 9)
    val bDescending = b.sortWith(_ > _) // ArrayBuffer(9, 7, 2, 1)
}

{
    // You can sort an array, but not an array buffer, in place:
    val a = Array(1, 7, 2, 9)
    scala.util.Sorting.quickSort(a)
    // a is now Array(1, 2, 7, 9)

    a.mkString("<", ",", ">")
}

// Like in Java, multidimensional arrays are implemented as arrays of arrays
{
    // a two-dimensional array of Double values has the type
    // Array[Array[Double]].
    // To construct such an array, use the ofDim method
    val matrix = Array.ofDim[Double](3, 4) // Three rows, four columns

    // To access an element, use two pairs of parentheses:
    val row = 2;
    val col = 3
    matrix(row)(col) = 42

    // You can make ragged arrays, with varying row lengths:
    val triangle = new Array[Array[Int]](10)
    for (i <- 0 until triangle.length)
        triangle(i) = new Array[Int](i + 1)
}

// Since Scala arrays are implemented as Java arrays,
// you can pass them back and forth between Java and Scala

{
    // import the implicit conversion methods in scala.collection.JavaConversions.
    // Then you can use Scala buffers in your code, and they automatically get
    // wrapped into Java lists when calling a Java method.
    // For example, the java.lang.ProcessBuilder class has a constructor with a
    // List<String> parameter. Here is how you can call it from Scala
    import scala.collection.JavaConversions.bufferAsJavaList
    import scala.collection.mutable.ArrayBuffer
    val command = ArrayBuffer("ls", "-al", "/home/cay")
    val pb = new ProcessBuilder(command) // Scala to Java

    // Conversely, when a Java method returns a java.util.List, you can have it
    // automatically converted into a Buffer:
    import scala.collection.JavaConversions.asScalaBuffer
    import scala.collection.mutable.Buffer
    val cmd : Buffer[String] = pb.command() // Java to Scala
    // You can't use ArrayBuffer: the wrapped object is only guaranteed to be a Buffer
}

// Exercises

// Write a code snippet that sets a to an array of n random integers
// between 0 (inclusive) and n (exclusive)
{
    val a = new Array[Int](10)
    for (i <- 0 until a.length) a(i) = util.Random.nextInt(a.length)
    println(a.mkString(","))
    // or
    val n = 10
    val b = Array.fill[Int](n){ util.Random.nextInt(n) }
    println(b.mkString(","))
}

// Write a loop that swaps adjacent elements of an array of integers.
// For example, Array(1, 2, 3, 4, 5) becomes Array(2, 1, 4, 3, 5)
{
    def swap(a: Array[Int], i: Int, j: Int) =
        if (i < a.length && j < a.length) { val t = a(i); a(i) = a(j); a(j) = t }

    val a = Array(1, 2, 3, 4, 5)
    for {
        i <- 0 until a.length
        if i % 2 == 0
    } swap(a, i, i+1)
    a.mkString(",")
}

// Repeat the preceding assignment, but produce a new array with the swapped values.
// Use for/yield
{
    val a = Array(1, 2, 3, 4, 5)
    val b = for {
        i <- 0 until a.length
        j = if (i % 2 == 0) i+1 else i-1
        k = if (j < a.length) j else i
    } yield a(k)
    b.mkString(",")
}

{
    val a = Array(1, 2, 3, 4, 5)
    val b = for {
        i <- 0 until a.length
     } yield
        if (i % 2 == 1) a(i-1) else { if (i+1 < a.length) a(i+1) else a(i) }
    b.mkString(",")
}

// Given an array of integers,
// produce a new array that contains all positive values of the original array,
// in their original order,
// followed by all values that are zero or negative, in their original order
{
    val a = Array(-3, -2, -1, 0, 1, 2, 3)
    val b = for (i <- 0 until a.length; if a(i) > 0) yield a(i)
    val c = for (i <- 0 until a.length; if a(i) <= 0) yield a(i)
    (b ++ c).mkString(",")
}

{
    val a = Array(-3, -2, -1, 0, 1, 2, 3)
    val (b, c) = a.partition(_ > 0)
    (b ++ c).mkString(",")
}

{
    val a = Array(-3, -2, -1, 0, 1, 2, 3)
    val b, c = ArrayBuffer.empty[Int]
    for (i <- 0 until a.length) {
        if (a(i) > 0) b += a(i)
        else c += a(i)
    }
    (b ++ c).toArray.mkString(",")
}

// How do you compute the average of an Array[Double]?
{
    val a = Array[Double](1.1, 1.2, 1.3, 1.4, 1.5)
    val avrg = a.sum / a.length
    avrg
}

// How do you rearrange the elements of an Array[Int] so that they appear in
// reverse sorted order?
// How do you do the same with an ArrayBuffer[Int]?
{
    val a = Array(-3, -2, -1, 0, 1, 2, 3)
    println(a.sorted.reverse.mkString(","))
    // or
    println(a.sortWith(_ > _).mkString(","))
    // or
    val revord = math.Ordering.fromLessThan[Int](_ > _)
    util.Sorting.quickSort[Int](a)(revord)
    println(a.mkString(","))

    val b = a.toBuffer
    println(b.sorted.reverse.mkString(","))
    // or
    println(b.sortWith(_ > _).mkString(","))
}

// Write a code snippet that produces all values from an array with
// duplicates removed. (Hint: Look at Scaladoc.)
{
    val a = Array(1, 1, 2, 2, 3, 3, 4, 4, 5)
    val b = a.distinct
    println(b.mkString(","))
}

// Rewrite the example at the end of Section 3.4, “Transforming Arrays,” on page 32
{
    // Given an array buffer of integers,
    // we want to remove all but the first negative number
    val arr = Array(2, -3, 5, -7, 11)
    val a = arr.toBuffer
    var first = true
    val indexes = for (i <- 0 until a.length if first || a(i) >= 0) yield {
        if (a(i) < 0) first = false; i
    }
    // time n; space n
    for (j <- 0 until indexes.length) a(j) = a(indexes(j))
    // time 2n; space n
    a.trimEnd(a.length - indexes.length)
    a.mkString(",")
}
// Collect indexes of the negative elements,
// reverse the sequence,
// drop the last index,
// and call a.remove(i) for each index.
// Compare the efficiency of this approach with the two approaches in Section 3.4.
{
    val a = ArrayBuffer(2, -3, 5, -7, 11)
    val idx = for {
        i <- 0 until a.length
        if (a(i) < 0)
    } yield i
    // time n; space n
    val revidx = idx.toBuffer.reverse
    // time 2n; space 2n
    revidx.trimEnd(1)
    revidx.foreach( a.remove(_) )
    // time 2n + n^2/2

    a.mkString(",")
}

// Make a collection of all time zones returned by java.util.TimeZone.getAvailableIDs
// that are in America. Strip off the "America/" prefix and sort the result
{
    val ids = java.util.TimeZone.getAvailableIDs
    val amids = ids.filter( _.startsWith("America"))
    val res = amids.map(_.stripPrefix("America/")).sorted
    println(res.mkString(","))
}

// Import java.awt.datatransfer._ and make an object of type SystemFlavorMap with the call
// val flavors = SystemFlavorMap.getDefaultFlavorMap().asInstanceOf[SystemFlavorMap]
// Then call the getNativesForFlavor method with parameter DataFlavor.imageFlavor
// and get the return value as a Scala buffer.
// (Why this obscure class? It’s hard to find uses of java.util.List in the standard Java library.)
{
    import java.awt.datatransfer._
    import scala.collection.JavaConversions.asScalaBuffer
    import scala.collection.mutable.Buffer

    val flavors = SystemFlavorMap.getDefaultFlavorMap.asInstanceOf[SystemFlavorMap]
    val imfl: Buffer[String] = flavors.getNativesForFlavor(DataFlavor.imageFlavor) // List[String]
    println(imfl.mkString(","))
}
// https://gist.github.com/parambirs/58b208a9b0abf279826a
