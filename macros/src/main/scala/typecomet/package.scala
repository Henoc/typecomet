import scala.language.dynamics
import scala.reflect.macros.whitebox
import scala.util.Try

package object typecomet {

  @macrocompat.bundle
  class SingletonMacroImpls(val c: whitebox.Context) {
    import c.universe.{Try => _, _}
    import internal.decorators._

    def typeofImpl(expr: Tree): Tree = {
      val q"${string: String}" = expr
      val tpe: Type = (for {
        parsed <- Try(c.parse(string)).toOption
        checked = c.typecheck(parsed, silent = true)
        if checked.nonEmpty
      } yield checked.tpe).getOrElse(c.abort(c.enclosingPosition, s"Fail to execute type check: $string"))
      typeCarrier(tpe)
    }

    /**
      * typeCarrier: Type -> {type T = $Type}
      */
    def typeCarrier(tpe: Type): Literal = {
      mkUnitLiteralWithSpecifiedType(tq"{ type T = $tpe }")
    }

    def mkUnitLiteralWithSpecifiedType(tree: Tree): Literal = {
      val carrier = c.typecheck(tree, mode = c.TYPEmode).tpe
      Literal(Constant(())).setType(carrier)
    }

  }

  /**
    * Provide the type of an expression.
    * {{{
    *   <typeof type-expr> := typeof.`<scala expression>`.T
    *
    *   ex.
    *   type a = typeof.`java.time.LocalDateTime.MAX`.T
    *   val b: a = java.time.LocalDateTime.now()
    *
    *   val c: Int = 1
    *   type e = typeof.`{val d = 2; s"c + d = ${c + d}"}`.T
    *   val f: e = "abc"
    * }}}
    *
    * @see [[https://github.com/milessabin/shapeless/blob/shapeless-2.3.3/core/src/main/scala/shapeless/singletons.scala literal type implementation in shapeless]]
    */
  object typeof extends Dynamic {
    def selectDynamic(expr: String): Any = macro SingletonMacroImpls.typeofImpl
  }

}
