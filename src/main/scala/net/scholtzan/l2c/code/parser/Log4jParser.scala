package net.scholtzan.l2c.code.parser

import fastparse.all._

class Log4jParser extends LogStatementParser {
  // Parse log4j logger instantiations eg. `Logger logger = Logger.getLogger(MyClass.class);`
  var logInstanceParser = P(AnyChar.?.rep ~ "Logger" ~ NoTrace(" ".rep) ~ AnyChar.rep.! ~ "=" ~
    NoTrace(" ".rep) ~ "Logger.getLogger(" ~ AnyChar.rep)

  // Parse log4j log call eg. ``
  var logStatementStartParser = P(AnyChar.?.rep ~ NoTrace(" ".rep) ~ AnyChar.rep.! ~ "." ~
    ("info" | "debug" | "error" | "warn" | "fatal") ~ ("(" ~ AnyChar.rep).! )

  // todo
  var logStatementStringParameterParser = P("(" ~ (("\"" ~ AnyChar.rep.! ~ "\"") | AnyChar.rep).rep ~ ")" )

  // todo
  val variableChars = P(CharIn('a' to 'z') | CharIn('A' to 'Z') | "_" | ".")
  var logStatementVariableParameterParser = P("(" ~ (("\"" ~ AnyChar.rep ~ "\"") | variableChars.rep.! |
    "(" ~ AnyChar.rep ~ ")" | AnyChar.rep).rep ~ ")")
}
