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
        val pair = (???, ???)
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
        ???
    }

    // patterns in for-expressions
    def patternsInForExpressions = {
        ???
    }

    // case classes
    def caseClasses = {
        ???
    }

    // the copy method and named parameters
    def theCopyMethodAndNamedParameters = {
        ???
    }

    // infix notations in case clauses
    def infixNotationsInCaseClauses = {
        ???
    }

    // matching nested structures
    def matchingNestedStructures = {
        ???
    }

    // are case classes evil?
    def areCaseClassesEvil = {
        ???
    }

    // sealed classes
    def sealedClasses = {
        ???
    }

    // simulating enumerations
    def simulatingEnumerations = {
        ???
    }

    // the option type
    def theOptionType = {
        ???
    }

    // partial functions
    def partialFunctions = {
        ???
    }

}

object PatternMatchingAndCaseClasses_Exercises {

// 1. Your Java Development Kit distribution has the source code for much of the JDK in the
// src.zip file.
// Unzip and search for case labels (regular expression case [^:]+:).
// Then look for comments starting with // and containing [Ff]alls? thr
// to catch comments such as // Falls through
// or // just fall thru
// Assuming the JDK programmers follow the Java code convention, which requires such a comment,
// what percentage of cases falls through?
    def ex1 = {
        ???
    }

// 2. Using pattern matching, write a function 'swap' that receives a pair of integers and returns the
// pair with the components swapped.
    def ex2 = {
        ???
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
