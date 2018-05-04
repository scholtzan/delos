import java.net.URLClassLoader

import net.scholtzan.l2c.{L2CPlugin, LogStatement}

import scala.reflect.internal.util.BatchSourceFile
import scala.reflect.io.VirtualDirectory
import scala.tools.nsc.{Global, Settings}
import scala.tools.nsc.reporters.ConsoleReporter
import scala.tools.nsc.util.ClassPath

trait LogInspectorSpec {
  val outputPath: String

  def runCompileTests(code: String) = {
    val sources = List(new BatchSourceFile("<test>", code))

    // todo: pass output as compiler option

    val settings = new Settings
    val loader = getClass.getClassLoader.asInstanceOf[URLClassLoader]
    val entries = loader.getURLs map (_.getPath)
    val sclpath = entries find (_.endsWith("scala-compiler.jar")) map (
      _.replaceAll("scala-compiler.jar", "scala-library.jar"))
    settings.classpath.value = ClassPath.join(entries ++ sclpath: _*)
    settings.processArgumentString("-Ylog:l2c -Xprint:l2c")

    settings.outputDirs.setSingleOutput(new VirtualDirectory("(memory)", None))

    val compiler = new Global(settings, new ConsoleReporter(settings)) {
      override protected def computeInternalPhases() {
        super.computeInternalPhases
        for (phase <- new L2CPlugin(this).components)
          phasesSet += phase
      }
    }
    new compiler.Run() compileSources sources
  }

  def expected(logStatements: Seq[LogStatement]): Boolean = {
    val outFileContent = io.Source.fromFile(outputPath).getLines().mkString
    val outLogStatements = upickle.default.read[Seq[LogStatement]](outFileContent)

    println(outLogStatements)
    println(logStatements)

    outLogStatements == logStatements
  }
}
