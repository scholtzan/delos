package net.scholtzan.l2c.code.parser

import fastparse.all._

class Log4jParser extends LogStatementParser {
  // Parse log4j logger instantiations eg. `Logger logger = Logger.getLogger(MyClass.class);`
//  var logInstanceParser = P(AnyElem.?.rep ~ "Logger" ~ NoTrace(" ".rep) ~ AnyChar.rep.! ~ NoTrace(" ".rep).? ~ "=" ~
//    NoTrace(" ".rep).? ~ "Logger.getLogger(" ~ AnyElem.rep)

  var WordSeperator = P(NoTrace(" ".rep) | "." | "(" | ")")
  var AnyWord = P(WordSeperator.? ~ (!" " ~ AnyChar).rep ~ WordSeperator.?)

  var logInstanceParser = P((!"Logger" ~ AnyWord).rep ~ "Logger" ~ NoTrace(" ".rep) ~ (!" " ~ AnyChar).rep.! ~ NoTrace(" ".rep).? ~ "=" ~
      NoTrace(" ".rep).? ~ "Logger.getLogger(" ~ AnyElem.rep)

  // Parse log4j log call eg. ``
  def logStatementStartParser(loggers: Seq[String]) = P((!StringIn(loggers: _*) ~ AnyWord).rep ~ AnyChar.rep.! ~ "." ~
    ("info" | "debug" | "error" | "warn" | "fatal") ~ ("(" ~ AnyChar.rep).! )

  // todo
  var logStatementStringParameterParser = P("(" ~ (("\"" ~ AnyChar.rep.! ~ "\"") | AnyChar.rep).map(_.toString).rep ~ ")" ~ End )

  // todo
  val variableChars = P(CharIn('a' to 'z') | CharIn('A' to 'Z') | "_" | ".")
  var logStatementVariableParameterParser = P("(" ~ (("\"" ~ AnyChar.rep ~ "\"") | variableChars.rep.! |
    "(" ~ AnyChar.rep ~ ")" | AnyChar.rep).map(_.toString).rep ~ ")" ~ End)
}
