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
        ???
    }

    // guards
    def guards = {
        ???
    }

    // variables in patterns
    def variablesInPatterns = {
        ???
    }

    // type patterns
    def typePatterns = {
        ???
    }

    // matching arrays, lists, tuples
    def matchingArraysListsTuples = {
        ???
    }

    // extractors
    def extractors = {
        ???
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

//1. Your Java Development Kit distribution has the source code for much of the JDK in the
//src.zip file. Unzip and search for case labels (regular expression case [^:]+:). Then
//look for comments starting with // and containing [Ff]alls? thr to catch comments such
//as // Falls through or // just fall thru. Assuming the JDK programmers
//follow the Java code convention, which requires such a comment, what percentage of cases
//falls through?
    def ex1 = {
        ???
    }

//2. Using pattern matching, write a function swap that receives a pair of integers and returns the
//pair with the components swapped.
    def ex2 = {
        ???
    }

//3. Using pattern matching, write a function swap that swaps the first two elements of an array
//provided its length is at least two.
    def ex3 = {
        ???
    }

//4. Add a case class Multiple that is a subclass of the Item class. For example,
//Multiple(10, Article("Blackwell Toaster", 29.95)) describes ten
//toasters. Of course, you should be able to handle any items, such as bundles or multiples, in the
//second argument. Extend the price function to handle this new case.
    def ex4 = {
        ???
    }

//5. One can use lists to model trees that store values only in the leaves. For example, the list ((3
//8) 2 (5)) describes the tree
//Click here to view code image
//•
///|\
//• 2 •
/// \
// |
//3
// 8
// 5
//However, some of the list elements are numbers and others are lists. In Scala, you cannot have
//heterogeneous lists, so you have to use a List[Any]. Write a leafSum function to compute
//the sum of all elements in the leaves, using pattern matching to differentiate between numbers
//and lists.
    def ex5 = {
        ???
    }

//6. A better way of modeling such trees is with case classes. Let’s start with binary trees.
//Click here to view code image
//sealed abstract class BinaryTree
//case class Leaf(value: Int) extends BinaryTree
//case class Node(left: BinaryTree, right: BinaryTree) extends BinaryTree
//Write a function to compute the sum of all elements in the leaves.
    def ex6 = {
        ???
    }

//7. Extend the tree in the preceding exercise so that each node can have an arbitrary number of
//children, and reimplement the leafSum function. The tree in Exercise 5 should be expressible
//as
//Click here to view code image
//Node(Node(Leaf(3), Leaf(8)), Leaf(2), Node(Leaf(5)))
    def ex7 = {
        ???
    }

//8. Extend the tree in the preceding exercise so that each nonleaf node stores an operator in
//addition to the child nodes. Then write a function eval that computes the value. For example,
//the tree
//Click here to view code image
//+
///|\
//* 2 -
/// \
// |
//3
// 8
// 5
//has value (3 × 8) + 2 + (–5) = 21.
//Pay attention to the unary minus.
    def ex8 = {
        ???
    }

//9. Write a function that computes the sum of the non-None values in a List[Option[Int]].
//Don’t use a match statement.
    def ex9 = {
        ???
    }

//10. Write a function that composes two functions of type Double => Option[Double],
//yielding another function of the same type. The composition should yield None if either
//function does. For example,
//Click here to view code image
//def f(x: Double) = if (x != 1) Some(1 / (x - 1)) else None
//def g(x: Double) = if (x >= 0) Some(sqrt(x)) else None
//val h = compose(g, f) // h(x) should be g(f(x))
//Then h(2) is Some(1), and h(1) and h(0) are None.
    def ex10 = {
        ???
    }

}
