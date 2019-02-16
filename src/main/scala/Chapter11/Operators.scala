package Chapter11

object Operators {
// topics:
    // identifiers
    // infix operators
    // unary operators
    // assignment operators
    // precedence
    // associativity
    // the apply and update methods
    // extractors
    // extractors with one or no arguments
    // the unapplySeq method
    // dynamic invocation

    // implementing your own operators;
    // operators + implicit conversions = Domain Specific Language;
    // special methods: apply, update, unapply;
    // unary and binary operators are method calls;
    // operator precedence depends on the first char, associativity on the last;
    // extractors extract tuples or sequences of values; or single value or just bool;
    // types with Dynamic trait can inspect/dispatch methods names and arguments at runtime;

    // identifiers
    def identifiers = {
        // names of variables, functions, classes, etc

        // in identifiers:
        // unicode chars are allowed;
        // operator chars are allowed: the ASCII characters
        //      ! # % & * + - / : < = > ? @ \ ^ | ~
        //      that are not letters, digits, underscore,
        //      the .,; punctuation marks, parentheses () [] {}, or quotation marks ' ` "

        val √ = scala.math.sqrt _
        println(s"sqrt of 2: ${√(2)}")

        // The identifiers that reserved in the specification
        //      @ # : = _ => <- <: <% >: ⇒ ←

        // you can include just about any id in backquotes
        val `val` = 42
        val `yield` = () => java.lang.Thread.`yield`()
    }

    // infix operators
    def infixOperators = {
        // binary operators
        // a id b : operator between the arguments
        // method with two parameters: implicit + explicit

        val `1to10` = 1 to 10
        val actually = 1.to(10)
        // def to(end: Int): Range.Inclusive = Range.inclusive(self, end)

        // operator chars
        val ten = 1 -> 10 // tuple
        val ten2 = 1.->(10)

        // just define a method with desired name
        class Fraction(n: Int, d: Int) {
            private val num: Int = ???
            private val den: Int = ???
            def *(other: Fraction) = new Fraction(num * other.num, den * other.den)
        }
    }

    // unary operators
    def unaryOperators = {
        // operator with one parameter: prefix, postfix

        // + - ! ~ allowed as prefix operators
        // converted into obj.unary_op
        // e.g.     -a => a.unary_-

        // postfix op can lead to parsing errors
        import scala.language.postfixOps

        // postfix operator follows its argument
        // obj op => obj.op()
        val `42` = 42 toString
        // 42.toString()
    }

    // assignment operators
    def assignmentOperators = {
        // in the form op=
        // means mutation

        // obj op= obj2     => obj = obj op obj2
        // e.g. a += b      => a = a + b

        // <=, >=, != are NOT assignment operators
        // operator starting with '=' is never an assignment op (==, ===, =/=, etc)
        // if obj has a method 'op=' that method is called directly
    }

    // precedence
    def precedence = {
        // except for assignment operators, the precedence is determined by
        // the FIRST character of the operator

        /* postfix operators have lower precedence than infix operators
        highest: an op character (# ? @ \ ~) other then those below

        * / %
        + -
        :
        < >
        ! =
        &
        ^
        |

        a char that is not an operator char (alphanumeric)
        lowest: assignment operators (op=)
         */

        // postfix operators have lower precedence than infix operators
        // a infix b postfix => (a infix b) postfix
    }

    // associativity
    def associativity = {
        // evaluated left-to-right or right-to-left

        // left-associative
        val left = 17 - 2 - 9 // => (17 - 2) - 9

        // all operators are left-associative
        // except:  'op:' // end in a colon;
        //          'op=' // assignment operators

        val right = 1 :: 2 :: Nil // => 1 :: (2 :: Nil)
        // def ::[B >: A] (x: B): List[B] = new scala.collection.immutable.::(x, this)

        // right-associative binary operator is a method of its second argument
        // 2 :: Nil =>  Nil.::(2)
    }

    // the apply and update methods
    def theApplyAndUpdateMethods = {
        // 'function call' syntax

        // f(args)          => f.apply(args)
        // or, if f(args) is a left side of an assignment
        // f(args) = v      => f.update(args, v)

        val scores = scala.collection.mutable.HashMap.empty[String, Int]
        scores("bob") = 100;            scores.update("bob", 100)
        var bobsScore = scores("bob");  bobsScore = scores.apply("bob")

        // apply in companion objects: factory method
        class Fraction(n: Int, d: Int) { def *(other: Fraction): Fraction = ??? }
        object Fraction {
            // factory
            def apply(n: Int, d: Int): Fraction = new Fraction(n, d)
        }

        // very convenient:
        val res = Fraction(3,4) * Fraction(2,5)
    }

    // extractors
    def extractors = {
        // object with 'unapply' method
        // extract values, check conditions;
        // pattern matching facilitator

        // example

        class Fraction(n: Int, d: Int) { val num: Int = ???; val den: Int = ???; def *(other: Fraction): Fraction = ??? }
        object Fraction {
            // factory
            def apply(n: Int, d: Int): Fraction = new Fraction(n, d)
            // extractor's method // n.b. Option
            def unapply(arg: Fraction): Option[(Int, Int)] =
                if (arg.den == 0) None else Some((arg.num, arg.den))
        }
        // use in variable definition: extract 'a' and 'b'
        var Fraction(a, b) = Fraction(3,4) * Fraction(2,5)
        // or pattern match
        Fraction(1,2) match {
            // n, d are bound to num,den of object
            case Fraction(n, d) => println(s"nominator: ${n}, denominator: ${d}")
            case _ => sys.error("oops")
        }

        // you can use extractors to extract information from any object (if appropriate unapply defined)

        // first, last name // no Name class, just extractor (object)
        object Name {
            def unapply(arg: String): Option[(String, String)] = {
                val pos = arg.indexOf(" ")
                if (pos < 0) None
                else Some((arg take pos, arg drop pos+1))
//                val parts = arg.split("""\s+""")
//                if (parts.length > 1) Some((parts.head, parts.tail.mkString(" ")))
//                else None
            }
        }
        val Name(first, last) = "Cay Horstmann" // Name.unapply
        println(s"$first, $last")

        // case class

        // every 'case class' automatically has companion with 'apply' and 'unapply'
        case class Currency(value: Double, unit: String)
        val money = Currency(29.95, "EUR") // apply
        money match {
            case Currency(amount, "USD") => println(s"$$$amount") // unapply
        }

    }

    // extractors with one or no arguments
    def extractorsWithOneOrNoArguments = {

        // extractors for one component should return Option[value]
        object Number {
            def unapply(arg: String): Option[Int] = scala.util.Try { arg.trim.toInt }.toOption
        }
        val Number(n) = "123"
        println(s"number $n")

        // extractor for testing: return a bool
        object IsCompound {
            def unapply(arg: String): Boolean = arg.contains(" ")
        }
        object Name {
            def unapply(arg: String): Option[(String, String)] = {val pos = arg.indexOf(" ");
                if (pos < 0) None  else Some((arg take pos, arg drop pos+1)) } }
        // n.b. 'IsCompound()' syntax, no 'last' name
        "John van der Linden" match {
            case Name(first, IsCompound()) => println(s"first: $first, last is compound")
        }
    }

    // the unapplySeq method
    def theUnapplySeqMethod = {
        // extract arbitrary sequence of values: unapplySeq => Option[Seq[T]]

        object Name {
            def unapplySeq(arg: String): Option[Seq[String]] =
                if (arg.trim == "") None else Some(arg.trim.split("""\s+"""))
            // !do not supply both: unapply and unapplySeq with the same argument types!
            // def unapply(arg: String): Option[(String, String)] = ???
        }
        "John van der Linden" match {
            case Name(first, last) => println(s"2: $first $last")
            case Name(first, middle, last) => println(s"3: $first $middle $last")
            case Name(first, "van", "der", last) => println(s"van der: $first $last")
            case _ => sys.error("oops")
        }
    }

    // dynamic invocation
    def dynamicInvocation = {
        // best used with restraint, like operator overloading

        // strongly typed language but it's possible to build dynamic dispatch subsystem;
        // common problem for ORM libs: person.lastName = "Doe";

        // trait scala.Dynamic: calls routed to special methods
        import scala.language.dynamics // exotic feature

        class Dynamite extends scala.Dynamic {
            def log(msg: String): Unit = println(msg)
            // *  foo.method("blah")      ~~> foo.applyDynamic("method")("blah")
            // *  foo.method(x = "blah")  ~~> foo.applyDynamicNamed("method")(("x", "blah"))
            // *  foo.method(x = 1, 2)    ~~> foo.applyDynamicNamed("method")(("x", 1), ("", 2))
            // *  foo.field           ~~> foo.selectDynamic("field")
            // *  foo.varia = 10      ~~> foo.updateDynamic("varia")(10)
            // *  foo.arr(10) = 13    ~~> foo.selectDynamic("arr").update(10, 13)
            def applyDynamic(method: String)(param: String): String = {
                println(s"method: $method, parameter: $param")
                "def applyDynamic(method: String)(param: String): String"
            }
            def applyDynamicNamed(method: String)(argpairs: (String, String)*): String = {
                println(s"""method: $method, params: ${argpairs.toList.mkString(";")}""")
                "def applyDynamicNamed(method: String)(argpairs: (String, String)*): String"
            }
            def updateDynamic(fldname: String)(value: String): Unit = {
                println(s"update field: $fldname = $value")
            }
            def selectDynamic(fldname: String): String = {
                println(s"read field: $fldname")
                "def selectDynamic(fldname: String): String"
            }
        }
        val obj = new Dynamite

        // call log method
        obj.log("foo")
        // unnamed arguments: call applyDynamic("getFoo")(arg)
        obj.getFoo("bar")
        // named arguments, at least one: call applyDynamicNamed
        obj.getFooExtended(file = "bar", section = "baz", "42")
        // field assignment: call updateDynamic
        obj.filename = "fileard"
        // field accessor: call selectDynamic
        val fn = obj.filename

        // person.lastName = "Doe"
        // val name = person.lastName
        // val does = people.findByLastName("Doe")
        // val johnDoes = people.find(lastName = "Doe", firstName = "John")

        // book example
        class DynamicProps(val props: java.util.Properties) extends Dynamic {
            private def norm(name: String ) = name.replaceAll("_", ".")
            def updateDynamic(name: String)(value: String) = props.setProperty(norm(name), value)
            def selectDynamic(name: String) = props.getProperty(norm(name))
            def applyDynamicNamed(name: String)(args: (String, String)*) = {
                if (name != "add") throw new IllegalArgumentException
                for ((k, v) <- args) props.setProperty(norm(k), v)
            }
        }
        val sysProps = new DynamicProps(System.getProperties)
        sysProps.username = "Fred"
        val home = sysProps.java_home
        sysProps.add(username="Fred", password="secret")
    }

}

object Operators_Exercises {

    // 1. According to the precedence rules, how are 3 + 4 -> 5 and 3 -> 4 + 5 evaluated?
    def ex1 = {

        /* postfix operators have lower precedence than infix operators
highest: an op character (# ? @ \ ~) other then those below

* / %
+ -
:
< >
! =
&
^
|

a char that is not an operator char (alphanumeric)
lowest: assignment operators (op=)
 */

        // + - have the same precedence // (3 + 4) -> 5 == (7, 5)
        val res = 3 + 4 -> 5
        assert(res == (7, 5))
        // 3 -> 4 + 5 = (3, 4) + 5 = type mismatch
    }

    // 2. The BigInt class has a pow method, not an operator.
    // Why didn’t the Scala library designers choose ** (as in Fortran) or ^ (as in Pascal) for a power operator?
    def ex2 = {
        val bi: BigInt = 2
        val res = 2 + bi pow 2
        // + precedence is higher than 'p' => (2+2) pow 2 = 16
        assert(res == 16)
        // if (pow = **) => res = 2 + (2 ** 2) == 6

        // ^ conflicts with bitwise XOR operator
    }

    // 3. Implement the Fraction class with operations + - * /
    // Normalize fractions,
    // for example, turning 15/–6 into –5/2.
    // Divide by the greatest common divisor, like this:
    //    class Fraction(n: Int, d: Int) {
    //        private val num: Int = if (d == 0) 1 else n * sign(d) / gcd(n, d);
    //        private val den: Int = if (d == 0) 0 else d * sign(d) / gcd(n, d);
    //        override def toString = s"$num/$den"
    //        def sign(a: Int) = if (a > 0) 1 else if (a < 0) -1 else 0
    //        def gcd(a: Int, b: Int): Int = if (b == 0) abs(a) else gcd(b, a % b)
    //        ...
    //    }
    def ex3 = {
        import scala.annotation.tailrec

        class Fraction(n: Int, d: Int) {
            import Fraction._

            private[this] val _gcd = gcd(n.abs, d.abs)
            val num: Int = if (d == 0) 1 else n * d.signum / _gcd
            val den: Int = if (d == 0) 0 else d * d.signum / _gcd

            override def toString = s"$num/$den"

            def +(other: Fraction): Fraction = add(other)
            def -(other: Fraction): Fraction = subtract(other)
            def *(other: Fraction): Fraction = product(other)
            def /(other: Fraction): Fraction = divide(other)

            def add(other: Fraction): Fraction = _add(other)
            def subtract(other: Fraction): Fraction = _add(other, plus=false)
            def product(other: Fraction): Fraction = Fraction(num * other.num, den * other.den)
            def divide(other: Fraction): Fraction = Fraction(num * other.den, den * other.num)

            private def _add(other: Fraction, plus: Boolean = true) = {
                val sign = if (plus) 1 else -1
                Fraction(num*other.den + sign * (other.num * den), den * other.den)
            }
        }
        object Fraction {
            def apply(n: Int, d: Int): Fraction = new Fraction(n, d)
            @tailrec def gcd(a: Int, b: Int): Int = if (b == 0) scala.math.abs(a) else gcd(b, a % b)
        }

        val res = Fraction(1,2) + Fraction(3,4) - Fraction(5,6) * Fraction(7,8) / Fraction(9,10)
        assert(res.toString == "95/216")
    }

    // 4. Implement a class Money with fields for dollars and cents.
    // Supply + - operators as well as comparison operators == and <
    // For example, Money(1, 75) + Money(0, 50) == Money(2, 25) should be true.
    // Should you also supply * and / operators? Why or why not?
    def ex4 = {
        // Should you also supply * and / operators? Why or why not?
        // $1.25 multiply by $3.33 ? or $4.2 divide by $3.42 ? it's meaningless

        def v1 = {
            class Money($dollars: Int, $cents: Int) {
                private val totalCents = Money.totalCents($dollars, $cents)
                require(totalCents >= 0, "no support for negative amounts")

                val dollars: Int = totalCents / 100
                val cents: Int = totalCents % 100

                def +(other: Money): Money = Money(dollars + other.dollars, cents + other.cents)
                def -(other: Money): Money = Money(dollars - other.dollars, cents - other.cents)
                def ==(other: Money): Boolean = totalCents == other.totalCents
                def <(other: Money): Boolean = totalCents < other.totalCents
            }
            object Money {
                def apply(dollars: Int, cents: Int): Money = new Money(dollars, cents)
                def totalCents(dollars: Int, cents: Int): Int = dollars * 100 + cents
            }
        }

        class Money(private val totalCents: Int) {
            val (dollars, cents) = Money.dollarsAndCents(totalCents)

            def +(other: Money): Money = new Money(totalCents + other.totalCents)
            def -(other: Money): Money = new Money(totalCents - other.totalCents)
            def <(other: Money): Boolean = totalCents < other.totalCents

            // def ==(other: Money): Boolean = totalCents == other.totalCents
            final override def equals(other: Any): Boolean = other match {
                case that: Money => this.totalCents == that.totalCents
                case _ => false
            }
            override def hashCode(): Int = totalCents
        }
        object Money {
            def apply(dollars: Int, cents: Int): Money = new Money(totalCents(dollars, cents))
            def totalCents(dollars: Int, cents: Int): Int = dollars * 100 + cents
            def dollarsAndCents(cents: Int): (Int, Int) = (cents / 100, cents % 100)
        }

        assert( Money(1, 75) + Money(0, 50) == Money(2, 25) )
    }

    // 5. Provide operators that construct an HTML table.
    // For example,
    //  Table() | "Java" | "Scala" || "Gosling" | "Odersky" || "JVM" | "JVM, .NET"
    //should produce
    //  <table><tr><td>Java</td><td>Scala</td></tr><tr><td>Gosling...
    def ex5 = {
        class Table {
            def |(cell: String): Table = ???
            def ||(row: String): Table = ???

            override def toString: String = ???
        }
        object Table {
            def apply() = new Table
        }

        val res = Table() | "Java" | "Scala" || "Gosling" | "Odersky" || "JVM" | "JVM, .NET"
        val expected =
            """
              |<table>
              |<tr><td>Java</td><td>Scala</td></tr>
              |<tr><td>Gosling</td><td>Odersky</td></tr>
              |<tr><td>JVM</td><td>JVM, .NET</td></tr>
              |</table>
            """.stripMargin.replaceAll("""\n\s*""", "").trim
        assert(res.toString == expected)
    }

    // 6. Provide a class ASCIIArt whose objects contain figures such as
    //    /\_/\
    //    ( '')
    //    ( - )
    //    | | |
    //    (_|_)
    //Supply operators for combining two ASCIIArt figures horizontally
    //    /\_/\     -----
    //    ( '' )  / Hello \
    //    ( -  ) <  Scala |
    //    | |  |  \ Coder /
    //    (_|__)    -----
    //or vertically. Choose operators with appropriate precedence.
    def ex6 = {
        ???
    }

    // 7. Implement a class BitSequence that stores a sequence of 64 bits packed in a Long value.
    // Supply apply and update operators to get and set an individual bit.
    def ex7 = {
        ???
    }

    // 8. Provide a class Matrix. Choose whether you want to implement 2 × 2 matrices,
    // square matrices of any size, or m × n matrices.
    // Supply operations + and *
    // The latter should also work with scalars, for example, mat * 2.
    // A single element should be accessible as mat(row, col)
    def ex8 = {
        ???
    }

    // 9. Define an object PathComponents with an unapply operation class that extracts the
    // directory path and file name from an java.nio.file.Path.
    // For example, the file /home/cay/readme.txt has directory path /home/cay and file name readme.txt
    def ex9 = {
        ???
    }

    // 10. Modify the PathComponents object of the preceding exercise to instead define an
    // unapplySeq operation that extracts all path segments.
    // For example, for the file /home/cay/readme.txt, you should produce a sequence of three segments:
    // home, cay, and readme.txt
    def ex10 = {
        ???
    }

    // 11. Improve the dynamic property selector in Section 11.11, “Dynamic Invocation,” on page 150
    // so that one doesn’t have to use underscores.
    // For example, sysProps.java.home should select the property with key "java.home".
    // Use a helper class, also extending Dynamic, that contains partially completed paths.
    def ex11 = {
        ???
    }

    // 12. Define a class XMLElement that models an XML element with a name, attributes, and child
    // elements. Using dynamic selection and method calls, make it possible to select paths such as
    // rootElement.html.body.ul(id="42").li
    // which should return all li elements inside ul with id attribute 42 inside body inside html
    def ex12 = {
        ???
    }

    // 13. Provide an XMLBuilder class for dynamically building XML elements, as
    // builder.ul(id="42", style="list-style: lower-alpha;")
    // where the method name becomes the element name and the named arguments become the attributes.
    // Come up with a convenient way of building nested elements.
    def ex13 = {
        ???
    }
}
