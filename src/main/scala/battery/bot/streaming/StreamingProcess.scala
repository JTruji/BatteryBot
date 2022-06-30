package battery.bot
import battery.bot.core.CommandProcess
import battery.bot.telegram.TelegramClient
import battery.bot.telegram.models.TelegramUpdate
import cats.effect.IO
import fs2.Stream

import scala.concurrent.duration.DurationInt
class StreamingProcess(telegramClient: TelegramClient, commandProcess: CommandProcess) {

  def process[TelegramClient]: Stream[IO, Unit] =
    streamProcess[List[TelegramUpdate]](lastUpdateId =>
      telegramClient.telegramGetUpdate.map { result =>
        val newLastUpdateId =
          result.result.map(_.updateId).maxOption.getOrElse(lastUpdateId)
        val filteredUpdates = result.result.filter(_.updateId > lastUpdateId)
        (newLastUpdateId, filteredUpdates)
      }
    )
      .flatMap(result => Stream.emits(result))
      .evalMap(update => commandProcess.interpreter(update))

  def streamProcess[A](getUpdates: Long => IO[(Long, A)]): Stream[IO, A] =
    Stream
      .awakeDelay[IO](2.seconds)
      .evalMapAccumulate(0L) { (startPoint, _) =>
        getUpdates(startPoint)
      }
      .map(_._2)
}
