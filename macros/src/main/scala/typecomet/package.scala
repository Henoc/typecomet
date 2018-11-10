import scala.language.dynamics
import scala.reflect.macros.whitebox
import scala.util.Try

/**
  * Package that defines typecomet functions. Implementation hint is provided by shapeless and typescript.
  *
  * @see [[https://github.com/milessabin/shapeless/blob/shapeless-2.3.3/core/src/main/scala/shapeless/singletons.scala literal type implementation in shapeless]]
  * @see [[https://www.typescriptlang.org/docs/handbook/release-notes/typescript-2-8.html typescript(conditional types)]]
  */
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

    def typecondImpl(expr: Tree): Tree = {
      val q"${string: String}" = expr
      val pattern = """(?x) (.*) ('<:<|'>:>|'=:=|'!=:=) (.*) '\? (.*) ': (.*)""".r
      val returnType = string match {
        case pattern(leftType, operator, rightType, thenType, elseType) =>
          val l :: r :: t :: e :: Nil = List(leftType, rightType, thenType, elseType).map(typeStringParse)
          operator match {
            case "'<:<" => if (l.<:<(r)) t else e
            case "'>:>" => if (r.<:<(l)) t else e
            case "'=:=" => if (l.=:=(r)) t else e
            case "'!=:=" => if (!l.=:=(r)) t else e
            case _ => c.abort(c.enclosingPosition, s"Fail to parse the typecond expr. undefined operator: $operator")
          }
        case _ =>
          c.abort(c.enclosingPosition, s"Fail to parse the typecond expr: $string")
      }
      typeCarrier(returnType)
    }

    def typeStringParse(typeString: String): Type = {
      val blockExpr = s"{type T = $typeString; var ret: T = _; ret}"
      (for {
        parsed <- Try(c.parse(blockExpr)).toOption
        checked = c.typecheck(parsed, silent = true)
        if checked.nonEmpty
      } yield checked.tpe).getOrElse(c.abort(c.enclosingPosition, s"Fail to execute type check: $typeString"))
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
    */
  object typeof extends Dynamic {
    def selectDynamic(expr: String): Any = macro SingletonMacroImpls.typeofImpl
  }

  /**
    * Provide the conditional type.
    * {{{
    *   <typecond type-expr> := typecond.`<typecond dsl>`.T
    *   <typecond dsl> := <left type-expr> <op> <right type-expr> '? <then type-expr> ': <else type-expr>
    *   <op> := '=:= | '!=:= | '<:< | '>:>
    *
    *   type a = String
    *   type b = typecond.`a '=:= String '? Int ': Boolean`.T
    *   val c: b = 100
    * }}}
    */
  object typecond extends Dynamic {
    def selectDynamic(expr: String): Any = macro SingletonMacroImpls.typecondImpl
  }

}
