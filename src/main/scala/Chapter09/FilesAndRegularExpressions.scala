package Chapter09

import scala.annotation.tailrec

object FilesAndRegularExpressions {
// topics:
    // reading lines
    // reading characters
    // reading tokens and numbers
    // reading from URLs and other sources
    // reading binary files
    // writing text files
    // visiting directories
    // serialization
    // process control
    // regular expressions
    // regular expression groups

    // read: Source.fromFile
    // write: java PrintWriter
    // "regex".r is a Regex object
    // use """ ... """
    // extract regex groups using pattern matching: regex(v1, v2, ...) <- string

    // reading lines
    def readingLines = {
        // reading text file

        import scala.io.Source
        val src = Source.fromFile("/tmp/test.txt", "UTF-8")
        try {
            val lineIterator = src.getLines
            for (line <- lineIterator) println(line)
        } finally {
            src.close()
        }

    }

    // reading characters
    def readingCharacters = {
        // Source extends Iterator[Char]

        import scala.io.Source
        val src = Source.fromFile("/tmp/test.txt", "UTF-8")
        try {
            for (char <- src) print(char)
        } finally {
            src.close()
        }

        def buffered = {
            // peek without consuming
            val src = Source.fromFile("/tmp/test.txt", "UTF-8")
            try {
                val iter = src.buffered // cache head
                while (iter.hasNext) {
                    if (iter.head != 'Q') {
                        print(iter.head)
                    }
                    val nextchar = iter.next()
                }

                //or, if file is small:
                val contents: String = src.mkString
            } finally {
                src.close()
            }
        }
    }

    // reading tokens and numbers
    def readingTokensAndNumbers = {
        // quick-and-dirty way of reading all whitespace-separated tokens

        import scala.io.Source
        val src = Source.fromFile("/tmp/test.txt", "UTF-8")
        try {
            val tokens = src.mkString.split(raw"\s+")
            val numbers = tokens.map(_.toDouble)
        } finally {
            src.close()
        }

        // you can always use the java.util.Scanner to process a mixture of text and numbers

        // or, from stdin
        import scala.io.StdIn
        val age = StdIn.readInt()
    }

    // reading from URLs and other sources
    def readingFromURLsAndOtherSources = {
        import scala.io.Source
        val urlSrc = Source.fromURL("http://ya.ru", "UTF-8")
        val stringSrc = Source.fromString("Hello, World!") // useful for debugging
        val sinSrc = Source.stdin

        List(urlSrc, stringSrc, sinSrc).foreach(_.close)
    }

    // reading binary files
    def readingBinaryFiles = {
        // scala has no provision for reading binary files

        import java.io.{File, FileInputStream}
        val file = new File("/tmp/test.bin")
        val ins = new FileInputStream(file)
        try {
            val buff = new Array[Byte](file.length.toInt)
            ins.read(buff)
        } finally {
            ins.close()
        }
    }

    // writing text files
    def writingTextFiles = {
        // scala has no built-in support for writing files, use java.io.PrintWriter
        import java.io.PrintWriter
        val out = new PrintWriter("/tmp/out.txt")
        try {
            for (n <- 1 to 100) out.println(n)

            // you may have problems with printf
            val price = 42.0
            out.printf("%10.2f", price.asInstanceOf[AnyRef]) // ugh

            // instead, use string interpolation
            out.print(f"${price}%10.2f")

        } finally {
            out.close()
        }

    }

    // visiting directories
    def visitingDirectories = {
        // there are no built-in classes for traversing a file system
        // use java.nio.file Files.list, Files.walk

        import java.nio.file.{Files, Paths, Path}
        import scala.util.Try
        def processPath(path: Path) = {
            val res = Try { println(path.toAbsolutePath.toString) }
            if (res.isFailure) println(res.failed.get.getMessage)
        }

        // not good: throws java.io.UncheckedIOException
        val allEntries = Files.walk(Paths.get("/tmp")) // recursive, DFS
        val onelevelEntries = Files.list(Paths.get("/tmp")) // not recursive
        try {
            allEntries.forEach(p => processPath(p))
        } finally {
            println("closing streams ...")
            allEntries.close(); onelevelEntries.close()
        }

    }

    // serialization
    def serialization = {
        // short-term storage or transmission to another jvm

        @SerialVersionUID(42L) class Person extends Serializable {
            // scala collections are serializable
            import scala.collection.mutable.ArrayBuffer
            private val friends = ArrayBuffer.empty[Person]
        }

        import java.io.{ObjectOutputStream, FileOutputStream, ObjectInputStream, FileInputStream}
        val fred = new Person
        // save
        val out = new ObjectOutputStream(new FileOutputStream("/tmp/test.obj"))
        out.writeObject(fred)
        out.close()
        // load
        val in = new ObjectInputStream(new FileInputStream("/tmp/test.obj"))
        val savedFred = in.readObject().asInstanceOf[Person]
        in.close()

    }

    // process control
    def processControl = {
        // shell commands, scripts
        // postfix syntax is being deprecated
        // implicit conversion from strings to ProcessBuilder
        import java.io.File
        import java.net.URL
        import scala.sys.process._

        var exitcode = "ls -al /tmp".! // print listing to stdout
        val listing = "ls -al /tmp".!! // if exitcode != 0 => exception

        // pipe
        exitcode = ("ls -al /tmp" #| "grep u").!
        // redirect
        exitcode = ("ls -al /tmp" #> new File("/tmp/list.txt")).!
        // append
        exitcode = ("ls -al /opt" #>> new File("/tmp/list.txt")).!
        // from a file
        exitcode = ("grep u" #< new File("/tmp/list.txt")).!
        exitcode = ("grep Scala" #< new URL("http://ya.ru")).!
        // you can combine processes with #&& and #||

        // # prefix ensure equal precedence for all ops

        // custom environment: Process object
        val p = Process("ls -la", new File("/tmp"), ("LANG", "en_US.utf8"))
        exitcode = (p #| "grep u").!

        // for perverts: scala for shell script
        /*
        #!/bin/sh
        exec scala "$0" "$@"
        !#
        Scala commands
         */

        // scala script from java program, javax.script
        // ScriptEngine engine = new ScriptEngineManager().getScriptEngineByName("scala")
    }

    // regular expressions
    def regularExpressions = {
        // scala.util.matching.Regex
        val numPattern = "[0-9]+".r
        val wsnumwsPattern = """\s+[0-9]+\s+""".r // raw string syntax

        // find all
        for (s <- numPattern.findAllIn("99 bottles, 98 bottles")) println(s) // 99, 98

        // find first
        val firstmatch = wsnumwsPattern.findFirstIn("99 bottles, 98 bottles") // Some("98")

        // check whole string against regex pattern
        "^[0-9]+$".r findFirstIn "some string" match {
            case None => println("not numeric")
            case Some(x) => println("all numbers")
        }
        // or
        if ("some string".matches("[0-9]+")) println("all numbers")

        // replace
        println(numPattern.replaceFirstIn("99 bottles, 98 bottles", "XX"))
        println(numPattern.replaceAllIn("99 bottles, 98 bottles", "XX"))
        println(numPattern.replaceSomeIn("99 bottles, 98 bottles", {
            m => if (m.matched.toInt % 2 == 0) Some("XX") else None
        }))

        // more complex example of replace
        val varPattern = """\$[0-9]+""".r
        def format(message: String, vars: String*) = varPattern.replaceSomeIn(message, {
            m => vars.lift(m.matched.tail.toInt)
        })
        println(format("At $1, there was $2 on $0",
            "planet 7", "12:30 pm", "a disturbance of the force"))
    }

    // regular expression groups
    def regularExpressionGroups = {
        // get subexpressions of regex: parentheses and Match object

        val numitemPattern = "([0-9]+) ([a-z]+)".r
        // m.matched: string
        // m.group(n): n-th group
        // m.start, m.end, m.start(n), m.end(n): substring indices
        for (m <- numitemPattern.findAllMatchIn("99 bottles, 98 bottles")) println(m.group(0))

        // groups by name
        val namedNumitemPattern = "([0-9]+) ([a-z]+)".r("num", "item")

        // using extractor: it MUST match the string, there MUST be a group for each variable
        val numitemPattern(num, item) = "99 bottles"

        // groups with multiple matches
        for (numitemPattern(num, item) <- numitemPattern.findAllIn("99 bottles, 98 bottles"))
            println(s"${num}: $item")
    }

}

object FilesAndRegularExpressions_Exercises {

    // 1. Write a Scala code snippet that reverses the lines in a file
    // (making the last line the first one, and so on).
    def ex1 = {
        val fname = "/tmp/lines.txt"

        def getLines(fn: String): Seq[String] = {
            import scala.io.Source
            val src = Source.fromFile(fn)
            val lines = src.getLines().toArray
            src.close()
            lines
        }

        def putLines(fn: String, ls: Seq[String]): Unit = {
            import java.io.PrintWriter
            val out = new PrintWriter(fn)
            ls.foreach(out.println)
            out.close()
        }

        val revLines = getLines(fname).reverse
        putLines(fname, revLines)

        // with try .. catch
        def inTry = {
            import scala.util.{Try, Success, Failure}
            val fname = "/tmp/lines.txt.nosuchfile"

            val res = Try{ getLines(fname).reverse }.flatMap(revlines =>
                Try { putLines(fname, revlines) })

            res match {
                case Success(x) => println("writed")
                case Failure(x) => println(s"failed: ${x.getMessage}")
            }
        }
    }

    // 2. Write a Scala program that reads a file with tabs,
    // replaces each tab with spaces so
    // that tab stops are at n-column boundaries,
    // and writes the result to the same file.
    def ex2 = {
        // n-column boundaries: if tabsize = 4: 0, 4, 8, 12, ...
        // n % 4 = 0,1,2,3,0,1,2,3,...

        // replace tabs for spaces in one line
        def tab2spaces(line: String, tabsize: Int = 4): String = {
            def nspaces(col: Int): Int = tabsize - (col % tabsize) // 0 => 4, 1 => 3, 2 => 2, 3 => 1
            val res = StringBuilder.newBuilder
            for (ch <- line) {
                if (ch == '\t') res.append(" " * nspaces(res.length))
                else res.append(ch)
            }
            res.toString
        }

        // mini test
        def check(s1: String, s2: String) = {
            assert(tab2spaces(s1).equals(s2), s"wrong: '${tab2spaces(s1)}' != '$s2'")
        }
        check("\t1", "    1")
        check("1\t2", "1   2")
        check("12\t3", "12  3")
        check("123\t4", "123 4")
        check("1234\t5", "1234    5")
        check("12345\t6", "12345   6")

        // process file
        import scala.io.Source
        import java.io.PrintWriter
        import scala.util.Try

        val fname = "/tmp/tabs.txt"
        val res = Try {
            val text = {
                for (line <- Source.fromFile(fname).getLines) yield tab2spaces(line)
            }.toArray // need to copy before PrintWriter // write to the same file
            // TODO: write to a temp file then rename; stream processing
            val out = new PrintWriter(fname)
            text.foreach(out.println)
            out.close()
        }
        if (res.isSuccess) println("file updated")
        else println(s"error:$res")

    }

    // 3. Write a Scala code snippet that
    // reads a file and prints all words with more than 12 characters
    // to the console. Extra credit if you can do this in a single line.
    def ex3 = {
        import scala.io.Source
        val fname = "/tmp/tabs.txt"
        val sep = """\s+"""

        Source.fromFile(fname).getLines.flatMap(_.split(sep)).filter(_.length > 12).foreach(println)
    }

    // 4. Write a Scala program that
    // reads a text file containing only floating-point numbers.
    // Print the sum, average, maximum, and minimum of the numbers in the file.
    def ex4 = {
        // one number on one line

        def writeFloats(count: Int, fname: String): Unit = {
            def nextFloat: Float = util.Random.nextFloat()
            val pw = new java.io.PrintWriter(fname)
            for (n <- 1 to count) pw.println(nextFloat)
            pw.close()
        }

        // TODO: process a stream of lines
        def readFloats(fname: String): Unit = {
            val src = scala.io.Source.fromFile(fname)
            val floats = src.getLines.flatMap(_.split("""\s+""")).map(_.toFloat).toVector
            src.close()

            println(s"sum: ${floats.sum}; average: ${floats.sum / floats.length}")
            println(s"max: ${floats.max}; min: ${floats.min}")
        }

        val fname = "/tmp/floats.txt"
        val numbersCount = 777

        // create test file
        writeFloats(numbersCount, fname)

        // read and calculate
        readFloats(fname)
    }

    // 5. Write a Scala program that writes the powers of 2 and their reciprocals to a file,
    // with the exponent ranging from 0 to 20.
    // Line up the columns:
    //
    //    1         1
    //    2         0.5
    //    4         0.25
    //    ...       ...
    def ex5 = {
        // Definitions of reciprocal
        // a mathematical expression or function so related to another that their product is one
        val fname = "/tmp/powers.txt"
        // https://docs.oracle.com/javase/7/docs/api/java/util/Formatter.html
        // val leftPad = "%8d    %1.9f".format(pow, recip)
        // val rightPad = "%s%.9f".format(pow.toString.padTo(12, ' ').mkString, recip)
        // val rightPad = "%-8d    %.9f".format(pow, recip)

        def simple = {
            val out = new java.io.PrintWriter(fname)
            for(i <- 0 to 20) {
                val power = math.pow(2, i)
                val recip = 1.0 / power
                val line = f"${power.toInt.toString.padTo(12, ' ')}${recip}%.7f"
                println(line); out.println(line)
            }
            out.close
        }

        def onStreams = {
            val powersOfTwo: Stream[Int] = Stream.iterate(1)(_ * 2)

            def reciprocals(inp: Stream[Int]): Stream[Double] =
                (1.0 / inp.head) #:: reciprocals(inp.tail)

            def lines(ints: Stream[Int], floats: Stream[Double]): Stream[String] =
                "%-11d %.7f".format(ints.head, floats.head) #:: lines(ints.tail, floats.tail)

            // output
            val text = lines(powersOfTwo, reciprocals(powersOfTwo))
                .take(21).mkString("\n")

            println(text)
            val pw = new java.io.PrintWriter(fname)
            pw.write(text)
            pw.close()
        }

    }

    // 6. Make a regular expression searching for quoted strings
    // "like this, maybe with \" or \\"
    // in a Java or C++ program.
    // Write a Scala program that prints out all such strings in a source file.
    def ex6 = {
        // https://docs.oracle.com/javase/7/docs/api/java/util/regex/Pattern.html
        // http://regexr.com/3ecvf

        val testData =
            """
              |"simple one"
              | a little "harder" string
              |escaped "quote\"here", no?
              |double "escaped quote\\"
              |invalid \"sequence\"
              |"like this, maybe with \" or \\"
            """.stripMargin.trim.split("\n").toVector

        val regex =
        """
          |"(?:[^"\\]|\\.)*"
        """.stripMargin.trim.r

        // test
        for (s <- testData) println(s"$s -> ${regex.findAllIn(s).toVector}")

        // quoted strings in a file
        val src = scala.io.Source.fromString(testData.mkString("\n"))
        for {
            line <- src.getLines
            quote <- regex.findAllIn(line)
        } println(quote)
        src.close()
    }

    // 7. Write a Scala program that reads a text file
    // and prints all tokens in the file that are not floating-point numbers.
    // Use a regular expression.
    def ex7 = {

        def isFloat(str: String): Boolean = {
            // http://regexr.com/3ed1t
            val regex = """ (?:\d*\.\d+)|(?:\d+\.\d*) """.trim
            str.matches(regex)
        }

        // input
        val src = scala.io.Source.fromString("one 2 three 4.0 5,6 7.8 .9 1. . ")
        // val src = scala.io.Source.fromFile("/tmp/myfile.txt")

        val tokens = for {
            line <- src.getLines
            token <- line.split("""\s+""")
        } yield token

        val notfloats = tokens.filterNot(isFloat)
        for (s <- notfloats) println(s)

        src.close()
    }

    // 8. Write a Scala program that prints the
    // src attributes of all img tags of a web page.
    // Use regular expressions and groups.
    def ex8 = {
        // http://regexr.com/3ed5l
        // http://docs.oracle.com/javase/6/docs/api/java/util/regex/Pattern.html#special

        val url = "https://www.foxnews.com/us"
        // regex
        val imgr = """ <\s*img\s*[^>]*> """.trim.r
        val srcr = """ \s+src\s*=\s*"(.*?)" """.trim.r

        def simple = {
            // get text
            val src = scala.io.Source.fromURL(url)
            val text = src.getLines.mkString(" ").toLowerCase
            src.close()

            // print src attr
            for {
                img <- imgr.findAllIn(text)
                srcr(src) <- srcr.findAllIn(img)
            } println(src)
        }

        def async = {
            import scala.concurrent._
            import scala.concurrent.ExecutionContext.Implicits.global
            import scala.concurrent.duration._

            def fetchUrl(url: String): Future[String] = Future {
                val src = scala.io.Source.fromURL(url)
                val text = src.getLines.mkString(" ").toLowerCase
                src.close()
                text
            }

            def extractImgSrc(text: String): Iterator[String] = {
                for {
                    img <- imgr.findAllIn(text.toLowerCase)
                    srcr(src) <- srcr.findAllIn(img)
                } yield src
            }

            // src iterator
            val srcs = fetchUrl(url) map extractImgSrc

            val res = Await.result(srcs, 3.seconds)
            res foreach println
        }

    }

    // 9. Write a Scala program that
    // counts how many files with .class extension are
    // in a given directory and its subdirectories.
    def ex9 = {
        // find / -iname '*.class' -type f 2>/dev/null | grep class | wc -l
        // find / -iname '*.class' -type f -print0 2>/dev/null | xargs -0i echo | wc -l

        def fileListFilesStream(startFrom: String = "/tmp", list: Boolean = false) = {
            // scala> fileListFilesStream("/")
            // java.lang.OutOfMemoryError: Java heap space

            import java.io.File

            def listFiles(dir: File): Option[Seq[File]] = Option(dir.listFiles)

            def listAll(dir: File): Stream[File] = {
                val children: Seq[File] = listFiles(dir).getOrElse(Array.empty[File])
                if (children.isEmpty) Stream.empty[File]
                else children.head #:: children.tail.toStream
                    .append(children.filter(_.isDirectory).flatMap(listAll))
            }

            val res = for {
                file <- listAll(new File(startFrom))
                if file.isFile && file.getName.endsWith(".class")
            } yield file.getAbsolutePath

            if (list) res foreach println

            println(s"number of '*.class' files: ${res.length}")
        }

        def fileListFilesStream2(startFrom: String = "/tmp", list: Boolean = false) = {
            // preceding version slightly refactored

            // scala> fileListFilesStream2("/")
            // number of '*.class' files: 14704

            import scala.util.{Failure, Success, Try}
            import java.io.File

            def listFiles(dir: File): Seq[File] = {
                // with links and exceptions handling
                def isLink = !dir.getAbsolutePath.equals(dir.getCanonicalPath)

                def files: Option[Seq[File]] = {
                    val lst = if (dir.isDirectory && !isLink) dir.listFiles else null
                    Option(lst)
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

            val res = listAll(new File(startFrom))
                .filter(f => f.isFile && f.getName.endsWith(".class"))
            // debug
            if (list) res foreach println
            // result
            println(s"number of '*.class' files: ${res.length}")
        }

        def fileListFilesStreamReactive(startFrom: String = "/tmp", list: Boolean = false) = {
            // scala> fileListFilesStreamReactive("/")
            // java.lang.OutOfMemoryError: GC overhead limit exceeded

            // http://reactivex.io/rxscala/
            import rx.lang.scala.Observable
            import scala.util.{Failure, Success, Try}
            import java.io.File

            def listFiles(dir: File): Option[Seq[File]] = Option(dir.listFiles)

            def listAll(dir: File): Stream[File] = {
                // possible exceptions processing
                val lst: Try[Seq[File]] = Try { listFiles(dir).getOrElse(Array.empty[File]) }
                val children: Seq[File] = lst match {
                    case Failure(err) => println(err); Array.empty[File]
                    case Success(seq) => seq
                }

                if (children.isEmpty) Stream.empty[File]
                else children.head #:: children.tail.toStream
                    .append(children.withFilter(_.isDirectory).flatMap(listAll))
            }

            def files(dir: File): Observable[File] = Observable[File](subscriber => {
                    val list = listAll(dir)
                    for (file <- list if !subscriber.isUnsubscribed) {
                        subscriber.onNext(file) // callbacks
                    }
                    subscriber.onCompleted()
            })

            val allfiles = files(new File(startFrom))
            val classes = allfiles.filter(f => f.isFile && f.getName.endsWith(".class"))
            // debug callback
            if (list) classes.subscribe(f => println(f))
            // result
            val count = classes.length // .count(file => true) // classes.foldLeft(0) { case (cnt, file) => cnt+1 }
            // result callback
            count.subscribe(c => println(s"observer, # of .class files: $c"))

            // for extra processing?
            classes
        }

        def jnfDirectoryStreamReactive(startFrom: String = "/tmp", list: Boolean = false) = {
            // java.nio.file DirectoryStream + RX

            // scala> jnfDirectoryStreamReactive("/")
            // # of class files: 14704

            import java.nio.{file => jnf}
            import rx.lang.scala.Observable

            def listFiles(dir: jnf.Path): Observable[jnf.Path] = Observable[jnf.Path](subscriber => {
                    val pstream = jnf.Files.newDirectoryStream(dir)
                    val iter = pstream.iterator
                    while (iter.hasNext && !subscriber.isUnsubscribed) {
                        subscriber.onNext(iter.next)
                    }
                    subscriber.onCompleted()
                    pstream.close()
            }).onErrorResumeNext(_ => Observable.empty)

            def listAll(dir: jnf.Path): Observable[jnf.Path] = {
                val lst = listFiles(dir)
                val files = lst.filter(jnf.Files.isRegularFile(_))
                val dirs = lst.filter(jnf.Files.isDirectory(_, jnf.LinkOption.NOFOLLOW_LINKS))
                val children = dirs.flatMap(listAll)
                files ++ dirs ++ children
            }

            val dir = jnf.FileSystems.getDefault.getPath(startFrom)
            val classFiles = listAll(dir)
                .filter(p => jnf.Files.isRegularFile(p) && p.toString.endsWith(".class"))

            if (list) classFiles subscribe { p => println(p.toString) }

            val count = classFiles.length.subscribe(cnt => println(s"# of class files: $cnt"))

            classFiles
        }

        def jnfWalkFileTreeReactive(startFrom: String = "/tmp", list: Boolean = false) = {
            // scala> jnfWalkFileTreeReactive("/")
            //visitFileFailed: java.nio.file.AccessDeniedException: /.pulse
            //visitFileFailed: java.nio.file.AccessDeniedException: /proc/tty/driver
            //visitFileFailed: java.nio.file.AccessDeniedException: /proc/1/task/1/fd
            // ...
            // # of class files: 14704

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

            val startDir = new File(startFrom).toPath
            val files = listFiles(startDir)
            val classfiles = files.filter(p => p.toString.endsWith(".class"))
            val count = classfiles.length
            // debug
            if (list) classfiles.subscribe(p => println(p))
            // result
            count.subscribe(c => println(s"# of class files: $c"))

            classfiles
        }

    }

    // 10. Expand the example in Section 9.8, “Serialization,” on page 113.
    // Construct a few Person objects,
    // make some of them friends of others,
    // and save an Array[Person] to a file.
    // Read the array back in and verify that the friend relations are intact.
    def ex10 = {
        import java.io.{FileInputStream, FileOutputStream, ObjectInputStream, ObjectOutputStream}
        import scala.collection.mutable

        @SerialVersionUID(42L) class Person(val name: String) extends Serializable {
            private val friends = mutable.ArrayBuffer.empty[String]
            def addFriend(fname: String): Unit = {
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
        require(saved.mkString == lst.mkString)
    }
}
