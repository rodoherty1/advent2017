
import java.nio.file.{Path, Paths}

import akka.{Done, NotUsed}
import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, IOResult}
import org.slf4j.LoggerFactory
import akka.stream.scaladsl.{FileIO, Flow, Keep, RunnableGraph, Sink, Source}

import scala.concurrent.Future
import scala.util.{Failure, Success}

object Two extends App {

  val log = LoggerFactory.getLogger(Two.getClass)


//  val length: RunnableGraph[Future[Int]] = FileIO.fromPath(Paths.get("two.txt")).toMat(Sink.fold(0) {
//    case (length, s) => length + s.length
//  })(Keep.right)

  def length(filename: Path)(implicit mat: ActorMaterializer, sys: ActorSystem): Source[Int, Future[IOResult]] = {
    val x: Source[Int, Future[IOResult]] = FileIO.fromPath(filename).flatMapConcat { bs =>
      val ints = bs.toString().toCharArray.map(_.toInt)
      println(ints)
      Source.fromIterator(() => ints.toIterator)
    }

    val y: Source[(Int, List[Int]), Future[IOResult]] = x.fold((0, List.empty[Int])) {
      case ((length, l), elem) => (length + 1, l :+ elem)
    }

    val z: Source[Int, Future[IOResult]] = y.flatMapConcat {
      case (length, ints) => Source.single(length).concat(Source.fromIterator(() => ints.toIterator))
    }

    z
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



  // Consider broadcasting to two streams an then zipping them.  Each broadcast will either drop the first half or drop the seocnd half

//  val g = RunnableGraph.fromGraph(GraphDSL.create() { implicit builder: GraphDSL.Builder[NotUsed] =>
//    import GraphDSL.Implicits._
//    val in: Source[String, Future[IOResult]] = FileIO.fromPath(Paths.get("two.txt")).map(_.toString()).
//    val out = Sink.ignore
//
//    val bcast = builder.add(Broadcast[Int](2))
//    val merge = builder.add(Merge[Int](2))
//
//    val f1, f2, f3, f4 = Flow[Int].map(_ + 10)
//
//    in ~> f1 ~> bcast ~> f2 ~> merge ~> f3 ~> out
//    bcast ~> f4 ~> merge
//    ClosedShape
//  })
//
}

