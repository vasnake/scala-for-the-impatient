package Chapter01

/**
  * see details in tests
  */
object TheBasics {
// topics:
    // REPL
    // val, var
    // common types: Int, String, StringOps, etc
    // arithmetic and operator overloading
    // more about calling methods
    // the apply method
    // scaladoc

    def ex2() = {
        import scala.math._
        3 - pow(sqrt(3), 2)
    }

    def ex4() = {
        "crazy" * 3 // crazycrazycrazy // StringLike
    }

    def ex5() = {
        (10 max 2) == 10.max(2) // 10 // RichInt
        10 == scala.math.max(10, 2)
    }

    def ex6() = {
        BigInt(2).pow(1024) == (BigInt(2) pow 1024)
    }

    def ex7() = {
        import scala.util.Random
        import scala.math.BigInt.probablePrime
        probablePrime(100, Random)
    }

    def ex8() = {
        // make a random string
        BigInt(256, scala.util.Random) toString 36
        // 2xtwx02ev2paxl9eh0jsukacw9julevi82on02esxpxrw4fusn
    }

    def ex9() = {
        "first char"(0) == "first".head
        "last char".last == 'r'
    }

    def ex10() = {
        "crazy".take(3) == "cra" // first 3 elems
        "crazy".drop(3) == "zy" // except first 3
        "crazy".takeRight(3) == "azy" // 3 from the end
        "crazy".dropRight(3) == "cr" // except 3 from the end
    }
}
