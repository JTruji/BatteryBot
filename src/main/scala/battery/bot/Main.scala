package battery.bot

import battery.bot.config.Config
import battery.bot.core.{CommandProcess, ScraperProcess}
import battery.bot.database.PersistenceService
import battery.bot.telegram.TelegramClient
import cats.effect._
import doobie.util.transactor.Transactor
import org.flywaydb.core.Flyway
import org.http4s.ember.client.EmberClientBuilder
import pureconfig.ConfigSource
import pureconfig.generic.auto._

object Main extends IOApp {

  override def run(args: List[String]): IO[ExitCode] = {

    val config = ConfigSource.default.loadOrThrow[Config]

    val ta = Transactor.fromDriverManager[IO](
      config.database.driver,  // driver classname
      config.database.url,     // connect URL (driver-specific)
      config.database.user,    // user
      config.database.password // password
    )

    val persistenceService = new PersistenceService(ta)

    Flyway
      .configure()
      .dataSource(config.database.url, config.database.user, config.database.password)
      .load()
      .migrate

    val telegramClient = EmberClientBuilder.default[IO].build.map { client =>
      new TelegramClient(client, config.telegramToken)
    }

    val scraperProcess = new ScraperProcess(persistenceService)

    def process(telegramClient: TelegramClient) = {
      val commandProcess   = new CommandProcess(persistenceService, telegramClient)
      val streamingProcess = new StreamingProcess(telegramClient, commandProcess, persistenceService)
      for {
        _ <- scraperProcess.getNewPrices
        _ <- streamingProcess.process.compile.drain
      } yield ExitCode.Success
    }

    telegramClient.use(process)
  }

}
