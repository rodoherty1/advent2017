
import java.nio.file.Path

import akka.actor.ActorSystem
import akka.stream.scaladsl.{FileIO, Flow, Keep, RunnableGraph, Sink, Source}
import akka.stream.{ActorMaterializer, IOResult}
import org.slf4j.LoggerFactory

import scala.concurrent.Future

object Two extends App {

  val log = LoggerFactory.getLogger(Two.getClass)

  def length(filename: Path)(implicit mat: ActorMaterializer, sys: ActorSystem): Source[Int, Future[IOResult]] = {
    val x: Source[Int, Future[IOResult]] = FileIO.fromPath(filename).flatMapConcat { bs =>
      val ints = bs.utf8String.map(_ - '0')
      Source.fromIterator(() => ints.toIterator)
    }

    val y: Source[(Int, List[Int]), Future[IOResult]] = x.fold((0, List.empty[Int])) {
      case ((length, l), elem) => (length + 1, l :+ elem)
    }

    y.flatMapConcat {
      case (length, ints) => Source.single(length).concat(Source.fromIterator(() => ints.toIterator))
    }
  }

  def flow(ab: (Int, Int)): Int = {
    println(ab)
    ab match {
      case (l, r) if l == r => l + r
      case _ => 0
    }
  }

  def runWith(src: Source[Int, Future[IOResult]])(implicit mat: ActorMaterializer, sys: ActorSystem): RunnableGraph[Future[Int]] = {
    val s: Source[Int, Future[IOResult]] = src.take(1)

    val u  = s flatMapConcat { length =>
      println(length)
      val left = src.drop(1).take(length/2)
      val right = src.drop(1).drop(length/2)

      (left zip right) via Flow.fromFunction(flow)
    }

    val sink = Sink.fold[Int, Int](0) {
      case (z, b) => z + b
    }

    u.toMat(sink)(Keep.right)
  }
}

