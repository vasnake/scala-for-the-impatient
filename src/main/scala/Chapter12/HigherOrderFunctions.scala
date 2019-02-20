package Chapter12

import scala.annotation.tailrec

object HigherOrderFunctions {
// topics:
    // functions as values
    // anonymous functions
    // functions with function parameters
    // parameter inference
    // useful higher-order functions
    // closures
    // SAM conversions
    // currying
    // control abstractions
    // the return expression

    // functions: first-class citizens;
    // function argument: behaviour that should be executed later;

    // functions as values
    def functionsAsValues = {
        // first-class citizens: store in a variable, pass it, call it
        import scala.math._

        val num = Pi // // num: Double = 3.141592653589793
        val fun = ceil _ // [eta-expansion](https://alvinalexander.com/scala/fp-book/how-to-use-scala-methods-like-functions)
        //fun: Double => Double = $$Lambda$5477/1677510064@42029f3d

        // def / val difference:
        // def ceil: method (class method definition)
        // val fun:  function (instance of Function?? class)
        // you cannot manipulate methods, only functions (n.b. with arrows)

        // '_' is not necessary when you have a context where a function is expected
        // compiler is smart enough
        // e.g. : '(Double) => Double' : function type / variable type
        val fun2: (Double) => Double = ceil
        // fun2: Double => Double = $$Lambda$5484/1902760865@3232656

        // n.b. syntax is different for class method where 'this' in play:
        // ceil: method from math package object // def ceil(x: Double): Double  = java.lang.Math.ceil(x)
        // if you have a method from a class, say 'string.charAt(pos)'
        val fun3 = (_: String).charAt(_: Int)
        // fun3: (String, Int) => Char = $$Lambda$5485/1616964849@7b7632f1
        val fun4: (String, Int) => Char = _ charAt _
        // fun4: (String, Int) => Char = $$Lambda$5486/1599488792@64da3fc9

        // apply
        val res = fun(num) // res: Double = 4.0

        // pass to another func.
        val res2 = Seq(num, res, 2.0) map fun // res2: Seq[Double] = List(4.0, 4.0, 2.0)
    }

    // anonymous functions
    def anonymousFunctions = {
        // you don't have to name the function

        // anon func:
        // (x: Double) => 3 * x

        // can save to a val
        val triple = (x: Double) => 3 * x // function type: triple: Double => Double = $$Lambda$5498/277464955@6c3c445c
        // n.b. val vs def
        // def triple(x: Double) = 3 * x // is a method: triple: (x: Double)Double

        // or, you can just pass it
        val res = Seq(1.2, 2.3, 3.4).map(
            (x: Double) => 3 * x
        )
        // enclosed in braces instead of parentheses
        val res2 = Seq(1.2, 2.3, 3.4) map { (x: Double) => 3 * x }

    }

    // functions with function parameters
    def functionsWithFunctionParameters = {
        // func that takes another func as a parameter,
        // produce func as a result:
        // higher-order functions

        import scala.math._

        // method, not function
        def valueAtOneQuarter(f: (Double) => Double) = f(0.25)
        // valueAtOneQuarter: (f: Double => Double)Double
        // valueAtOneQuarter type:  (paramType) => resultType
        //                          ((Double) => Double) => Double

        // take a function and produce a value:
        val res1 = valueAtOneQuarter(ceil _) // res1: Double = 1.0
        val res2 = valueAtOneQuarter(sqrt _) // res2: Double = 0.5

        // higher-order function: func that receives a function
        val vaoq = valueAtOneQuarter(_)
        // vaoq: (Double => Double) => Double = $$Lambda$5507/1737478246@5679fc19

        // and produce a value
        val res3 = vaoq(ceil)
        val res4 = vaoq(sqrt)

        // higher-order func can also produce functions
        def mulBy(factor: Double) = (x: Double) => factor * x
        // mulBy method: (factor: Double)Double => Double
        val mulByFun = mulBy(_)
        // mulByFun function: (Double) => ((Double) => Double) = $$Lambda$5514/1875171051@d2090af

        // produce a functions:
        val mulBy3 = mulBy(3) // mulBy3: Double => Double = $$Lambda$5513/1949082721@64c281af
        val quintuple = mulBy(5)

        // produce a value:
        val res5 = quintuple(20) // 100
    }

    // parameter inference
    def parameterInference = {
        // scala helps by deducing types when possible

        // having
        def valueAtOneQuarter(f: (Double) => Double): Double = f(0.25)

        // you don't have to write
        val res1 = valueAtOneQuarter((x: Double) => 3 * x) // 0.75
        // compiler knows that x: Double, you can just write
        val res2 = valueAtOneQuarter((x) => 3 * x)
        // if you have just one parameter, you can omit the '()'
        val res3 = valueAtOneQuarter(x => 3 * x)
        // if a parameter occurs only once, you can replace it with '_' and drop left side
        val res4 = valueAtOneQuarter(3 * _)

        // these shortcuts only work when the parameter types are known
        // val fun = 3 * _ // ERR
        val fun = 3 * (_: Double) // OK
        val fun2: (Double) => Double = 3 * _ // OK

        // specifying the type of '_' is useful for turning methods into functions
        val len = (_: String).length // String => Int
        val subs = (_: String).substring(_: Int, _: Int) // (String, Int, Int) => String
    }

    // useful higher-order functions
    def usefulHigherOrderFunctions = {
        // practice with Scala collections library

        // general principle: if you want a sequence, try to transform it from a simple one
        val res1 = (1 to 9).map(0.1 * _)

        // triangle
        (1 to 9).map("*" * _).foreach(println _)

        // even numbers
        val res2 = (1 to 9).filter(_ % 2 == 0)

        // anonymous binary function
        val res3 = (1 to 9).reduceLeft(_ * _)

        // another binary func
        val res4 = "Mary had a little lamb".split(" ")
            .sortWith(_.length < _.length)
    }

    // closures
    def closures = {
        // you can define a function inside any scope;
        // you can access any variable from outer scope;
        // it's a task for compiler, to build a closure to make this possible

        // produces a Double => Double functions, bounding a factor
        def mulBy(factor: Double) = (x: Double) => factor * x

        // create two function objects, with captured factor: closure
        // code with the definition of any nonlocal variables used in that code
        val triple = mulBy(3)
        val half = mulBy(0.5)
        println(s"${triple(14)} ${half(14)}") // 42 7

        // java 8 has closures in the form of lambda expressions
    }

    // SAM conversions
    def SAM_conversions = {
        // as of Scala 2.12 (Java 8) one can pass scala functions to java code,
        // if java code expecting a 'SAM interface' -- Single Abstract Method,
        // 'functional interfaces'

        // prior to java 8, passing a function as a parameter was not possible,
        // you had to define an object

        import java.awt.event.{ActionEvent, ActionListener}
        import javax.swing.JButton
        val button = new JButton("Increment")

        button.addActionListener( new ActionListener {
            override def actionPerformed(e: ActionEvent): Unit = println(e.getActionCommand)
        })

        // in java 8 it's possible (pass scala function to java code), with lambda expressions
        button.addActionListener(event => println(event.getActionCommand))

        // conversions from a scala function to a java SAM interface only works for
        // function literals

        val listener = (event: ActionEvent) => println(event.getActionCommand)
        // button.addActionListener(listener) // nonliteral function, can't convert to java SAM interface

        // simple remedy: explicit conversion
        val listener2: ActionListener = (event: ActionEvent) => println(event.getActionCommand)
        button.addActionListener(listener2)

        // or, turn a function variable into a literal
        button.addActionListener(listener(_))
    }

    // currying
    def currying = {
        // Haskell Brooks Curry
        // the process of turning a function with two arguments into a function with one argument;
        // that new function returns a function that consumes the second argument

        // create function with two arguments
        val mul =
            (x: Int, y: Int) =>
                x * y

        // function with one argument, returns function for second argument
        val mul2 =
            (x: Int) =>
                ((y: Int) => x * y)

        // to perform multiplication, call
        val res1 = mul2(6)(7) // 42
        // return ((y: Int) => 6 * y)
        // return 6 * 7

        // using 'def' you can use a shortcut for defining curried methods
        def mul3(x: Int)(y: Int) = x * y
        // compare to defining a function
        val mul4 = (x: Int) => ((y: Int) => x * y)

        // currying can be very helpful for type inference (more information for second parameter)
        // e.g.
        val a = Array("Hello", "World")
        val b = Array("hello", "world")
        a.corresponds(b)(_ equalsIgnoreCase _)
        // n.b. equalsIgnoreCase is passed as a curried parameter;
        // after processing 'corresponds' compiler knows that String values will be passed to predicate function
        // (String, String) => Boolean
        // and can accept 'a.equalsIgnoreCase(b)'
    }

    // control abstractions
    def controlAbstractions = {
        // call-by-name:
        // you can model a sequence of statements as a function
        // Unit => Unit

        // e.g. run block of code in a thread, no syntactic sugar
        def runInThread(
                           block: () => Unit // block is a function parameter
                       ): Unit = {
            val thread = new Thread {
                override def run(): Unit = {
                    block() // call given function
            } }
            thread.start()
        }
        // call it, () => expression
        runInThread(
            () => { println("Hi"); Thread.sleep(10000); println("Bye") }
        )
        // not really nice, is it?

        // to avoid '() => expr' use the 'call by name' notation:
        // omit '()'
        def runInThread2(block: => Unit): Unit = { // block still is a function parameter: () => Unit
            new Thread {
                override def run(): Unit =
                    block // call given function
            }.start()
        }
        // call it, call-by-name notation, function parameter as a sequence of statements
        runInThread2(
            { println("Hi"); Thread.sleep(10000); println("Bye") }
        )

        // control abstractions: functions that look like language keywords

        // e.g. until: two call-by-name parameters converted to two functions
        @tailrec def until(condition: => Boolean)(block: => Unit): Unit = {
            if (!condition) {
                block; // do stuff
                until(condition)(block) // repeat
            }
        }
        // use it:
        var x = 10
        until (x == 0) {
            x -= 1; println(x)
        }
    }

    @tailrec def until(condition: => Boolean)(block: => Unit): Unit = {
        if (!condition) { block; until(condition)(block) }
    }

    // the return expression
    def theReturnExpression = {
        // return value of a function is the value of the function body (last expression)
        // 'return' implemented as an exception

        // but using control abstractions it can be useful
        // e.g.
        def indexOf(str: String, ch: Char): Int = {
            var i = 0
            until (i == str.length) {
                if (str(i) == ch) return i
                i += 1
            }
            return -1
        }
        // return terminates 'indexOf' and it's good enough, if you don't catch exception earlier
    }

}

object HigherOrderFunctions_Exercises {

    // 1. Write a function
    //      values(fun: (Int) => Int, low: Int, high: Int)
    // that yields a collection of function inputs and outputs in a given range.
    // For example,
    // values(x => x * x, -5, 5) should produce a collection of pairs
    // (-5, 25), (-4, 16), (-3, 9), . . . , (5, 25).
    def ex1 = {
        def values(fun: (Int) => Int, low: Int, high: Int): Seq[(Int, Int)] = {
            (low to high).map((inp) => (inp, fun(inp)))
        }

        // test
        val res = values(x => x*x, -5, 5)
        println(res)
        assert(res == Seq(
            (-5, 25), (-4, 16), (-3, 9), (-2, 4), (-1, 1), (0, 0),
            (1, 1), (2, 4), (3, 9), (4, 16), (5, 25)
        ))
    }

    // 2. How do you get the largest element of an array with reduceLeft?
    def ex2 = {
        def largest(seq: Seq[Int]): Int =
            seq reduceLeft { (a, b) => if (a > b) a else b }

        // test
        val res = largest( Seq(1,2,3,4,5,6,5,4,3,2,1) )
        println(res)
        assert(res == 6)
    }

    // 3. Implement the factorial function using 'to' and 'reduceLeft', without a loop or recursion
    def ex3 = {
        def factorial(x: Int): Int = {
            require(x >= 0, "factorial of negative is unknown to me")
            if (x > 0) (1 to x).reduceLeft(_ * _)
            else 1
        }

        // test
        assert(factorial(0) == 1)
        assert(factorial(1) == 1)
        assert(factorial(2) == 2)
        assert(factorial(3) == 6)
    }

    // 4. The previous implementation needed a special case when n < 1.
    // Show how you can avoid this with foldLeft.
    // (Look at the Scaladoc for foldLeft. It’s like reduceLeft, except that
    // the first value in the chain of combined values is supplied in the call.)
    def ex4 = {
        def factorial(x: Int): Int = {
            require(x >= 0, "factorial of negative is unknown to me")
            // (1 /: (1 to x))(_ * _)
            (1 to x foldLeft 1)(_ * _)
        }

        // test
        val data = Seq((0, 1), (1, 1), (2, 2), (3, 6))
        data foreach { case (x, res) => assert(factorial(x) == res) }
    }

    // 5. Write a function
    // largest(fun: (Int) => Int, inputs: Seq[Int])
    // that yields the largest value of a function within a given sequence of inputs. For example,
    // largest(x => 10 * x - x * x, 1 to 10) should return 25.
    // Don’t use a loop or recursion.
    def ex5 = {
        def largest(fun: (Int) => Int, inputs: Seq[Int]): Int =
            inputs.map(fun).reduce((a, b) => if (a < b) b else a)

        // test
        assert(largest(x => 10*x - x*x, 1 to 10) == 25)
    }

    // 6. Modify the previous function to return the input at which the output is largest. For example,
    // largestAt(x => 10 * x - x * x, 1 to 10) should return 5. Don’t use a loop or recursion.
    def ex6 = {
        ???
    }

    // 7. It’s easy to get a sequence of pairs, for example:
    //      val pairs = (1 to 10) zip (11 to 20)
    // Now, suppose you want to do something with such a sequence—say, add up the values. But you can’t do
    //      pairs.map(_ + _)
    // The function _ + _ takes two Int parameters, not an (Int, Int) pair. Write a function
    // adjustToPair that receives a function of type (Int, Int) => Int and returns the
    // equivalent function that operates on a pair. For example, adjustToPair(_ * _)((6, 7)) is 42.
    // Then use this function in conjunction with map to compute the sums of the elements in pairs.
    def ex7 = {
        ???
    }

    // 8. In Section 12.8, “Currying,” on page 164, you saw the corresponds method used with two
    // arrays of strings. Make a call to corresponds that checks whether the elements in an array
    // of strings have the lengths given in an array of integers.
    def ex8 = {
        ???
    }

    // 9. Implement corresponds without currying. Then try the call from the preceding exercise.
    // What problem do you encounter?
    def ex9 = {
        ???
    }

    // 10. Implement an unless control abstraction that works just like if, but with an inverted
    // condition. Does the first parameter need to be a call-by-name parameter? Do you need currying?
    def ex10 = {
        ???
    }
}
