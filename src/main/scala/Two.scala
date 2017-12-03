
import java.nio.file.{Path, Paths}

import org.slf4j.LoggerFactory
import akka.stream.scaladsl.{FileIO, Keep, RunnableGraph, Sink, Source}

import scala.concurrent.Future
import scala.util.{Failure, Success}

object Two extends App {

  val log = LoggerFactory.getLogger(One.getClass)


  val length: RunnableGraph[Future[Int]] = FileIO.fromPath(Paths.get("two.txt")).toMat(Sink.fold(0) {
    case (length, s) => length + s.length
  })(Keep.right)

  length.run().onComplete {
    case Success(l) => ???
    case Failure(th) => throw th
  }

Consider broadcasting to two streams an then zipping them.  Each broadcast will either drop the first half or drop the seocnd half

}

