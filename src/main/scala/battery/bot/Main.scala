package battery.bot

import battery.bot.config.Config
import battery.bot.telegram.TelegramClient
import cats.effect._
import org.http4s.ember.client.EmberClientBuilder
import pureconfig.ConfigSource
import pureconfig.generic.auto._

object Main extends IOApp {

  override def run(args: List[String]): IO[ExitCode] = {

    val config = ConfigSource.default.loadOrThrow[Config]
    println(config)
    println(config.database)

//    Flyway
//      .configure()
//      .dataSource(config.database.url, config.database.user, config.database.password)
//      .load()
//      .migrate

    val telegramClient = EmberClientBuilder.default[IO].build.map { client =>
      new TelegramClient(client, config.telegramToken)
    }

    //telegramClient.use(_.telegramGetMe.as(ExitCode.Success))
    telegramClient.use(_.telegramGetUpdate.as(ExitCode.Success))
    //telegramClient.use(_.telegramGetMyCommands.as(ExitCode.Success))
    //telegramClient.use(_.telegramDeleteMyCommands.as(ExitCode.Success))
  }
}
