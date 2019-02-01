package Chapter02

object ControlStructuresAndFunctions {
// topics:
    // conditional expressions: if ... else have a value
    // statement termination: no need for ';'
    // block expressions and assignments: val x = { ... } last value in block
    // input and output: print, println, readLine; string interpolation s"", f"", raw""
    // loops: while, for(n <- gen)
    // advanced 'for' loops: multiple generators, guards, assignments; for comprehension for-yield
    // functions: def x(args) = {}
    // default and named arguments
    // variable arguments: seq :_*
    // procedures: x => Unit
    // lazy values: compute once, check every time, threadsafe
    // exceptions: try {} catch { case ex: SomeEx } finally {}

// expressions: almost all constructs have values

    // conditional expressions: if ... else have a value
    def conditionalExpressions(x: Int) = {
        val s = if (x > 0) 1 else -1 // s: Int
        val mixed = if (x > 0) "positive" else -1 // mixed: Any

        val mixedNoValue = if (x > 0) 1 // mixed: AnyVal = 1 ot Unit
        assert(mixedNoValue == ( if (x > 0) 1 else () )) // () is Unit value
    }

    // statement termination: no need for ';' before EOL or '}' or 'else', etc
    def statementTermination() = {
        var n, r = 0

        if (n > 0) { r = r * n; n -= 1}

        // two line expression
        n * r +
            math.Pi * r
    }

    // block expressions and assignments: val x = { ... } last value in block
    def blockExpressions(x0: Int, x1: Int, y0: Int, y1: Int) = {
        val distance = {
            val dx = x1 - x0
            val dy = y1 - y0
            math.sqrt(dx*dx + dy*dy) // distance
        }

        // assignment value == Unit i.e. no value
        var x = 0
        val y = x = 1
        assert(y.equals( () ) && x == 1)
        assert(y.isInstanceOf[Unit])
    }

    // input and output: print, println, readLine; string interpolation s"", f"", raw""
    def inputAndOutput(name: Int, age: Int) = {
        print("Answer: ")
        println(42)
        // the same as
        println("Answer: " + 42)

        printf("Hello, %s! You are %d years old\n", name, age)

        // string interpolation, type safe
        print(f"Hello $name! In six month, you'll be ${age + 0.5}%7.2f years old\n")
        println(s"age $age" + raw"\n is a new line symbol" + s"with dollar sign: $$$name")

        // read line
        import scala.io.StdIn
        val n = StdIn.readLine("name: ")
        val a = StdIn.readInt()
        println(s"name: $n, age: $a")
    }

    // loops: while, for(n <- gen)
    def loops() = {
        var r, n = 0

        while(n > 0) {
            r = r * n
            n -= 1
        }

        do {
            r = r * n
            n -= 1
        } while (n > 0)

        for (i <- 1 to n) r = r * i

        val s = "Hello"; var sum = 0
        for (i <- 0 until s.length) sum += s(i)
        for (c <- s) sum += c

        // no break or continue
        import scala.util.control.Breaks._
        breakable {
            for (c <- s) {
                if (c > 18) break; // implemented as try catch
            }
        }

        // shadowing
        for (n <- s) r = n // n is a local char, not int
    }

    // advanced 'for' loops: multiple generators, guards, assignments; for comprehension for-yield
    def advancedForLoops() = {

        // multiple generators
        for (i <- 1 to 3; j <- 1 to 3) print(f"${10 * i + j}%3d") // 11 12 13 ...

        // guard for any generator
        for (i <- 1 to 3; j <- 1 to 3 if i != j) print(f"${10 * i + j}%3d") // 12 13 21 23 31 32

        // definitions
        for (i <- 1 to 3; start = 4 - i; j <- start to 3) print(f"${10 * i + j}%3d") // 13 22 23 31 32 33

        // for comprehension, type derived from first generator
        val v = for (i <- 1 to 10) yield i % 3 // scala.collection.immutable.IndexedSeq[Int] = Vector(1, 2, 0, 1, 2, 0, 1, 2, 0, 1)
        val s = for (c <- "Hello"; i <- 0 to 1) yield (c+i).toChar // String = HIeflmlmop
        val w = for (i <- 0 to 1; c <- "Hello") yield (c+i).toChar // scala.collection.immutable.IndexedSeq[Char] = Vector(H, e, l, l, o, I, f, m, m, p)

        // newlines instead of ';'
        for {
            i <- 1 to 3
            start = 4 - i
            j <- start to 3
            if i != j
        } print(f"${10 * i + j}%3d") // 13 23 31 32
    }

    // functions: def x(args) = {} // like static method in java
    def functions() = {

        // double => double
        def abs(x: Double) = if (x >= 0) x else -x

        def fac(n: Int) = {
            var r = 1
            for (i <- 1 to n) r *= i
            // function value // don't use 'return'
            r
        }

        // specify return type for recursive func
        def fac_r(n: Int): Int = if (n <= 0) 1 else n * fac_r(n-1)
    }

    // default and named arguments
    def defaultAndNamedArguments() = {

        def decorate(str: String, left: String = "[", right: String = "]") = {
            left + str + right
        }
        assert(decorate("Hello") == "[Hello]")
        assert(decorate("Hello", "<") == "<Hello]")

        // named
        decorate(left="(", str="Hello", right=")")
    }

    // variable arguments: seq :_*
    def variableArguments() = {

        def sum(args: Int*) = {
            args.sum
        }
        assert(sum(1) == 1)
        assert(sum(1, 2) == 3)

        // unpack seq
        assert(sum(1 to 5 :_*) == 15)

        def sum_r(args: Int*): Int = {
            if (args.isEmpty) 0
            else args.head + sum_r(args.tail :_*)
        }

        // java method need conversion 42 to Object
        import java.text.MessageFormat
        MessageFormat.format("The answer to {0} is {1}", "everything", 42.asInstanceOf[AnyRef])
    }

    // procedures: x => Unit
    def procedures() = {
        // returns no value, used for side effect // i.e. returns Unit

        // n.b. no '='
        def box(s: String) {
            val border = "-" * (s.length + 2)
            println(s"$border")
            print(s"|$s|")
            println(s"$border")
        }

        // I prefer explicit syntax
        def e_box(s: String): Unit = {
            ???
        }

    }

    // lazy values: compute once, check every time, threadsafe
    def lazyValues() = {

        lazy val words = scala.io.Source.fromFile("/temp/words").mkString

        // val words = ... // evaluated once
        // lazy val words = ... // evaluated once on first access
        // def words = ... // evaluated on every access
    }

    // exceptions: try {} catch { case ex: SomeEx } finally {}
    def exceptions() = {
        // java.lang.Throwable, no "checked" exceptions

        def nothingType(x: Int) = {
            if (x >= 0) math.sqrt(x)
            else throw new IllegalArgumentException("x should be positive") // Double is a supertype of Nothing
        }

        // catch example
        import java.net.URL
        import java.io.IOException
        import java.net.MalformedURLException
        val url = new URL("http://www...")
        def process(inp: Any) = ???
        try {
            process(url)
        } catch {
            // pattern matching
            case _: MalformedURLException => println(s"Bad URL: ${url}") // more specific
            case ex: IOException => ex.printStackTrace() // more general
        }

        // try / finally for cleanup
        val in = new URL("http://www...").openStream
        try {
            process(in)
        } finally {
            in.close()
        }

        // try / catch / finally
        try {
            process(in)
        } catch {
            case ex: IOException => ex.printStackTrace()
        } finally {
            in.close()
        }

        // Automatic-Resource-Management http://jsuereth.com/scala-arm/
//        import resource._
//        for(input <- managed(new FileInputStream("test.txt")) {
//            // Code that uses the input as a FileInputStream
//        }

        // monadic exceptions
        import scala.io.StdIn
        import scala.util.Try
        val res = for {
            a <- Try { StdIn.readLine("a: ").toInt }
            b <- Try { StdIn.readLine("b: ").toInt }
        } yield a/b
        res.getOrElse(new IllegalArgumentException("a and b should be numbers and b != 0"))
    }

}

object ControlStructuresAndFunctions_Exercises {

    def ex1 = {
        def signum(n: Int) = {
            if (n > 0) 1
            else if (n < 0) -1
            else 0
        }
    }

    def ex2 = {
        val eb = {} // eb: Unit = ()
    }

    def ex3 = {
        var y = 0
        val x: Unit = y = 1
    }

    def ex4 = {
        // for (int i = 10; i >= 0; i--) println(i)
        for (i <- Range.inclusive(10, 0, -1)) println(i)
        for (i <- 10 to 0 by -1) println(i)
    }

    def ex5 = {

        def countdownWithBug(n: Int) = {
            for (i <- n to 0 by -1) println(i)
        }

        def countdown(n: Int): Unit = {
            print(s"$n ")
            if (n < 0) countdown(n+1)
            else if (n > 0) countdown(n-1)
        }

    }

    def ex6 = {
        // product of the Unicode codes of all letters in a string
        def unicode(ch: Char): Long = {
            ch.toLong
        }

        def charProd(str: String): BigInt = {
            var res: BigInt = 1
            for {
                ch <- str
                uch = unicode(ch)
            } res = res * uch

            res
        }

        assert(charProd("Hello") == 9415087488L)
    }

    def ex7 = {
        // w/o loop
        def charProd(str: String): BigInt = {
            //str.codePoints.toArray.product //.foldLeft(1)(_ * _)
            //str.map(c => c.toLong).product
            //str.foldLeft(1L)(_*_)
            (1L /: str)(_*_)
        }
        assert(charProd("Hello") == 9415087488L)
    }

    def ex8 = {
        def product(s: String) = (1L /: s)(_*_)
    }

    def ex9 = {
        // recursive
        def r_product(s: String): Long = {
            if (s.isEmpty) 1L
            else s.head * r_product(s.tail)
        }
        assert(r_product("Hello") == 9415087488L)
    }

    def ex10 = {
        def even(n: Int): Boolean = n % 2 == 0
        // x^n
        def pow(x: Double, n: Int): Double = {
            if (n == 0) 1
            else if (n > 0) {
                if (even(n)) {
                    val y = pow(x, n/2); y*y
                }
                else x * pow(x, n-1)
            }
            else 1 / pow(x, -n)
        }
    }

    // ex11
    // define a string interpolator date""
    // val d: LocalDate = date"$year-$month-$day"
    import java.time.LocalDate
    implicit class DateInterpolator(val sc: StringContext) extends AnyVal {
        def date(args: Any*): LocalDate = {
            import scala.util.Try
            println(sc.parts.mkString("(", ",", ")"))

            if (sc.parts.length != 4)// || sc.parts.slice(1, 3).exists(_ != '-'))
                sys.error(s"wrong number of parts: ${sc.parts.length}; format: year-month-day")
            if (!sc.parts.slice(1, 3).forall(_ == "-"))
                sys.error(s"wrong dashes: '${sc.parts.slice(1, 3)}'")

            val res = for {
                year    <- Try {args(0).toString.toInt}
                month   <- Try {args(1).toString.toInt}
                day     <- Try {args(2).toString.toInt}
            } yield LocalDate.of(year, month, day)

            res.getOrElse(sys.error(s"unparsable args: '${args}'"))
        }
    }

    def ex11 = {
        val y, m, d = 11
        date"$y-$m-$d"
    }
}
