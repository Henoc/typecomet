package typecomet

import java.time.LocalDateTime

object Main extends App {
  val a: Int = 1
  type b = typeof.`{val b = 2; s"a + b = ${a + b}"}`.T
  val c: b = "b is string type!"
  println(c)
}
