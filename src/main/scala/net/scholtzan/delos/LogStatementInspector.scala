package net.scholtzan.delos

import scala.collection.mutable.ListBuffer
import scala.tools.nsc.Global


/** Abstract class to be implemented by inspectors for specific log libraries. */
abstract class LogStatementInspector(val ctx: InspectionContext) {
  /** Traverses provided source code during compilation. */
  def traverser: ctx.Traverser
}


/** Context for the log statement inspection process. */
case class InspectionContext(global: Global) {
  /** Stores all detected log statements. */
  val detectedLogStatements = new ListBuffer[LogStatement]

  /** Store a detected log statement. */
  def addLogStatement(logStatement: LogStatement) = {
    detectedLogStatements.append(logStatement)
  }

  trait Traverser extends global.Traverser {
    import global._

    protected def inspect(tree: Tree): Unit

    protected def continue(tree: Tree) = super.traverse(tree)

    override final def traverse(tree: Tree): Unit = {
      inspect(tree)
    }
  }
}