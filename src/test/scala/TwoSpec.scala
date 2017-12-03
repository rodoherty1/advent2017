import java.nio.file.Paths

import akka.Done
import akka.stream.IOResult
import akka.stream.scaladsl.{Sink, Source}
import org.scalatest.{AsyncFlatSpec, BeforeAndAfterAll, FlatSpec, Matchers}

import scala.concurrent.Future
import scalaz.stream.Process

class TwoSpec extends AsyncFlatSpec with BeforeAndAfterAll with Matchers with AkkaStreamsFixture {

  behavior of "Day One Puzzle 2"

  "Puzzle 2" should "unlock the 2nd star" in {

    val s: Source[Int, Future[IOResult]] = Two.length(Paths.get("two1.txt"))

    Two.runWith(s).run() map {
      i => assert(i == 6)
    }
  }


  override def afterAll(): Unit = {
    system.terminate()
  }
}
