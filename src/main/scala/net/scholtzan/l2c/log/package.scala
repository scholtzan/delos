package net.scholtzan.l2c

import scala.tools.nsc.Global


/**
  * Created by anna on 18.04.18.
  */
package object log {
  def inspectors(ctx: InspectionContext): Seq[LogStatementInspector] = Seq(
    new SLF4JInspector(ctx)
  )
}
