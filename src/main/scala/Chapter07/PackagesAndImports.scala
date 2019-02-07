package Chapter07

object PackagesAndImports {
// topics:
    // packages
    // scope rules
    // chained package clauses
    // top-of-file notation
    // package objects
    // package visibility
    // imports
    // imports can be anywhere
    // renaming and hiding members
    // implicit imports

    // packages
    def packages = {
        ???
    }

    // scope rules
    def scopeRules = {
        ???
    }

    // chained package clauses
    def chainedPackageClauses = {
        ???
    }

    // top-of-file notation
    def topOfFileNotation = {
        ???
    }

    // package objects
    def packageObjects = {
        ???
    }

    // package visibility
    def packageVisibility = {
        ???
    }

    // imports
    def imports = {
        ???
    }

    // imports can be anywhere
    def importsCanBeAnywhere = {
        ???
    }

    // renaming and hiding members
    def renamingAndHidingMembers = {
        ???
    }

    // implicit imports
    def implicitImports = {
        ???
    }

}

object PackagesAndImports_Exercises {

    // 1. Write an example program to demonstrate that
    //    package com.horstmann.impatient
    // is not the same as
    //    package com
    //    package horstmann
    //    package impatient
    def ex1 = {
        ???
    }

    // 2. Write a puzzler that baffles your Scala friends, using a package com that isn’t at the top level.
    def ex2 = {
        ???
    }

    // 3. Write a package random with functions nextInt(): Int, nextDouble(): Double,
    // and setSeed(seed: Int): Unit. To generate random numbers, use the linear
    // congruential generator
    //    next = (previous × a + b) mod 2^n
    // where a = 1664525, b = 1013904223, n = 32, and the initial value of previous is seed.
    def ex3 = {
        ???
    }

    // 4. Why do you think the Scala language designers provided the package object syntax
    // instead of simply letting you add functions and variables to a package?
    def ex4 = {
        ???
    }

    // 5. What is the meaning of private[com] def giveRaise(rate: Double)? Is it
    // useful?
    def ex5 = {
        ???
    }

    // 6. Write a program that copies all elements from a Java hash map into a Scala hash map. Use
    // imports to rename both classes.
    def ex6 = {
        ???
    }

    // 7. In the preceding exercise, move all imports into the innermost scope possible.
    def ex7 = {
        ???
    }

    // 8. What is the effect of
    //    import java._
    //    import javax._
    // Is this a good idea?
    def ex8 = {
        ???
    }

    // 9. Write a program that imports the java.lang.System class, reads the user name from the
    // user.name system property, reads a password from the StdIn object, and prints a message
    // to the standard error stream if the password is not "secret". Otherwise, print a greeting to
    // the standard output stream. Do not use any other imports, and do not use any qualified names
    // (with dots).
    def ex9 = {
        ???
    }

    // 10. Apart from StringBuilder, what other members of java.lang does the scala
    // package override?
    def ex10 = {
        ???
    }
}
