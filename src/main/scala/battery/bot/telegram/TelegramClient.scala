package battery.bot.telegram

import battery.bot.telegram.models.{TelegramChat, TelegramJSON}
import cats.effect.IO
import io.circe.Json
import org.http4s.QueryParamEncoder
import org.http4s.circe.CirceEntityCodec._
import org.http4s.client.Client
import org.http4s.implicits._
import shapeless.Lazy.apply

class TelegramClient(client: Client[IO], telegramToken: String) {
val telegramUri = uri"https://api.telegram.org"
  def callTelegram(): IO[TelegramJSON] = {

//    client
//      .expect[String](
//        telegramUri / s"bot$telegramToken" / "getMe"
//      )
//      .map(hj => println(hj))

    client
      .expect[TelegramJSON](
        telegramUri / s"bot$telegramToken" / "getUpdates"
      )
      .flatTap(hj => IO(println(hj)))
//      .map(_.result.map(_.message.chat.id))

//    client
//      .expect[String](
//        (telegramUri / s"bot$telegramToken" / "sendMessage")
////          .withQueryParam("chat_id", s"$chatId")
//          .withQueryParam("chat_id", id)
//          .withQueryParam("text", "This is an example")
//      )
//      .map(hj => println(hj))
  }
}
