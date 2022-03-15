import config.Config
import pureconfig._
import pureconfig.generic.auto._

object Main {
  def main(args: Array[String]): Unit = {

  val config = ConfigSource.default.loadOrThrow[Config]
  println (config)

  }
}
