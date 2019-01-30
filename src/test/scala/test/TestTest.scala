package test

import org.scalatest.{FlatSpec, Matchers}

class TestTest extends FlatSpec with Matchers {
    it should "check toString" in {
        assert(true === (false.toString == "false"))
    }
}
