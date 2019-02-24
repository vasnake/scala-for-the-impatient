package Chapter13

import org.scalameter

object Collections {
// topics:
    // the main collections traits
    // mutable and immutable collections
    // sequences
    // lists
    // sets
    // operators for adding or removing elements
    // common methods
    // mapping a function
    // reducing, folding and scanning
    // zipping
    // iterators
    // streams
    // lazy views
    // interoperability with java collections
    // parallel collections

    // library users point of view;
    // coll. extends the 'Iterable' trait;
    // three major categories: sequence, set, map;
    // mutable/immutable versions;
    // list: head :: tail;
    // LinkedHashSet: insertion order; SortedSet: sorted iterator;
    // operators: + add to unordered; +:, :+ prepend or append; ++ concatenate, - and -- remove elements;
    // Iterable, Seq traits with dozens of useful methods;
    // use mapping, folding, zipping techniques

    // the main collections traits
    def theMainCollectionsTraits = {
        import scala.collection.mutable

        // https://docs.scala-lang.org/overviews/collections/overview.html

        // Iterable:
        //      Seq, Set, Map
        //          Seq <- IndexedSeq
        //          Set <- SortedSet
        //          Map <- SortedMap

        // Iterable can yield an Iterator { hasNext; next }
        val coll = Seq(1,2,3)
        val iter = coll.iterator
        while (iter.hasNext) println( iter.next() )
        // most basic way of traversing a collection

        // Seq: ordered sequence (array, list, etc);
        // IndexedSeq: fast random access through an index;
        // Set: unordered collection of distinct values; SortedSet -- visited in sorted order;
        // Map: is a set of pairs (key, value); SortedMap -- visits as sorted by keys;

        // similar to java but with improvements:
        // - maps are a part of the hierarchy;
        // - IndexedSeq is the supertype of arrays but not lists.
        // in java ArrayList and LinkedList implement a common List interface,
        // RandomAccess marker interface was added later.

        // uniform creation principle:
        // companion objects with 'apply' for constructing instances
        val i = Iterable(1, 2, 3)
        val s = Set(1, 2, 3)
        val m = Map('a'->1, 'b'->2)

        // translate between coll. types
        val (s1, s2, s3) = (i.toSet, s.toSeq, m.to[mutable.ArrayBuffer])

        // can use '==' operator to compare any iterables with same type;
        // use 'sameElements' method for other
        s2 == coll
        s2 sameElements s1

    }

    // mutable and immutable collections
    def mutableAndImmutableCollections = {
        import scala.collection.mutable
        // https://docs.scala-lang.org/overviews/collections/overview.html

        // implemented with immutable.Map
        val supertype = scala.collection.Map.empty[String, String]

        // extends scala.collection.Map
        val immutabletype = scala.collection.immutable.Map.empty[String, String]

        // extends scala.collection.Map
        val mutabletype = scala.collection.mutable.Map.empty[String, String]

        // Predef val Map         = immutable.Map
        val defaulttype = Map.empty[String, String]

        // immutable collections are useful in recursions
        def digits(n: Int): Set[Int] = {
            if (n < 0) digits(-n)
            else if (n < 10) Set(n)
            else digits(n / 10) + (n % 10) // + for unordered collections
            // construct a new set
        }
    }

    // sequences
    def sequences = {
        // Seq:
        // IndexedSeq <- Vector, Range
        // (unindexed) List, Stream, Stack, Queue

        // Vector: immutable version of ArrayBuffer, indexed sequence with fast random access;
        // implemented as a tree with up to 32 values in each node; 4 hops max for apply(i) in 1M elements
        // scala> math.pow(32, 4) // res0: Double = 1 048 576

        // Range: monotonic sequence represented by start, stop, step;
        val r = 1 to 100 by 10

        // mutable sequences, most useful:
        // Seq:
        // IndexedSeq <- ArrayBuffer
        // Stack, Queue, PriorityQueue, ListBuffer
    }

    // lists
    def lists = {
        // recursive data structure:
        // list is Nil or head :: tail, where tail is list

        val digits = List(4,2)
        digits.head // 4
        digits.tail // List(2)
        digits.tail.head // 2
        digits.tail.tail // Nil

        // :: operator, right associative
        val list = 9 :: List(4, 2) // List(9,4,2)
        val list2 = 9 :: 4 :: 2 :: Nil // starts from Nil and goes to left

        // natural for recursion
        def sum(lst: List[Int]): Int = lst match {
            case Nil => 0
            case h :: t => h + sum(t) // h: head, t: tail
        }

        // mutable list: ListBuffer, linked list with ref. to the last node.
        // with java LinkedList you can remove item after every second call to next;
        // no such operator in ListBuffer, better generate a new list

        // deprecated LinkedList, DoubleLinkedList
    }

    // sets
    def sets = {
        import scala.collection.mutable

        // set: unordered collection of distinct elements;
        // adding an existing el. has no effect
        Set(1,2) + 1 == Set(1,2)

        // sets implemented as hash sets by default (hashCode method)

        // LinkedHashSet: keeps in linked list the order of insertion
        val weekdays = mutable.LinkedHashSet("Mo", "Tu", "We", "Th", "Fr")

        // sorted order: SortedSet
        val numbers = mutable.SortedSet(5, 4, 3, 1, 2)

        // bit set: set of non-negative integers as a sequence of bits,
        // effective as long as max value is not too large
        val bits = scala.collection.immutable.BitSet(1,2,3)

        // contains, subsetOf, union, intersect, diff, etc
        weekdays contains "Sa" // false
        Set("Mo") subsetOf weekdays // true
        Set("Sa", "Su") union weekdays ++ weekdays -- weekdays diff weekdays intersect weekdays
    }

    // operators for adding or removing elements
    def operatorsForAddingOrRemovingElements = {
        import scala.collection.mutable

        // adding, removing operators: depending on the collection type

        // apply(i) :+ + - ++ ++: :: :::
        // mutable: += ++= -= +-: ++=:

        // + for unordered collection, generally
        // +: :+ for prepend append to ordered coll.
        Vector(1,2,3) :+ 5 // colon to sequence
        1 +: Vector(1,2,3)

        // mutable
        val numbers = mutable.ArrayBuffer(1,2,3)
        numbers += 5

        // immutable collection, mutable reference
        var numbers2 = Set(1,2,3)
        numbers2 += 5 // creates a new set
        var numberV = Vector(1,2,3)
        numberV :+= 5 // creates a new vector; += does not work since Vector immutable

        // summary:
        // :+ or +: for append or prepend
        // + for unordered coll.
        // - to remove
        // ++ -- for bulk add remove
        // mutations: += ++= -= --=
        // for lists :: ::: // pattern matching won't work with +:
        // stay away from ++: +=: ++=:
    }

    // common methods
    def commonMethods = {
        // Iterable methods
        // https://www.scala-lang.org/api/current/scala/collection/Iterable.html

        // n.b.
        // headOption/lastOption
        // tail/init
        // transform, collect
        // aggregate
        // partition, span
        // splitAt
        // slice, view
        // grouped, sliding
        // groupBy
        // addString

        // Seq methods
        // https://www.scala-lang.org/api/current/scala/collection/Seq.html

        // n.b.
        // containsSlice
        // lastIndexOfSlice
        // indexWhere
        // prefixLength, segmentLength
        // intersect, diff
        // permitations, combinations

        // uniform return type principle:
        // methods return a new collection of the same type
    }

    // mapping a function
    def mapping_a_function = {
        import scala.collection.mutable

        // transform elements of a collection, apply unary function

        val names = List("Peter", "Paul", "Mary")
        names.map(_.toUpperCase) // yields a collection of transformed items
        // exactly the same:
        for (n <- names) yield n.toUpperCase

        // if transformation yields a collection, you may want to concatenate: flatMap
        def ulcase(s: String) = Seq(s.toUpperCase, s.toLowerCase) // yields a collection
        names.map(ulcase) // List[Seq[String]] = List(List(PETER, peter), List(PAUL, paul), List(MARY, mary))
        names.flatMap(ulcase) // List[String] = List(PETER, peter, PAUL, paul, MARY, mary)

        // useful: flatMap with Option transformation result: map + filter

        // map, flatMap are important: used for translating 'for expressions'
        for (i <- 1 to 10) yield i * i
        // translated to
        (1 to 10).map(i => i * i)
        // and
        for (i <- 1 to 10; j <- 1 to i) yield i * j
        // becomes
        (1 to 10).flatMap(i => (1 to i).map(j => i * j))

        // method 'transform' like 'map' only in-place, for mutable collections
        val buf = mutable.ArrayBuffer("Peter", "Paul", "Mary")
        buf.transform(_.toUpperCase)

        // foreach: elem => Unit, for side effect
        names foreach println

        // collect: works with partial functions
        "-3+4".collect { case '+' => 1; case '-' => -1 } // Vector(-1, 1)

        // groupBy: yields a map
        val map = buf.groupBy(_.substring(0, 1).toUpperCase)
        // Map(M -> ArrayBuffer(Mary), P -> ArrayBuffer(Peter, Paul))

    }

    // reducing, folding and scanning
    def reducingFoldingAndScanning = {
        import scala.collection.mutable

        // combine elements with a binary function,
        // operations on adjacent elements

        // reduceLeft // can't work with empty list
        List(1,7,2,9).reduceLeft(_ - _) // ((1 - 7) -2) - 9

        // reduceRight : useful for growing a list
        List(1,7,2,9).reduceRight(_ - _) // 1 - (7 - (2 - 9))

        // but foldLeft or foldRight can work with empty list
        (0 /: List(1,7,2,9))(_ - _) // is equivalent to (colon to collection)
        List(1,7,2,9).foldLeft(0)(_ - _) // (((0 - 1) - 7) -2) - 9
        // init and op are curried: for type inference

        // folding as replacement for a loop:
        // instead of
        val freq = mutable.Map.empty[Char, Int].withDefaultValue(0)
        for (char <- "Mississippi") freq(char) = 1 + freq(char)
        // freq = Map(M -> 1, s -> 4, p -> 2, i -> 4)
        // you can do in f style // n.b. immutable map
        (Map.empty[Char, Int].withDefaultValue(0) /: "Mississippi") { (map, char) => map + (char -> (1 + map(char)))}
        // Map(M -> 1, i -> 4, s -> 4, p -> 2)

        // it is possible to replace any loop with a fold,
        // just build a data structure to hold a state and  define a operation that
        // implements one step

        // scanLeft, scanRight: combine fold and map, yielding a coll. of all intermediate results
        (1 to 10).scanLeft(0)(_ + _) // Vector(0, 1, 3, 6, 10, 15, 21, 28, 36, 45, 55)
    }

    // zipping
    def zipping = {
        // two collections, operations on pairs of corresponding elements
        val prices = Seq(5d, 20d, 9.95)
        val quantities = Seq(10, 2, 1)

        // zip: res.length = shortest.length
        prices zip quantities // Seq[(Double, Int)] = List((5.0,10), (20.0,2), (9.95,1))

        // list of prices
        (prices zip quantities) map { case (p, c) => p * c } // List(50.0, 40.0, 9.95)
        // total price
        (prices zip quantities) map { case (p, c) => p * c } sum // Double = 99.95

        // zipAll: let you specify defaults for shorter collection
        Seq(1,2,3).zipAll(Seq(4,5), -42, 42) // Seq[(Int, Int)] = List((1,4), (2,5), (3,42))

        // zipWithIndex: useful if you want index for an element with a certain property
        "Scala".zipWithIndex.max // (Char, Int) = (l,3)

    }

    // iterators
    def iterators = {
        // not very useful, but basic: 'iterator' method;
        // useful for "lazy" computations: read file or expensive computations;
        // fragile, 'next' mutates the iterator, no cache, one pass only.

        // 'grouped', 'sliding' returns an iterator

        // while (iter.hasNext) ... iter.next()
        // or
        // for (elem <- iter) ...

        // 'buffered': cache iter.head
        val iter = scala.io.Source.fromFile("/tmp/test").buffered
        while (iter.hasNext && iter.head.isWhitespace) iter.next()
        // iter points to the first non-whitespace char

        // you can copy iter to collection
        iter.toVector
    }

    // streams
    def streams = {
        // immutable list in which the tail is computed lazily;
        // stream methods are evaluated lazily;
        // stream caches the visited values;

        // scala streams != java8 streams
        // scala lazy views == java8 streams

        // stream lazy tail
        def numsFrom(n: BigInt): Stream[BigInt] = n #:: numsFrom(n + 1)

        val tenOrMore = numsFrom(10) // Stream(10, ?)
        // tail is unevaluated

        tenOrMore.tail.tail.tail // Stream(13, ?)
        // and cache
        // scala> tenOrMore
        // res19: Stream[BigInt] = Stream(10, 11, 12, 13, ?)

        // lazy methods
        val squares = numsFrom(1).map(x => x * x) // Stream(1, ?)

        // take + force methods to get a collection
        squares.take(5).force // Stream(1, 4, 9, 16, 25)
        // squares.force // No!

        // stream from iterator
        val words = scala.io.Source.fromString(
            """a
              |b
              |c
              |d
              |e
              |f
            """.stripMargin).getLines.toStream
        // stream caches the visited lines!
        words       // Stream(a, ?)
        words(5)    // f
        words       // Stream(a, b, c, d, e, f, ?)
    }

    // lazy views
    def lazyViews = {
        // 'view' method: yields a collection on which methods are applied lazily;
        // no cache; even first elem is unevaluated;
        // 'apply' method forces evaluation of the entire view;
        // mutating view of mutable collection you mutate original coll;

        val palindromicSquares = (1 to 10000000).view
            .map(x => x*x)
            .filter(x => x.toString == x.toString.reverse)
        // scala.collection.SeqView[Int,Seq[_]] = SeqViewMF(...)

        palindromicSquares.take(10).mkString(",") // String = 1,4,9,121,484,676,10201,12321,14641,40804
        palindromicSquares.take(10).force // Seq[Int] = Vector(1, 4, 9, 121, 484, 676, 10201, 12321, 14641, 40804)

        // don't call
        // palindromicSquares(3)
        // 'apply' method forces evaluation of the entire view;
        // call
        palindromicSquares.take(3).last

        // mutating view of mutable collection you mutate original coll;
        import scala.collection.mutable
        val buff = (1 to 30).to[mutable.ArrayBuffer]
        buff.view(10, 20).transform(x => 0)
        // scala> buff
        // ArrayBuffer(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 21, ...
    }

    // interoperability with java collections
    def interoperabilityWithJavaCollections = {
        import scala.collection.mutable

        // https://www.scala-lang.org/api/current/scala/collection/JavaConverters$.html

        // import scala.collection.JavaConversions // deprecated
        import scala.collection.JavaConverters._

        // use 'asScala'
        val props: mutable.Map[String, String] = System.getProperties.asScala

        // or, import converters you needed only
        import scala.collection.JavaConverters.propertiesAsScalaMap

        // n.b. conversions yield wrappers, no transformation processes involved

    }

    // parallel collections
    def parallelCollections = {
        // concurrent programs

        // parallel collections use a global fork-join pool,
        // well suited for processor-bound programs.
        // for IO tasks or some other blocking/waiting you should choose a different
        // execution context

        // n.b. to run this code in REPL you will need a crutch (I'm using a def foo = ...; foo)
        // https://github.com/scala/scala-parallel-collections/issues/34
        // https://stackoverflow.com/questions/15176199/scala-parallel-collection-in-object-initializer-causes-a-program-to-hang/15176433#15176433
        // https://github.com/scala/bug/issues/8119

        // split collection to chunks, process chunks in parallel, combine chunks back to single collection
        val largecoll = (1 to 10000000).toArray
        val largecollPar = largecoll.par // wrapper for array, other coll. types may need copying
        // computes concurrently
        println(largecollPar.sum)
        println(largecollPar.count(_ % 2 == 0))

        // parallelize a 'for loop'
        for (i <- (0 until 1000).par) print(s" $i")
        // n.b. numbers are printed
        // out of order: first in, first printed

        // but, in 'for loop' constructing a new collection, results are
        // assembled in order
        assert( (for (i <- (0 until 10000000).par) yield i) == (0 until 10000000) )

        // in parallel computations do NOT mutate shared variables
        var count = 0; for (i <- (1 to 1000000).par) { if (i % 2 == 0) count += 1 }

        // 'seq' method:
        // method 'par' return ParSeq, ParSet, ParMap objects
        // these are NOT subtypes of Seq, Set, Map and can't be passed to not par methods.
        // so, you need to convert it back
        val res = largecollPar.seq

        // not all methods can be parallelized:
        // operator must be associative: (a op b) op c == a op (b op c)
        // parallel: fold, reduce, aggregate
        val str = (' ' to 'z').foldLeft("")(_ :+ _)
        str.par.aggregate(Set.empty[Char])(_ + _, _ ++ _)
    }
}

object Collections_Exercises {

    // 1. Write a function that, given a string, produces a map of the indexes of all characters.
    // For example, indexes("Mississippi") should return a map associating
    // 'M' with the set {0},
    // 'i' with the set {1, 4, 7, 10}, and so on.
    // Use a mutable map of characters to mutable sets.
    // How can you ensure that the set is sorted?
    def ex1 = {
        // SortedSet
        import scala.collection.mutable

        def charsToIndexes(str: String) = {
            val map = mutable.Map.empty[Char, mutable.SortedSet[Int]].withDefault(_ => mutable.SortedSet.empty)

            str.zipWithIndex.foreach { case (char, idx) =>
                map += (char -> (map(char) += idx))
            }

            map
        }

        // test
        val str = "Mississippi"
        val map = charsToIndexes(str)
        println(map)
        assert(map('M') == Set(0))
        assert(map('i') == Set(1,4,7,10))
        assert(map('s') == Set(2,3,5,6))
        assert(map('p') == Set(8,9))
    }

    //2. Repeat the preceding exercise, using an immutable map of characters to lists.
    def ex2 = {
        def charsToIndexes(str: String): Map[Char, List[Int]] = {
            // group by char, get map char=>(char,idx)
            val pairsmap = str.zipWithIndex.groupBy { case (char, idx) => char }
            // extract result
            pairsmap.map { case (char, pairs) =>
                char -> pairs.map{ case (_, idx) => idx}.toList }
        }

        // test
        val str = "Mississippi"
        val map = charsToIndexes(str)
        println(map)
        assert(map('M') == List(0))
        assert(map('i') == List(1,4,7,10))
        assert(map('s') == List(2,3,5,6))
        assert(map('p') == List(8,9))

    }

    //3. Write a function that removes every second element from a ListBuffer.
    // Try it two ways.
    // Call remove(i) for all even i starting at the end of the list.
    // Copy every second element to a new list.
    // Compare the performance.
    def ex3 = {
        import scala.collection.mutable

        def removeEverySecond(lst: mutable.ListBuffer[Int], first: Boolean): mutable.ListBuffer[Int] = {

            def firstway = {
                println("way 1")
                val res = lst map identity
                for (i <- res.indices.reverse if i % 2 != 0) res.remove(i)
                res
            }

            def secondway = {
                println("way 2")
                // fastest method
                // lst.zipWithIndex.withFilter { case (x, idx) => idx % 2 == 0 }.map(_._1)
                // first way (remove bad): 7263 ms
                // second way (copy good): 10 ms

                // lst.zipWithIndex.flatMap { case (x, idx) => if (idx % 2 == 0) Some(x) else None }
                // first way (remove bad): 7256 ms
                // second way (copy good): 45 ms

                // slowest method
                val res = mutable.ListBuffer.empty[Int]
                res.sizeHint(1 + lst.length / 2)
                for (i <- lst.indices if i % 2 == 0) res += lst(i)
                res
                // first way (remove bad): 7296 ms
                // second way (copy good): 11953 ms
            }

            if (first) firstway
            else secondway
        }

        // test
        val lst = (0 to 10).to[mutable.ListBuffer]
        val expected = Seq(0, 2, 4, 6, 8, 10).to[mutable.ListBuffer]

        Seq(true, false) foreach { way =>
            val res = removeEverySecond(lst, way)
            println(res)
            assert(res == expected)
        }

        assert(lst == (0 to 10).to[mutable.ListBuffer])

        def performanceTest(): Unit = {
            // https://stackoverflow.com/questions/9160001/how-to-profile-methods-in-scala
            case class Result[T](result: T, elapsedNs: Long)
            def time[R](block: => R): Result[R] = {
                val t0 = System.nanoTime()
                val result = block
                val t1 = System.nanoTime()
                Result(result, t1 - t0)
            }

            val lstsize = 50000
            val count = 5

            val lst = (0 to lstsize).to[mutable.ListBuffer]

            val first = time {
                (0 to count) foreach (_ => removeEverySecond(lst, first = true))
            }

            val second = time {
                (0 to count) foreach (_ => removeEverySecond(lst, first = false))
            }

            println(s"first way (remove bad): ${first.elapsedNs / 1000000} ms")
            println(s"second way (copy good): ${second.elapsedNs / 1000000} ms")
            // first way (remove bad): 7296 ms
            // second way (copy good): 11953 ms

            // second is slower because for each copied element list creates a new Cons object;
            // on first way list just switch references.
        }

        def performanceTest2(): Unit = {
            // http://scalameter.github.io/home/gettingstarted/0.7/inline/index.html
            import org.scalameter._

            val timeBench = config(
                Key.exec.minWarmupRuns -> 5,
                Key.exec.maxWarmupRuns -> 20,
                Key.exec.benchRuns -> 15,
                Key.verbose -> true
            ).withWarmer(new Warmer.Default)

            val memBench = timeBench.withMeasurer(new Measurer.MemoryFootprint)

            val lst = (0 to 30000).to[mutable.ListBuffer]

            val firstTime = timeBench measure { removeEverySecond(lst, first = true) }
            val secondTime = timeBench measure { removeEverySecond(lst, first = false) }

            val firstMem = memBench measure { removeEverySecond(lst, first = true) }
            val secondMem = memBench measure { removeEverySecond(lst, first = false) }

            println(Console.YELLOW)
            println(s"first way (remove bad from-end-to-start): $firstTime, $firstMem")
            println(s"second way (copy good from start-to-end): $secondTime, $secondMem")
            println(Console.RESET)
            // first way (remove bad from-end-to-start): 467.3844768 ms, 599.8 kB
            // second way (copy good from start-to-end): 849.0186980666668 ms, 358.05 kB
        }

        performanceTest()
        performanceTest2()
    }

    // 4. Write a function that receives a collection of strings and a map from strings to integers.
    // Return a collection of integers that are values of the map corresponding to
    // one of the strings in the collection.
    // For example, given Array("Tom", "Fred", "Harry") and
    // Map("Tom" -> 3, "Dick" -> 4, "Harry" -> 5),
    // return Array(3, 5).
    // Hint: Use flatMap to combine the Option values returned by get.
    def ex4 = {
        def str2int(strs: Iterable[String], map: Map[String, Int]): Iterable[Int] =
            strs.flatMap(map.get)

        // test
        val res = str2int(Array("Tom", "Fred", "Harry"), Map("Tom" -> 3, "Dick" -> 4, "Harry" -> 5))
        println(res.toList)
        assert(res == Array(3, 5).toIterable)
    }

    // 5. Implement a function that works just like mkString, using reduceLeft.
    def ex5 = {
        def mkString(lst: Iterable[String], start: String = "", sep: String = "", end: String = ""): String =
            start + lst.reduceLeft(_ + sep + _) + end

        // test
        val lst = Seq("a", "b", "c")
        assert(lst.mkString("[", ",", "]") == mkString(lst, "[", ",", "]"))
    }

    // 6. Given a list of integers lst, what is
    // (lst :\ List[Int]())(_ :: _) ?
    // (List[Int]() /: lst)(_ :+ _) ?
    // How can you modify one of them to reverse the list?
    def ex6 = {
        val lst = List(1, 2, 3)
        def emptyList = List.empty[Int]

        // apply 'cons' to lst elements, right to left, building copy of lst
        val foldRightCons = (lst :\ emptyList)(_ :: _)
        assert(foldRightCons == lst)

        // apply 'append' to lst elements, left to right, building copy of lst
        val foldLeftAppend = (emptyList /: lst)(_ :+ _)
        assert(foldRightCons == lst)

        // to revert lst: apply foldLeft with cons or foldRight with append
        assert(lst.reverse == (emptyList /: lst)((xs, x) => x :: xs ) )
        assert(lst.reverse == (lst :\ emptyList)((x, xs) => xs :+ x) )
    }

    // 7. In Section 13.10, “Zipping,” on page 187, the expression
    // (prices zip quantities) map { p => p._1 * p._2 }
    // is a bit inelegant.
    // We can’t do
    // (prices zip quantities) map { _ * _ }
    // because _ * _ is a function with two arguments, and we need a function with
    // one argument that is a tuple.
    // The tupled method of the Function object changes a function with two arguments
    // to one that takes a tuple.
    // Apply tupled to the multiplication function so you can map it over the list of pairs.
    def ex7 = {
        val prices = Seq(5d, 20d, 9.95)
        val quantities = Seq(10, 2, 1)
        val res = (prices zip quantities) map Function.tupled(_ * _)
        assert(res == List(50.0, 40.0, 9.95))
    }

    // 8. Write a function that turns an array of Double values into a two-dimensional array.
    // Pass the number of columns as a parameter.
    // For example, with
    // Array(1, 2, 3, 4, 5, 6) and three columns,
    // return Array(Array(1, 2, 3), Array(4, 5, 6)).
    // Use the grouped method.
    def ex8 = {
        implicit def int2double(i: Int): Double = i.toDouble

        def splitToRows(arr: Array[Double], ncols: Int): Array[Array[Double]] =
            arr.grouped(ncols).toArray


        // test
        val data: Array[Double] = Array(1, 2, 3, 4, 5, 6)
        val expected: Array[Array[Double]] = Array(
            Array(1, 2, 3),
            Array(4, 5, 6)
        )
        val res = splitToRows(data, 3)

        assert(res.length == 2)
        assert(res.head.toList == expected.head.toList)
        assert(res.last.toList == expected.last.toList)
    }

    // 9. The Scala compiler transforms a for/yield expression
    //      for (i <- 1 to 10; j <- 1 to i) yield i * j
    // to invocations of flatMap and map, like this:
    //      (1 to 10).flatMap(i => (1 to i).map(j => i * j))
    // Explain the use of flatMap.
    // Hint: What is (1 to i).map(j => i * j) when i is 1, 2, 3?
    // What happens when there are three generators in the for/yield expression?
    def ex9 = {
        // second generator makes collections, we don't want 10 collections,
        // we want unwrapped/flattened values.

        // for three generators we need two flatMap
        val res = for (i <- 1 to 3; j <- 1 to i; k <- 1 to j) yield s"i: $i, j: $j, k: $k"
        res foreach println

        assert(res.mkString ==
            (1 to 3).flatMap(i =>
                (1 to i).flatMap(j =>
                    (1 to j).map(k =>
                        s"i: $i, j: $j, k: $k"))).mkString
        )
    }

    //10. The method java.util.TimeZone.getAvailableIDs yields time zones such as
    //Africa/Cairo and Asia/Chungking. Which continent has the most time zones? Hint:
    //groupBy.
    def ex10 = {
        ???
    }

    //11. Harry Hacker reads a file into a string and wants to use a parallel collection to update the letter
    //frequencies concurrently on portions of the string. He uses the following code:
    //Click here to view code image
    //val frequencies = new scala.collection.mutable.HashMap[Char, Int]
    //for (c <- str.par) frequencies(c) = frequencies.getOrElse(c, 0) + 1
    //Why is this a terrible idea? How can he really parallelize the computation? (Hint: Use
    //aggregate.)
    def ex11 = {
        ???
    }

}
