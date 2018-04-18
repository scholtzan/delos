package net.scholtzan.l2c.log


import com.typesafe.scalalogging.Logger
import net.scholtzan.l2c.{InspectionContext, LogStatement, LogStatementInspector}

import scala.reflect.runtime.universe._
import scala.tools.nsc.Global
import com.sun.crypto.provider._
import com.sun.crypto.provider.BlowfishCipher

class SLF4JInspector(override val ctx: InspectionContext) extends LogStatementInspector(ctx) {

  import ctx.global._

  override def traverser: ctx.Traverser = {
    new ctx.Traverser {
      override protected def inspect(tree: ctx.global.Tree): Unit = {
        println("SLF4J Inspect")

        println(showRaw(tree))

        tree match {
          case Apply(Select(Select(sel, _), f), arg) =>
            println("yes")
            val s = sel.asInstanceOf[Select]
            println(s.tpe <:< typeOf[Logger])
//            val tt = iden.asInstanceOf[TypeName]
//            println(iden.tpe <:< typeOf[Logger])
//            val ff = functionName.asInstanceOf[TermName]
//            println(ff.companionName)

            val fn = f.asInstanceOf[TermName]
            println(fn.localName)
          //        Some(LogStatement("test", 1, "test", "test", Seq(), "test"))
          case _ =>

            continue(tree)
          //        None
        }
      }
    }
  }
//
//  def traverser = (tree: Tree) => {
//    println("SLF4J Inspect")
//
//    tree match {
//      case Expr(Apply(Select(Select(termName, loggerName), functionName), parameters)) =>
//        val tt = termName.asInstanceOf[Ident]
//        println(tt.name)
//      //        Some(LogStatement("test", 1, "test", "test", Seq(), "test"))
//      case _ =>
//        println("no")
//        showRaw(tree)
//        continue(tree)
//      //        None
//    }
//  }
}


