import cats.effect.{ExitCode, IO, IOApp}
import config.Config
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

//    Flyway
//      .configure()
//      .dataSource(config.database.url, config.database.user, config.database.password)
//      .load()
//      .migrate

    val telegramClient= EmberClientBuilder.default[IO].build.map { client =>
      new TelegramClient(client, config.telegramToken)
    }

    telegramClient.use(_.callTelegram()).as(ExitCode.Success)
  }
}
