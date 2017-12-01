import scalaz.concurrent.Task
import scalaz.stream._

object One extends App {

  object XXX {
    def empty: XXX = XXX(0, 0, 0)
  }
  case class XXX(first: Int, last: Int, acc: Int)

  def process(xxx: XXX, s: String): XXX = {
      s.toList match {
        case Nil => xxx
        case h :: t =>

      }
  }

  io.linesR("input.txt").scan[XXX](XXX.empty) {
    case (xxx, s) => process(xxx, s)
  }

}

