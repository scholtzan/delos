package net.scholtzan.l2c


import scala.tools.nsc.plugins.{Plugin, PluginComponent}
import scala.tools.nsc.transform.{Transform, TypingTransformers}
import scala.tools.nsc.{Global, Phase}


class L2CPlugin(val global: Global) extends Plugin {
  override val name = "l2c"
  override val description = "Extracts all log statements from the code"
  override val components: List[PluginComponent] = List(new L2CPluginComponent(global))
}

class L2CPluginComponent(val global: Global) extends PluginComponent with TypingTransformers with Transform {
  override val phaseName = "l2c"
  override val runsAfter = List("typer")

  override protected def newTransformer(unit: global.CompilationUnit): global.Transformer = new L2CTransformer(unit)

  val inspectors = log.inspectors(InspectionContext(global))

  override def newPhase(prev: scala.tools.nsc.Phase): Phase = new Phase(prev) {
    override def run(): Unit = {
      // todo
      super.run()
    }
  }


  class L2CTransformer(unit: global.CompilationUnit) extends TypingTransformer(unit) {
    override def transform(tree: global.Tree): global.Tree = {
      println("Inspect tree")

      inspectors.foreach { inspector =>
        inspector.traverser.traverse(tree.asInstanceOf[inspector.ctx.global.Tree])
      }

      tree
    }
  }
}