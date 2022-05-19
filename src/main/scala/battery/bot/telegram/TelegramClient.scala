package battery.bot.telegram

import battery.bot.telegram.models.{TelegramBotCommand, TelegramJSON, TelegramResult}
import cats.effect.{IO, Resource}
import io.circe.Json
import org.http4s.{Method, Request, Response}
import org.http4s.circe.CirceEntityCodec._
import org.http4s.client.Client
import org.http4s.implicits._

class TelegramClient(client: Client[IO], telegramToken: String) {
  val telegramUri = uri"https://api.telegram.org"

  def telegramGetMe: IO[TelegramJSON[TelegramResult]] = {
    client
      .expect[TelegramJSON[TelegramResult]](
        telegramUri / s"bot$telegramToken" / "getMe"
      )
      .flatTap(hj => IO(println(hj)))
  }

  def telegramGetUpdate: IO[TelegramJSON[TelegramResult]] = {
    client
      .expect[TelegramJSON[TelegramResult]](
        telegramUri / s"bot$telegramToken" / "getUpdates"
      )
      .flatTap(hj => IO(println(hj)))
  }

  def telegramGetMyCommands: IO[TelegramJSON[TelegramBotCommand]] = {
    client
      .expect[TelegramJSON[TelegramBotCommand]](
        telegramUri / s"bot$telegramToken" / "getMyCommands"
      )
      .flatTap(hj => IO(println(hj)))
  }

  def telegramDeleteMyCommands: IO[TelegramJSON[TelegramBotCommand]] = {
    client
      .expect[TelegramJSON[TelegramBotCommand]](
        telegramUri / s"bot$telegramToken" / "deleteMyCommands"
      )
      .flatTap(hj => IO(println(hj)))
  }

  def telegramSetMyCommands: IO[TelegramJSON[TelegramBotCommand]] = {
    client
      .expect[TelegramJSON[TelegramBotCommand]](
        telegramUri / s"bot$telegramToken" / "SetMyCommands"
      )
      .flatTap(hj => IO(println(hj)))
  }

  def sendMessage(chatId: Long, text: String): IO[Boolean] = {
    client
      .successful(
        Request[IO](
          method = Method.POST,
          (telegramUri / s"bot$telegramToken" / "sendMessage")
            .withQueryParam("chat_id", chatId.toString)
            .withQueryParam("text", text)
        )
      )
      .flatTap(hj => IO(println(s"sendMessage: $hj")))
  }
}
