import com.typesafe.scalalogging.Logger
import scala.reflect.runtime.universe._

//val logger = Logger("log")

val e = reify {
  import com.typesafe.scalalogging.Logger
  val logger = Logger("log")
  logger.debug("test")
}


e match {
  case Expr(Apply(Select(Select(termName, loggerName), functionName), parameters)) =>
    val tt = termName.asInstanceOf[Ident]
    println(tt.name)
  case _ => println("no")
}

showRaw(e)
