package Chapter13

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
        ???
    }

    // parallel collections
    def parallelCollections = {
        ???
    }
}

object Collections_Exercises {

    // 1. Write a function that, given a string, produces a map of the indexes of all characters. For
    //example, indexes("Mississippi") should return a map associating 'M' with the set
    //{0}, 'i' with the set {1, 4, 7, 10}, and so on. Use a mutable map of characters to
    //mutable sets. How can you ensure that the set is sorted?
    def ex1 = {
        ???
    }

    //2. Repeat the preceding exercise, using an immutable map of characters to lists.
    def ex2 = {
        ???
    }

    //3. Write a function that removes every second element from a ListBuffer. Try it two ways.
    //Call remove(i) for all even i starting at the end of the list. Copy every second element to a
    //new list. Compare the performance.
    def ex3 = {
        ???
    }

    //4. Write a function that receives a collection of strings and a map from strings to integers. Return
    //a collection of integers that are values of the map corresponding to one of the strings in the
    //collection. For example, given Array("Tom", "Fred", "Harry") and Map("Tom"
    //-> 3, "Dick" -> 4, "Harry" -> 5), return Array(3, 5). Hint: Use flatMap
    //to combine the Option values returned by get.
    def ex4 = {
        ???
    }

    //5. Implement a function that works just like mkString, using reduceLeft.
    def ex5 = {
        ???
    }

    //6. Given a list of integers lst, what is (lst :\ List[Int]())(_ :: _)?
    //(List[Int]() /: lst)(_ :+ _)? How can you modify one of them to reverse the
    //list?
    def ex6 = {
        ???
    }

    //7. In Section 13.10, “Zipping,” on page 187, the expression (prices zip quantities)
    //map { p => p._1 * p._2 } is a bit inelegant. We can’t do (prices zip
    //quantities) map { _ * _ } because _ * _ is a function with two arguments, and
    //we need a function with one argument that is a tuple. The tupled method of the Function
    //object changes a function with two arguments to one that takes a tuple. Apply tupled to the
    //multiplication function so you can map it over the list of pairs.
    def ex7 = {
        ???
    }

    //8. Write a function that turns an array of Double values into a two-dimensional array. Pass the
    //number of columns as a parameter. For example, with Array(1, 2, 3, 4, 5, 6) and
    //three columns, return Array(Array(1, 2, 3), Array(4, 5, 6)). Use the
    //grouped method.
    def ex8 = {
        ???
    }

    //9. The Scala compiler transforms a for/yield expression
    //Click here to view code image
    //for (i <- 1 to 10; j <- 1 to i) yield i * j
    //to invocations of flatMap and map, like this:
    //Click here to view code image
    //(1 to 10).flatMap(i => (1 to i).map(j => i * j))
    //Explain the use of flatMap. Hint: What is (1 to i).map(j => i * j) when i is 1,
    //2, 3?
    //What happens when there are three generators in the for/yield expression?
    def ex9 = {
        ???
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
