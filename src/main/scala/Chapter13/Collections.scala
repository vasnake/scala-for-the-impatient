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
    // coll. extends the 'Iterble' trait;
    // three major categories: sequence, set, map;
    // mutable/immutable versions;
    // list: head :: tail;
    // LinkedHashSet: insertion order; SortedSet: sorted iterator;
    // operators: + add to unordered; +:, :+ prepend or append; ++ concatenate, - and -- remove elements;
    // Iterable, Seq traits with dozens of useful methods;
    // use mapping, folding, zipping techniques

    // the main collections traits
    def theMainCollectionsTraits = {
        ???
    }

    // mutable and immutable collections
    def mutableAndImmutableCollections = {
        ???
    }

    // sequences
    def sequences = {
        ???
    }

    // lists
    def lists = {
        ???
    }

    // sets
    def sets = {
        ???
    }

    // operators for adding or removing elements
    def operatorsForAddingOrRemovingElements = {
        ???
    }

    // common methods
    def commonMethods = {
        ???
    }

    // mapping a function
    def mapping_a_function = {
        ???
    }
    // reducing, folding and scanning
    def reducingFoldingAndScanning = {
        ???
    }

    // zipping
    def zipping = {
        ???
    }

    // iterators
    def iterators = {
        ???
    }

    // streams
    def streams = {
        ???
    }

    // lazy views
    def lazyViews = {
        ???
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
