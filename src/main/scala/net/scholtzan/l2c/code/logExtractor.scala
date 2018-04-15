package net.scholtzan.l2c.code

import net.scholtzan.l2c.code.parser.LogStatementParser
import scala.io.Source

sealed trait Statement {
  val line: Int
  val raw: String
}

sealed trait ParserResult { }

/** Represents a logging statement extracted from source code.
  *
  * @param filePath path to source code file
  * @param line line number
  */
case class LogStatement(
  line: Int,
  raw: String,
  filePath: Option[String],
  information: LogStatementInformation
) extends Statement


case class LogStatementInformation(
  logLevel: Option[String] = None,
  logger: String,
  parameterStrings: Seq[String] = Seq(),
  parameterVariables: Seq[String] = Seq(),
  completelyParsed: Boolean
) extends ParserResult

case class LogInstance(
  line: Int,
  raw: String,
  information: LogInstanceInformation
) extends Statement


case class LogInstanceInformation(
  variableName: String
) extends ParserResult


class LogExtractor(parsers: Seq[LogStatementParser]) {
  def extractFromFile(filePath: String): List[LogStatement] = {
    extract(Source.fromFile(filePath).getLines, Some(filePath))
  }

  def extractFromString(code: String): List[LogStatement] = {
    extract(code.split("\n").iterator, None)
  }

  private def extract(lines: Iterator[String], filePath: Option[String]): List[LogStatement] = {
    val parsedStatements = lines.foldLeft((List[LogStatement](), List[LogInstance]())) { (acc, current) =>
      val lineToBeParsed = logStatements._1.last match {
        case log: LogStatement if !log.information.completelyParsed => log.raw + line
        case _ => line
      }

      val loggersFound =

      val parsingResult = parsers.collectFirst {
        case parser if parser.parse(lineToBeParsed, loggers).nonEmpty => parser.parse(line, loggers)
      }

      parsingResult match {
        case None => (acc._1, acc._2 + 1)
        case Some(parsedInfo) =>
          val result: Statement = parsedInfo.get match {
            case info: LogInstanceInformation =>
              LogInstance (
                line = acc._2,
                raw = lineToBeParsed,
                information = info
              )
            case info: LogStatementInformation =>
              LogStatement (
                line = acc._2,
                raw = lineToBeParsed,
                filePath = filePath,
                information = info
              )
          }
          (acc._1 :+ result, acc._2 + 1)
      }
    }

    val logInstances = parsedStatements._1.flatMap {
      case s: LogInstance => Some(s)
      case _ => None
    }

    parsedStatements._1.flatMap {
      case s: LogStatement if logInstances.exists(_.information.variableName == s.information.logger) => Some(s)
      case _ => None
    }
  }
}
