package net.scholtzan.l2c.log

import com.typesafe.scalalogging.Logger
import net.scholtzan.l2c.LogLevel.LogLevel
import net.scholtzan.l2c.{InspectionContext, LogLevel, LogStatement, LogStatementInspector}


class SLF4JInspector(override val ctx: InspectionContext) extends LogStatementInspector(ctx) {
  import ctx.global._

  val libraryName = "SLF4J"

  override def traverser: ctx.Traverser = {
    new ctx.Traverser {

      private def logLevel(functionName: String): Option[LogLevel] = {
        functionName.trim match {
          case "debug" => Some(LogLevel.Debug)
          case "error" => Some(LogLevel.Error)
          case "info" => Some(LogLevel.Info)
          case _ => None
        }
      }

      private def extractLogStrings(logParameter: List[ctx.global.Tree]): Seq[String] = {
        logParameter.flatMap {
          case Literal(Constant(str)) => Some(str.toString) // single string as parameter
          case Apply(Select(left, TermName("$plus")), right) =>   // concatenated string
            extractLogStrings(List(left)) ++ extractLogStrings(right)
          case _ => None
        }
      }

      private def extractVariables(logParameter: List[ctx.global.Tree]): Seq[String] = {
        logParameter.flatMap {
          case Select(_, TermName(varName)) => Some(varName.toString)
//          case Apply(Select(left, TermName(functionName)), right) =>
//            extractVariables(List(left)) ++ extractVariables(right)
          case Select(Select(_, TermName(varName)), _) =>     // todo: not working
            Some(varName.toString)
          case Apply(Select(left, _), right) =>
            extractVariables(List(left)) ++ extractVariables(right)
          case _ => None
        }
      }

      override protected def inspect(tree: ctx.global.Tree): Unit = {
        tree match {
          case Apply(Select(Select(sel, _), f), arg) if sel.isInstanceOf[Select] && sel.asInstanceOf[Select].tpe <:< typeOf[Logger] =>
            println(showRaw(arg))

            logLevel(f.asInstanceOf[TermName].localName.toString) match {
              case Some(logLevel) =>
                ctx.addLogStatement(LogStatement(
                  tree.pos.source.path,
                  tree.pos.line,
                  logLevel,
                  libraryName,
                  extractVariables(arg),
                  extractLogStrings(arg)
                ))
              case None => continue(tree)
            }
          case _ =>
            continue(tree)
        }
      }
    }
  }
}


