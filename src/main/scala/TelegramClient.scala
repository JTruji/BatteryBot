import cats.effect.IO
import org.http4s.client.Client
import org.http4s.implicits.http4sLiteralsSyntax

class TelegramClient(client: Client[IO], telegramToken: String) {

  def callTelegram(): IO[Unit] = {

   client.expect[String](uri"https://api.telegram.org" / s"bot$telegramToken"/ "getMe").map(
      hj =>println(hj)
    )

    client.expect[String](uri"https://api.telegram.org" / s"bot$telegramToken"/ "sendMessage").map(
      hj =>println(hj)
    )
    client.
  }
}
