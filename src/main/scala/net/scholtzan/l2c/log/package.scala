package net.scholtzan.l2c

package object log {
  /** Provide list of all available log file inspectors.
    *
    * @param ctx  inspection context
    * @return   list of all available log file inspectors
    */
  def inspectors(ctx: InspectionContext): Seq[LogStatementInspector] = Seq(
    new SLF4JInspector(ctx)
  )
}
