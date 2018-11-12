# typecomet: provide intuitive type handlings

## Quick example

### typeof

Provide the type of an expression.
```
<typeof type-expr> := typeof.`<scala expression>`.T
```

```scala
import typecomet.typeof

object Main extends App {
    type a = typeof.`java.time.LocalDateTime.MAX`.T
    val b: a = java.time.LocalDateTime.now()
    val c: Int = 1
    type e = typeof.`{val d = 2; s"c + d = ${c + d}"}`.T
    val f: e = "abc"
}
```

### @signature

Generate function signatures from class or function definitions.

```scala
import typecomet.signature

@signature
class Foo {
  def bar(a: Int, b: Boolean): String = s"a: $a, b: $b"
}

object Main extends App {
  type firstArgType = Foo#bar#a
  type returnType = Foo#bar#$return
  // ...
}
```

## Install

TODO

