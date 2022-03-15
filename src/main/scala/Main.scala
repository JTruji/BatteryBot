import config.Config
import org.flywaydb.core.Flyway
import pureconfig._
import pureconfig.generic.auto._

object Main {

  def main(args: Array[String]): Unit = {

    val config = ConfigSource.default.loadOrThrow[Config]
  println (config)
println(config.database)
    Flyway
      .configure()
      .dataSource(config.database.url, config.database.user, config.database.password)
      .load()
      .migrate

  }
}
