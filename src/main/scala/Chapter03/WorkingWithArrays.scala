package Chapter03

object WorkingWithArrays {
// topics:
    // fixed-length arrays
    // variable-length arrays: array buffers
    // traversing arrays and array buffrs
    // transforming arrays
    // common algorithms
    // deciphering scaladoc
    // multidimensional arrays
    // interoperating with java

    // fixed-length arrays
    def fixedLengthArrays = {

        // ten integers, initialized with zero
        val nums = new Array[Int](10)

        // ten nulls
        val strs = new Array[String](10)

        // array of strings, length 2 // n.b. no 'new'
        val s = Array("Hello", "World")
        s(0) = "Goodbye"
    }

    // variable-length arrays: array buffers
    def variableLengthArrays_ArrayBuffers = {
        // can grow and shrink, like ArrayList

        import scala.collection.mutable.ArrayBuffer
        val b = ArrayBuffer[Int]()
        b += 1 // add one element
        b += (1, 2, 3, 5) // add multiple elems
        b ++= Array(8, 13, 21) // append collection
        b.trimEnd(5) // remove last 5, constant time (amortized)

        // insert and remove in the middle are not effective
        b.insert(2, 6) // insert before idx 2
        b.insert(2, 7, 8, 9)
        b.remove(2) // remove one elem on idx 2
        b.remove(2, 3) // remove 3 elems from idx 2

        // build an array
        val a = b.toArray
        // convert to a buffer
        a.toBuffer
    }

    // traversing arrays and array buffrs
    def traversingArraysAndArrayBuffers = {
        val a = Array(1, 2, 3)
        for (i <- 0 until a.length) println(s"a[${i}] = ${a(i)}")

        // every second
        for (i <- 0 until a.length by 2) println(s"a[${i}] = ${a(i)}")

        // from end to start
        for (i <- a.length-1 to 0 by -1) println(s"a[${i}] = ${a(i)}")

        // using indices
        for (i <- a.indices) println(s"a[${i}] = ${a(i)}")
        for (i <- a.indices.reverse) println(s"a[${i}] = ${a(i)}")

        // no indicies
        for (elem <- a) ???
    }

    // transforming arrays
    def transformingArrays = {
        // transformation don't modify the original
        val a = Array(2, 3, 5, 7, 11)
        // type derived from a
        val res = for (elem <- a) yield 2 * elem

        // with condition
        val res2 = for (elem <- a if elem % 2 == 0) yield 2 * elem
        // alternatively
        val res3 = a.filter(_ % 2 == 0).map(2 * _)
        // or
        val res4 = a filter { _ % 2 == 0 } map { 2 * _ }

        // example: remove all negative elems
        val b = a.toBuffer
        // creating a new array:
        val res5 = for (elem <- b if elem >= 0) yield elem
        // in place:
        // inefficient version
        val positionsToRemove = for (i <- b.indices if b(i) < 0) yield i
        for (i <- positionsToRemove.reverse) b.remove(i)
        // may be better
        val positionsToKeep = for (i <- b.indices if b(i) >= 0) yield i
        for (i <- positionsToKeep.indices) b(i) = b(positionsToKeep(i))
        b.trimEnd(b.length - positionsToKeep.length)
    }

    // common algorithms
    def commonAlgorithms = {
        // business computations all about sums and sorts

        val a = Array(1, 2, 3)
        val asum = a.sum

        val smax = Array("Mary", "had", "a", "little", "lamb").max

        // sorted creates a new collection
        val b = a.sorted
        // with comparison function
        val bDesc = a.sortWith(_ > _)

        // array can be sorted in place
        scala.util.Sorting.quickSort(a)
        // elements must be with Ordered trait

        // array toString is no good, use mkString
        println(a.mkString("(", ",", ")"))
    }

    // deciphering scaladoc

    // multidimensional arrays
    def multidimensionalArrays = {
        // array of array
        val rows = 3; val columns = 4
        val matrix = Array.ofDim[Int](rows, columns)

        matrix(1)(2) // row 1 co 2

        // ragged arrays
        val triangle = new Array[Array[Int]](10) // 10 rows
        for (i <- triangle.indices) triangle(i) = new Array[Int](i + 1) // variable num of columns
    }

    // interoperating with java
    def interoperatingWithJava = {
        // Scala arrays implemented as Java arrays
        // but, in java type automatically converted to supertype, not in scala
        val a = Array("Mary", "had", "a", "little", "lamb")
        val b = java.util.Arrays.binarySearch(a.asInstanceOf[Array[Object]], "beef")

        // to apply search in scala you can
        import scala.collection.Searching._
        val c = a.search("beef") // Found(n) or InsertionPoint(n)

        // implicit conversions to/from java ArrayList / List<?>
        // import scala.collection.JavaConversions.bufferAsJavaList
        import scala.collection.JavaConverters._
        import scala.collection.mutable.ArrayBuffer
        val cmd = ArrayBuffer("ls", "-al", "/temp")
        val pb = new ProcessBuilder(cmd.asJava)
        // and back
        import scala.collection.mutable.Buffer
        val command: Buffer[String] = pb.command.asScala
    }

}

object WorkingWithArrays_Exercises {

    // a = array of n random integers between 0 inclusive and n exclusive
    def ex1 = {
        ???
    }

    // loop: swap adjacent elements of an array (in place)
    def ex2 = {
        ???
    }

    // ex2 only create a new array
    def ex3 = {
        ???
    }

    // produce a new partially sorted array: all positives first, then zero or negatives; both parts in original order
    def ex4 = {
        ???
    }

    // average of an Array[Double]
    def ex5 = {
        ???
    }

    // reverse sorted order
    def ex6 = {
        ???
    }

    // remove duplicates
    def ex7 = {
        ???
    }

    // remove all negatives but first
    def ex8 = {
        ???
    }

    // improve ex8: move good elements instead of removing bad elements
    def ex9 = {
        ???
    }

    // collection of time zones
    def ex10 = {
        ???
    }

    // return Buffer from java
    def ex11 = {
        ???
    }

}
