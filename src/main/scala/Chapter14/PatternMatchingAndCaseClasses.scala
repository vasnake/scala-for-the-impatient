package Chapter14

object PatternMatchingAndCaseClasses {
// topics:
    // a better switch
    // guards
    // variables in patterns
    // type patterns
    // matching arrays, lists, tuples
    // extractors
    // patterns in variable declarations
    // patterns in for-expressions
    // case classes
    // the copy method and named parameters
    // infix notations in case clauses
    // matching nested structures
    // are case classes evil?
    // sealed classes
    // simulating enumerations
    // the option type
    // partial functions

    // pattern matching applications: 'switch' statement, type inquiry, destructuring;
    // case classes are optimized for pattern matching, compiler produce some methods;
    // 'match' expression is a better 'switch', no fal-through;
    // MatchError if no matches, use 'case _' to avoid it;
    // guard: arbitrary condition in a pattern;
    // can match on type;
    // can bing parts to variables;
    // in a 'for-expression' nonmatches are silently skipped;
    // the common sup. in hierarchy should be sealed;

    // a better switch
    def aBetterSwitch = {
        import java.awt.Color

        // C-style switch
        var sign: Int = ???
        val ch: Char = ???
        ch match {
            case '+' => sign = 1
            case '-' => sign = -1
            case _ => sign = 0 // default, w/o default MatchError may be thrown
        }
        // does not suffer from the 'fall-through' problem

        // 'match' is an expression (have a value)
        sign = ch match {
            case '+' => 1
            case '-' => -1
            case _ => 0
        }

        // use '|' to separate multiple alternatives
        ch match {
            case '0' | 'o' | 'O' => ???
        }

        // match with any types
        val color: Color = ???
        color match {
            case Color.RED => ???
        }
    }

    // guards
    def guards = {
        // match all digits? add a guard: any boolean condition
        val ch: Char = ???
        ch match {
            case _ if Character.isDigit(ch) => ???
            case '+' => ???
            case _ => ??? // default
        }
        // always matched top-to-bottom
    }

    // variables in patterns
    def variablesInPatterns = {
        // case keyword is followed by variable name: match expression is assigned to that variable
        val str: String = ???
        val i: Int = ???
        str(i) match {
            case ch => Character.digit(ch, 10) // ch holds str(i) char
        }
        // '_' is a special case of this feature

        // use the variable in a guard
        str(i) match {
            case ch if Character.isDigit(ch) => ???
        }

        // n.b. variable pattern can conflict with constants !!!
        // case ch ... // ch holds str(i) char
        // vs
        // case ZERO ... // str(i) is ZERO char
        import scala.math.Pi
        0.5 * i match {
            case Pi => ??? // 0.5 * i equals Pi?
            case x => ??? // set x to 0.5 * i
        }
        // constants starts with UPPERCASE letter,
        // variable must start with a LOWERCASE letter
        // or, if you need to check equality with lowerCaseConst: use back-ticks (symbol notation)
        import java.io.File.pathSeparator
        str match {
            case `pathSeparator` => ???
        }
    }

    // type patterns
    def typePatterns = {
        //match on the type, preferred form (vs isInstanceOf)
        val obj: Any = ???
        obj match {
            case x: Int => x
            case s: String => Integer.parseInt(s)
            case _: BigInt => Int.MaxValue
            // case Double => ??? // matching against a type you must supply a variable name
        }
        // matches occur at runtime: generic types are erased
        // e.g. you can't match for a specific Map type
        obj match {
            // case m: Map[String, Int] => ??? // don't: can't match for a specific Map type
            case mm: Map[_, _] => ??? // OK for generic map
            // but, arrays type are not erased (jvm feature)
            case a: Array[Int] => ??? // OK
        }
    }

    // matching arrays, lists, tuples
    def matchingArraysListsTuples = {
        // extractors in pattern matching: match against content
        val arr: Array[Int] = ???
        arr match {
            case Array(0) => "one 0"
            case Array(x, y) => s"two: $x, $y" // binds x, y
            case Array(0, _*) => "starts with 0"
            // bind a variable to tail of array
            case Array(x, rest @ _*) => s"starts with $x and rest is: ${rest.mkString}"
        }

        // lists can be matched in the same way
        // or, better, with '::' operator
        val lst: List[Int] = ???
        lst match {
            case 0 :: Nil => ???
            case x :: y :: Nil => ???
            case 0 :: tail => ???
        }

        // tuples notation for tuples
        val pair: (Int, Int) = (???, ???)
        pair match {
            case (0, _) => ???
            case (y, 0) => ???
            case _ => "neither is 0"
        }
        // n.b. variables are bound to parts of the expression
        // called 'destructuring'

        // n.b. you can't use named variables with alternatives
        pair match {
            case (_, 0) | (0, _) => ??? // OK
            // case (x, 0) | (0, x) => ??? // error
        }
    }

    // extractors
    def extractors = {
        // destructuring done using extractors: unapply, unapplySeq
        val arr: Array[Int] = ???
        arr match {
            case Array(0) => "one 0"
            case Array(x, y) => s"two: $x, $y"
            case Array(0, _*) => "starts with 0"
            case Array(x, rest @ _*) => s"starts with $x and rest is: ${rest.mkString}"
        }
        // Array companion object is an extractor: Array.unapplySeq(arr)

        // regular expressions provide another good use of extractors
        // match groups with pattern
        val re = "([0-9]+) ([a-z]+)".r
        "99 bottles" match {
            case re(num, item) => ??? // num=99, item=bottles
        }
        // re.unapplySeq("99 bottles") // extractor is a Regex instance
    }

    // patterns in variable declarations
    def patternsInVariableDeclarations = {
        val (x, y) = (1, 2)
        // useful for functions that return a pair
        val (q, r) = BigInt(10) /% 3

        // works for any patters with variable names
        val arr: Array[Int] = ???
        val Array(first, second, rest @ _*) = arr

        // it works like this:
        // val p(x1, ..., xn) = e
        // is the same as
        // val $result = e match { case p(x1, ...) => (x1, ...)
        // val x1 = $result._1; ...

        // this definition holds even w/o free variables
        val 2 = x
        // x match { case 2 => () }
        // effectively: if (2 != x) throw new MatchError
    }

    // patterns in for-expressions
    def patternsInForExpressions = {
        import scala.collection.JavaConverters.propertiesAsScalaMapConverter

        // for each iteration, the variables are bound

        // traverse a map
        for ((k, v) <- System.getProperties.asScala) println(s"$k -> $v")

        // in a 'for' expression match failures are silently ignored
        for ((k, "") <- System.getProperties.asScala) println(s"no value for $k")
        // skip all non-empty

        // or, you can use a guard
        for ((k, v) <- System.getProperties.asScala if v == "") println(s"no value for $k")
    }

    // case classes
    def caseClasses = {
        // special kind of classes, optimized for pattern matching:
        // constructor parameters become a 'val'
        // methods generated: toString, equals, hashCode, copy
        // companion object constructed with 'apply', 'unapply'

        sealed abstract class Amount // sealed: hinted compiler about exhaustiveness match partial function
        case class Dollar(value: Double) extends Amount
        case class Currency(value: Double, unit: String) extends Amount
        // use case objects for singletons
        case object Nothing extends Amount

        val amt: Amount = ???
        amt match {
            case Dollar(v) => ???
            case Currency(_, u) => ???
            case Nothing => ??? // no parenthesis for object
        }
    }

    // the copy method and named parameters
    def theCopyMethodAndNamedParameters = {
        // 'copy' makes a new object with the same values, allowing to redefine only selected values

        sealed abstract class Amount
        case class Dollar(value: Double) extends Amount
        case class Currency(value: Double, unit: String) extends Amount
        case object Nothing extends Amount

        val amt = Currency(29.95, "EUR")
        val price = amt.copy(unit = "CHF") // use named parameters to modify properties
    }

    // infix notations in case clauses
    def infixNotationsInCaseClauses = {
        // if 'unapply' yields a pair, you can use infix notation in pattern
        // usable for DSL construction

        sealed abstract class Amount
        case class Dollar(value: Double) extends Amount
        case class Currency(value: Double, unit: String) extends Amount
        case object Nothing extends Amount

        val amt: Amount = ???
        amt match {
            case amount Currency "EUR" => ??? // same as: case Currency(amount, "EUR")
        }

        // the feature is ment for matching sequences: case class ::(head, tail) extends List
        val lst: List[Int] = ???
        lst match { case h :: t => ??? } // same as: case ::(h, t) // calls ::.unapply(lst)

        // later you will encounter case class ~ // for parser combinators
        // res match { case p ~ q => ??? } // same as: case ~(p, q)

        // easier to read when more than one
        // res match { case p ~ q ~ r => ??? } // vs: case ~(~(p, q), r)

        // colon ':' on the end means right-to-left associativity
        // case a :: b :: c // means case ::(a, ::(b, c))

        // example : unapply return a pair
        case object +: {
            def unapply[T](arg: List[T]) = if (arg.isEmpty) None else Some( (arg.head, arg.tail) )
        }

        1 +: 7 +: Nil match {
            case a +: b +: rest => ???
        }

    }

    // matching nested structures
    def matchingNestedStructures = {
        // with case classes it's easy: match nested structures
        // example: bundle of items
        sealed abstract class Item
        case class Article(descr: String, price: Double) extends Item
        case class Bundle(descr: String, discount: Double, items: Item*) extends Item

        val itm: Item = ???
        itm match {
            case Bundle(_, _, Article(descr, _), _*) => ??? // binds descr to the description of the first article
            case Bundle(_, _, art @ Article(_, _), rest @ _*) => ??? // binds first article and the rest of the bundle
        }

        // app: compute price
        def price(itm: Item): Double = itm match {
            case Article(_, p) => p
            case Bundle(_, disc, items @ _*) => items.map(price).sum - disc
        }
    }

    // are case classes evil?
    def areCaseClassesEvil = {
        // compute price example: not good from OO poin of view.
        // 'price' should be a method of the sup. and be redefined in bundle

        // if you don't have to add another operations to class hierarchy, it's true.

        // see 'expression problem'
        // if you add classes and have a fixed set of operators: use polymorphism
        // if you add operators and have a fixed set of classes: use pattern matching

        // case classes and pattern matching is good for fixed set of classes: sealed sup
        // example: List
        // abstract class List
        // case object Nil extends List
        // case class ::(head, tail) extends List

        // case classes are quite convenient:
        // more concise code
        // easier to read
        // toString, equals, hashCode, copy for free

        // some people call them 'value classes', which is wrong: value class creates no objects on instantiation

        // n.b. if, god forbid, you have 'var' in case class, derive hashCode from immutable fields only;

        // don't extend case class from case class: toString, equals, hashCode, copy will not be generated,
        // only leaves of a tree should be case classes

    }

    // sealed classes
    def sealedClasses = {
        // compiler could check that you exhausted all alternatives in match expression
        // to make it possible, declare sup as 'sealed'

        sealed trait Amount
        case class Dollar(value: Double) extends Amount
        case class Currency(value: Double, unit: String) extends Amount

        // all subclasses of a sealed must be defined in the same file
    }

    // simulating enumerations
    def simulatingEnumerations = {
        // you may prefer Enumeration class

        // example
        sealed trait TrafficLightColor
        case object Red extends TrafficLightColor
        case object Yellow extends TrafficLightColor
        case object Green extends TrafficLightColor

        val color: TrafficLightColor = ???
        color match {
            case Red => ???
            case _ => ???
        }
    }

    // the option type
    def theOptionType = {
        // monadic Option type with
        // case class None
        // case class Some

        // don't use "" or null, use Option
        // e.g. with maps
        val score = Map("A" -> 3).get("B")
        score match {
            case Some(sc) => ???
            case None => ???
        }
        // or with map.getOrElse

        // option may be considered as a collection
        for (sc <- score) println(s"$sc") // print only if have some value
        // can use map, filter, flatMap, foreach, ...
        score.map(_ + 1)
        score.filter(_ > 5)
        score.foreach(println)
    }

    // partial functions
    def partialFunctions = {
        // a set of case clauses enclosed in braces: partial function
        // may not be defined on all inputs

        // class PartialFunction[A, B]
        // two methods: apply, isDefinedAt
        val f: PartialFunction[Char, Int] = { case '+' => 1; case '-' => -1 }

        f('-') == f.apply('-')
        f.isDefinedAt(' ') // false
        f('0') // MatchError

        // method 'collect' of the traversable accept a partial function
        "1 - 3 + 4" collect { case '+' => 1; case '-' => -1 }

        // an exhaustive set of cases define a Function1, not a PartialFunction
        "1 - 3 + 4" map { case '+' => 1; case '-' => -1; case _ => 0 }

        // Seq is a partial function idx => T
        // Map is a partial function k => v
        // you can pass a map to 'collect'
        " " collect Map(' ' -> 42)

        // 'lift' method
        // turns a PartialFunction[T, R] into Function1[T, Option[R]]
        val g = f.lift
        g('0') == None
        g('+') == Some(1)

        // example: use map in Regex.replaceSomeIn
        import scala.util.matching.Regex
        val msg = "At {1}, there was {2} on {3}"
        val map = Map("{1}" -> "planet 7", "{2}" -> "12:30 pm", "{3}" -> "a disturbance of the force")
        val pattern = """\{([0-9]+)\}""".r // {number}
        // def mf(map: Map[String, String])(rm: Regex.Match): Option[String] = map.lift(rm.matched)
        // val res = pattern.replaceSomeIn(msg, mf(map))
        val res = pattern.replaceSomeIn(msg, m => map.lift(m.matched))
    }

}

object PatternMatchingAndCaseClasses_Exercises {

    // 1. Your Java Development Kit distribution has the source code for much of the JDK in the src.zip file.
    // Unzip and search for case labels (regular expression case [^:]+:).
    // Then look for comments starting with // and containing [Ff]alls? thr
    // to catch comments such as // Falls through
    // or // just fall thru
    // Assuming the JDK programmers follow the Java code convention, which requires such a comment,
    // what percentage of cases falls through?
    def ex1(startFrom: String = "/tmp") = {
        import scala.util.matching.Regex

        // case class CaseLabels(count: Int = 0) extends AnyVal
        // case class FallsThrough(count: Int = 0) extends AnyVal
        // value class may not be a local class
        case class CaseLabels(count: Int = 0)
        case class FallsThrough(count: Int = 0)

        case class Counts(labels: CaseLabels = CaseLabels(), falls: FallsThrough = FallsThrough()) {
            def add(other: Counts): Counts = Counts(
                CaseLabels(other.labels.count + labels.count),
                FallsThrough(other.falls.count + falls.count)
            )
        }

        def exist(re: Regex, line: String): Boolean = (re findFirstIn line).fold(false)(_ => true)

        def lineProcessing(labels: Regex, falls: Regex, line: String): Counts = Counts(
            CaseLabels( if (exist(labels, line)) 1 else 0 ),
            FallsThrough( if (exist(falls, line)) 1 else 0 )
        )

        def textProcessing(lines: Iterator[String]): Counts = {
            val caseLabelsRe = """case [^:]+:""".r
            val fallsThroughRe = """ //.*[Ff]alls? thr""".r

            val cntlist = for {
                line <- lines
            } yield lineProcessing(caseLabelsRe, fallsThroughRe, line)

            (Counts() /: cntlist)(_ add _)
        }

        def fileLines(path: String): Iterator[String] = {
            scala.io.Source.fromFile(path).getLines
        }

        def files(root: String = "/tmp"): Iterable[String] = {
            import java.nio.{file => jnf}
            import java.io.{File, IOException}
            import rx.lang.scala.Observable
            import scala.language.implicitConversions

            def listFiles(dir: jnf.Path) = Observable[jnf.Path](subscriber => {

                val visitor = new jnf.SimpleFileVisitor[jnf.Path] {
                    override def visitFile(file: jnf.Path, attrs: jnf.attribute.BasicFileAttributes) = {
                        if (subscriber.isUnsubscribed) jnf.FileVisitResult.TERMINATE
                        else {
                            subscriber.onNext(file)
                            jnf.FileVisitResult.CONTINUE
                        }
                    }
                    override def visitFileFailed(file: jnf.Path, exc: IOException) = {
                        println(s"visitFileFailed: $exc")
                        //subscriber.onError(exc) // exactly once
                        jnf.FileVisitResult.CONTINUE
                    }
                    override def postVisitDirectory(dir: jnf.Path, exc: IOException) = {
                        if (exc != null) println(s"postVisitDirectory: $exc")
                        jnf.FileVisitResult.CONTINUE
                    }
                }

                jnf.Files.walkFileTree(dir, visitor)
                subscriber.onCompleted()
            }) // .onErrorResumeNext(_ => Observable.empty)

            val startDir = new File(root).toPath
            val files = listFiles(startDir)
            val srcfiles = files.filter(p => p.toString.endsWith(".java"))
            files.length.subscribe(c => println(s"total files check: $c"))
            srcfiles.length.subscribe(c => println(s"java files check: $c"))
            // debug
            srcfiles.subscribe(p => println(p))

            // result: escape async world // not really good decision
            srcfiles.map(p => p.toAbsolutePath.toString).toBlocking.toIterable
        }

        // TODO: all pipeline should be reactive (try Akka Streams)
        // add unit tests for text processing stages

        // do grep and count
        val res = files(startFrom)
            .map(path => fileLines(path))
            .map(lines => textProcessing(lines))
            .fold(Counts())(_ add _)

        // scala> f"'$res16%8.3f'" // float width.precision example
        // res26: String = ' 101.000'
        println(f"counts: $res; falls thru ${100d * res.falls.count / (1 + res.labels.count)}%4.2f%%")
        // java files check: 7711
        // counts: Counts(CaseLabels(10099),FallsThrough(102)); falls thru 1.01
    }


    // 2. Using pattern matching, write a function 'swap' that receives a pair of integers and returns the
    // pair with the components swapped.
    def ex2 = {
        def swap(pair: (Int, Int)) = pair match { case (a, b) => (b, a) }

        // test
        assert((1, 2) == swap((2, 1)))
    }

// 3. Using pattern matching, write a function 'swap' that swaps the first two elements of an array
// provided its length is at least two.
    def ex3 = {
        ???
    }

// 4. Add a case class 'Multiple' that is a subclass of the 'Item' class.
// For example,
// Multiple(10, Article("Blackwell Toaster", 29.95))
// describes ten toasters. Of course, you should be able to handle any items,
// such as bundles or multiples, in the second argument.
// Extend the price function to handle this new case.
    def ex4 = {
        ???
    }

// 5. One can use lists to model trees that store values only in the leaves.
// For example, the list ((3 8) 2 (5)) describes the tree
//          •
//        / |  \
//       •  2   •
//      /\      |
//     3 8      5
// However, some of the list elements are numbers and others are lists.
// In Scala, you cannot have heterogeneous lists, so you have to use a List[Any].
// Write a leafSum function to compute the sum of all elements in the leaves,
// using pattern matching to differentiate between numbers and lists.
    def ex5 = {
        ???
    }

// 6. A better way of modeling such trees is with case classes.
// Let’s start with binary trees.
//  sealed abstract class BinaryTree
//  case class Leaf(value: Int) extends BinaryTree
//  case class Node(left: BinaryTree, right: BinaryTree) extends BinaryTree
// Write a function to compute the sum of all elements in the leaves.
    def ex6 = {
        ???
    }

// 7. Extend the tree in the preceding exercise so that
// each node can have an arbitrary number of children, and reimplement the leafSum function.
// The tree in Exercise 5 should be expressible as
//  Node(Node(Leaf(3), Leaf(8)), Leaf(2), Node(Leaf(5)))
    def ex7 = {
        ???
    }

// 8. Extend the tree in the preceding exercise so that each nonleaf node stores an operator in
// addition to the child nodes.
// Then write a function 'eval' that computes the value.
// For example, the tree
//          +
//        / |  \
//       *  2   -
//      /\      |
//     3 8      5
// has value (3 × 8) + 2 + (–5) = 21
// Pay attention to the unary minus
    def ex8 = {
        ???
    }

// 9. Write a function that computes the sum of the non-None values in a List[Option[Int]]
// Don’t use a match statement.
    def ex9 = {
        ???
    }

// 10. Write a function that composes two functions of type Double => Option[Double]
// yielding another function of the same type.
// The composition should yield None if either function does.
// For example,
//  def f(x: Double) = if (x != 1) Some(1 / (x - 1)) else None
//  def g(x: Double) = if (x >= 0) Some(sqrt(x)) else None
//  val h = compose(g, f) // h(x) should be g(f(x))
// Then h(2) is Some(1), and h(1) and h(0) are None.
    def ex10 = {
        ???
    }

}
