package net.scholtzan.delos

import net.scholtzan.delos.LogLevel.LogLevel
import ujson.Js
import upickle.default.{macroRW, ReadWriter => RW}


/** Represents a log statements that was detected in the source code. */
// todo: add raw
case class LogStatement(
  filePath: String,
  line: Int,
  logLevel: LogLevel,
  library: String,
  variables: Seq[String],
  logStrings: Seq[String]
)


/** Describes the JSON serialization of `LogStatement`. */
object LogStatement{
  implicit val rw = upickle.default.readwriter[Js.Value].bimap[LogStatement](
    log => Js.Obj(
      ("filePath", Js.Str(log.filePath)),
      ("line", Js.Num(log.line)),
      ("logLevel", Js.Str(log.logLevel.toString)),
      ("library", Js.Str(log.library)),
      ("variables", log.variables),
      ("logStrings", log.logStrings)
    ),
    json => {
      new LogStatement(
        json("filePath").str,
        json("line").num.toInt,
        LogLevel.withName(json("logLevel").str),
        json("library").str,
        json("variables").arr.map(_.str),
        json("logStrings").arr.map(_.str)
      )
    }
  )
}


/** Available log levels that can be detected. */
// todo: add more log level
object LogLevel extends Enumeration {
  type LogLevel = Value
  val Debug = Value("debug")
  val Error = Value("error")
  val Info = Value("info")
}