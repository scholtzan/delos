package net.scholtzan.l2c.code.parser

import fastparse.all._
import net.scholtzan.l2c.code._

/** Parses source code files to extract logging statements. */
trait LogStatementParser {
  var logInstanceParser: Parser[String]
  def logStatementStartParser(loggers: Seq[String]): Parser[(String, String)]
  var logStatementVariableParameterParser: Parser[Seq[String]]
  var logStatementStringParameterParser: Parser[Seq[String]]

  def parseLogInstantiation(line: String): Option[LogInstanceInformation] = {
    logInstanceParser.parse(line) match {
      case Parsed.Success(logger, _) =>
        Some(LogInstanceInformation(
          variableName = logger
        ))
      case _ => None
    }
  }

  // todo add parser for log level
  def parseLogStatement(line: String, loggers: Seq[String]): Option[LogStatementInformation] = {
    logStatementStartParser(loggers).parse(line) match {
      case Parsed.Success((logger, parameter), _) =>
        logStatementStringParameterParser.parse(parameter) match {
          case Parsed.Success(stringParameter, _) =>
            logStatementVariableParameterParser.parse(parameter) match {
              case Parsed.Success(variableParameter, _) =>
                Some(LogStatementInformation(
                  logger = logger,
                  completelyParsed = true,
                  parameterStrings = stringParameter,
                  parameterVariables = variableParameter
                ))
              case _ =>
                Some(LogStatementInformation(
                  logger = logger,
                  completelyParsed = false
                ))
            }
          case _ =>
            Some(LogStatementInformation(
              logger = logger,
              completelyParsed = false
            ))
        }
      case _ => None
    }
  }

  /** Parses text for logging statements. */
  def parse(line: String, loggers: Seq[String]): Option[ParserResult] = {
    if (parseLogInstantiation(line).isDefined) {
      parseLogInstantiation(line)
    } else if (parseLogStatement(line, loggers).isDefined) {
      parseLogStatement(line, loggers)
    } else {
      None
    }
  }
}
