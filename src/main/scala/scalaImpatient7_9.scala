package scala.forthe.impatient.ch7
/*
Scala for the Impatient
chapter 7. Packages and Imports
exercise 9.

Write a program that imports the java.lang.System class,
reads the user name from the user.name system property,
reads a password from the Console object,
and prints a message to the standard error stream if the password is not "secret".
Otherwise, print a greeting to the standard output stream.
Do not use any other imports, and do not use any qualified names (with dots)
 */

object ch7ex9 {
    import java.lang.{System => jls}

    val uname = jls.getProperty("user.name", "John/Jane Doe")
    println(s"your name is $uname and your password is (it's a question):")
    val pwd = jls.console().readPassword().mkString("")

    if (pwd == "secret") jls.out.print(s"Hello $uname, welcome back!")
    else jls.err.print(s"$uname, you should run away now.")
}

object ch7ex9v2 extends App {
    import java.lang.System._

    val uname = getProperty("user.name", "John/Jane Doe")
    println(s"your name is $uname and your password is (it's a question):")
    val pwd = console().readPassword().mkString("")

    if (pwd == "secret") out.print(s"Hello $uname, welcome back!")
    else err.print(s"$uname, you should run away now.")
}
