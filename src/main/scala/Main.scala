
import battery.bot.database.PersistenceService
import cats.effect.IO
import cats.effect.unsafe.implicits.global
import cats.effect.{ExitCode, IO, IOApp}
import doobie.Transactor
import doobie.util.update.Update
import org.flywaydb.core.Flyway
import pureconfig._
import pureconfig.generic.auto._
import org.http4s.ember.client._
import org.http4s.client._

object Main extends IOApp{

  override def run(args: List[String]): IO[ExitCode] = {

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

    val telegramClient= EmberClientBuilder.default[IO].build.map { client =>
      new TelegramClient(client, config.telegramToken, config.chatId)
    }

    telegramClient.use(_.callTelegram()).as(ExitCode.Success)
  }
}