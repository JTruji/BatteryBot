package battery.bot.telegram

import battery.bot.telegram.models.{TelegramBotCommand, TelegramJSON, TelegramResult}
import cats.effect.IO
import io.circe.Json
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

    //    client
//      .expect[String](
//        (telegramUri / s"bot$telegramToken" / "sendMessage")
////          .withQueryParam("chat_id", s"$chatId")
//          .withQueryParam("chat_id", id)
//          .withQueryParam("text", "This is an example")
//      )
//      .map(hj => println(hj))

}
