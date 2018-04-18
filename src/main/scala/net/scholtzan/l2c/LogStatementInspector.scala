package net.scholtzan.l2c

import scala.tools.nsc.Global

abstract class LogStatementInspector(val ctx: InspectionContext) {
  def traverser: ctx.Traverser
}


case class InspectionContext(global: Global) {
  trait Traverser extends global.Traverser {

    import global._

    protected def inspect(tree: Tree): Unit

    protected def continue(tree: Tree) = super.traverse(tree)

    override final def traverse(tree: Tree): Unit = {
      inspect(tree)
    }
  }
}