package Chapter04

object MapsAndTuples {
// topics:
    // constructing a map
    // accessing map values
    // updating map values
    // iterating over maps
    // sorted maps
    // interoperating with java
    // tuples
    // zipping

// hash table by default, tree map, map in general
// collection of key/value pairs / (k,v) tuples
// creating, querying and traversing maps

    // constructing a map
    def constructingAMap = {

        // Map[String, Int] immutable
        val scores = Map("Alice" -> 10, "Bob" -> 3, "Cindy" -> 8)

        import scala.collection.mutable
        val scoresMutable = mutable.Map("Alice" -> 10, "Bob" -> 3, "Cindy" -> 8)

        // new blank
        val scoresEmpty = mutable.Map[String, Int]()
        // I prefer
        val scoresEmpty2 = mutable.Map.empty[String, Int]

        // map = collection of pairs, Tuple2
        val scoresPairs = List(
            ("Alice", 10),
            ("Bob", 3),
            ("Cindy", 8)
        ).toMap
    }

    // accessing map values
    def accessingMapValues = {
        val scores = Map("Alice" -> 10, "Bob" -> 3, "Cindy" -> 8)

        // accessing by apply
        val bobsScore = scores("Bob")
        // if no key => exception
        // def default(key: K): V = throw new NoSuchElementException("key not found: " + key)

        // check key presence
        val bobsScore2 = if (scores.contains("Bob")) scores("Bob") else 0

        // shortcut
        val bobsScore3 = scores.getOrElse("Bob", 0)

        // more functional: monad Option
        val bobsScoreOption = scores.get("Bob") // Some or None

        // immutable maps can have 'default'
        val scoresWithDefault = scores.withDefaultValue(0)
        def tryToPredictValueByAI(str: String) = ???
        val scoresWithDefault2 = scores.withDefault(k => tryToPredictValueByAI(k))
    }

    // updating map values
    def updatingMapValues = {

        def mutableMaps = {
            import scala.collection.mutable
            val scores = mutable.Map("Alice" -> 10, "Bob" -> 3, "Cindy" -> 8)

            // in mutable map you can update value or add a new one, etc
            scores("Bob") = 10
            scores("Fred") = 7

            // or add/update a few pairs
            scores += ("Bob" -> 10, "Fred" -> 7)

            // or remove a pair
            scores -= "Alice"
        }

        def immutableMaps = {
            val scores = Map("Alice" -> 10, "Bob" -> 3, "Cindy" -> 8)

            // obtain a new modified map
            val updatedScores = scores + ("Bob" -> 10, "Fred" -> 7)

            // you can use a mutable reference to immutable map
            // easy way to share data, but may lead to data inconsistency
            var scoresMutableRef = scores + ("Bob" -> 10, "Fred" -> 7)
            scoresMutableRef += ("Bob" -> 10, "Fred" -> 7)
            scoresMutableRef -= "Alice"
        }
    }

    // iterating over maps
    def iteratingOverMaps = {
        val scores = Map("Alice" -> 10, "Bob" -> 3, "Cindy" -> 8)

        // loop over all k/v pairs using pattern matching
        for ((k,v) <- scores) println(s"key: ${k}, value: ${v}")

        // only keys
        for (k <- scores.keySet) println(s"key: ${k}")

        // only values
        for (v <- scores.values) println(s"value: ${v}")

        // reverse a map, naive approach
        for ((k,v) <- scores) yield (v,k)
    }

    // sorted maps
    def sortedMaps = {
        import scala.collection.mutable
        // maps: hash table by default, tree
        // hast table unordered

        // sorted keys // TreeMap red-black tree
        val scores = mutable.SortedMap("Alice" -> 10, "Bob" -> 3, "Cindy" -> 8)

        // keys in insertion order
        val insordScores = mutable.LinkedHashMap("Alice" -> 10, "Bob" -> 3, "Cindy" -> 8)
    }

    // interoperating with java
    def interoperatingWithJava = {
        import scala.collection.mutable
        // ? java have a mutable tree map that scala have not (SortedMAp?)

        import scala.collection.JavaConverters._

        // from java to scala
        val scores: mutable.Map[String, Int] = new java.util.TreeMap[String, Int]().asScala
        // java.util.Properties = HashTable[Object, Object]
        val props: mutable.Map[String, String] = System.getProperties.asScala

        // from scala to java
        import java.awt.font.TextAttribute._
        val attrs = Map(FAMILY -> "Serif", SIZE -> 12) // scala map
        val font = new java.awt.Font(attrs.asJava)

    }

    // tuples
    def tuples = {
        // pairs is a simplest tuples: Tuple2(x, y)

        val tup3 = (1, 3.14, "Fred") // Tuple3[Int, Double, String]
        val name = tup3._3
        val (i, d, s) = tup3 // pattern matching
        println(s"int: ${i}, double: ${d}, string: ${s}")

        // useful for compound results
        val (upper, lower) = "New York".partition(_.isUpper)
    }

    // zipping
    def zipping = {
        // produce pairs for processing together
        val symbols = List("<", "-", ">")
        val counts = List(2, 10, 2)

        val pairs = symbols zip counts
        for ((s,n) <- pairs) print(s*n)
    }
}

object MapsAndTuples_Exercises {

    // map of prices; with discount
    def ex1 = {
        def gizmosOnSail = {
            val gizmos = Map("phone" -> 30, "sail" -> 40, "acesspoint" -> 5)
            val withDiscount = for ((k, v) <- gizmos) yield (k, v * 0.9)
        }
    }

    // read words from a file; count words with mutable map
    def ex2 = {
        import scala.collection.mutable
        def wordCount(path: String) = {
            val counter = mutable.Map.empty[String, Int].withDefaultValue(0)
            def processNextToken(str: String) = counter(str) = counter(str) + 1

            val in = new java.util.Scanner(new java.io.File(path))
            while (in.hasNext) processNextToken(in.next)

            for ((w,c) <- counter) println(s"word: '$w' count: $c")
        }
    }

    // ex2 with immutable map
    def ex3 = {
        def wordCount(path: String) = {
            // mutable reference!
            var counter = Map.empty[String, Int].withDefaultValue(0)
            def processNextToken(str: String) = counter.updated(str, counter(str) + 1)

            val in = new java.util.Scanner(new java.io.File(path))
            while (in.hasNext) counter = processNextToken(in.next)

            for ((w,c) <- counter) println(s"word: '$w' count: $c")
        }
    }

    // ex3 with a sorted map
    def ex4 = {
        def wordCount(path: String) = {
            import scala.collection.immutable
            // mutable reference!
            var counter = immutable.SortedMap.empty[String, Int].withDefaultValue(0)
            def processNextToken(str: String) = counter.updated(str, counter(str) + 1)

            val in = new java.util.Scanner(new java.io.File(path))
            while (in.hasNext) counter = processNextToken(in.next)

            for ((w,c) <- counter) println(s"word: '$w' count: $c")
        }
    }

    // ex4 with java TreeMap
    def ex5 = {
        def wordCount(path: String) = {
            import scala.collection.JavaConverters._
            // mutable map!
            val counter = new java.util.TreeMap[String, Int]().asScala.withDefaultValue(0)
            def processNextToken(str: String) = counter.update(str, counter(str) + 1)

            val in = new java.util.Scanner(new java.io.File(path))
            while (in.hasNext) processNextToken(in.next)

            for ((w,c) <- counter) println(s"word: '$w' count: $c")
        }
    }

    // linked hash map for week days, insertion order
    def ex6 = {
        def insertionOrder = {
            import java.util.Calendar
            import scala.collection.mutable
            val days = mutable.LinkedHashMap("Monday" -> Calendar.MONDAY)

            for ((k,v) <- List(
                "Tuesday" -> Calendar.TUESDAY,
                "Wednesday" -> Calendar.WEDNESDAY,
                "Thursday" -> Calendar.THURSDAY,
                "Friday" -> Calendar.FRIDAY)
            ) days.update(k, v)

            for ((k,v) <- days) println(s"name: ${k}, value: ${v}")
        }
    }

    // all java properties, aligned
    def ex7 = {
        def java2scala = {
            import scala.collection.JavaConverters._
            val props = java.lang.System.getProperties.asScala
            val longestKeySize = props.keys.maxBy(_.length).length
            // def aligned(str: String) = { str + (" " * (longestKeySize - str.length)) }
            // for ((k,v) <- props) println(s"${aligned(k)} | ${v}")
            for ((k,v) <- props) println(s"""${k.padTo(longestKeySize, ' ')} | ${v}""")
        }
    }

    // return (min, max) tuple
    def ex8 = {
        def minmaxPair(values: Array[Int]) = (values.min, values.max)
    }

    // return (lt, eq, gt) tuple
    def ex9 = {
        def lteqgtTuple(values: Array[Int], v: Int) = {
            // can be done in one pass
            val cntLess = values.count(_ < v)
            val cntGreat = values.count(_ > v)
            val cntEq = values.length - (cntLess + cntGreat)
            (cntLess, cntEq, cntGreat)
        }
    }

    // zip two words
    def ex10 = {
        "Hello" zip "World" // Vector((H,W), (e,o), (l,r), (l,l), (o,d))
        // codec? compress/decompress?
    }
}
