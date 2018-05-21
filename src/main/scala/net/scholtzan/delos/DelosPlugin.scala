package net.scholtzan.delos


import scala.tools.nsc.plugins.{Plugin, PluginComponent}
import scala.tools.nsc.transform.{Transform, TypingTransformers}
import scala.tools.nsc.{Global, Phase}
import upickle.default._
import java.nio.file.{Paths, Files}
import java.nio.charset.StandardCharsets


/** Compiler plugin for extracting all log statements from provided source code. */
class DelosPlugin(val global: Global) extends Plugin {
  override val name = "delos"
  override val description = "Extracts all log statements from the code"
  override val components: List[PluginComponent] = List[PluginComponent](DelosPluginComponent)

  /** All extracted log statements will be written to the provided path. */
  var outputPath = "/tmp/out.json"

  override def processOptions(options: List[String], error: String => Unit) {
    for (option <- options) {
      if (option.startsWith("out:")) {
        outputPath = option.substring("out:".length)
      } else {
        error("Option not understood: " + option)
      }
    }
  }

  override val optionsHelp: Option[String] = Some(
    "  -P:delos:out:path            set path of output file")


  object DelosPluginComponent extends PluginComponent with TypingTransformers with Transform {
    val global: DelosPlugin.this.global.type = DelosPlugin.this.global
    override val phaseName = "delos"
    override val runsAfter = List("typer")

    override protected def newTransformer(unit: global.CompilationUnit): global.Transformer = new DelosTransformer(unit)

    val context = InspectionContext(global)
    val inspectors = log.inspectors(context)

    override def newPhase(prev: scala.tools.nsc.Phase): Phase = new Phase(prev) {
      override def run(): Unit = {
        println("run")
        super.run()
        println(context.detectedLogStatements)
        val logJson = upickle.default.write(context.detectedLogStatements)
        Files.write(Paths.get(outputPath), logJson.getBytes(StandardCharsets.UTF_8))
      }
    }

    class DelosTransformer(unit: global.CompilationUnit) extends TypingTransformer(unit) {
      override def transform(tree: global.Tree): global.Tree = {
        println("Inspect tree")

        inspectors.foreach { inspector =>
          inspector.traverser.traverse(tree.asInstanceOf[inspector.ctx.global.Tree])
        }

        tree
      }
    }
  }
}

