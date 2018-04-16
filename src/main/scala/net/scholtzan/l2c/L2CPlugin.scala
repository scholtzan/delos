package net.scholtzan.l2c


import scala.tools.nsc.plugins.{Plugin, PluginComponent}
import scala.tools.nsc.{Global, Phase}


class L2CPlugin(val global: Global) extends Plugin {
  override val name = "l2c"
  override val description = "Extracts all log statements from the code"
  override val components: List[PluginComponent] = List(new L2CPluginComponent(global))
}

class L2CPluginComponent(val global: Global) extends PluginComponent {
  override val phaseName = "l2c"
  override val runsAfter = List("typer")

  override def newPhase(prev: Phase): Phase = new StdPhase(prev) {
    override def apply(unit: global.CompilationUnit): Unit = {
      global.reporter.echo("implement me ")
    }
  }
}