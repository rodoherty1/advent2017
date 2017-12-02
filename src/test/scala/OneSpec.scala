import One.RunningSum
import org.scalatest.{FlatSpec, Matchers}

import scalaz.stream.Process

class OneSpec extends FlatSpec with Matchers {

  behavior of "Day One"

  "Puzzle 1" should "unlock the 1st star" in {
    One.runWith(Process("1122")) shouldBe 3
  }
}
