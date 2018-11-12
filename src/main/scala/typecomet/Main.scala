package typecomet

import java.time.LocalDateTime
import java.util.regex.Pattern
import scala.reflect.runtime.universe._

@signature
class Sample {
  def foo(bar: Int): String = bar.toString
}

object Main extends App {
  type fooArg1 = Sample#foo#bar
  val a: fooArg1 = 100
  println(a)
}
