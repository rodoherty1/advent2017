import org.slf4j.LoggerFactory

import scalaz.{-\/, \/-}
import scalaz.concurrent.Task
import scalaz.stream._

object One extends App {

  val log = LoggerFactory.getLogger(One.getClass)

  sealed trait Sum
  case class RunningSum(first: Int = 0, last: Int = 0, runningTotal: Long = 0) extends Sum
  case object Empty extends Sum


  def f(sum: Sum, i: Int): RunningSum = {
    sum match {
      case Empty => RunningSum(i, i)
      case RunningSum(first, last, runningTotal) if last == i =>
        RunningSum(first, i, runningTotal + i)
      case RunningSum(first, _, runningTotal) =>
        RunningSum(first, i, runningTotal)
    }
  }

  def h(s: Process[Task, String]): Process[Task, Char] = {
    s.flatMap(str => Process.emitAll(str))
  }

  def g(chars: Process[Task, Char]): Process[Task, Sum] = {
    chars.scan[Sum](Empty) {
      case (xxx, ch) => f(xxx, ch - '0')
    }
  }

  def runWith(str: Process[Task, String]): Long = {
    (g _ compose h)(str).runLast.attemptRun match {
      case \/-(Some(RunningSum(first, last, total))) if first == last => total + last
      case \/-(Some(RunningSum(_, _, total))) => total
      case -\/(th) => throw th
    }
  }

  def run: Long = runWith(io.linesR("one.txt"))
}

