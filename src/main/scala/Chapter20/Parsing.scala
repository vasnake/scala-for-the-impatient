package Chapter20

import scala.util.matching.Regex
import scala.util.parsing.combinator.lexical.StdLexical
import scala.util.{Failure, Success}
import scala.util.parsing.combinator.syntactical.{StandardTokenParsers, StdTokenParsers}
import scala.util.parsing.combinator.{PackratParsers, RegexParsers}
import scala.util.parsing.input.CharSequenceReader

object Parsing {
// topics:
    // grammars
    // combining parser operations
    // transforming parsing results
    // discarding tokens
    // generating parse trees
    // avoiding left recursion
    // more combinators
    // avoiding backtracking
    // packrat parsers
    // what exactly are parsers
    // regex parsers
    // token-based parsers
    // error handling

    // https://www.scala-lang.org/files/archive/api/current/scala-parser-combinators/index.html

    // parser combinators library helps you analyze texts with fixed structure (json, for example);
    // basic concepts of grammars and parsers;
    // good example of Domain-Specific Language;
    // RegexParser, literal strings and regular expressions match tokens;
    // 'repsep' combinator, repeated items with a separator;
    // token-based parser for languages with reserved words;
    // parsers are functions reader => result;
    // you need a robust error reporting;
    // parser combinator library useful with a context-free grammars;

    // grammars
    def grammars = {
        // a few concepts from the theory of formal languages;
        // a grammar: set of rules for producing all strings in a particular format;

        // e.g. arithmetic expression rules:
        // - whole number is an expression;
        // - '+', '-', '*' are operators;
        // - 'left op right' is an expression if left, right are expressions and op is an operator;
        // - '( expr )' is an expression;

        // BNF
        // a grammar is written in a notation: Backus-Naur Form, arithmetic expression rules:
        // op ::= "+" | "-" | "*"
        // expr ::= number | expr op expr | "(" expr ")"

        // number is undefined, although it could be, as an sequence of digits;
        // but in practice, is more efficient to collect numbers (and other tokens) before
        // actual parsing, in a step called 'lexical analysis'.

        // 'op', 'expr' are not tokens, they are structural elements of a grammar;
        // aka 'nonterminal symbols'.
        // to produce parsed text, you start with the 'start symbol' (one of nonterminal),
        // and apply grammar rules until only tokens remain.

        // EBNF
        // Extended BNF: allows optional elements and repetition;
        // ?: 0..1
        // *: 0..n
        // +: 1..n
        // e.g. numberList ::= number ( "," numberList )?
        // or   numberList ::= number ( "," number )*

        // arithmetic expr. rules with operator precedence support:
        // expr ::= term ( ( "+" | "-" ) expr )?    // term with optional (op expr)
        // term ::= factor ( "*" factor )*          // factor with optional many (* factor) // n.b. forgot "/"
        // factor ::= number | "(" expr ")"         // number or (expr) // n.b. loop: expr defined through expr

    }

    // combining parser operations
    def combiningParserOperations = {
        // extend Parsers trait and
        // define parsing operations, combined from given primitives:
        // - match a token
        // - ops alternative (|) // first op or second op or Failure/Error
        // - sequence of ops (~) // ~ case class, similar to a pair
        // - repeat an op (rep)  // List            // postfix '*'
        // - optional op (opt)   // Option class    // postfix '?'

        // e.g. simple arithmetic expressions parser
        class ExprParser extends RegexParsers {
            val number = "[0-9]+".r                                 // regex for number tokens
            def expr: Parser[Any] = term ~ opt(("+" | "-") ~ expr)  // expr ::= term ( ( "+" | "-" ) expr )?
            def term: Parser[Any] = factor ~ rep("*" ~ factor)      // term ::= factor ( "*" factor )*
            def factor: Parser[Any] = number | "(" ~ expr ~ ")"     // factor ::= number | "(" expr ")"
        }

        val parser = new ExprParser
        val result = parser.parseAll(parser.expr, "3-4*5")
        println(result.get)
        // ParseResult[Any] = [1.6] parsed: ((3~List())~Some((-~((4~List((*~5)))~None))))

    }

    // transforming parsing results
    def transformingParsingResults = {
        // you should transform intermediate outputs to a useful form, value or tree.

        // ^^ combinator precedence lower than ~ and higher than |

        // compute the value of an expression
        class ExprParser extends RegexParsers {
            val number = "[0-9]+".r

            def factor: Parser[Int] =                           // number or (expr)
                number ^^ { _.toInt } |                         // apply toInt to number.result
                "(" ~ expr ~ ")" ^^ { case _ ~ e ~ _ => e }     // drop parenthesis

            def expr: Parser[Int] =                             // term with optional (op expr)
                term ~ opt(("+" | "-") ~ expr) ^^ {
                    case t ~ None => t
                    case t ~ Some("+" ~ e) => t + e
                    case t ~ Some("-" ~ e) => t - e
                }

            def term: Parser[Int] =                             // factor with optional many (* factor)
                factor ~ rep("*" ~ factor) ^^ {
                    case f ~ lst => f * lst.map(_._2).product
                }
        }

        val parser = new ExprParser
        val result = parser.parseAll(parser.expr, "3-4*5")
        println(result.get)
        // ParseResult[Int] = [1.6] parsed: -17

        // compute the parsing tree: see below
    }

    // discarding tokens
    def discardingTokens = {
        // to do the match and then discard the token, use '~>', '<~' operators;

        // <~ has a lower precedence than ~ and ~>
        // e.g.: "if" ~> "(" ~> expr <~ ")" ~ expr
        // this discards subexpr (")" ~ expr), not just ")"
        // correct version would be
        // "if" ~> "(" ~> (expr <~ ")") ~ expr

        class ExprParser extends RegexParsers {
            val number = "[0-9]+".r

            def term: Parser[Int] =                             // factor with optional many (* factor)
                factor ~ rep("*" ~> factor) ^^ {
                    case f ~ lst => f * lst.product             // rep("*" ~> factor) become a list of factors, no "*" tokens
                }

            def factor: Parser[Int] =                           // number or (expr)
                number ^^ { _.toInt } |                         // apply toInt to number.result
                    "(" ~> expr <~ ")"                          // drop parenthesis before forming op.result

            def expr: Parser[Int] =                             // term with optional (op expr)
                term ~ opt(("+" | "-") ~ expr) ^^ {
                    case t ~ None => t
                    case t ~ Some("+" ~ e) => t + e
                    case t ~ Some("-" ~ e) => t - e
                }
        }

        val parser = new ExprParser
        val result = parser.parseAll(parser.expr, "3-4*5")
        println(result.get)
        // ParseResult[Int] = [1.6] parsed: -17

    }

    // generating parse trees
    def generatingParseTrees = {
        // to build a compiler or interpreter you want to build up a parse tree

        // done with case classes usually
        class Expr
        case class Number(value: Int) extends Expr
        case class Operator(op: String, left: Expr, right: Expr) extends Expr
        // parser should transform "3+4*5" into
        // Operator("+", Number(3), Operator("*", Number(4), Number(5)))

        class ExprParser extends RegexParsers {
            val number = "[0-9]+".r

            def term: Parser[Expr] =                             // replaced rep/list with option. why?
                factor ~ opt("*" ~> factor) ^^ {
                    case a ~ None => a
                    case a ~ Some(b) => Operator("*", a, b)
                }

            def factor: Parser[Expr] =                           // number or (expr)
                number ^^ { n => Number(n.toInt) } |
                    "(" ~> expr <~ ")"

            def expr: Parser[Expr] =                             // term with optional (op expr)
                term ~ opt(("+" | "-") ~ expr) ^^ {
                    case t ~ None => t
                    case a ~ Some("+" ~ b) => Operator("+", a, b)
                    case a ~ Some("-" ~ b) => Operator("-", a, b)
                }
        }

        val parser = new ExprParser
        val result = parser.parseAll(parser.expr, "3-4*5")
        println(result.get)
        // ParseResult[Expr] = [1.6] parsed: Operator(-,Number(3),Operator(*,Number(4),Number(5)))

    }

    // avoiding left recursion
    def avoidingLeftRecursion = {
        // if parser func calls itself without consuming some input first: endless loop:
        // def ones: Parser[Any] = ones ~ "1" | "1"
        // left-recursive function

        // two alternatives (three: use Packrat parser)
        // def ones: Parser[Any] = "1" ~ ones | "1"
        // def ones: Parser[Any] = rep1("1")

        // it's a common problem.

        // consider: 3-4-5
        // the result should be ((3-4) - 5) = -6
        // but using rule
        // def expr: Parser[Expr] = term ~ opt(("+" | "-") ~ expr)
        // we got (3) - (4-5) = 4

        // turning rule around we would get the correct parse tree,
        // but with left-recursive function
        // def expr: Parser[Expr] = expr ~ opt(("+" | "-") ~ term)

        // you need to collect the intermediate results and then combine them in the correct order
        // easier with lists: collect -t or +t and then coll.sum
        // def expr: Parser[Int] = term ~ rep(("+" | "-") ~ term)

        class ExprParser extends RegexParsers {
            val number = "[0-9]+".r

            def term: Parser[Int] =                             // factor with optional many (* factor)
                factor ~ rep("*" ~> factor) ^^ {
                    case f ~ lst => f * lst.product
                }

            def factor: Parser[Int] =                           // number or (expr)
                number ^^ { _.toInt } |
                    "(" ~> expr <~ ")"

            def expr: Parser[Int] = term ~ rep(                 // term with list(+/- term)
                ("+" | "-") ~ term ^^ {
                    case "+" ~ t => t
                    case "-" ~ t => -t
                }) ^^ { case t ~ lst => t + lst.sum }

//            def expr: Parser[Int] =
//                term ~ opt(("+" | "-") ~ expr) ^^ {
//                    case t ~ None => t
//                    case t ~ Some("+" ~ e) => t + e
//                    case t ~ Some("-" ~ e) => t - e
//                }
        }

        val parser = new ExprParser
        val result = parser.parseAll(parser.expr, "3-4-5")
        println(result.get)
        // ParseResult[Int] = [1.6] parsed: -6

    }

    // more combinators
    def moreCombinators = {
        // https://www.scala-lang.org/files/archive/api/current/scala-parser-combinators/scala/util/parsing/combinator/Parsers.html#rep[T](p:=%3EParsers.this.Parser[T]):Parsers.this.Parser[List[T]]

        // rep: 0..n repetitions
        // repsep
        // rep1
        // repN
        // chainl1

        // def numberList = number ~ rep("," ~> number)
        // def numberList = repsep(number, ",")

        // into or >> : store to a variable
        // p ^^^ v: for parsing literals "true" ^^^ true
        // p ^? f : partial function f
        // log(p)(str)
        // guard(p) : useful for looking ahead

        // def term = factor ~ rep("*" ~> factor)
        // def term = factor into { first => rep("*" ~> factor) ^^ { first * _.product }

        // def factor = log(number)("number") ^^ { _.toInt } ...

    }

    // avoiding backtracking
    def avoidingBacktracking = {
        // backtracking: processing (p | q) if p fails, the parser tries q on the same input.
        // also happens when there is a failure in an 'opt' or 'rep';

        // backtracking can be inefficient

        // consider (3+4)*5 parsing with rules:
        // def expr = term ~ ("+" | "-") ~ expr | term
        // def term = factor ~ "*" ~ term | factor
        // def factor = "(" ~ expr ~ ")" | number

        // while parsing expr ( term ~ ("+" | "-") ~ expr | term )
        // term matches the entire input, then match for +/- fails and the
        // compiler backtracks to the second alternative, parsing term again

        // it is often possible to rearrange the grammar rules to avoid backtracking
        // def expr = term ~ opt(("+" | "-") ~ expr)
        // def term = factor ~ rep("*" ~ factor)
        // def factor = "(" ~ expr ~ ")" | number

        // then you can use the '~!' operator to express that there is no need to backtrack
        class ExprParser extends RegexParsers {
            val number = "[0-9]+".r

            def expr: Parser[Any] = term ~ opt(("+" | "-") ~! expr) // always should be expr after +/-
            def term: Parser[Any] = factor ~ rep("*" ~! factor)     // always a factor after *
            def factor: Parser[Any] = "(" ~! expr ~! ")" | number   // always an expr inside parenthesis
        }

        val parser = new ExprParser
        val result = parser.parseAll(parser.expr, "(3+4)-5")
        println(result.get)
        // ParseResult[Any] = [1.8] parsed: (((((~((3~List())~Some((+~((4~List())~None)))))~))~List())~Some((-~((5~List())~None))))

    }

    // packrat parsers
    def packratParsers = {
        // parsing algorithm that caches previous parse results.

        // advantages:
        // - parse time is guaranteed to be proportional to the length of the input;
        // - the parser can accept left-recursive grammars;

        // to use packrat parser:
        // mix in PackratParsers into your parser;
        // use val/lazy val for parser functions;
        // parser functions return : PackratParser[T];
        // use a PackratReader and supply a parseAll method;

        class OnesPackratParser extends RegexParsers with PackratParsers {
            lazy val ones: PackratParser[Any] = ones ~ "1" | "1"

            def parseAll[T](p: Parser[T], in: String): ParseResult[T] =
                phrase(p)(new PackratReader(new CharSequenceReader(in)))
        }

        val parser = new OnesPackratParser
        val result = parser.parseAll(parser.ones, "111")
        println(result.get)
        // ParseResult[Any] = [1.4] parsed: ((1~1)~1)

    }

    // what exactly are parsers
    def whatExactlyAreParsers = {
        // Parser[T] is a function(r: Reader[Elem]): ParseResult[T]

        // Elem is an abstract type inside Parsers trait;
        // Elem is a Char in RegexParsers trait; is a Token in StdTokenParsers;

        // Reader[Elem] reads a sequence of elems;
        // parser returns one of three options: Success[T], Failure, Error;

        // Error terminates the parser;
        // error happens when: p ~! q fails to match q (after p), commit(p) fails, err(msg) invoked;

        // Failure is a failure to math, normally triggers alternatives in an enclosing |;

        // Sucess[T] has a 'result: T'; has a 'next: Reader[Elem]';

        // consider
        class ExprParser extends RegexParsers {
            val number = "[0-9]+".r // implicit conversion to Parser[String] function
            def expr: Parser[Any] = number | "(" ~ expr ~ ")" // (p | q) is a combined function
        }

    }

    // regex parsers
    def regexParsers = {
        // provides two implicit conversions
        // Parser[String] from a literal "foo";
        // Parser[String] from a regexp "bar".r

        // by default, regex parsers skip whitespaces;
        // you may override val whiteSpace = ...;

        // JavaTokenParsers trait extends RegexParsers and specifies five tokens:
        // ident, wholeNumber, decimalNumber, stringLiteral, floatingPointNumber;
        // none of them correspond exactly to their Java forms.
    }

    // token-based parsers
    def tokenBasedParsers = {
        // use a Reader[Token] instead of a Reader[Char];
        import scala.util.parsing.combinator.token.Tokens

        // subtype
        import scala.util.parsing.combinator.token.StdTokens
        // defines four types of tokens:
        // Identifier, Keyword, NumericLit, StringLit

        new StandardTokenParsers
        // provides parser for these tokens

        // extending this parser you can add any reserved words and tokens
        class MLP extends StandardTokenParsers {
            lexical.reserved += ("break", "case")   // becomes a Keyword, not Identifier
            lexical.delimiters += ("=", "!=")
        }
        // ident function parses an identifier;
        // numericLit, stringLit parse literals;

        class ExprParser extends StandardTokenParsers {
            lexical.delimiters += ("+", "-", "*", "(", ")")

            def expr: Parser[Any] = term ~ rep(("+" | "-") ~ term)
            def term: Parser[Any] = factor ~ rep("*" ~> factor)
            def factor: Parser[Any] = numericLit | "(" ~> expr <~ ")"

            def parseAll[T](p: Parser[T], in: String): ParseResult[T] =
                phrase(p)(new lexical.Scanner(in))
        }

        // to process languages with different tokens, adapt the token parser.
        // extend StdLexical and override the 'token' method;
        // extend StdTokenParsers and override lexical

        // e.g. overriding 'token' method using regexp
        class MyLexical extends StdLexical {
            def regex(r: Regex): Parser[String] = new Parser[String] {
                def apply(in: Input) = r.findPrefixMatchOf(in.source.subSequence(in.offset, in.source.length)
                ) match {
                    case Some(matched) =>
                        Success(in.source.subSequence(in.offset, in.offset + matched.end).toString, in.drop(matched.end))
                    case None =>
                        Failure("string matching regex '$r' expected but ${in.first} found", in)
                }}

            override def token: Parser[Token] = {
                regex("[a-z][a-zA-Z0-9]*".r) ^^ { processIdent(_) } |
                    regex("0|[1-9][0-9]*".r) ^^ { NumericLit(_) } |
                    ???
            }
        }

    }

    // error handling
    def errorHandling = {
        // you want a message indicating where the failure occured;

        // the parser reported the last visited failure point;
        // e.g. in rule
        // def value = numericLit | "true" | "false"
        // input failed to match "false" will be reported, if none is matched;

        // you can add explicit failure
        // def value = numericLit | "true" | "false" | failure("not a valid value")
        // or
        // def value = opt(sign) ~ digits withFailureMessage "not a valid number"

        // when the parser fails, parseAll returns Failure result with 'msg' property;
        // 'next' property: Reader pointer to unconsumed input;
        // next.pos.line, next.pos.column: failed position;
        // next.first : lexical element of failure, Char for RegexParsers;

        // you can add 'positioned' combinator to save positions to a parse result
        // def vardecl = "var" ~ positioned(ident ^^ { Ident(_)}) ~ "=" ~ value

    }
}

object Parsing_Exercises {

    // 1. Add / and % operations to the arithmetic expression evaluator.
    def ex1 = {

        class ExprParser extends RegexParsers {
            val number = "[0-9]+".r

            // factor with list(*/% factor)
            def term: Parser[Int] = factor ~ rep(("*" | "/" | "%") ~ factor) ^^ {
                case f ~ lst => (f /: lst)((acc, pair) => pair._1 match {
                    case "*" => acc * pair._2
                    case "/" => acc / pair._2
                    case "%" => acc % pair._2
                })
            }

            // number or (expr)
            def factor: Parser[Int] = number ^^ { _.toInt } | "(" ~> expr <~ ")"

            // term with list(+/- term)
            def expr: Parser[Int] = term ~ rep(
                ("+" | "-") ~ term ^^ {
                    case "+" ~ t => t
                    case "-" ~ t => -t
                }) ^^ { case t ~ lst => t + lst.sum }
        }

        class ExprParser2 extends ExprParser {
            val mul: (Int, Int) => Int = (x, y) => x * y
            val div: (Int, Int) => Int = (x, y) => x / y
            val mod: (Int, Int) => Int = (x, y) => x % y

            // factor with list(*/% factor)
            override def term: Parser[Int] = factor ~ rep(
                ("*" | "/" | "%") ~ factor ^^ {
                    case "*" ~ n => (mul, n)
                    case "/" ~ n => (div, n)
                    case "%" ~ n => (mod, n)
                }) ^^ { case x ~ lst => (x /: lst)((acc, elem) => elem._1(acc, elem._2)) }
        }

        // test
        val parser = new ExprParser
        val result = parser.parseAll(parser.expr, "2*3-4/2-5%2") // 6 - 2 - 1
        assert(result.get == 3)
        result // ParseResult[Int] = [1.12] parsed: 3
    }

    // 2. Add a ^ operator to the arithmetic expression evaluator.
    // As in mathematics, ^ should have a higher precedence than multiplication,
    // and it should be right-associative. That is, 4^2^3 should be 4^(2^3), or 65 536.
    def ex2 = {

        class ExprParser extends RegexParsers {
            val number = "[0-9]+".r

            // expr = term with list(+/- term)
            def expr: Parser[Int] = term ~ rep(
                ("+" | "-") ~ term ^^ {
                    case "+" ~ t => t
                    case "-" ~ t => -t
                }) ^^ { case t ~ lst => t + lst.sum }

            // term = exponent with list(*/% exponent)
            def term : Parser[Int] = exponent ~ rep(("*" | "/" | "%") ~ exponent) ^^ {
                case f ~ lst => (f /: lst)((acc, pair) => pair._1 match {
                    case "*" => acc * pair._2
                    case "/" => acc / pair._2
                    case "%" => acc % pair._2
                })
            }

            // exponent = factor with list(^ factor)
            def exponent: Parser[Int] = factor ~ rep("^" ~> factor) ^^ {
                case n ~ lst => (n :: lst).reduceRight((a, b) => math.pow(a.toDouble, b.toDouble).toInt)
            }

            // factor = number or (expr)
            def factor: Parser[Int] = number ^^ { _.toInt } | "(" ~> expr <~ ")"
        }

        // test
        def eval(e: String) = {
            val parser = new ExprParser
            parser.parseAll(parser.expr, e)
        }

        assert(eval("2*3^4").get == 162)
        assert(eval("4^2^3 - (2*3-4/2-5%2)").get == 65533)

    }

    // 3. Write a parser that parses a list of integers (such as (1, 23, -79)) into a List[Int].
    def ex3 = {

        class ListParser extends RegexParsers {
            def expr: Parser[List[Int]] = "(" ~> repsep(number, ",") <~ ")"
            def number: Parser[Int] = numberRegex ^^ { _.toInt }
            val numberRegex: Regex = """-?\d+""".r
        }

        // test
        def eval(e: String) = {
            val parser = new ListParser
            parser.parseAll(parser.expr, e)
        }
        assert(eval("(1, 23, -79)").get == List(1, 23, -79))

    }

    // 4. Write a parser that can parse date and time expressions in ISO 8601.
    // Your parser should return a java.time.LocalDateTime object.
    def ex4 = {
        import java.time.{LocalDateTime, OffsetDateTime}
        // Date and time in UTC
        // 2019-03-22T09:19:51+00:00
        // 2019-03-22T09:19:51Z

//        case class DateTime(year: String, month: String, day: String,
//                            hour: String, minutes: String, seconds: String,
//                            offset: String)
//
//        class DateTimeParser extends RegexParsers {
//            val year: Regex = """\d{4}""".r
//            val month: Regex = """\d{2}""".r
//            val day: Regex = """\d{2}""".r
//            val hour: Regex = """\d{2}""".r
//            val minutes: Regex = """\d{2}""".r
//            val seconds: Regex = """\d{2}""".r
//            val offset: Regex = """(\+|\-)\d{2}\:\d{2}""".r
//
//            def expr: Parser[DateTime] = ???
//        }

        class DateTimeParser extends RegexParsers {
            def expr: Parser[LocalDateTime] = ".+".r ^^ { OffsetDateTime.parse(_).toLocalDateTime }
        }

        // test
        def eval(e: String) = {
            val parser = new DateTimeParser
            val res = parser.parseAll(parser.expr, e)
            println(s"input: '$e', parsed: '${res.get.toString}'")
            res
        }

        assert(eval("2019-03-22T09:19:51+00:00").get.toString == "2019-03-22T09:19:51")
        assert(eval("2019-03-22T09:19:51Z").get.toString == "2019-03-22T09:19:51")

    }

    // 5. Write a parser that parses a subset of XML. Handle tags of the form <ident>...
    //</ident> or <ident/>. Tags can be nested. Handle attributes inside tags. Attribute values
    //can be delimited by single or double quotes. You don’t need to deal with character data (that is,
    //text inside tags or CDATA sections). Your parser should return a Scala XML Elem value. The
    //challenge is to reject mismatched tags. Hint: into, accept.
    def ex5 = {
        ???
    }

    // 6. Assume that the parser in Section 20.5, “Generating Parse Trees,” on page 309 is completed
    //with
    //Click here to view code image
    //class ExprParser extends RegexParsers {
    //def expr: Parser[Expr] = (term ~ opt(("+" | "-") ~ expr)) ^^ {
    //case a ~ None => a
    //case a ~ Some(op ~ b) => Operator(op, a, b)
    //}
    //...
    //}
    //Unfortunately, this parser computes an incorrect expression tree—operators with the same
    //precedence are evaluated right-to-left. Modify the parser so that the expression tree is correct.
    //For example, 3-4-5 should yield an Operator("-", Operator("-", 3, 4), 5).
    def ex6 = {
        ???
    }

    // 7. Suppose in Section 20.6, “Avoiding Left Recursion,” on page 310, we first parse an expr into
    //a list of ~ with operations and values:
    //Click here to view code image
    //def expr: Parser[Int] = term ~ rep(("+" | "-") ~ term) ^^ {...}
    //To evaluate the result, we need to compute ((t 0 ± t1) ± t2) ± . . . Implement this computation as a
    //fold (see Chapter 13).
    def ex7 = {
        ???
    }

    // 8. Add variables and assignment to the calculator program. Variables are created when they are
    //first used. Uninitialized variables are zero. To print a value, assign it to the special variable
    //out.
    def ex8 = {
        ???
    }

    // 9. Extend the preceding exercise into a parser for a programming language that has variable
    //assignments, Boolean expressions, and if/else and while statements.
    def ex9 = {
        ???
    }

    // 10. Add function definitions to the programming language of the preceding exercise.
    def ex10 = {
        ???
    }

}
