package battery.bot.telegram.models

import io.circe.Codec
import io.circe.generic.extras.semiauto.deriveConfiguredCodec


case class TelegramChat(
    id: Long,
    first_name: String,
    username: String
)

object TelegramChat {
    implicit val codec: Codec[TelegramChat] = deriveConfiguredCodec[TelegramChat]
}