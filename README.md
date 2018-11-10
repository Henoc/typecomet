# typecomet: provide intuitive type handlings

## Quick example

### typeof

Provide the type of an expression.
```
<typeof type-expr> := typeof.`<scala expression>`.T
```

```scala
object Main extends App {
    type a = typeof.`java.time.LocalDateTime.MAX`.T
    val b: a = java.time.LocalDateTime.now()
    val c: Int = 1
    type e = typeof.`{val d = 2; s"c + d = ${c + d}"}`.T
    val f: e = "abc"
}
```

## Install

TODO

