package battery.bot.telegram

import battery.bot.telegram.models.{TelegramBotCommand, TelegramResult, TelegramUpdate}
import cats.effect.IO
import org.http4s.circe.CirceEntityCodec._
import org.http4s.client.Client
import org.http4s.implicits._
import org.http4s.{Method, Request, Uri}

class TelegramClient(client: Client[IO], telegramToken: String) {
  val telegramUri: Uri = uri"https://api.telegram.org" / s"bot$telegramToken"

  def telegramGetMe: IO[TelegramResult[TelegramUpdate]] = {
    client
      .expect[TelegramResult[TelegramUpdate]](
        telegramUri / "getMe"
      )
      .flatTap(hj => IO(println(hj)))
  }

  def telegramGetUpdate: IO[TelegramResult[TelegramUpdate]] = {
    client
      .expect[TelegramResult[TelegramUpdate]](
        (telegramUri / """getUpdates"""
      ).withQueryParam("allowed_updates","""["message"]"""))
      .flatTap(hj => IO(println(hj)))
  }

  def telegramGetMyCommands: IO[TelegramResult[TelegramBotCommand]] = {
    client
      .expect[TelegramResult[TelegramBotCommand]](
        telegramUri / "getMyCommands"
      )
      .flatTap(hj => IO(println(hj)))
  }

  def telegramDeleteMyCommands: IO[TelegramResult[TelegramBotCommand]] = {
    client
      .expect[TelegramResult[TelegramBotCommand]](
        telegramUri / "deleteMyCommands"
      )
      .flatTap(hj => IO(println(hj)))
  }

  def telegramSetMyCommands: IO[TelegramResult[TelegramBotCommand]] = {
    client
      .expect[TelegramResult[TelegramBotCommand]](
        telegramUri / "SetMyCommands"
      )
      .flatTap(hj => IO(println(hj)))
  }

  def sendMessage(chatId: Long, text: String): IO[Unit] = {
    client
      .successful(
        Request[IO](
          method = Method.POST,
          (telegramUri / "sendMessage")
            .withQueryParam("chat_id", chatId.toString)
            .withQueryParam("text", text)
        )
      )
      .void
  }
}
