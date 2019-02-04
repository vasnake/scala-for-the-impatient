package Chapter03

object WorkingWithArrays {
// topics:
    // fixed-length arrays
    // variable-length arrays: array buffers
    // traversing arrays and array buffers
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

    // traversing arrays and array buffers
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
        def intArray(n: Int): Array[Int] = {
            Array.fill(n)(scala.util.Random.nextInt(n))
        }
        assert(intArray(10).length == 10 && !intArray(999).contains(999))
    }

    // loop: swap adjacent elements of an array (in place)
    def ex2 = {
        def shuffle(arr: Array[Int]): Array[Int] = {
            def swap(i: Int, j: Int) = { val t = arr(i); arr(i) = arr(j); arr(j) = t }

            for {
                idx <- arr.indices
                if idx % 2 == 1
            } swap(idx - 1, idx)

            arr
        }

        assert(shuffle(Array.empty[Int]).mkString(",") == Array.empty[Int].mkString(","))
        assert(shuffle(Array(1)).mkString(",") == Array(1).mkString(","))
        assert(shuffle(Array(1,2)).mkString(",") == Array(2,1).mkString(","))
        assert(shuffle(Array(1,2,3,4,5)).mkString(",") == Array(2,1,4,3,5).mkString(","))
        assert(shuffle(Array(1,2,3,4)).mkString(",") == Array(2,1,4,3).mkString(","))
    }

    // ex2 only create a new array using for/yield
    def ex3 = {
        def shuffle(arr: Array[Int]): Array[Int] = {
            for {
                idx <- arr.indices.toArray
                j = if (idx % 2 == 0 ) idx+1 else idx-1
                k = if (j < arr.length) j else idx
            } yield arr(k)
        }
    }

    // produce a new partially sorted array: all positives first,
    // then zero or negatives; both parts in original order
    def ex4 = {
        def sort(arr: Array[Int]): Array[Int] = {
            val (a, b) = arr.partition(_ > 0)
            a ++ b
        }
        assert(sort(Array(1,-2,3,-4,5,0)).mkString(",") == Array(1,3,5,-2,-4,0).mkString(","))
    }

    // average of an Array[Double]
    def ex5 = {
        def avg(arr: Array[Double]): Double = {
            require(arr.nonEmpty, "Can't compute average of empty array")
            arr.sum / arr.length
        }
    }

    // reverse sorted order (in place?)
    def ex6 = {
        import scala.collection.mutable.ArrayBuffer

        // array can be sorted in place easy using API
        def reversedArray(arr: Array[Int]): Array[Int] = {
            // create new
            // arr.sorted.reverse
            // arr.sortBy(x => -x)
            // arr.sortWith(_ > _)

            // in place
            val rev = (a: Int, b: Int) => a > b
            scala.util.Sorting.stableSort(arr, rev)
            // or
            val ord = math.Ordering.fromLessThan[Int](_ > _)
            scala.util.Sorting.quickSort[Int](arr)(ord)

            arr
        }

        // buffer can be sorted in place by implementing quicksort by hand
        def reversedArrayBuffer(buf: ArrayBuffer[Int]): ArrayBuffer[Int] = {
            buf.sortWith(_>_)
        }

        assert(reversedArray(Array(1,2,3)).mkString(",") == "3,2,1")
        assert(reversedArrayBuffer(ArrayBuffer(1,2,3)).mkString(",") == "3,2,1")
    }

    // remove duplicates
    def ex7 = {
        def dropDuplicates[T](arr: Array[T]): Array[T] = {
            arr.distinct
        }
        assert(dropDuplicates(Array(1,2,3,3,2,1)).sorted.mkString(",") == "1,2,3")
    }

    // remove all negatives but first
    def ex8 = {
        import scala.collection.mutable
        def dropNegativesButFirst(arr: mutable.ArrayBuffer[Int]): Unit = {

            val negativePositions: mutable.Buffer[Int] = for {
                idx <- arr.indices.toBuffer
                if arr(idx) < 0
            } yield idx

            val reversedNegPos = negativePositions.reverse
            reversedNegPos.trimEnd(1)
            reversedNegPos.foreach(arr.remove)
        }

        val arr = mutable.ArrayBuffer(1,2,3,-1,4,-2,5,-3); dropNegativesButFirst(arr)
        assert(arr.mkString(",") == "1,2,3,-1,4,5")
    }

    // improve ex8: move good elements that needs moving instead of removing bad elements
    def ex9 = {
        import scala.collection.mutable

        def dropNegativesButFirst(arr: mutable.ArrayBuffer[Int]): Unit = {

            var first = true
            def isFirstNegative(idx: Int): Boolean = {
                require(arr(idx) < 0, "process only negative elements")
                if (first) {
                    first = false
                    true
                }
                else false
            }

            val positivePositions = for {
                idx <- arr.indices
                if arr(idx) >= 0 || isFirstNegative(idx)
            } yield idx

            val from_to = positivePositions.zipWithIndex

            from_to foreach { case (from, to) => if (from != to) arr(to) = arr(from) }
            arr.trimEnd(arr.length - positivePositions.length)
        }

        val arr = mutable.ArrayBuffer(1,2,3,-1,4,-2,5,-3, 0); dropNegativesButFirst(arr)
        assert(arr.mkString(",") == "1,2,3,-1,4,5,0")
    }

    // collection of time zones
    def ex10 = {
        def timeZones = {
            val ids = java.util.TimeZone.getAvailableIDs
            val amids = ids.filter( _.startsWith("America"))
            amids.map(_.stripPrefix("America/")).sorted
        }
    }

    // return Buffer from java
    def ex11 = {
        def nativeImageFlavor = {
            import java.awt.datatransfer._
            import scala.collection.mutable
            import scala.collection.JavaConverters._

            val flavors = SystemFlavorMap.getDefaultFlavorMap.asInstanceOf[SystemFlavorMap]
            val res: mutable.Buffer[String] = flavors.getNativesForFlavor(DataFlavor.imageFlavor).asScala // List[String]

            res
        }

    }

}
