package Chapter20

import scala.util.parsing.combinator.RegexParsers

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
        // - repeat an op (rep)  // List
        // - optional op (opt)   // Option class

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
        ???
    }

    // discarding tokens
    def discardingTokens = {
        ???
    }

    // generating parse trees
    def generatingParseTrees = {
        ???
    }

    // avoiding left recursion
    def avoidingLeftRecursion = {
        ???
    }

    // more combinators
    def moreCombinators = {
        ???
    }

    // avoiding backtracking
    def avoidingBacktracking = {
        ???
    }

    // packrat parsers
    def packratParsers = {
        ???
    }

    // what exactly are parsers
    def whatExactlyAreParsers = {
        ???
    }

    // regex parsers
    def regexParsers = {
        ???
    }

    // token-based parsers
    def tokenBasedParsers = {
        ???
    }

    // error handling
    def errorHandling = {
        ???
    }
}

object Parsing_Exercises {

    // 1. Add / and % operations to the arithmetic expression evaluator.
    def ex1 = {
        ???
    }

    // 2. Add a ^ operator to the arithmetic expression evaluator. As in mathematics, ^ should have a
    //higher precedence than multiplication, and it should be right-associative. That is, 4^2^3
    //should be 4^(2^3), or 65536.
    def ex2 = {
        ???
    }

    // 3. Write a parser that parses a list of integers (such as (1, 23, -79)) into a List[Int].
    def ex3 = {
        ???
    }

    // 4. Write a parser that can parse date and time expressions in ISO 8601. Your parser should return
    //a java.time.LocalDateTime object.
    def ex4 = {
        ???
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
