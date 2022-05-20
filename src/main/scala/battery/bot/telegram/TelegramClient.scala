package battery.bot.telegram

import battery.bot.telegram.models.{TelegramBotCommand, TelegramJSON, TelegramResult}
import cats.effect.{IO, Resource}
import io.circe.Json
import org.http4s.{Method, Request, Response}
import org.http4s.circe.CirceEntityCodec._
import org.http4s.client.Client
import org.http4s.implicits._

class TelegramClient(client: Client[IO], telegramToken: String) {
  val telegramUri = uri"https://api.telegram.org" / s"bot$telegramToken"

  def telegramGetMe: IO[TelegramJSON[TelegramResult]] = {
    client
      .expect[TelegramJSON[TelegramResult]](
        telegramUri  / "getMe"
      )
      .flatTap(hj => IO(println(hj)))
  }

  def telegramGetUpdate: IO[TelegramJSON[TelegramResult]] = {
    client
      .expect[TelegramJSON[TelegramResult]](
        telegramUri / "getUpdates"
      )
      .flatTap(hj => IO(println(hj)))
  }

  def telegramGetMyCommands: IO[TelegramJSON[TelegramBotCommand]] = {
    client
      .expect[TelegramJSON[TelegramBotCommand]](
        telegramUri / "getMyCommands"
      )
      .flatTap(hj => IO(println(hj)))
  }

  def telegramDeleteMyCommands: IO[TelegramJSON[TelegramBotCommand]] = {
    client
      .expect[TelegramJSON[TelegramBotCommand]](
        telegramUri / "deleteMyCommands"
      )
      .flatTap(hj => IO(println(hj)))
  }

  def telegramSetMyCommands: IO[TelegramJSON[TelegramBotCommand]] = {
    client
      .expect[TelegramJSON[TelegramBotCommand]](
        telegramUri / "SetMyCommands"
      )
      .flatTap(hj => IO(println(hj)))
  }

  def sendMessage(chatId: Long, text: String): IO[Boolean] = {
    client
      .successful(
        Request[IO](
          method = Method.POST,
          (telegramUri / "sendMessage")
            .withQueryParam("chat_id", chatId.toString)
            .withQueryParam("text", text)
        )
      )
      .flatTap(hj => IO(println(s"sendMessage: $hj")))
  }
}
