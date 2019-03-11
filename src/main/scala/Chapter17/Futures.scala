package Chapter17

object Futures {
// topics:
    // running tasks in the future
    // waiting for results
    // the Try class
    // callbacks
    // composing future tasks
    // other future transformations
    // methods in the Future Object
    // promises
    // execution contexts

    // as long as the computations don't have side effects, you can let them run concurrently
    // and combine the results when they become available

    // Future { concurrent block }
    // you can use callbacks, but chaining them is a pain in the ass (callback hell)
    // use map/flatMap, for-expr to compose futures
    // a promise has a future whose value can be set (once)
    // fork-join pool for cpu-bound tasks

    import java.time._
    import scala.util._
    import scala.concurrent._
    import scala.concurrent.duration._
    import scala.concurrent.ExecutionContext.Implicits.global

    // running tasks in the future
    def runningTasksInTheFuture = {
        // future: an object that give you a result/failure eventually.
        // java.util.concurrent.Future interface: much more limited;
        // java CompletionStage interface more like scala future

        // execute a block of code in the future
        Future {
            // run on some thread from the pool defined in ExecutionContext
            // default ex.context: fork-join pool from import scala.concurrent.ExecutionContext.Implicits.global
            Thread.sleep(1.second.toMillis)
            println(s"this is the future at ${LocalTime.now}")
        }
        println(s"this is the present at ${LocalTime.now}")
        // this is the present at 15:01:39.659
        // this is the future at 15:01:40.684

        // in a real program use a custom exec.context
        // cpu bound vs i/o bound

        // concurrent execution for multiple futures
        Future { for (i <- 1 to 10) {print("A"); Thread.sleep(100)} }
        Future { for (i <- 1 to 10) {print("B"); Thread.sleep(100)} }
        // BABABABABABABABABA

        // future can have a result
        val f: Future[Int] = Future { Thread.sleep(1000); 42 }
        f // res2: scala.concurrent.Future[Int] = Future(<not completed>)
        //scala> f
        //res3: scala.concurrent.Future[Int] = Future(Success(42))

        // failure as a result
        val f2: Future[Int] = Future { if (LocalTime.now.getHour > 5) sys.error("too late"); 42 }
        // f2: scala.concurrent.Future[Int] = Future(<not completed>)
        //scala> f2
        //res7: scala.concurrent.Future[Int] = Future(Failure(java.lang.RuntimeException: too late))

        // stay away from side effects/shared mutable state, even threadsafe ones.
        // have each future compute a value and then combine them

    }

    // waiting for results
    def waitingForResults = {
        // check using 'isCompleted';

        // make a blocking call // better not
        val f = Future { Thread.sleep(1.second.toMillis); 42 }
        val res: Int = Await.result(f, 2.second) // blocks for 1 sec. and yield the result
        // or throw an error: java.util.concurrent.TimeoutException

        // if task throws an exception, it is rethrown in Await.result
        val f2: Future[Int] = Future { if (LocalTime.now.getHour > 5) sys.error("too late"); 42 }
        val res2: Int = Await.result(f2, 1.second) // java.lang.RuntimeException: too late

        // to avoid rethrown exception use 'ready'
        val res3: Future[Int] = Await.ready(f2, 1.second) // block, wait and return f2
        // scala> val res3 = Await.ready(f2, 1.second)
        //res3: f2.type = Future(Failure(java.lang.RuntimeException: too late))
        //
        //scala> val res3 = Await.ready(f, 1.second)
        //res3: f.type = Future(Success(42))

        res3.value // Option[scala.util.Try[Int]] = Some(Success(42))

        // Future class also has 'result' and 'ready': thread blocking methods, don't use them

        // not all exceptions are stored in the result, jvm errors and InterruptedException are allowed to propagate

    }

    // the Try class
    def theTryClass = {
        // monadic error wrapper, case classes Success[T], Failure[E <: Throwable]
        val res: Try[Int] = ???
        res match {
            case Success(v) => println(s"result $v")
            case Failure(ex) => println(s"error: ${ex.getMessage}")
        }

        res.isSuccess // Boolean
        res.isFailure

        // failed Try[T] into Try[Throwable]
        val fail: Try[Throwable] = res.failed

        // get value from try
        fail.get.getMessage

        // try to option
        res.toOption.getOrElse(-1)

        // construct try
        val res2: Try[Int] = Try("".toInt)

        val res3 = Try {
            val inp = scala.io.StdIn.readLine("enter a number:")
            inp.trim.toInt
        }

    }

    // callbacks
    def callbacks = {
        ???
    }

    // composing future tasks
    def composingFutureTasks = {
        // https://github.com/scala/scala-async
    }

    // other future transformations
    def otherFutureTransformations = {
        ???
    }

    // methods in the Future Object
    def methodsInTheFutureObject = {
        ???
    }

    // promises
    def promises = {
        ???
    }

    // execution contexts
    def executionContexts = {
        ???
    }

}

object Futures_Exercises {

    // 1. Consider the expression
    //Click here to view code image
    //for (n1 <- Future { Thread.sleep(1000) ; 2 }
    //n2 <- Future { Thread.sleep(1000); 40 })
    //println(n1 + n2)
    //How is the expression translated to map and flatMap calls? Are the two futures executed
    //concurrently or one after the other? In which thread does the call to println occur?
    def ex1 = {
        ???
    }

    // 2. Write a function doInOrder that, given two functions f: T => Future[U] and g: U
    //=> Future[V], produces a function T => Future[U] that, for a given t, eventually
    //yields g(f(t)).
    def ex2 = {
        ???
    }

    // 3. Repeat the preceding exercise for any sequence of functions of type T => Future[T].
    def ex3 = {
        ???
    }

    // 4. Write a function doTogether that, given two functions f: T => Future[U] and g: U
    //=> Future[V], produces a function T => Future[(U, V)], running the two
    //computations in parallel and, for a given t, eventually yielding (f(t), g(t)).
    def ex4 = {
        ???
    }

    // 5. Write a function that receives a sequence of futures and returns a future that eventually yields a
    //sequence of all results.
    def ex5 = {
        ???
    }

    // 6. Write a method
    //Click here to view code image
    //Future[T] repeat(action: => T, until: T => Boolean)
    //that asynchronously repeats the action until it produces a value that is accepted by the until
    //predicate, which should also run asynchronously. Test with a function that reads a password
    //from the console, and a function that simulates a validity check by sleeping for a second and
    //then checking that the password is "secret". Hint: Use recursion.
    def ex6 = {
        ???
    }

    // 7. Write a program that counts the prime numbers between 1 and n, as reported by
    //BigInt.isProbablePrime. Divide the interval into p parts, where p is the number of
    //available processors. Count the primes in each part in concurrent futures and combine the
    //results.
    def ex7 = {
        ???
    }

    // 8. Write a program that asks the user for a URL, reads the web page at that URL, and displays all
    //the hyperlinks. Use a separate Future for each of these three steps.
    def ex8 = {
        ???
    }

    // 9. Write a program that asks the user for a URL, reads the web page at that URL, finds all the
    //hyperlinks, visits each of them concurrently, and locates the Server HTTP header for each of
    //them. Finally, print a table of which servers were found how often. The futures that visit each
    //page should return the header.
    def ex9 = {
        ???
    }

    // 10. Change the preceding exercise where the futures that visit each header update a shared Java
    //ConcurrentHashMap or Scala TrieMap. This isn’t as easy as it sounds. A threadsafe data
    //structure is safe in the sense that you cannot corrupt its implementation, but you have to make
    //sure that sequences of reads and updates are atomic.
    def ex10 = {
        ???
    }

    // 11. Using futures, run four tasks that each sleep for ten seconds and then print the current time. If
    //you have a reasonably modern computer, it is very likely that it reports four available
    //processors to the JVM, and the futures should all complete at around the same time. Now repeat
    //with forty tasks. What happens? Why? Replace the execution context with a cached thread pool.
    //What happens now? (Be careful to define the futures after replacing the implicit execution
    //context.)
    def ex11 = {
        ???
    }

    // 12. Write a method that, given a URL, locates all hyperlinks, makes a promise for each of them,
    //starts a task in which it will eventually fulfill all promises, and returns a sequence of futures for
    //the promises. Why would it not be a good idea to return a sequence of promises?
    def ex12 = {
        ???
    }

    // 13. Use a promise for implementing cancellation. Given a range of big integers, split the range into
    //subranges that you concurrently search for palindromic primes. When such a prime is found, set
    //it as the value of the future. All tasks should periodically check whether the promise is
    //completed, in which case they should terminate.
    def ex13 = {
        ???
    }

}
