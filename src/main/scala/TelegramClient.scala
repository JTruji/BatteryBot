import cats.effect.IO
import org.http4s.client.Client
import org.http4s.implicits.http4sLiteralsSyntax

class TelegramClient(client: Client[IO], telegramToken: String, chatId: String) {

  def callTelegram(): IO[Unit] = {

    client
      .expect[String](
        uri"https://api.telegram.org" / s"bot$telegramToken" / "getMe"
      )
      .map(hj => println(hj))

    client
      .expect[String](
        (uri"https://api.telegram.org" / s"bot$telegramToken" / "sendMessage")
          .withQueryParam("chat_id", s"$chatId")
          .withQueryParam("text", "This is an example")
      )
      .map(hj => println(hj))

  }
}
