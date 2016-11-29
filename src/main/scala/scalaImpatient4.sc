// Scala for the Impatient
// 4 Maps and Tuples

// Maps are collections of key/value pairs.

{
    val scores = Map("Alice" -> 10, "Bob" -> 3, "Cindy" -> 8)
    // This constructs an immutable Map[String, Int] whose contents can’t be changed
}

{
    // If you want a mutable map, use
    val scores = scala.collection.mutable.Map("Alice" -> 10, "Bob" -> 3, "Cindy" -> 8)
}

{
    // You could have equally well defined the map as
    val scores = Map(("Alice", 10), ("Bob", 3), ("Cindy", 8))
    // The -> operator is just a little easier on the eyes than the parentheses.
    // It also supports the intuition that a map data structure is a kind of function
    // that maps keys to values.
    // The difference is that a function computes values, and a map just looks them up.

    // To check whether there is a key with the given value, call the contains method:
    val bobsScore = if (scores.contains("Bob")) scores("Bob") else 0
    // Since this call combination is so common, there is a shortcut:
    val bobsScore2 = scores.getOrElse("Bob", 0)
}

{
    val scores = Map(("Alice", 10), ("Bob", 3), ("Cindy", 8))
    // The following amazingly simple loop iterates over all key/value pairs of a map:
    for ((k, v) <- scores) yield (v, k)
}

{
    // You might want a tree map if you don’t have a good hash function for the keys,
    // or if you need to visit the keys in sorted order.
    // To get an immutable tree map instead of a hash map, use
    val scores = scala.collection.immutable.SortedMap(
        "Alice" -> 10, "Fred" -> 7, "Bob" -> 3, "Cindy" -> 8)
    // Unfortunately, there is (as of Scala 2.9) no mutable tree map.
    // Your best bet is to adapt a Java TreeMap
}

{
    // If you want to visit the keys in insertion order, use a LinkedHashMap
    val months = scala.collection.mutable.LinkedHashMap(
        "January" -> 1,  "February" -> 2, "March" -> 3, "April" -> 4, "May" -> 5)
}

{
    // If you get a Java map from calling a Java method, you may want to convert it
    // to a Scala map so that you can use the pleasant Scala map API.
    import scala.collection.JavaConversions.mapAsScalaMap
    // Then trigger the conversion by specifying the Scala map type:
    val scores: scala.collection.mutable.Map[String, Int] =
        new java.util.TreeMap[String, Int]

    // In addition, you can get a conversion from
    // java.util.Properties to a Map[String, String]:
    import scala.collection.JavaConversions.propertiesAsScalaMap
    val props: scala.collection.Map[String, String] = System.getProperties()
}

{
    // Conversely, to pass a Scala map to a method that expects a Java map,
    // provide the opposite implicit conversion. For example:
    import scala.collection.JavaConversions.mapAsJavaMap
    import java.awt.font.TextAttribute._ // Import keys for map below
    val attrs = Map(FAMILY -> "Serif", SIZE -> 12) // A Scala map
    val font = new java.awt.Font(attrs) // Expects a Java map
}

// If you have a collection of keys and a parallel collection of values,
// then zip them up and turn them into a map like this:
//    keys.zip(values).toMap

// Exercises

// Set up a map of prices for a number of gizmos that you covet.
// Then produce a second map with the same keys and the prices at a 10 percent discount
{
    val gizmos = Map("phone" -> 30, "sail" -> 40, "acesspoint" -> 5)
    val disc = for ((g, p) <- gizmos) yield (g, p * 0.9)
    disc
}

// Write a program that reads words from a file.
// Use a mutable map to count how often each word appears.
// At the end, print out all words and their counts.
{
    val counter = collection.mutable.Map.empty[String, Int].withDefaultValue(0)
    val path = "/home/valik/Dropbox/clipb/english.txt"
    val in = new java.util.Scanner(new java.io.File(path))
    while (in.hasNext()) {
        val word = in.next()
        counter(word) = counter(word) + 1
    }
    for ((w,c) <- counter) println(s"$w: $c")
}

// Repeat the preceding exercise with an immutable map
{
    var counter = collection.immutable.Map.empty[String, Int].withDefaultValue(0)
    val path = "/home/valik/Dropbox/clipb/english.txt"
    val in = new java.util.Scanner(new java.io.File(path))
    while (in.hasNext()) {
        val word = in.next()
        counter = counter.updated(word, counter(word)+1)
    }
    for ((w,c) <- counter) println(s"$w: $c")
}

// Repeat the preceding exercise with a sorted map,
// so that the words are printed in sorted order
{
    var counter = collection.immutable.SortedMap.empty[String, Int].withDefaultValue(0)
    val path = "/home/valik/Dropbox/clipb/english.txt"
    val in = new java.util.Scanner(new java.io.File(path))
    while (in.hasNext()) {
        val word = in.next()
        counter += word -> (counter(word)+1)
    }
    for ((w,c) <- counter) println(s"$w: $c")
}

// Repeat the preceding exercise with a java.util.TreeMap
// that you adapt to the Scala API
{
    import collection.JavaConversions.mapAsScalaMap
    var counter: collection.mutable.Map[String, Int] =
        new java.util.TreeMap[String, Int].withDefaultValue(0)
    val path = "/home/valik/Dropbox/clipb/english.txt"
    val in = new java.util.Scanner(new java.io.File(path))
    while (in.hasNext()) {
        val word = in.next()
        counter += word -> (counter(word)+1)
    }
    for ((w,c) <- counter) println(s"$w: $c")
}

// Define a linked hash map that maps "Monday" to java.util.Calendar.MONDAY,
// and similarly for the other weekdays.
// Demonstrate that the elements are visited in insertion order.
{
    import java.util.Calendar._
    val days = collection.mutable.LinkedHashMap(
        "Monday" -> MONDAY, "Tuesday" -> TUESDAY, "Wednesday" -> WEDNESDAY, "Thursday" -> THURSDAY)
    for ((k, v) <- days) println(s"'$k': '$v'")
}

// Print a table of all Java properties, like this:
/*
    java.runtime.name             | Java(TM) SE Runtime Environment
    sun.boot.library.path         | /home/apps/jdk1.6.0_21/jre/lib/i386
    java.vm.version               | 17.0-b16
    java.vm.vendor                | Sun Microsystems Inc.
    java.vendor.url               | http://java.sun.com/
    path.separator                | :
    java.vm.name                  | Java HotSpot(TM) Server VM
 */
// You need to find the length of the longest key before you can print the table.
{
    import collection.JavaConversions.propertiesAsScalaMap
    val props: collection.Map[String, String] = System.getProperties()
    //val maxlen = props.keys.map(_.length).max
    val maxlen = props.keys.maxBy(_.length).length
    for ((k,v) <- props) {
        val key = k.padTo(maxlen, ' ')
        println(s"    $key | $v")
    }
}

// Write a function minmax(values: Array[Int])
// that returns a pair containing the smallest and largest values in the array.
{
    def minmax(vals: Array[Int]) = {
        (vals.min, vals.max)
    }
    println(minmax(Array(1,2,3,4,5,6,7,8,9)))
}

// Write a function lteqgt(values: Array[Int], v: Int)
// that returns a triple containing the
// counts of values less than v, equal to v, and greater than v.
{
    def lteqgt(vals: Array[Int], v: Int) = {
        var lt, gt, eq = 0
        for (a <- vals) {
            if (a < v) lt += 1
            else if (a > v) gt += 1
            else eq += 1
        }
        (lt, eq, gt)
    }
    println(lteqgt(Array(1,2,3,4,5,6,7,8,9), 7))
}

// What happens when you zip together two strings,
// such as "Hello".zip("World")?
// Come up with a plausible use case
// (H,W), (e,o), (l,r), ..., (o,d)
"Hello".zip("World")
// encode/decode?
