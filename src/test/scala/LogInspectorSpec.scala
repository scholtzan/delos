import java.net.URLClassLoader

import net.scholtzan.delos.{DelosPlugin, LogStatement}

import scala.reflect.internal.util.BatchSourceFile
import scala.reflect.io.VirtualDirectory
import scala.tools.nsc.{Global, Settings}
import scala.tools.nsc.reporters.ConsoleReporter
import scala.tools.nsc.util.ClassPath

/** Trait can be implemented by Specs to test compiler plugin on provided source code. */
trait LogInspectorSpec {
  /** All results will be written to provided path. */
  val outputPath: String

  /** Runs the compiler plugin against the provided code.
    *
    * @param code  Source code to be inspected for log statements.
    */
  def runCompileTests(code: String) = {
    val sources = List(new BatchSourceFile("<test>", code))

    // todo: pass output as compiler option

    // todo: allow subset of inspectors to be executed, otherwise Specs need to handle mixed results

    val settings = new Settings
    val loader = getClass.getClassLoader.asInstanceOf[URLClassLoader]
    val entries = loader.getURLs map (_.getPath)
    val sclpath = entries find (_.endsWith("scala-compiler.jar")) map (
      _.replaceAll("scala-compiler.jar", "scala-library.jar"))
    settings.classpath.value = ClassPath.join(entries ++ sclpath: _*)
    settings.processArgumentString("-Ylog:delos -Xprint:delos")

    settings.outputDirs.setSingleOutput(new VirtualDirectory("(memory)", None))

    val compiler = new Global(settings, new ConsoleReporter(settings)) {
      override protected def computeInternalPhases() {
        super.computeInternalPhases
        for (phase <- new DelosPlugin(this).components)
          phasesSet += phase
      }
    }
    new compiler.Run() compileSources sources
  }

  /** Checks if the compiler plugin delivers the expected output.
    *
    * @param logStatements  expected output
    * @return   compiler plugin returns same results as expected output
    */
  def expected(logStatements: Seq[LogStatement]): Boolean = {
    val outFileContent = io.Source.fromFile(outputPath).getLines().mkString
    val outLogStatements = upickle.default.read[Seq[LogStatement]](outFileContent)

    println(outLogStatements)
    println(logStatements)

    outLogStatements == logStatements
  }
}
