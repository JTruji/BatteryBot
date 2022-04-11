package battery.bot.telegram

import cats.effect.IO
import io.circe.Json
import org.http4s.circe._
import org.http4s.client.Client
import org.http4s.implicits._

class TelegramClient(client: Client[IO], telegramToken: String, chatId: String) {

  def callTelegram(): IO[Unit] = {

    //client
    //  .expect[String](
    //    uri"https://api.telegram.org" / s"bot$telegramToken" / "getMe"
    //  )
    //  .map(hj => println(hj))

//    client
//      .expect[Json](
//        uri"https://api.telegram.org" / s"bot$telegramToken" / "getUpdates"
//      )
//      .map(hj => println(hj.noSpaces))

    client
      .expect[String](
        (uri"https://api.telegram.org" / s"bot$telegramToken" / "sendMessage")
          .withQueryParam("chat_id", s"$chatId")
          .withQueryParam("text", "This is an example")
      )
      .map(hj => println(hj))

  }
}
