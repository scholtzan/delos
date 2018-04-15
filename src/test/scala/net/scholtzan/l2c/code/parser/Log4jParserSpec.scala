package net.scholtzan.l2c.code.parser

import net.scholtzan.l2c.code.{LogExtractor, LogInstance, LogInstanceInformation, LogStatementInformation}
import utest._

object Log4jParserSpec extends TestSuite {
  val allParsers = AllLogParsers
  val extractor = new LogExtractor(allParsers)
  val log4jParser = new Log4jParser

  val tests = Tests{
    'ParseLoggerInstance - {
      val code = "final static Logger logger = Logger.getLogger(classname.class);"

      val result = log4jParser.parseLogInstantiation(code)
      assert(result.isDefined)
      assert(result.get.variableName.equals("logger"))
    }
    'ParseSingleLineDebugStatement - {
      val code = "logger.debug(\"This is debug\");"

      val result = log4jParser.parseLogStatement(code)
      assert(result.isDefined)
      assert(result.get.completelyParsed)
      assert(result.get.logger.equals("logger"))
    }
  }
}
