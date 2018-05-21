import net.scholtzan.delos.{LogLevel, LogStatement}
import org.scalatest.FlatSpec

/** Tests for SLF4J log statements. */
class SLF4JInspectorSpec extends FlatSpec with LogInspectorSpec {
  override val outputPath: String = "/tmp/out.json"

  "SLF4JInspectorSpec" should "detect simple log statement" in {
    val code =
      s"""
        import com.typesafe.scalalogging.Logger
        import com.sun.crypto.provider.AESCipher
        import javax.crypto.KeyGenerator

        object XXX {
          val logger = Logger("log")
          logger.debug("Log output")
          val k = KeyGenerator.getInstance("Blowfish");
        }
      """.stripMargin

    runCompileTests(code)

    assert(expected(Seq(LogStatement(
      "<test>",
      8,
      LogLevel.Debug,
      "SLF4J",
      Seq(),
      Seq("Log output")
    ))))
  }

  "SLF4JInspectorSpec" should "detect simple log statements from different loggers" in {
    val code =
      s"""
        import com.typesafe.scalalogging.Logger
        import com.sun.crypto.provider.AESCipher
        import javax.crypto.KeyGenerator

        object XXX {
          val logger = Logger("log")
          val logger2 = Logger("log2")

          logger.debug("Log output")
          val k = KeyGenerator.getInstance("Blowfish");
          logger2.info("Info")
        }
      """.stripMargin

    runCompileTests(code)

    assert(expected(Seq(LogStatement(
      "<test>",
      10,
      LogLevel.Debug,
      "SLF4J",
      Seq(),
      Seq("Log output")
    ), LogStatement(
      "<test>",
      12,
      LogLevel.Info,
      "SLF4J",
      Seq(),
      Seq("Info")
    ))))
  }

  "SLF4JInspectorSpec" should "extract different string parameters" in {
    val code =
      s"""
        import com.typesafe.scalalogging.Logger

        object XXX {
          val logger = Logger("log")
          logger.debug("Log output")
          logger.debug("Concatenated " + "string")

          val foo = 12
          logger.debug("Foo " + foo + " Bar")
          logger.debug(s"String interpolation $${foo}")
        }
      """.stripMargin

    runCompileTests(code)

    assert(expected(Seq(LogStatement(
      "<test>",
      6,
      LogLevel.Debug,
      "SLF4J",
      Seq(),
      Seq("Log output")
    ), LogStatement(
      "<test>",
      7,
      LogLevel.Debug,
      "SLF4J",
      Seq(),
      Seq("Concatenated string")
    ), LogStatement(
      "<test>",
      10,
      LogLevel.Debug,
      "SLF4J",
      Seq("foo"),
      Seq("Foo ", " Bar")
    ), LogStatement(
      "<test>",
      11,
      LogLevel.Debug,
      "SLF4J",
      Seq(),   // todo: foo
      Seq("String interpolation {}")
    ))))
  }

  "SLF4JInspectorSpec" should "extract different variable parameters" in {
    val code =
      s"""
        import com.typesafe.scalalogging.Logger

        object XXX {
          val logger = Logger("log")
          val foo = 12
          logger.debug("Foo " + foo + " Bar")
          logger.debug(s"String interpolation $${foo}")
          logger.debug(s"String interpolation $${foo.toString()} with multiple $${logger.toString()}")
          logger.debug(foo.toString())
        }
      """.stripMargin

    runCompileTests(code)

    assert(expected(Seq(LogStatement(
      "<test>",
      7,
      LogLevel.Debug,
      "SLF4J",
      Seq("foo"),
      Seq("Foo ", " Bar")
    ), LogStatement(
      "<test>",
      8,
      LogLevel.Debug,
      "SLF4J",
      Seq(),    // todo: "foo"
      Seq("String interpolation {}")
    ), LogStatement(
      "<test>",
      9,
      LogLevel.Debug,
      "SLF4J",
      Seq(),    // todo: "foo", "logger"
      Seq("String interpolation {} with multiple {}")
    ), LogStatement(
      "<test>",
      10,
      LogLevel.Debug,
      "SLF4J",
      Seq("foo"),
      Seq()
    ))))
  }

  "SLF4JInspectorSpec" should "prevent detection of non-logging statements with same form but" in {
    val code =
      s"""
        class Logger(name: String) {
          def debug(str: String) = {
            println(str)
          }
        }

        object XXX {
          val logger = new Logger("log")
          logger.debug("ignore")
        }

      """.stripMargin

    runCompileTests(code)

    assert(expected(Seq()))
  }


  "SLF4JInspectorSpec" should "determine correct log level" in {
    val code =
      s"""
        import com.typesafe.scalalogging.Logger

        object XXX {
          val logger = Logger("log")
          logger.debug("Foo")
          logger.info("Foo")
          logger.error("Foo")
        }
      """.stripMargin

    runCompileTests(code)

    assert(expected(Seq(LogStatement(
      "<test>",
      6,
      LogLevel.Debug,
      "SLF4J",
      Seq(),
      Seq("Foo")
    ), LogStatement(
      "<test>",
      7,
      LogLevel.Info,
      "SLF4J",
      Seq(),
      Seq("Foo")
    ), LogStatement(
      "<test>",
      8,
      LogLevel.Error,
      "SLF4J",
      Seq(),
      Seq("Foo")
    ))))
  }
}
