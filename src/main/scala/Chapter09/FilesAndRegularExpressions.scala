package Chapter09

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
        ???
    }

    // reading characters
    def readingCharacters = {
        ???
    }

    // reading tokens and numbers
    def readingTokensAndNumbers = {
        ???
    }

    // reading from URLs and other sources
    def readingFromURLsAndOtherSources = {
        ???
    }

    // reading binary files
    def readingBinaryFiles = {
        ???
    }

    // writing text files
    def writingTextFiles = {
        ???
    }

    // visiting directories
    def visitingDirectories = {
        ???
    }

    // serialization
    def serialization = {
        ???
    }

    // process control
    def processControl = {
        ???
    }

    // regular expressions
    def regularExpressions = {
        ???
    }

    // regular expression groups
    def regularExpressionGroups = {
        ???
    }

}

object FilesAndRegularExpressions_Exercises {

    // 1. Write a Scala code snippet that reverses the lines in a file
    // (making the last line the first one, and so on).
    def ex1 = {
        ???
    }

    // 2. Write a Scala program that reads a file with tabs, replaces each tab with spaces so that tab
    // stops are at n-column boundaries, and writes the result to the same file.
    def ex2 = {
        ???
    }

    // 3. Write a Scala code snippet that reads a file and prints all words with more than 12 characters
    // to the console. Extra credit if you can do this in a single line.
    def ex3 = {
        ???
    }

    // 4. Write a Scala program that reads a text file containing only floating-point numbers. Print the
    // sum, average, maximum, and minimum of the numbers in the file.
    def ex4 = {
        ???
    }

    // 5. Write a Scala program that writes the powers of 2 and their reciprocals to a file, with the
    // exponent ranging from 0 to 20. Line up the columns:
    //
    //    1         1
    //    2         0.5
    //    4         0.25
    //    ...       ...
    def ex5 = {
        ???
    }

    // 6. Make a regular expression searching for quoted strings "like this, maybe with \"
    // or \\" in a Java or C++ program. Write a Scala program that prints out all such strings in a
    // source file.
    def ex6 = {
        ???
    }

    // 7. Write a Scala program that reads a text file and prints all tokens in the file that are not floating-
    // point numbers. Use a regular expression.
    def ex7 = {
        ???
    }

    // 8. Write a Scala program that prints the src attributes of all img tags of a web page. Use regular
    // expressions and groups.
    def ex8 = {
        ???
    }

    // 9. Write a Scala program that counts how many files with .class extension are in a given
    // directory and its subdirectories.
    def ex9 = {
        ???
    }

    // 10. Expand the example in Section 9.8, “Serialization,” on page 113. Construct a few Person
    // objects, make some of them friends of others, and save an Array[Person] to a file. Read
    // the array back in and verify that the friend relations are intact.
    def ex10 = {
        ???
    }
}
