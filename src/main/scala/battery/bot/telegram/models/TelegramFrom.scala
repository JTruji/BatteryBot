package battery.bot.telegram.models

import io.circe.Codec
import io.circe.generic.extras.semiauto.deriveConfiguredCodec

case class TelegramFrom(
    id: Long,
    isBot: Boolean,
    firstName: String,
    username: Option[String],
    languageCode: Option[String]
)

object TelegramFrom{
  implicit val codec: Codec[TelegramFrom] = deriveConfiguredCodec[TelegramFrom]
}
