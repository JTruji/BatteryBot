import cats.effect.IO
import cats.effect.unsafe.implicits.global
import config.Config
import database.PersistenceService
import doobie.Transactor
import doobie.util.update.Update
import org.flywaydb.core.Flyway
import pureconfig._
import pureconfig.generic.auto._

object Main {

  def main(args: Array[String]): Unit = {

    val config = ConfigSource.default.loadOrThrow[Config]
  println (config)
println(config.database)

    val ta = Transactor.fromDriverManager[IO](
      config.database.driver,     // driver classname
      config.database.url,     // connect URL (driver-specific)
      config.database.user,                  // user
      config.database.password               // password
    )

    val persistenceService = new PersistenceService(ta)
    println(persistenceService.addPrice(1,0.123).unsafeRunSync())

    Flyway
      .configure()
      .dataSource(config.database.url, config.database.user, config.database.password)
      .load()
      .migrate

  }
}
