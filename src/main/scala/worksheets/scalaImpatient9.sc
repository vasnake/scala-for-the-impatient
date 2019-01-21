// Scala for the Impatient
// Chapter 9. Files and Regular Expressions

/*
• Source.fromFile(...).getLines.toArray yields all lines of a file.
• Source.fromFile(...).mkString yields the file contents as a string.
• To convert a string into a number, use the toInt or toDouble method.
• Use the Java PrintWriter to write text files.
• "regex".r is a Regex object.
• Use """...""" if your regular expression contains backslashes or quotes.
• If a regex pattern has groups, you can extract their contents using the syntax for (regex(var1, ...,varn) <- string).
 */

import java.io.{PrintWriter, Serializable}
import java.net.URL
import scala.collection.mutable.ArrayBuffer

// 9.1 Reading Lines

{
    import scala.io.Source
    val source = Source.fromFile("/tmp/myfile.txt", "UTF-8")
    // The first argument can be a string or a java.io.File
    // You can omit the encoding if you know that the file uses
    // the default platform encoding
    val lineIterator = source.getLines
    // Sometimes, you just want to read an entire file into a string. That’s even simpler:
    val contents = source.mkString

    // Call close when you are done using the Source object
    source.close()
    "9.1"
}

// 9.2 Reading Characters

/*
To read individual characters from a file, you can use a Source object directly
as an iterator since the Source class extends Iterator[Char]:
for (c <- source) process c

If you want to be able to peek at a character without consuming it
(like istream::peek in C++ or a PushbackInputStreamReader in Java),
call the 'buffered' method on the source object.
 */
{
    import scala.io.Source
    val source = Source.fromFile("/tmp/myfile.txt", "UTF-8")
    val iter = source.buffered
    while (iter.hasNext) {
        if (iter.head == 'c') println(iter.head)
        iter.next
    }

    source.close()
    "9.2"
}

// 9.3 Reading Tokens and Numbers

/*
Here is a quick-and-dirty way of reading all whitespace-separated tokens in a source:

val tokens = source.mkString.split("\\s+")
 */
{
    import scala.io.Source
    val source = Source.fromFile("/tmp/myfile.txt", "UTF-8")
    val tokens = source.mkString.split("\\s+")
    source.close()
    println(s"#words: ${tokens.length}")
    "9.3"
}
/*
Remember—you can always use the java.util.Scanner class to process a file that contains a mixture of text and numbers.

Finally, note that you can read numbers from the console:
print("How old are you? ")
  // Console is imported by default, so you don't need to qualify print and readInt
val age = readInt()
 */

// 9.4 Reading from URLs and Other Sources

/*
The Source object has methods to read from sources other than files:

When you read from a URL, you need to know the character set in advance, perhaps from an HTTP header.
See www.w3.org/International/O-charset for more information
 */
{
    import scala.io.Source
    val source1 = Source.fromURL("http://horstmann.com", "UTF-8")

    val source2 = Source.fromString("Hello, World!")
    // Reads from the given string—useful for debugging

    val source3 = Source.stdin
    // Reads from standard input

    List(source1, source2, source3) foreach (_.close())
    "9.4"
}

// 9.5 Reading Binary Files

/*
Scala has no provision for reading binary files.
You’ll need to use the Java library.
Here is how you can read a file into a byte array:
 */
{
    import java.io.{File, FileInputStream}
    val file = new File("/tmp/myfile.txt")
    val in = new FileInputStream(file)
    val bytes = new Array[Byte](file.length.toInt)
    in.read(bytes)
    in.close()
    println(s"#bytes: ${bytes.length}")
    "9.5"
}

// 9.6 Writing Text Files

/*
Scala has no built-in support for writing files.
To write a text file, use a java.io.PrintWriter, for example:
 */
{
    import java.io.PrintWriter
    val out = new PrintWriter("/tmp/numbers.txt")
    for (i <- 1 to 100) out.println(i)
    out.close()
    "9.6"
}
/*
Everything works as expected, except for the printf method.
Instead, use the format method of the String class:

out.print("%6d %10.2f".format(quantity, price))
 */

// 9.7 Visiting Directories

/*
There are currently no “official” Scala classes for visiting all files in a directory,
or for recursively traversing directories.
 */
{
    // It is simple to write a function that produces an iterator through all subdirectories of a directory:
    import java.io.File
    def listFiles(dir: File): Option[Array[File]] = {
        val fs = dir.listFiles
        if (fs == null) None
        else Some(fs)
    }
    def subdirs(dir: File): Iterator[File] = {
        val children = listFiles(dir).getOrElse(Array.empty[File])
            .filter(_.isDirectory)
        children.toIterator ++ children.toIterator.flatMap(subdirs _)
    }
    // With this function, you can visit all subdirectories like this:
    for (d <- subdirs(new File("/tmp"))) println(d)

    "9.7 1"
}

/*
Alternatively, if you use Java 7, you can adapt the walkFileTree method of the java.nio.file.Files class.
That class makes use of a FileVisitor interface.
The following implicit conversion adapts a function to the interface:
 */
/*

{
    import java.nio.file._
    import java.io.File
    import scala.language.implicitConversions

    implicit def makeFileVisitor(f: (Path) => Unit) = new SimpleFileVisitor[Path] {
        override def visitFile(p: Path, attrs: attribute.BasicFileAttributes) = {
            f(p)
            FileVisitResult.CONTINUE
        }
    }
    try {
        Files.walkFileTree(new File("/tmp").toPath, (f: Path) => println(f))
    } finally {
        println("oops")
    }

    "9.7 2"
}
 */

// 9.8 Serialization

/*
In Java, serialization is used to transmit objects to other virtual machines or for short-term storage.
(For long-term storage, serialization can be awkward—it is tedious to deal with different
object versions as classes evolve over time.)

Here is how you declare a serializable class in Java and Scala.

Java:
public class Person implements java.io.Serializable {
  private static final long serialVersionUID = 42L;
  ...
}

Scala:
The Serializable trait is defined in the scala package and does not require an import.
 */
{
    @SerialVersionUID(42L) class Person extends Serializable
    // You can omit the @SerialVersionUID annotation if you are OK with the default ID.
    "9.8 1"
}
/*
You serialize and deserialize objects in the usual way:
 */
{
    import java.io._
    class Person(val name: String = "John") extends Serializable
    val fred = new Person()

    val out = new ObjectOutputStream(new FileOutputStream("/tmp/test.obj"))
    out.writeObject(fred)
    out.close()

    val in = new ObjectInputStream(new FileInputStream("/tmp/test.obj"))
    val savedFred = in.readObject().asInstanceOf[Person]

    "9.8 2"
}

// The Scala collections are serializable, so you can have them as members of your serializable classes:
{
    class Person extends Serializable {
        private val friends = new ArrayBuffer[Person] // OK—ArrayBuffer is serializable
    }
    "9.8 3"
}

// 9.9 Process Control

/*
Scala was designed to scale from humble scripting tasks to massive programs.
The scala.sys.process package provides utilities to interact with shell programs
 */
{
    // Here is a simple example:
    import sys.process._
    import scala.language.postfixOps

    "ls -al .." !
    // As a result, the ls -al .. command is executed, showing all files in the parent directory.
    // The result is printed to standard output.
}
// The sys.process package contains an implicit conversion from strings to ProcessBuilder objects.
// The ! operator executes the ProcessBuilder object
// The result of the ! operator is the exit code of the executed program: 0 if the program was successful,
// or a nonzero failure indicator otherwise
{
    import java.net.URL
    import java.io.File
    import sys.process._
    import scala.language.postfixOps

    // If you use !! instead of !, the output is returned as a string:
    val result = "ls -al .." !!

    // You can pipe the output of one program into the input of another, using the #| operator:
    "ls -al .." #| "grep sec" !

    // To redirect the output to a file, use the #> operator:
    "ls -al .." #> new File("/tmp/output.txt") !

    // To append to a file, use #>> instead:
    "ls -al .." #>> new File("/tmp/output.txt") !

    // To redirect input from a file, use #<:
    "grep sec" #< new File("/tmp/output.txt") !

    // You can also redirect input from a URL:
    "grep Scala" #< new URL("http://horstmann.com/index.html") !
}
/*
If you need to run a process in a different directory, or with different environment variables,
construct a ProcessBuilder with the apply method of the Process object.
 */
{
    import java.io.File
    import sys.process._
    import scala.language.postfixOps

    val p = Process("less", new File("/tmp"), ("LANG", "en_US"))
    // Then execute it with the ! operator:
    "echo 42" #| p !

    "9.9"
}

// 9.10 Regular Expressions

/*
The scala.util.matching.Regex class makes this simple.
To construct a Regex object, use the r method of the String class:
 */
{
    val numPattern = "[0-9]+".r
}
/*
If the regular expression contains backslashes or quotation marks,
then it is a good idea to use the “raw” string syntax, """...""". For example:
 */
{
    val wsnumwsPattern = """\s+[0-9]+\s+""".r

    // The findAllIn method returns an iterator through all matches. You can use it in a for loop:
    val numPattern = "[0-9]+".r
    for (matchString <- numPattern.findAllIn("99 bottles, 98 bottles"))
        println( matchString)

        // or turn the iterator into an array:
    val matches = numPattern.findAllIn("99 bottles, 98 bottles").toArray
    // Array(99, 98)
}

{
    // To find the first match anywhere in a string, use findFirstIn.
    // You get an Option[String].
    val wsnumwsPattern = """\s+[0-9]+\s+""".r
    val m1 = wsnumwsPattern.findFirstIn("99 bottles, 98 bottles")
    // Some(" 98 ")
}

{
    // To check whether the beginning of a string matches, use findPrefixOf:
    val numPattern = "[0-9]+".r
    numPattern.findPrefixOf("99 bottles, 98 bottles")
    // Some(99)

    val wsnumwsPattern = """\s+[0-9]+\s+""".r
    wsnumwsPattern.findPrefixOf("99 bottles, 98 bottles")
    // None

    // You can replace the first match, or all matches:
    numPattern.replaceFirstIn("99 bottles, 98 bottles", "XX")
    // "XX bottles, 98 bottles"
    numPattern.replaceAllIn("99 bottles, 98 bottles", "XX")
    // "XX bottles, XX bottles"

    "9.10"
}

// 9.11 Regular Expression Groups

{
    // Groups are useful to get subexpressions of regular expressions.
    // Add parentheses around the subexpressions that you want to extract, for example:
    val numitemPattern = "([0-9]+) ([a-z]+)".r

    // To match the groups, use the regular expression object as an “extractor”
    val numitemPattern(num, item) = "99 bottles"
    // Sets num to "99", item to "bottles"

    // If you want to extract groups from multiple matches, use a for statement like this:
    for (numitemPattern(num, item) <- numitemPattern.findAllIn("99 bottles, 98 bottles"))
        println(s"$num, $item")

    "9.11"
}

// Exercises

/*
1. Write a Scala code snippet that reverses the lines in a file
(making the last line the first one, and so on).
 */
{
    val fname = "/tmp/lines.txt"

    def getLines(fn: String) = {
        import scala.io.Source
        val src = Source.fromFile(fn)
        val lines = src.getLines().toArray
        src.close()
        lines
    }

    val revLines = getLines(fname).reverse

    def putLines(fn: String, ls: Seq[String]): Boolean = {
        import java.io.PrintWriter
        val out = new PrintWriter(fn)
        ls.foreach(out.println(_))
        out.close()
        true
    }

    if (putLines(fname, revLines)) println(s"writed reversed lines")
    else println(s"can't write reversed lines")

    "1 1"
}

{
    import scala.util.Try

    def getLines(fn: String) = {
        import scala.io.Source
        val src = Source.fromFile(fn)
        val lines = src.getLines().toArray
        src.close()
        lines
    }

    def putLines(fn: String, ls: Seq[String]): Unit = {
        import java.io.PrintWriter
        val out = new PrintWriter(fn)
        ls.foreach(out.println(_))
        out.close()
    }

    val fname = "/tmp/lines.txt.nosuchfile"
    val revLines = Try{ getLines(fname).reverse }
    if (revLines.isSuccess) {
        val res = Try{ putLines(fname, revLines.get) }
        if (res.isSuccess) println(s"writed")
        else println(s"write error")
    } else println(s"read error")

    "1 2"
}

/*
2. Write a Scala program that reads a file with tabs,
replaces each tab with spaces
so that tab stops are at n-column boundaries,
and writes the result to the same file.

n-column boundaries: if tabsize = 4: 0, 4, 8, 12, ...
n % 4 = 0,1,2,3,0,1,2,3,...
 */
{
    def tab2spaces(line: String, tabsize: Int = 4): String = {
        def nspaces(col: Int): Int = tabsize - (col % tabsize)
        val res = StringBuilder.newBuilder
        var column = 0
        for (ch <- line) {
            if (ch == '\t') {
                val ns = nspaces(column)
                res.append(" " * ns)
                column += ns
            }
            else { res.append(ch); column += 1 }
        }
        res.toString
    }
    def check(s1: String, s2: String) = {
        assert(tab2spaces(s1).equals(s2), s"wrong: '${tab2spaces(s1)}' != '$s2'")
    }
    check("\t1", "    1")
    check("1\t2", "1   2")
    check("12\t3", "12  3")
    check("123\t4", "123 4")
    check("1234\t5", "1234    5")
    check("12345\t6", "12345   6")

    import scala.io.Source
    import java.io.PrintWriter
    import scala.util.Try

    val fname = "/tmp/tabs.txt"
    val res = Try {
        val text = {
            for (line <- Source.fromFile(fname).getLines) yield tab2spaces(line)
        }.toArray // need to copy before PrintWriter
        val out = new PrintWriter(fname)
        text.foreach(out.println(_))
        out.close()
    }
    if (res.isSuccess) println("file updated")
    else println(s"error:$res")

    "2"
}

/*
3. Write a Scala code snippet that reads a file and prints all words with more
than 12 characters to the console. Extra credit if you can do this in a single line.
 */
{
    import scala.io.Source
    val fname = "/tmp/tabs.txt"
    val sep = """\s+"""
    Source.fromFile(fname).getLines.flatMap(_.split(sep))
        .filter(_.length > 12).foreach(println(_))

    "3"
}

/*
4. Write a Scala program that reads a text file containing only floating-point numbers.
Print the sum, average, maximum, and minimum of the numbers in the file
 */
{
    def writeFloats(numf: Int, fname: String) = {
        def nextFloat: Float = util.Random.nextFloat()
        val pw = new java.io.PrintWriter(fname)
        for (n <- 1 to numf) pw.println(nextFloat)
        pw.close()
    }

    def readFloats(fname: String) = {
        val src = io.Source.fromFile(fname)
        val floats = src.getLines().flatMap(_.split("\\s+")).map(_.toFloat).toVector
        src.close()
        println(s"sum: ${floats.sum}; average: ${floats.sum / floats.length}")
        println(s"max: ${floats.max}; min: ${floats.min}")
    }

    val fname = "/tmp/floats.txt"
    writeFloats(777, fname)
    readFloats(fname)

    "4"
}

/*
5. Write a Scala program that writes the powers of 2 and their reciprocals to a file,
with the exponent ranging from 0 to 20. Line up the columns:
  1      1
  2      0.5
  4      0.25
...      ...
 */
{
    val fname = "/tmp/powers.txt"
    val powersOfTwo: Stream[Int] = Stream.iterate(1)(_ * 2)

    def reciprocals(str: Stream[Int]): Stream[Double] =
        (1.0 / str.head) #:: reciprocals(str.tail)
    def lines(ints: Stream[Int], floats: Stream[Double]): Stream[String] =
        "%-11d %.9f".format(ints.head, floats.head) #:: lines(ints.tail, floats.tail)
    val text = lines(powersOfTwo, reciprocals(powersOfTwo))
        .take(21).mkString("\n")

    println(text)
    val pw = new java.io.PrintWriter(fname)
    pw.write(text)
    pw.close()

    // https://docs.oracle.com/javase/7/docs/api/java/util/Formatter.html
    // val leftPad = "%8d    %1.9f".format(pow, recip)
    // val rightPad = "%s%.9f".format(pow.toString.padTo(12, ' ').mkString, recip)
    // val rightPad = "%-8d    %.9f".format(pow, recip)

    "5.1"
}

{
    val fname = "/tmp/powers.txt"
    val out = new java.io.PrintWriter(fname)
    for(i <- 0 to 20) {
        val power = math.pow(2, i).toString
        val recip = math.pow(2, -i).toString
        out.println(power + (" " * (12 - power.length)) + recip)
    }
    out.close

    "5.2"
}

/*
6. Make a regular expression searching for quoted strings
"like this, maybe with \" or \\" in a Java or C++ program.
Write a Scala program that prints out all such strings in a source file.
 */
{
    val testData =
        """
          |"simple one"
          | a little "harder" string
          |escaped "quote\"here", no?
          |double "escaped quote\\"
          |invalid \"sequence\"
          |"like this, maybe with \" or \\"
        """.stripMargin.trim.split("\n").toVector

    // https://docs.oracle.com/javase/7/docs/api/java/util/regex/Pattern.html
    // http://regexr.com/3ecvf
    val rexpr =
    """
      |"(?:[^"\\]|\\.)*"
    """.stripMargin.trim.r
    for (s <- testData) println(s"$s -> ${rexpr.findAllIn(s).toVector}")

    def findQuotes(inp: io.Source): Seq[String] = {
        val rexpr =
            """
              |"(?:[^"\\]|\\.)*"
            """.stripMargin.trim.r

        val res = for {
            line <- inp.getLines
            quote <- rexpr.findAllIn(line)
        } yield quote
        res.toVector
    }

    val src = io.Source.fromString(testData.mkString("\n"))
    val quotes = findQuotes(src)
    quotes foreach println

    "6"
}

/*
7. Write a Scala program that reads a text file
and prints all tokens in the file
that are not floating-point numbers.
Use a regular expression.
 */
{
    def isFloat(str: String): Boolean = {
        // http://regexr.com/3ed1t
        val rexpr = """ (?:\d*\.\d+)|(?:\d+\.\d*) """.trim
        str.matches(rexpr)
    }

    val src = io.Source.fromString("one 2 three 4.0 5,6 7.8 .9 1. . ")
    // val src = io.Source.fromFile("/tmp/myfile.txt")
    val tokens = for {
        line <- src.getLines
        token <- line.split("""\s+""")
    } yield token

    val notfloats = tokens.filter(!isFloat(_))
    println(notfloats.mkString("\n"))
    src.close()
}

/*
8. Write a Scala program that prints the src attributes of all img tags of a web page.
Use regular expressions and groups.

<img src="URL">
 */
{
    val url = "http://www.bing.com/search?q=gif"
    val src = io.Source.fromURL(url)
    val text = src.getLines.mkString(" ").toLowerCase
    src.close()
    // http://regexr.com/3ed5l
    // http://docs.oracle.com/javase/6/docs/api/java/util/regex/Pattern.html#special
    val imgr = """ <\s*img\s*[^>]*> """.trim.r
    val srcr = """ src\s*=\s*"(.*?)" """.trim.r
    for {
        img <- imgr.findAllIn(text)
        // p = println(img)
        srcr(src) <- srcr.findAllIn(img)
    } println(src)

    "8.1"
}

{
    import scala.concurrent._
    import scala.concurrent.ExecutionContext.Implicits.global
    import concurrent.duration._
    import language.postfixOps

    def fetchUrl(url: String): Future[String] = Future {
        val src = io.Source.fromURL(url)
        val text = src.getLines.mkString(" ")
        src.close()
        text
    }

    def extractImgSrc(text: String): Seq[String] = {
        // http://regexr.com/3ed5l
        // http://docs.oracle.com/javase/6/docs/api/java/util/regex/Pattern.html#special
        val imgr = """ <\s*img\s*[^>]*> """.trim.r
        val srcr = """ src\s*=\s*"(.*?)" """.trim.r
        val res = for {
            img <- imgr.findAllIn(text.toLowerCase)
            srcr(src) <- srcr.findAllIn(img)
        } yield src
        res.toVector
    }

    val url = "http://www.bing.com/search?q=gif"
    val sources = fetchUrl(url) map extractImgSrc
    val res = Await.result(sources, 3 seconds)
    println(res.mkString("\n"))

    "8.2"
}

/*
9. Write a Scala program that counts how many files with .class extension are
in a given directory and its subdirectories.
 */
{
    import java.io.File

    def listFiles(dir: File): Option[Seq[File]] = {
        val lst = dir.listFiles
        if (lst == null) None
        else Some(lst)
    }

    def listAll(dir: File): Stream[File] = {
        val children: Seq[File] = listFiles(dir).getOrElse(Array.empty[File])
        if (children.isEmpty) Stream.empty[File]
        else children.head #:: children.tail.toStream
            .append(children.filter(_.isDirectory).flatMap(listAll(_)))
    }

    val res = for {
        file <- listAll(new File("/tmp"))
        if (file.isFile && file.getName.endsWith(".class"))
    } yield file.getAbsolutePath
    println(s"number of '*.class' files: ${res.length}")

    "9.1"
}

{
    import scala.util.{Failure, Success, Try}
    import java.io.File
    import rx.lang.scala.Observable

    def listFiles(dir: File): Option[Seq[File]] = {
        val lst = dir.listFiles
        if (lst == null) None else Some(lst)
    }

    def listAll(dir: File): Stream[File] = {
        val lst: Try[Seq[File]] = Try { listFiles(dir).getOrElse(Array.empty[File]) }
        val children: Seq[File] = lst match {
            case Failure(err) =>
                println(err)
                Array.empty[File]
            case Success(seq) => seq
        }

        if (children.isEmpty) Stream.empty[File]
        else children.head #:: children.tail.toStream
            .append(children.filter(_.isDirectory).flatMap(listAll(_)))
    }

    def files(dir: File): Observable[File] = {
        Observable[File](subscriber => {
            val list = listAll(dir)
            for (file <- list if !subscriber.isUnsubscribed) {
                subscriber.onNext(file)
            }
            subscriber.onCompleted()
        })
    }

    val obs = files(new File("/tmp"))
    val classes = obs.filter(f => f.isFile && f.getName.endsWith(".class"))
    val sub = classes.subscribe(f => println(f))
    val count = classes.foldLeft(0) { case (cnt, file) => cnt+1 }
    count.subscribe(c => println(s"observer, # of .class files: $c"))

    "9.2"
}

{
    import scala.util.{Failure, Success, Try}
    import java.io.File

    def listFiles(dir: File): Seq[File] = {
        def isLink = !dir.getAbsolutePath.equals(dir.getCanonicalPath)

        def files: Option[Seq[File]] = {
            val lst = if (dir.isDirectory && !isLink) dir.listFiles else null
            if (lst == null) None else Some(lst)
        }

        Try { files } match {
            case Failure(err) => println(s"listFiles error: $err"); Array.empty[File]
            case Success(opt) => opt.getOrElse(Array.empty[File])
        }
    }

    def listAll(dir: File): Stream[File] = {
        val children = listFiles(dir)
        dir #:: {
            if (children.isEmpty) Stream.empty[File]
            else children.toStream flatMap listAll
        }
    }

    val startPoint = "/"

    val res = listAll(new File(startPoint)).filter(f =>
        f.isFile && f.getName.endsWith(".class"))
    res foreach println
    println(s"number of '*.class' files: ${res.length}")

    "9.3"
}

def jnfList = {
    import java.nio.file._
    import rx.lang.scala.Observable

    val startPoint = "/tmp"
    val dir = FileSystems.getDefault.getPath(startPoint)

    def listFiles(dir: Path): Observable[Path] = {
        Observable[Path](subscriber => {
            val lst = Files.newDirectoryStream(dir)
            val iter = lst.iterator
            while (iter.hasNext && !subscriber.isUnsubscribed) {
                val path = iter.next
                subscriber.onNext(path)
            }
            lst.close()
            subscriber.onCompleted()
        }).onErrorResumeNext(_ => Observable.empty)
    }

    def listAll(dir: Path): Observable[Path] = {
        val lst = listFiles(dir)
        val files = lst.filter(Files.isRegularFile(_))
        val dirs = lst.filter(Files.isDirectory(_, LinkOption.NOFOLLOW_LINKS))
        val children = dirs.flatMap(listAll(_))
        files ++ dirs ++ children
    }

    val classFiles = listAll(dir).filter(p => Files.isRegularFile(p) &&
        p.toString.endsWith(".class"))
    val subs = classFiles.subscribe(p => println(p.toString))
    val count = classFiles.foldLeft(0)((cnt, _) => cnt + 1)
        .subscribe(cnt => println(s"# of class files: $cnt"))

    "9.4"
}

{
    import java.nio.file._
    import java.io.{File, IOException}
    import rx.lang.scala.Observable
    import scala.language.implicitConversions

    val startDir = new File("/").toPath
    // find / -iname '*.class' -type f 2>/dev/null | grep class | wc -l
    // find / -iname '*.class' -type f -print0 2>/dev/null | xargs -0i echo | wc -l

    def listFiles(dir: Path) = Observable[Path](subscriber => {

        val visitor = new SimpleFileVisitor[Path] {
            override def visitFile(file: Path, attrs: attribute.BasicFileAttributes) = {
                if (subscriber.isUnsubscribed) FileVisitResult.TERMINATE
                else {
                    subscriber.onNext(file)
                    FileVisitResult.CONTINUE
                }
            }
            override def visitFileFailed(file: Path, exc: IOException) = {
                println(s"visitFileFailed: $exc")
                //subscriber.onError(exc) // exactly once
                FileVisitResult.CONTINUE
            }
            override def postVisitDirectory(dir: Path, exc: IOException) = {
                if (exc != null) println(s"postVisitDirectory: $exc")
                FileVisitResult.CONTINUE
            }
        }

        Files.walkFileTree(dir, visitor)
        subscriber.onCompleted()

    }) // .onErrorResumeNext(_ => Observable.empty)

    val files = listFiles(startDir)
    val classfiles = files.filter(p => p.toString.endsWith(".class"))
    val count = classfiles.foldLeft(0)((cnt, _) => cnt + 1)

    classfiles.subscribe(p => println(p))
    count.subscribe(c => println(s"# of class files: $c"))
    //files.subscribe(p => println(p))

    "9.5"
}

/*
10. Expand the example with the serializable Person class
that stores a collection of friends.
Construct a few Person objects,
make some of them friends of another,
and then save an Array[Person] to a file.
Read the array back in and verify that the friend relations are intact.
 */

{
    // refuses to work in worksheet env
    import java.io.{FileInputStream, FileOutputStream, ObjectInputStream, ObjectOutputStream}
    import scala.collection.mutable.ArrayBuffer

    @SerialVersionUID(42L) class Person(val name: String) extends Serializable {
        private val friends = ArrayBuffer.empty[String]
        def addFriend(fname: String) = {
            friends += fname
        }
        override def toString: String = s"Person($name), friends = ${friends.mkString(",")}"
    }

    val alice = new Person("Alice")
    val bob = new Person("Bob")
    val charlie = new Person("Charlie")

    alice.addFriend(bob.name)
    alice.addFriend(charlie.name)
    bob.addFriend(alice.name)

    val lst = Array(alice, bob, charlie)

    val fname = "/tmp/friends.oos"

    val oos = new ObjectOutputStream(new FileOutputStream(fname))
    oos.writeObject(lst)
    oos.close()

    val ois = new ObjectInputStream(new FileInputStream(fname))
    val saved = ois.readObject().asInstanceOf[Array[Person]]
    ois.close()

    println(saved.mkString("\n"))

    "10"
}
