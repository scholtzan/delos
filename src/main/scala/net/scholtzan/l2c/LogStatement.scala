package net.scholtzan.l2c

/**
  * Created by anna on 16.04.18.
  */
case class LogStatement(
  filePath: String,
  line: Int,
  logLevel: String, // todo enum?
  library: String,
  variables: Seq[String],
  logString: String
)