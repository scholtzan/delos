package net.scholtzan.l2c

import net.scholtzan.l2c.LogLevel.LogLevel

/**
  * Created by anna on 16.04.18.
  */
case class LogStatement(
  filePath: String,
  line: Int,
  logLevel: LogLevel,
  library: String,
  variables: Seq[String],
  logStrings: Seq[String]
)

// todo: add more
object LogLevel extends Enumeration {
  type LogLevel = Value
  val Debug, Error, Info = Value
}