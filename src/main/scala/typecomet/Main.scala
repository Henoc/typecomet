package typecomet

import java.time.LocalDateTime

object Main extends App {
  type a = String
  type b = typecond.`a '=:= String '? Int ': Boolean`.T
  val c: b = 100
  println(c)
}
