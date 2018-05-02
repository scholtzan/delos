import java.net.URLClassLoader

import net.scholtzan.l2c.L2CPlugin

import scala.tools.nsc.{Global, Settings}
import scala.tools.nsc.io.VirtualDirectory
import scala.tools.nsc.reporters.ConsoleReporter
import scala.tools.nsc.util.{BatchSourceFile, ClassPath}
import org.scalatest.FlatSpec


class SLF4JInspectorSpec  extends FlatSpec {
  "SLF4JInspectorSpec" should "test" in {
    println("testing")
    // prepare the code you want to compile
    val code =
      s"""
        import com.typesafe.scalalogging.Logger
        import com.sun.crypto.provider.AESCipher
        import javax.crypto.KeyGenerator

        object XXX {
          def funct(a: Int) = a.toString()

          val logger = Logger("log")
          val logger2 = Logger("log2")
          val foo = 123
          logger.debug(s"test $${foo.toString()}")
          val k = KeyGenerator.getInstance("Blowfish");

        }
      """.stripMargin
    val sources = List(new BatchSourceFile("<test>", code))

    val settings = new Settings
    val loader = getClass.getClassLoader.asInstanceOf[URLClassLoader]
    val entries = loader.getURLs map (_.getPath)
    // annoyingly, the Scala library is not in our classpath, so we have to add it manually
    val sclpath = entries find (_.endsWith("scala-compiler.jar")) map (
      _.replaceAll("scala-compiler.jar", "scala-library.jar"))
    settings.classpath.value = ClassPath.join((entries ++ sclpath): _*)
    settings.processArgumentString("-Ylog:l2c -Xprint:l2c")

    // save class files to a virtual directory in memory
    settings.outputDirs.setSingleOutput(new VirtualDirectory("(memory)", None))

    val compiler = new Global(settings, new ConsoleReporter(settings)) {
      override protected def computeInternalPhases() {
        super.computeInternalPhases
        for (phase <- new L2CPlugin(this).components)
          phasesSet += phase
      }
    }
    new compiler.Run() compileSources (sources)

    assert(true)
  }
}
