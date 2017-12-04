import java.nio.file.Paths

import akka.stream.IOResult
import akka.stream.scaladsl.Source
import org.scalatest.{AsyncFlatSpec, BeforeAndAfterAll, Matchers}

import scala.concurrent.Future

class TwoSpec extends AsyncFlatSpec with BeforeAndAfterAll with Matchers with AkkaStreamsFixture {

  behavior of "Day One Puzzle 2"

  "Puzzle 2" should "unlock the 2nd star" in {

    val s: Source[Int, Future[IOResult]] = Two.length(Paths.get("two.txt"))

    Two.runWith(s).run() map {
      i => assert(i == 6)
    }
  }


  override def afterAll(): Unit = {
    system.terminate()
  }
}
